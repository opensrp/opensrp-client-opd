package org.smartregister.opd.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.NativeFormProcessor;
import org.smartregister.util.NativeFormProcessorFieldSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class RepeatingGroupsValueSource implements NativeFormProcessorFieldSource {
    private final NativeFormProcessor processor;
    private final Map<String, List<Map<String, Object>>> rulesFileMap = new HashMap<>();

    public static final String GENERATED_GRP = "generated_grp";

    public RepeatingGroupsValueSource(NativeFormProcessor processor) {
        this.processor = processor;
    }

    @Override
    public <T> void populateValue(String stepName, JSONObject step, JSONObject fieldJson, Map<String, T> dictionary) {
        try {
            Map<String, Map<String, T>> loaded = processor.getRepeatingGroupValues(fieldJson, dictionary);
            populate(stepName, step, fieldJson.getString(JsonFormUtils.KEY), loaded);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private <T> void populate(String stepName, JSONObject step, String repeatingGroupKey, Map<String, Map<String, T>> loaded) throws JSONException {
        JSONArray stepFields = step.optJSONArray(JsonFormConstants.FIELDS);
        for (int i = 0; i < stepFields.length(); i++) {
            JSONObject field = stepFields.optJSONObject(i);
            String key = field.optString(JsonFormConstants.KEY);
            if (key.equals(repeatingGroupKey)) {
                JSONArray repeatingGrpValues = field.optJSONArray(JsonFormConstants.VALUE);
                readGroupValue(stepName, field, repeatingGrpValues, stepFields, i, loaded);
                break;
            }
        }
    }


    private <T> void readGroupValue(String stepName, JSONObject field, JSONArray repeatingGrpValues, JSONArray stepFields, int pos, Map<String, Map<String, T>> loaded) throws JSONException {
        int mPos = pos;
        for (Map.Entry<String, Map<String, T>> entry : loaded.entrySet()) {

            for (int i = 0; i < repeatingGrpValues.length(); i++) {
                JSONObject object = repeatingGrpValues.optJSONObject(i);
                JSONObject repeatingGrpField = new JSONObject(object.toString());
                String repeatingGrpFieldKey = repeatingGrpField.optString(JsonFormConstants.KEY);

                if (entry.getValue().containsKey(repeatingGrpFieldKey)) {
                    String jsonKey = repeatingGrpField.optString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) ? JsonFormConstants.TEXT : JsonFormConstants.VALUE;
                    repeatingGrpField.put(jsonKey, entry.getValue().get(repeatingGrpFieldKey));

                    updateFieldProperties(stepName, field, entry.getKey(), repeatingGrpField, repeatingGrpFieldKey);

                    updateField(repeatingGrpField, entry.getValue());
                    repeatingGrpField.put(JsonFormConstants.KEY, repeatingGrpFieldKey + "_" + entry.getKey());
                    stepFields.put(++mPos, repeatingGrpField);
                }
            }
        }
    }

    private void updateFieldProperties(String stepName, JSONObject parentField, @NonNull String fieldGroupId, @NonNull JSONObject repeatingGrpField, @NonNull String repeatingGrpFieldKey) throws JSONException {

        if (repeatingGrpField.has(JsonFormConstants.RELEVANCE) || repeatingGrpField.has(JsonFormConstants.CALCULATION))
            generateDynamicRules(stepName, repeatingGrpField, fieldGroupId);

        if (repeatingGrpFieldKey.equals(GENERATED_GRP))
            repeatingGrpField.put(JsonFormConstants.VALUE, "true");
    }

    protected void generateDynamicRules(String stepName, @NonNull JSONObject field, @NonNull String uniqueId) {

        try {
            Context context = OpdLibrary.getInstance().context().applicationContext();

            com.vijay.jsonwizard.utils.Utils.buildRulesWithUniqueId(field, uniqueId, JsonFormConstants.RELEVANCE,
                    context, rulesFileMap, stepName);

            com.vijay.jsonwizard.utils.Utils.buildRulesWithUniqueId(field, uniqueId, JsonFormConstants.CALCULATION,
                    context, rulesFileMap, stepName);

            JSONObject relativeMaxValidator = field.optJSONObject(JsonFormConstants.V_RELATIVE_MAX);
            if (relativeMaxValidator != null) {
                String currRelativeMaxValidatorValue = relativeMaxValidator.getString(JsonFormConstants.VALUE);
                String newRelativeMaxValidatorValue = currRelativeMaxValidatorValue + "_" + uniqueId;
                relativeMaxValidator.put(JsonFormConstants.VALUE, newRelativeMaxValidatorValue);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    /**
     * This might have a bug
     *
     * @param repeatingGrpField
     * @param entryMap
     * @param <T>
     * @throws JSONException
     */
    public <T> void updateField(JSONObject repeatingGrpField, Map<String, T> entryMap) throws JSONException {
        String type = repeatingGrpField.optString(JsonFormConstants.TYPE);
        if (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || type.equals(JsonFormConstants.SPINNER)) {
            repeatingGrpField.put(JsonFormConstants.VALUE, entryMap.get(JsonFormConstants.VALUE));
        }
    }

}
