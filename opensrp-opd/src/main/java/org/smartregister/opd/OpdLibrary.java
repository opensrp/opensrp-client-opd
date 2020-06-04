package org.smartregister.opd;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.helper.OpdRulesEngineHelper;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.repository.OpdCheckInRepository;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.opd.repository.OpdDiagnosisAndTreatmentFormRepository;
import org.smartregister.opd.repository.OpdDiagnosisDetailRepository;
import org.smartregister.opd.repository.OpdTestConductedRepository;
import org.smartregister.opd.repository.OpdTreatmentDetailRepository;
import org.smartregister.opd.repository.OpdVisitRepository;
import org.smartregister.opd.repository.OpdVisitSummaryRepository;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;
import timber.log.Timber;

import static org.smartregister.opd.utils.OpdJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

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
    private OpdDiagnosisDetailRepository opdDiagnosisDetailRepository;
    private OpdTreatmentDetailRepository opdTreatmentDetailRepository;
    private OpdTestConductedRepository opdTestConductedRepository;
    private OpdVisitSummaryRepository opdVisitSummaryRepository;

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
            uniqueIdRepository = new UniqueIdRepository();
        }
        return uniqueIdRepository;
    }

    @NonNull
    public OpdCheckInRepository getCheckInRepository() {
        if (checkInRepository == null) {
            checkInRepository = new OpdCheckInRepository();
        }

        return checkInRepository;
    }

    @NonNull
    public OpdVisitRepository getVisitRepository() {
        if (visitRepository == null) {
            visitRepository = new OpdVisitRepository();
        }

        return visitRepository;
    }

    @NonNull
    public OpdDetailsRepository getOpdDetailsRepository() {
        if (opdDetailsRepository == null) {
            opdDetailsRepository = new OpdDetailsRepository();
        }
        return opdDetailsRepository;
    }

    @NonNull
    public OpdDiagnosisAndTreatmentFormRepository getOpdDiagnosisAndTreatmentFormRepository() {
        if (opdDiagnosisAndTreatmentFormRepository == null) {
            opdDiagnosisAndTreatmentFormRepository = new OpdDiagnosisAndTreatmentFormRepository();
        }
        return opdDiagnosisAndTreatmentFormRepository;
    }

    @NonNull
    public OpdDiagnosisDetailRepository getOpdDiagnosisDetailRepository() {
        if (opdDiagnosisDetailRepository == null) {
            opdDiagnosisDetailRepository = new OpdDiagnosisDetailRepository();
        }
        return opdDiagnosisDetailRepository;
    }

    @NonNull
    public OpdTestConductedRepository getOpdTestConductedRepository() {
        if (opdTestConductedRepository == null) {
            opdTestConductedRepository = new OpdTestConductedRepository();
        }
        return opdTestConductedRepository;
    }

    @NonNull
    public OpdTreatmentDetailRepository getOpdTreatmentDetailRepository() {
        if (opdTreatmentDetailRepository == null) {
            opdTreatmentDetailRepository = new OpdTreatmentDetailRepository();
        }
        return opdTreatmentDetailRepository;
    }

    @NonNull
    public OpdVisitSummaryRepository getOpdVisitSummaryRepository() {
        if (opdVisitSummaryRepository == null) {
            opdVisitSummaryRepository = new OpdVisitSummaryRepository();
        }
        return opdVisitSummaryRepository;
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
            compressor = new Compressor(context().applicationContext());
        }

        return compressor;
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
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
        customTypeDescription.addPropertyParameters(YamlConfigItem.GENERIC_YAML_ITEMS, YamlConfigItem.class);
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

        HashMap<String, String> injectedFields = new HashMap<>();
        injectedFields.put("visit_id", JsonFormUtils.generateRandomUUIDString());
        injectedFields.put("visit_date", OpdUtils.convertDate(new Date(), OpdDbConstants.DATE_FORMAT));

        OpdJsonFormUtils.populateInjectedFields(jsonFormObject, injectedFields);

        FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

        String baseEntityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.ENTITY_TABLE);
        Event opdCheckinEvent = OpdJsonFormUtils.createEvent(fieldsArray, jsonFormObject.getJSONObject(METADATA)
                , formTag, baseEntityId, eventType, entityTable)
                .withChildLocationId(OpdLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality());

        AllSharedPreferences allSharedPreferences = OpdUtils.getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        opdCheckinEvent.setProviderId(providerId);
        opdCheckinEvent.setLocationId(OpdJsonFormUtils.locationId(allSharedPreferences));
        opdCheckinEvent.setFormSubmissionId(opdCheckinEvent.getFormSubmissionId());

        opdCheckinEvent.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        opdCheckinEvent.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        opdCheckinEvent.setClientDatabaseVersion(OpdLibrary.getInstance().getDatabaseVersion());
        opdCheckinEvent.setClientApplicationVersion(OpdLibrary.getInstance().getApplicationVersion());

        return opdCheckinEvent;
    }

    public List<Event> processOpdDiagnosisAndTreatmentForm(@NonNull String jsonString, @NonNull Intent data) throws JSONException {
        JSONObject jsonFormObject = new JSONObject(jsonString);

        String entityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);

        Map<String, String> opdCheckInMap = OpdLibrary.getInstance().getCheckInRepository().getLatestCheckIn(entityId);
        FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

        if (opdCheckInMap != null && !opdCheckInMap.isEmpty()) {
            String visitId = opdCheckInMap.get(OpdDbConstants.Column.OpdCheckIn.VISIT_ID);
            String steps = jsonFormObject.optString(JsonFormConstants.COUNT);
            int numOfSteps = Integer.valueOf(steps);
            List<Event> eventList = new ArrayList<>();

            for (int j = 0; j < numOfSteps; j++) {
                JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));
                String title = step.optString(JsonFormConstants.STEP_TITLE);
                String stepEncounterType = step.optString(JsonFormConstants.ENCOUNTER_TYPE);
                String bindType = step.optString(OpdConstants.BIND_TYPE);


                JSONArray fields = step.getJSONArray(OpdJsonFormUtils.FIELDS);
                String valueIds = null;
                JSONObject jsonObject;
                JSONArray valueJsonArray = null;

                HashMap<String, String> eventDetails = new HashMap<>();
                if (OpdConstants.StepTitle.TEST_CONDUCTED.equals(title)) {
                    HashMap<String, HashMap<String, String>> buildRepeatingGroupTests = OpdUtils.buildRepeatingGroupTests(step);
                    if (!buildRepeatingGroupTests.isEmpty()) {
                        String strTest = gson.toJson(buildRepeatingGroupTests);
                        eventDetails.put(OpdConstants.REPEATING_GROUP_MAP, strTest);
                        JSONObject repeatingGroupObj = new JSONObject();
                        repeatingGroupObj.put(JsonFormConstants.KEY, OpdConstants.REPEATING_GROUP_MAP);
                        repeatingGroupObj.put(JsonFormConstants.VALUE, strTest);
                        repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                        fields.put(repeatingGroupObj);
                        valueIds = OpdUtils.generateNIds(buildRepeatingGroupTests.size());
                    } else {
                        continue;
                    }
                } else if (OpdConstants.StepTitle.DIAGNOSIS.equals(title)) {
                    jsonObject = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
                    JSONObject jsonDiagnosisType = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
                    String diagnosisType = jsonDiagnosisType.optString(OpdConstants.KEY.VALUE);
                    String value = jsonObject.optString(OpdConstants.KEY.VALUE);
                    if (StringUtils.isBlank(value) || (new JSONArray(value).length() == 0)) {
                        valueIds = OpdUtils.generateNIds(1);
                    } else {
                        valueJsonArray = new JSONArray(value);
                        JSONArray jsonArrayWithOpenMrsIds = addOpenMrsEntityId(diagnosisType.toLowerCase(), valueJsonArray);
                        jsonObject.put(OpdConstants.KEY.VALUE, jsonArrayWithOpenMrsIds);
                        valueIds = OpdUtils.generateNIds(valueJsonArray.length());
                    }
                } else if (OpdConstants.StepTitle.TREATMENT.equals(title)) {
                    jsonObject = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.MEDICINE);
                    jsonObject.put(AllConstants.TYPE, AllConstants.MULTI_SELECT_LIST);
                    String value = jsonObject.optString(OpdConstants.KEY.VALUE);
                    if (StringUtils.isBlank(value) || (new JSONArray(value).length() == 0)) {
                        valueIds = OpdUtils.generateNIds(1);
                    } else {
                        valueJsonArray = new JSONArray(value);
                        valueIds = OpdUtils.generateNIds(valueJsonArray.length());
                    }
                }

                Event baseEvent = JsonFormUtils.createEvent(fields, jsonFormObject.getJSONObject(METADATA),
                        formTag, entityId, stepEncounterType, bindType).withChildLocationId(OpdLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality());

                OpdJsonFormUtils.tagSyncMetadata(baseEvent);
                baseEvent.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
                if (StringUtils.isNotBlank(valueIds)) {
                    baseEvent.addDetails(OpdConstants.JSON_FORM_KEY.ID, valueIds);
                }
                if (valueJsonArray != null) {
                    baseEvent.addDetails(OpdConstants.KEY.VALUE, valueJsonArray.toString());
                }
                if (!eventDetails.isEmpty()) {
                    baseEvent.getDetails().putAll(eventDetails);
                }

                eventList.add(baseEvent);

            }
            //remove any saved sessions
            OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(entityId);
            OpdLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().delete(opdDiagnosisAndTreatmentForm);

            Event closeOpdVisit = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                    formTag, entityId, OpdConstants.EventType.CLOSE_OPD_VISIT, "").withChildLocationId(OpdLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality());

            OpdJsonFormUtils.tagSyncMetadata(closeOpdVisit);
            closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
            closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE, OpdUtils.convertDate(new Date(), OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
            eventList.add(closeOpdVisit);

            return eventList;
        } else {
            Timber.e("Corresponding OpdCheckIn for EntityId %s is missing", entityId);
            return null;
        }

    }

    protected JSONArray addOpenMrsEntityId(String diagnosisType, JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                jsonObject.put(JsonFormConstants.OPENMRS_ENTITY_ID, jsonObject.optJSONObject(JsonFormConstants.MultiSelectUtils.PROPERTY)
                        .optString(diagnosisType.concat("-id")));
            }

            return jsonArray;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return jsonArray;
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

    public boolean isPatientInTreatedState(@NonNull String strVisitEndDate) {
        Date visitEndDate = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, strVisitEndDate);
        if (visitEndDate != null) {
            return isPatientInTreatedState(visitEndDate);
        }

        return false;
    }

    public boolean isPatientInTreatedState(@NonNull Date visitEndDate) {
        // Get the midnight of that day when the visit happened
        Calendar date = Calendar.getInstance();
        date.setTime(visitEndDate);
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        // next day
        date.add(Calendar.DAY_OF_MONTH, 1);
        return getDateNow().before(date.getTime());
    }

    @VisibleForTesting
    @NonNull
    protected Date getDateNow() {
        return new Date();
    }

    /**
     * This checks if the patient can perform a Check-In evaluated based on their latest visit details & opd details. This however does not consider the TREATED status
     * which appears after a visit is completed within the same day. If you need to consider the TREATED status, you should first call {@link #isPatientInTreatedState(Date)}
     * and then call this method if the result is false.
     *
     * @param visit
     * @param opdDetails
     * @return
     */
    public boolean canPatientCheckInInsteadOfDiagnoseAndTreat(@Nullable OpdVisit visit, @Nullable OpdDetails opdDetails) {
        Date latestValidCheckInDate = OpdLibrary.getInstance().getLatestValidCheckInDate();

        // If we are past the 24 hours or so, then the status should be check-in
        // If your opd
        return visit == null || visit.getVisitDate().before(latestValidCheckInDate) || (opdDetails != null && opdDetails.getCurrentVisitEndDate() != null);
    }

    public boolean isClientCurrentlyCheckedIn(@Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails) {
        return !canPatientCheckInInsteadOfDiagnoseAndTreat(opdVisit, opdDetails);
    }
}
