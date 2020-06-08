package org.smartregister.opd.processor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdFormProcessor;
import org.smartregister.opd.pojo.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.gson;

public class OpdDiagnoseAndTreatFormProcessor implements OpdFormProcessor<List<Event>> {
    /***
     * This method creates an event for each step in the Opd Diagnose and treatment form
     * @param jsonFormObject {@link JSONObject}
     * @param data {@link Intent}
     * @return {@link List}
     * @throws JSONException
     */
    @Nullable
    @Override
    public List<Event> processForm(@NonNull JSONObject jsonFormObject, @NonNull Intent data) throws JSONException {

        String entityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);

        if (StringUtils.isNotBlank(entityId)) {

            Map<String, String> opdCheckInMap = OpdLibrary.getInstance().getCheckInRepository().getLatestCheckIn(entityId);

            FormTag formTag = OpdJsonFormUtils.formTag(OpdUtils.getAllSharedPreferences());

            if (opdCheckInMap != null && !opdCheckInMap.isEmpty()) {
                String visitId = opdCheckInMap.get(OpdDbConstants.Column.OpdCheckIn.VISIT_ID);
                String steps = jsonFormObject.optString(JsonFormConstants.COUNT);
                int numOfSteps = Integer.parseInt(steps);
                List<Event> eventList = new ArrayList<>();

                for (int j = 0; j < numOfSteps; j++) {
                    JSONObject step = jsonFormObject.optJSONObject(JsonFormConstants.STEP.concat(String.valueOf(j + 1)));
                    String title = step.optString(JsonFormConstants.STEP_TITLE);
                    String stepEncounterType = step.optString(JsonFormConstants.ENCOUNTER_TYPE);
                    String bindType = step.optString(OpdConstants.BIND_TYPE);

                    JSONArray fields = step.optJSONArray(OpdJsonFormUtils.FIELDS);

                    JSONArray multiSelectListValueJsonArray = null;

                    if (OpdConstants.StepTitle.TEST_CONDUCTED.equals(title)) {
                        HashMap<String, HashMap<String, String>> buildRepeatingGroupTests = OpdUtils.buildRepeatingGroupTests(step);
                        if (!buildRepeatingGroupTests.isEmpty()) {
                            String strTest = gson.toJson(buildRepeatingGroupTests);
                            JSONObject repeatingGroupObj = new JSONObject();
                            repeatingGroupObj.put(JsonFormConstants.KEY, OpdConstants.REPEATING_GROUP_MAP);
                            repeatingGroupObj.put(JsonFormConstants.VALUE, strTest);
                            repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
                            fields.put(repeatingGroupObj);
                        } else {
                            continue;
                        }
                    } else if (OpdConstants.StepTitle.DIAGNOSIS.equals(title)) {
                        JSONObject diseaseCodeJsonObject = OpdJsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
                        JSONObject jsonDiagnosisType = OpdJsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
                        String diagnosisType = jsonDiagnosisType.optString(OpdConstants.KEY.VALUE);
                        String value = diseaseCodeJsonObject.optString(OpdConstants.KEY.VALUE);
                        if (!StringUtils.isBlank(value) && (new JSONArray(value).length() != 0)) {
                            multiSelectListValueJsonArray = new JSONArray(value);
                            JSONArray jsonArrayWithOpenMrsIds = OpdUtils.addOpenMrsEntityId(diagnosisType.toLowerCase(), multiSelectListValueJsonArray);
                            diseaseCodeJsonObject.put(OpdConstants.KEY.VALUE, jsonArrayWithOpenMrsIds);
                        }
                    } else if (OpdConstants.StepTitle.TREATMENT.equals(title)) {
                        JSONObject medicineJsonObject = JsonFormUtils.getFieldJSONObject(fields, OpdConstants.JSON_FORM_KEY.MEDICINE);
                        medicineJsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.MULTI_SELECT_LIST);
                        String value = medicineJsonObject.optString(OpdConstants.KEY.VALUE);
                        if (!StringUtils.isBlank(value) && (new JSONArray(value).length() != 0)) {
                            multiSelectListValueJsonArray = new JSONArray(value);
                        }
                    }

                    Event baseEvent = JsonFormUtils.createEvent(fields, jsonFormObject.optJSONObject(METADATA),
                            formTag, entityId, stepEncounterType, bindType);

                    OpdJsonFormUtils.tagSyncMetadata(baseEvent);

                    baseEvent.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);

                    if (multiSelectListValueJsonArray != null) {
                        baseEvent.addDetails(OpdConstants.KEY.VALUE, multiSelectListValueJsonArray.toString());
                    }
                    eventList.add(baseEvent);
                }

                OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(entityId);
                OpdLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().delete(opdDiagnosisAndTreatmentForm);

                Event closeOpdVisit = JsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                        formTag, entityId, OpdConstants.EventType.CLOSE_OPD_VISIT, "");

                OpdJsonFormUtils.tagSyncMetadata(closeOpdVisit);
                closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, visitId);
                closeOpdVisit.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE, OpdUtils.convertDate(new Date(), OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
                eventList.add(closeOpdVisit);
                return eventList;
            } else {
                Timber.e("Corresponding OpjsonObjectdCheckIn record for EntityId %s is missing", entityId);
                return null;
            }
        }
        return null;
    }
}
