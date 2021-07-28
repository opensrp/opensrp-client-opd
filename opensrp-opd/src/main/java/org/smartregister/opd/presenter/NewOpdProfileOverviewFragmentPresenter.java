package org.smartregister.opd.presenter;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.ProfileAction;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.opd.utils.RepeatingGroupsValueSource;
import org.smartregister.util.CallableInteractor;
import org.smartregister.util.CallableInteractorCallBack;
import org.smartregister.util.GenericInteractor;
import org.smartregister.util.NativeFormProcessor;
import org.smartregister.util.NativeFormProcessorFieldSource;
import org.smartregister.util.Utils;
import org.smartregister.view.ListContract;
import org.smartregister.view.presenter.ListPresenter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP1;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.ENCOUNTER_TYPE;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.FIELDS;


public class NewOpdProfileOverviewFragmentPresenter extends ListPresenter<ProfileAction> implements OpdProfileFragmentContract.Presenter<ProfileAction> {

    private CallableInteractor callableInteractor;

    @Override
    public void openForm(Context context, String formName, String baseEntityID, String formSubmissionId) {
        CallableInteractor myInteractor = getCallableInteractor();
        Callable<JSONObject> callable = () -> readFormAndAddValues(readFormAsJson(context, formName, baseEntityID), formSubmissionId);
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

    public JSONObject readFormAndAddValues(JSONObject jsonObject, String formSubmissionId) throws JSONException {
        attachAgeAndGender(jsonObject);
        if (getView() != null) {
            getView().attachGlobals(jsonObject, formSubmissionId);
        }
        attachLocationHierarchy(jsonObject);

        if (StringUtils.isEmpty(formSubmissionId)) return jsonObject;

        NativeFormProcessor processor = OpdLibrary.getInstance().getFormProcessorFactory().createInstance(jsonObject);

        // read values
        JSONObject savedEvent = VisitDao.fetchEventAsJson(formSubmissionId);
        Map<String, Object> values = processor.getFormResults(savedEvent);

        // multi_select_list
        if (values.containsKey("disease_code_primary"))
            values.put("disease_code_primary", values.get("disease_code_object"));

        if (values.containsKey("disease_code_final_diagn"))
            values.put("disease_code_final_diagn", values.get("disease_code_object_final"));

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

    protected void attachAgeAndGender(JSONObject jsonObject) {
        try {
            String encounterType = jsonObject.getString(ENCOUNTER_TYPE);
            if (getView() == null)
                return;
            CommonPersonObjectClient commonPersonObject = getView().getCommonPersonObject();
            if (commonPersonObject != null && encounterType.equals(OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS)) {
                String gender = commonPersonObject.getColumnmaps().get(OpdDbConstants.Column.Client.GENDER);
                String age = String.valueOf(Utils.getAgeFromDate(commonPersonObject.getColumnmaps().get(OpdDbConstants.Column.Client.DOB)));
                JSONArray fields = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);
                    if (field.getString(OpdConstants.KEY.KEY).equals(OpdConstants.JSON_FORM_KEY.AGE)) {
                        field.put(OpdConstants.JSON_FORM_KEY.VALUE, age);
                    } else if (field.getString(OpdConstants.KEY.KEY).equals(OpdConstants.JSON_FORM_KEY.GENDER)) {
                        field.put(OpdConstants.JSON_FORM_KEY.VALUE, gender);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
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
        /**
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
