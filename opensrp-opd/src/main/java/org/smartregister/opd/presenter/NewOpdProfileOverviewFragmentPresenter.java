package org.smartregister.opd.presenter;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.NativeFormFieldProcessor;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.ProfileAction;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.opd.utils.RepeatingGroupsValueSource;
import org.smartregister.opd.utils.VisitUtils;
import org.smartregister.util.CallableInteractor;
import org.smartregister.util.CallableInteractorCallBack;
import org.smartregister.util.GenericInteractor;
import org.smartregister.util.NativeFormProcessor;
import org.smartregister.util.NativeFormProcessorFieldSource;
import org.smartregister.util.Utils;
import org.smartregister.view.ListContract;
import org.smartregister.view.presenter.ListPresenter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_ID;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_PARENT;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.ENCOUNTER_TYPE;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.VALUE;
import static org.smartregister.opd.utils.OpdConstants.KEY.KEY;


public class NewOpdProfileOverviewFragmentPresenter extends ListPresenter<ProfileAction> implements OpdProfileFragmentContract.Presenter<ProfileAction> {

    private CallableInteractor callableInteractor;

    @Override
    public void openForm(Context context, String formName, String baseEntityID, String formSubmissionId) {
        CallableInteractor myInteractor = getCallableInteractor();
        Callable<JSONObject> callable = () -> readFormAndAddValues(readFormAsJson(context, formName, baseEntityID), formSubmissionId, baseEntityID);
        myInteractor.execute(callable, new CallableInteractorCallBack<JSONObject>() {
            @Override
            public void onResult(JSONObject jsonObject) {
                OpdProfileFragmentContract.View<ProfileAction> view = getView();
                if (view != null) {
                    if (jsonObject != null) {
                        view.startJsonForm(jsonObject);
                    } else {
                        view.onFetchError(new IllegalArgumentException("Form not found"));
                    }
                    view.setLoadingState(false);
                }
            }

            @Override
            public void onError(Exception ex) {
                ListContract.View<ProfileAction> view = getView();
                if (view != null) {
                    view.onFetchError(ex);
                    view.setLoadingState(false);
                }
            }
        });
    }

    public JSONObject readFormAndAddValues(JSONObject jsonObject, String formSubmissionId, String baseEntityID) throws JSONException {
        if (getView() != null) {
            OpdJsonFormUtils.attachAgeAndGender(jsonObject, getView().getCommonPersonObject());
            getView().attachGlobals(jsonObject, formSubmissionId);
        }
        attachLocationHierarchy(jsonObject);

        if (StringUtils.isEmpty(formSubmissionId)) {
            VisitUtils.addPreviousVisitHivStatus(jsonObject, baseEntityID);
            return jsonObject;
        }

        NativeFormProcessor processor = OpdLibrary.getInstance().getFormProcessorFactory().createInstance(jsonObject);

        // read values
        JSONObject savedEvent = VisitDao.fetchEventAsJson(formSubmissionId);
        Map<String, Object> values = processor.getFormResults(savedEvent);

        // multi_select_list
        OpdJsonFormUtils.patchMultiSelectList(values);

        // inject values
        processor.populateValues(values, jsonObject1 -> {
            try {
                String key = ((jsonObject1.has(JsonFormConstants.KEY)) ? jsonObject1.getString(JsonFormConstants.KEY) : "");
                //Repeating Group
                if (key.contains("test_ordered_avail")) {
                    jsonObject1.put(JsonFormConstants.READ_ONLY, true);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }, customElementLoaders(processor));

        jsonObject.put(OpdConstants.Properties.FORM_SUBMISSION_ID, formSubmissionId);

        return jsonObject;
    }

    private void attachLocationHierarchy(JSONObject jsonObject) {
        try {
            if (jsonObject.getString(ENCOUNTER_TYPE).equals(OpdConstants.OpdModuleEventConstants.OPD_PHARMACY)
                    || jsonObject.getString(ENCOUNTER_TYPE).equals(OpdConstants.OpdModuleEventConstants.OPD_LABORATORY)
                    || jsonObject.getString(ENCOUNTER_TYPE).equals(OpdConstants.OpdModuleEventConstants.OPD_FINAL_OUTCOME)) {
                OpdJsonFormUtils.addRegLocHierarchyQuestions(jsonObject);
            }
        } catch (Exception e) {
            Timber.e(e, "NewOpdProfileOverviewFragmentPresenter -> attachLocationHierarchy()");
        }

    }

    @Override
    public JSONObject readFormAsJson(Context context, String formName, String baseEntityID) throws JSONException {
        // read form and inject base id
        String jsonForm = readAssetContents(context, formName);
        JSONObject jsonObject = new JSONObject(jsonForm);
        jsonObject.put(OpdConstants.Properties.BASE_ENTITY_ID, baseEntityID);
        return jsonObject;
    }

    private Map<String, NativeFormProcessorFieldSource> customElementLoaders(NativeFormProcessor processor) {
        Map<String, NativeFormProcessorFieldSource> elements = new HashMap<>();

        elements.put(JsonFormConstants.REPEATING_GROUP, new RepeatingGroupsValueSource(processor));
        /*
         elements.put(JsonFormConstants.REPEATING_GROUP, new NativeFormProcessorFieldSource() {
        @Override public <T> void populateValue(String stepName, JSONObject step, JSONObject fieldJson, Map<String, T> dictionary) {
        RepeatingGroupGenerator repeatingGroupGenerator = new RepeatingGroupGenerator(json.optJSONObject("step4"), stepName,
        "baby_alive_group",
        outcomeColumnMap(),
        PncDbConstants.KEY.BASE_ENTITY_ID,
        storedValues(entityId));
        repeatingGroupGenerator
        .setFieldsWithoutSpecialViewValidation
        (new HashSet<>(
        Arrays.asList("birth_weight_entered", "birth_height_entered",
        "birth_record_date", "baby_gender", "baby_first_name",
        "baby_last_name", "baby_dob")));
        repeatingGroupGenerator.init();
        }
        });
         **/
        return elements;
    }

    @Override
    public String readAssetContents(Context context, String path) {
        return Utils.readAssetContents(context, path);
    }

    @Override
    public void saveForm(String jsonString, Context context) {
        CallableInteractor myInteractor = getCallableInteractor();

        Callable<Void> callable = () -> {
            JSONObject jsonObject = new JSONObject(jsonString);
            String eventType = jsonObject.getString(ENCOUNTER_TYPE);

            // inject map value for repeating groups
            if (eventType.equalsIgnoreCase(OpdConstants.OpdModuleEventConstants.OPD_LABORATORY)) {
                OpdUtils.injectGroupMap(jsonObject);
            }

            NativeFormProcessor processor = OpdLibrary.getInstance().getFormProcessorFactory().createInstance(jsonObject);
            String entityId = jsonObject.getString(OpdConstants.Properties.BASE_ENTITY_ID);
            String formSubmissionId = jsonObject.has(OpdConstants.Properties.FORM_SUBMISSION_ID) ?
                    jsonObject.getString(OpdConstants.Properties.FORM_SUBMISSION_ID) : null;


            // update metadata
            processor.withBindType("ec_client")
                    .withEncounterType(eventType)
                    .withFieldProcessors(getFieldProcessorMap())
                    .withFormSubmissionId(formSubmissionId)
                    .withEntityId(entityId)
                    .tagEventMetadata()
                    // create and save event to db
                    .saveEvent()
                    // execute client processing
                    .clientProcessForm();

            return null;
        };

        myInteractor.execute(callable, new CallableInteractorCallBack<Void>() {
            @Override
            public void onResult(Void aVoid) {
                OpdProfileFragmentContract.View<ProfileAction> view = getView();
                if (view != null) {
                    view.reloadFromSource();
                    view.setLoadingState(false);
                }
            }

            @Override
            public void onError(Exception ex) {
                OpdProfileFragmentContract.View<ProfileAction> view = getView();
                if (view == null) return;
                view.onFetchError(ex);
                view.setLoadingState(false);
            }
        });
    }

    private Map<String, NativeFormFieldProcessor> getFieldProcessorMap() {
        Map<String, NativeFormFieldProcessor> fieldProcessorMap = new HashMap<>();
        fieldProcessorMap.put(OpdConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, (event, jsonObject1) -> {
            JSONArray valuesJsonArray;
            try {
                valuesJsonArray = new JSONArray(jsonObject1.optString(VALUE));
                for (int i = 0; i < valuesJsonArray.length(); i++) {
                    JSONObject jsonValObject = valuesJsonArray.optJSONObject(i);
                    String fieldType = jsonValObject.optString(OPENMRS_ENTITY);
                    String fieldCode = jsonObject1.optString(OPENMRS_ENTITY_ID);
                    String parentCode = jsonObject1.optString(OPENMRS_ENTITY_PARENT);
                    String value = jsonValObject.optString(OPENMRS_ENTITY_ID);
                    String humanReadableValues = jsonValObject.optString(AllConstants.TEXT);
                    String formSubmissionField = jsonObject1.optString(KEY);
                    event.addObs(new Obs(fieldType, AllConstants.TEXT, fieldCode, parentCode, Collections.singletonList(value),
                            Collections.singletonList(humanReadableValues), "", formSubmissionField));
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        });
        return fieldProcessorMap;
    }

    @Override
    public CallableInteractor getCallableInteractor() {
        if (callableInteractor == null)
            callableInteractor = new GenericInteractor();

        return callableInteractor;
    }

    @Override
    public OpdProfileFragmentContract.View<ProfileAction> getView() {
        return (OpdProfileFragmentContract.View<ProfileAction>) super.getView();
    }
}
