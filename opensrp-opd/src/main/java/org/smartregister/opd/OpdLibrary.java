package org.smartregister.opd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.helper.OpdRulesEngineHelper;
import org.smartregister.opd.pojos.OpdCheckIn;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.repository.OpdCheckInRepository;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.opd.repository.OpdDiagnosisAndTreatmentFormRepository;
import org.smartregister.opd.repository.OpdDiagnosisRepository;
import org.smartregister.opd.repository.OpdServiceDetailRepository;
import org.smartregister.opd.repository.OpdTestConductedRepository;
import org.smartregister.opd.repository.OpdTreatmentRepository;
import org.smartregister.opd.repository.OpdVisitRepository;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

import static org.smartregister.opd.utils.OpdJsonFormUtils.METADATA;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdLibrary {

    private static OpdLibrary instance;
    private final Context context;
    private final Repository repository;
    private OpdConfiguration opdConfiguration;
    private ECSyncHelper syncHelper;

    private UniqueIdRepository uniqueIdRepository;
    private OpdCheckInRepository checkInRepository;
    private OpdVisitRepository visitRepository;
    private OpdDetailsRepository opdDetailsRepository;
    private OpdDiagnosisAndTreatmentFormRepository opdDiagnosisAndTreatmentFormRepository;
    private OpdServiceDetailRepository opdServiceDetailRepository;
    private OpdDiagnosisRepository opdDiagnosisRepository;
    private OpdTreatmentRepository opdTreatmentRepository;
    private OpdTestConductedRepository opdTestConductedRepository;
    private Compressor compressor;
    private int applicationVersion;
    private int databaseVersion;

    private Yaml yaml;

    private OpdRulesEngineHelper opdRulesEngineHelper;

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
    public Context context() {
        return context;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    @NonNull
    public OpdCheckInRepository getCheckInRepository() {
        if (checkInRepository == null) {
            checkInRepository = new OpdCheckInRepository(getRepository());
        }

        return checkInRepository;
    }

    @NonNull
    public OpdVisitRepository getVisitRepository() {
        if (visitRepository == null) {
            visitRepository = new OpdVisitRepository(getRepository());
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
    public OpdDiagnosisAndTreatmentFormRepository getOpdDiagnosisAndTreatmentFormRepository() {
        if (opdDiagnosisAndTreatmentFormRepository == null) {
            opdDiagnosisAndTreatmentFormRepository = new OpdDiagnosisAndTreatmentFormRepository(getRepository());
        }
        return opdDiagnosisAndTreatmentFormRepository;
    }

    @NonNull
    public OpdDiagnosisRepository getOpdDiagnosisRepository() {
        if (opdDiagnosisRepository == null) {
            opdDiagnosisRepository = new OpdDiagnosisRepository(getRepository());
        }
        return opdDiagnosisRepository;
    }

    @NonNull
    public OpdTestConductedRepository getOpdTestConductedRepository() {
        if (opdTestConductedRepository == null) {
            opdTestConductedRepository = new OpdTestConductedRepository(getRepository());
        }
        return opdTestConductedRepository;
    }

    @NonNull
    public OpdTreatmentRepository getOpdTreatmentRepository() {
        if (opdTreatmentRepository == null) {
            opdTreatmentRepository = new OpdTreatmentRepository(getRepository());
        }
        return opdTreatmentRepository;
    }

    @NonNull
    public OpdServiceDetailRepository getOpdServiceDetailRepository() {
        if (opdServiceDetailRepository == null) {
            opdServiceDetailRepository = new OpdServiceDetailRepository(getRepository());
        }
        return opdServiceDetailRepository;
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

    @NonNull
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

    @NonNull
    public Iterable<Object> readYaml(@NonNull String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                context.applicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    @NonNull
    public OpdRulesEngineHelper getOpdRulesEngineHelper() {
        if (opdRulesEngineHelper == null) {
            opdRulesEngineHelper = new OpdRulesEngineHelper();
        }

        return opdRulesEngineHelper;
    }

    @NonNull
    public Event processOpdCheckInForm(@NonNull String eventType, String jsonString, @Nullable Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);

        JSONObject stepOne = jsonFormObject.getJSONObject(OpdJsonFormUtils.STEP1);
        JSONArray fieldsArray = stepOne.getJSONArray(OpdJsonFormUtils.FIELDS);

        FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

        String baseEntityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.ENTITY_TABLE);
        Event opdCheckinEvent = OpdJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
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
        opdCheckinEvent.addDetails(OpdConstants.Event.CheckIn.Detail.VISIT_DATE, OpdUtils.convertDate(new Date(), OpdDbConstants.DATE_FORMAT));

        return opdCheckinEvent;
    }

    public List<Event> processOpdDiagnosisAndTreatmentForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);
        JSONObject step1JsonObject = jsonFormObject.optJSONObject(OpdConstants.JSON_FORM_EXTRA.STEP1);

        JSONObject step2JsonObject = jsonFormObject.optJSONObject(OpdConstants.JSON_FORM_EXTRA.STEP2);

        JSONObject step3JsonObject = jsonFormObject.optJSONObject(OpdConstants.JSON_FORM_EXTRA.STEP3);
        JSONObject step4JsonObject = jsonFormObject.optJSONObject(OpdConstants.JSON_FORM_EXTRA.STEP4);

        String entityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);

        OpdCheckIn opdCheckIn = OpdLibrary.getInstance().getCheckInRepository().getLatestCheckIn(entityId);

        String visitId = opdCheckIn.getVisitId();

        List<JSONObject> steps = Arrays.asList(step1JsonObject, step2JsonObject, step3JsonObject, step4JsonObject);

        FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

        List<Event> eventList = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            JSONObject step = steps.get(i);
            JSONArray fields = step.getJSONArray(OpdJsonFormUtils.FIELDS);
            String valueIds = null;
            JSONObject jsonObject;
            if (i == 0 || i == 3) {
                valueIds = OpdUtils.generateNIds(1);
            } else if (i == 1) {
                jsonObject = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
                String value = jsonObject.optString(OpdConstants.KEY.VALUE);
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                valueIds = OpdUtils.generateNIds(new JSONArray(value).length());
            } else if (i == 2) {
                jsonObject = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.MEDICINE);
                String value = jsonObject.optString(OpdConstants.KEY.VALUE);
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                valueIds = OpdUtils.generateNIds(new JSONArray(value).length());
            }
            Event baseEvent = JsonFormUtils.createEvent(fields, jsonFormObject.getJSONObject(METADATA),
                    formTag, entityId, getDiagnosisAndTreatmentEventArray()[i], getDiagnosisAndTreatmentTableArray()[i]);
            OpdJsonFormUtils.tagSyncMetadata(baseEvent);
            baseEvent.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
            baseEvent.addDetails(OpdConstants.JSON_FORM_KEY.ID, valueIds);
            eventList.add(baseEvent);
        }

        //remove any saved sessions
        OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(entityId);
        OpdLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().delete(opdDiagnosisAndTreatmentForm);

        Event closeOpdVisit = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                formTag, entityId, OpdConstants.EventType.CLOSE_OPD_VISIT, "");
        OpdJsonFormUtils.tagSyncMetadata(closeOpdVisit);
        closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
        closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE, OpdUtils.convertDate(new Date(), OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
        eventList.add(closeOpdVisit);

        return eventList;
    }

    protected String[] getDiagnosisAndTreatmentEventArray() {
        return new String[]{OpdConstants.EventType.TEST_CONDUCTED, OpdConstants.EventType.DIAGNOSIS,
                OpdConstants.EventType.TREATMENT, OpdConstants.EventType.SERVICE_DETAIL};
    }

    protected String[] getDiagnosisAndTreatmentTableArray() {
        return new String[]{OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Table.OPD_DIAGNOSIS,
                OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Table.OPD_SERVICE_DETAIL};
    }

    public String opdLookUpQuery() {
        String lookUpQueryForChild = "select id as _id, relationalid, first_name, last_name, gender, dob, base_entity_id, null as national_id from ec_child where [condition] ";
        String lookUpQueryForMother = "select id as _id, relationalid, first_name, last_name, gender, dob, base_entity_id, nrc_number as national_id from ec_mother where [condition] ";
        String lookUpQueryForOpdClient = "select id as _id, relationalid, first_name, last_name, gender, dob, base_entity_id, null as national_id from ec_client where [condition] ";
        return lookUpQueryForChild + " union all " + lookUpQueryForMother + " union all " + lookUpQueryForOpdClient;
    }

    /**
     * This method enables us to configure how-long ago we should consider a valid check-in so that
     * we enable the next step which is DIAGNOSE & TREAT. This method returns the latest date that a check-in
     * should be so that it can be considered for moving to DIAGNOSE & TREAT
     *
     * @return Date
     */
    @NonNull
    public Date getLatestValidCheckInDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return calendar.getTime();
    }
}
