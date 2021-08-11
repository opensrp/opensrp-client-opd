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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Event;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.ProfileHistory;
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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_ID;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_PARENT;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP1;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.ENCOUNTER_TYPE;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.FIELDS;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.VALUE;
import static org.smartregister.opd.utils.OpdConstants.KEY.KEY;


public class NewOpdProfileVisitsFragmentPresenter extends ListPresenter<ProfileHistory> implements OpdProfileFragmentContract.Presenter<ProfileHistory> {

    private CallableInteractor callableInteractor;

    @Override
    public void openForm(Context context, String formName, String baseEntityID, String formSubmissionId) {
        CallableInteractor myInteractor = getCallableInteractor();
        Callable<JSONObject> callable = () -> readFormAndAddValues(readFormAsJson(context, formName, baseEntityID), formSubmissionId);
        myInteractor.execute(callable, new CallableInteractorCallBack<JSONObject>() {
            @Override
            public void onResult(JSONObject jsonObject) {
                OpdProfileFragmentContract.View<ProfileHistory> view = getView();
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
                ListContract.View<ProfileHistory> view = getView();
                if (view != null) {
                    view.onFetchError(ex);
                    view.setLoadingState(false);
                }
            }
        });
    }

    public JSONObject readFormAndAddValues(JSONObject jsonObject, String formSubmissionId) throws JSONException {
        if (getView() != null) {
            OpdJsonFormUtils.attachAgeAndGender(jsonObject, getView().getCommonPersonObject());
            getView().attachGlobals(jsonObject, formSubmissionId);
        }
        attachLocationHierarchy(jsonObject);

        if (StringUtils.isEmpty(formSubmissionId)) return jsonObject;

        NativeFormProcessor processor = OpdLibrary.getInstance().getFormProcessorFactory().createInstance(jsonObject);

        // read values
        String eventJson = VisitDao.fetchEventByFormSubmissionId(formSubmissionId);
        JSONObject savedEvent = new JSONObject(eventJson);

        Event event = OpdLibrary.getInstance().getEcSyncHelper().convert(savedEvent, Event.class);
        SimpleDateFormat sdfDate = new SimpleDateFormat(OpdConstants.DateTimeFormat.dd_MMM_yyyy, Locale.ENGLISH);
        boolean readonly = !sdfDate.format(event.getEventDate().toDate()).equals(sdfDate.format(new Date()));

        Map<String, Object> values = processor.getFormResults(savedEvent);
        jsonObject.put(OpdConstants.Properties.FORM_SUBMISSION_ID, formSubmissionId);

        // little hack for pesky multi_select_list
        OpdJsonFormUtils.patchMultiSelectList(values);

        // inject values
      /*  processor.populateValues(values, jsonObject1 -> {
            try {
                jsonObject1.put(JsonFormConstants.READ_ONLY, readonly);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }, customElementLoaders(processor));*/


        // inject values
        processor.populateValues(values, jsonObject1 -> {
            try {
                String key = ((jsonObject1.has(JsonFormConstants.KEY)) ? jsonObject1.getString(JsonFormConstants.KEY) : "");
                //Repeating Group
                if ((key.contains("test_ordered_avail")) || readonly) {
                    jsonObject1.put(JsonFormConstants.READ_ONLY, true);
                }

               //jsonObject1.put(JsonFormConstants.READ_ONLY, readonly);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }, customElementLoaders(processor));

        return jsonObject;
    }

    private Map<String, NativeFormProcessorFieldSource> customElementLoaders(NativeFormProcessor processor) {
        Map<String, NativeFormProcessorFieldSource> elements = new HashMap<>();

        elements.put(JsonFormConstants.REPEATING_GROUP, new RepeatingGroupsValueSource(processor));

        return elements;
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

    @Override
    public String readAssetContents(Context context, String path) {
        return Utils.readAssetContents(context, path);
    }

    @Override
    public void saveForm(String jsonString, Context context) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String title = jsonObject.getString(ENCOUNTER_TYPE);
            CallableInteractor myInteractor = getCallableInteractor();

            Callable<Void> callable = () -> {
                //JSONObject jsonObject = new JSONObject(jsonString);
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
                        .withEncounterType(title)
                        .withFormSubmissionId(formSubmissionId)
                        .withEntityId(entityId)
                        .withFieldProcessors(getFieldProcessorMap())

                        // process the data
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
                    OpdProfileFragmentContract.View<ProfileHistory> view = getView();
                    if (view != null) {
                        view.reloadFromSource();
                        view.setLoadingState(false);
                    }
                }

                @Override
                public void onError(Exception ex) {
                    OpdProfileFragmentContract.View<ProfileHistory> view = getView();
                    if (view == null) return;
                    view.onFetchError(ex);
                    view.setLoadingState(false);
                }
            });

        } catch (Exception ex) {
            Timber.e(ex);
        }

    }

    private Map<String, NativeFormFieldProcessor> getFieldProcessorMap(){
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
    public OpdProfileFragmentContract.View<ProfileHistory> getView() {
        return (OpdProfileFragmentContract.View<ProfileHistory>) super.getView();
    }
}
