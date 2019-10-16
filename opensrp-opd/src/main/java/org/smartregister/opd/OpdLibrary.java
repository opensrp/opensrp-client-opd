package org.smartregister.opd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.helper.AncRulesEngineHelper;
import org.smartregister.opd.repository.CheckInRepository;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.opd.repository.VisitRepository;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.smartregister.sync.helper.ECSyncHelper;

import id.zelory.compressor.Compressor;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdLibrary {

    private final Context context;
    private static OpdLibrary instance;
    private OpdConfiguration opdConfiguration;
    private final Repository repository;
    private ECSyncHelper syncHelper;

    private UniqueIdRepository uniqueIdRepository;
    private CheckInRepository checkInRepository;
    private VisitRepository visitRepository;
    private OpdDetailsRepository opdDetailsRepository;

    private Compressor compressor;
    private int applicationVersion;
    private int databaseVersion;
    private Yaml yaml;

    private AncRulesEngineHelper ancRulesEngineHelper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);

    protected OpdLibrary(@NonNull Context context, @NonNull OpdConfiguration opdConfiguration
            , @NonNull Repository repository, int applicationVersion, int databaseVersion) {
        this.context = context;
        this.opdConfiguration = opdConfiguration;
        this.repository = repository;
        this.applicationVersion = applicationVersion;
        this.databaseVersion = databaseVersion;

        // Initialize configs processor
        initializeYamlConfigs();
    }

    public static void init(Context context, @NonNull Repository repository, @NonNull OpdConfiguration opdConfiguration
            , int applicationVersion, int databaseVersion) {
        if (instance == null) {
            instance = new OpdLibrary(context, opdConfiguration, repository, applicationVersion, databaseVersion);
        }
    }

    @NonNull
    public Context context() {
        return context;
    }

    public static OpdLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance does not exist!!! Call "
                    + OpdLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class");
        }
        return instance;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    @NonNull
    public CheckInRepository getCheckInRepository() {
        if (checkInRepository == null) {
            checkInRepository = new CheckInRepository(getRepository());
        }

        return checkInRepository;
    }

    @NonNull
    public VisitRepository getVisitRepository() {
        if (visitRepository == null) {
            visitRepository = new VisitRepository(getRepository());
        }

        return visitRepository;
    }

    @NonNull
    public OpdDetailsRepository getOpdDetailsRepository() {
        if (opdDetailsRepository == null) {
            opdDetailsRepository = new OpdDetailsRepository(getRepository());
        }

        return opdDetailsRepository;
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }


    @NonNull
    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    @NonNull
    public OpdConfiguration getOpdConfiguration() {
        return opdConfiguration;
    }

    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = Compressor.getDefault(context().applicationContext());
        }
        return compressor;
    }


    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    private void initializeYamlConfigs() {
        Constructor constructor = new Constructor(YamlConfig.class);
        TypeDescription customTypeDescription = new TypeDescription(YamlConfig.class);
        customTypeDescription.addPropertyParameters(YamlConfigItem.FIELD_CONTACT_SUMMARY_ITEMS, YamlConfigItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
    }

    public Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                DrishtiApplication.getInstance().getApplicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    public AncRulesEngineHelper getAncRulesEngineHelper() {
        if (ancRulesEngineHelper == null) {
            ancRulesEngineHelper = new AncRulesEngineHelper(context.applicationContext().getApplicationContext());
        }
        return ancRulesEngineHelper;
    }

    @NonNull
    public Event processOpdCheckInForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONObject stepOne = jsonFormObject.getJSONObject(OpdJsonFormUtils.STEP1);
        JSONArray fieldsArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);

        FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

        String baseEntityId = OpdUtils.getBaseEntityId(data);
        String entityTable = OpdUtils.getEntityTable(data);
        Event opdCheckinEvent = OpdJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(OpdJsonFormUtils.METADATA)
                , formTag, baseEntityId, eventType, entityTable);

        // Generate the eventId and add it
        opdCheckinEvent.setEventId(JsonFormUtils.generateRandomUUIDString());

        AllSharedPreferences allSharedPreferences = OpdUtils.getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        opdCheckinEvent.setProviderId(providerId);
        opdCheckinEvent.setLocationId(OpdJsonFormUtils.locationId(allSharedPreferences));

        opdCheckinEvent.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        opdCheckinEvent.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        opdCheckinEvent.setClientDatabaseVersion(OpdLibrary.getInstance().getDatabaseVersion());
        opdCheckinEvent.setClientApplicationVersion(OpdLibrary.getInstance().getApplicationVersion());

        // Create the visit Id
        opdCheckinEvent.addDetails(OpdConstants.Event.CheckIn.Detail.VISIT_ID, JsonFormUtils.generateRandomUUIDString());
        opdCheckinEvent.addDetails(OpdConstants.Event.CheckIn.Detail.VISIT_DATE, dateFormat.format(new Date()));

        return opdCheckinEvent;
    }
}
