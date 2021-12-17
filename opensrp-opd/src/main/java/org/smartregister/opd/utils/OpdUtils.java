package org.smartregister.opd.utils;

import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_ID;
import static com.vijay.jsonwizard.constants.JsonFormConstants.OPENMRS_ENTITY_PARENT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;

import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.google.common.base.CaseFormat;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.NativeFormFieldProcessor;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.domain.ProfileHistory;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.util.FormUtils;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.StringUtil;
import org.smartregister.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_EXTRA.STEP1;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.VALUE;
import static org.smartregister.opd.utils.OpdConstants.KEY.KEY;
import static org.smartregister.util.JsonFormUtils.REPEATING_GROUP;
import static org.smartregister.util.JsonFormUtils.TYPE;
import static org.smartregister.util.JsonFormUtils.gson;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdUtils extends Utils {

    private static final String OTHER_SUFFIX = ", other]";
    private static FormUtils formUtils;

    public static float convertDpToPixel(float dp, @NonNull Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @NonNull
    public static String fillTemplate(boolean isHtml, @NonNull String stringValue, @NonNull Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = processValue(key, facts);
            stringValueResult = stringValueResult.replace("{" + key + "}", value).replaceAll(", $", "").trim();
        }

        //Remove unnecessary commas by cleaning the returned string
        return isHtml ? stringValueResult : cleanValueResult(stringValueResult);
    }

    public static String fillTemplate(@NonNull String stringValue, @NonNull Facts facts) {
        return fillTemplate(false, stringValue, facts);
    }

    public static boolean isTemplate(@NonNull String stringValue) {
        return stringValue.contains("{") && stringValue.contains("}");
    }

    public static void setTextAsHtml(@NonNull TextView textView, @NonNull String html) {
        textView.setAllCaps(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(html));
        }
    }

    @NonNull
    private static String processValue(@NonNull String key, @NonNull Facts facts) {
        String value = "";
        if (facts.get(key) instanceof String) {
            value = facts.get(key);
            if (value != null && value.endsWith(OTHER_SUFFIX)) {
                Object otherValue = value.endsWith(OTHER_SUFFIX) ? facts.get(key + ConstantsUtils.SuffixUtils.OTHER) : "";
                value = otherValue != null ?
                        value.substring(0, value.lastIndexOf(",")) + ", " + otherValue.toString() + "]" :
                        value.substring(0, value.lastIndexOf(",")) + "]";

            }
        }

        return keyToValueConverter(value);
    }

    @NonNull
    private static String cleanValueResult(@NonNull String result) {
        List<String> nonEmptyItems = new ArrayList<>();

        for (String item : result.split(",")) {
            if (item.length() > 1) {
                nonEmptyItems.add(item);
            }
        }
        //Get the first item that usually  has a colon and remove it form list, if list has one item append separator
        String itemLabel = "";
        if (!nonEmptyItems.isEmpty() && nonEmptyItems.get(0).contains(":")) {
            String[] separatedLabel = nonEmptyItems.get(0).split(":");
            itemLabel = separatedLabel[0];
            if (separatedLabel.length > 1) {
                nonEmptyItems.set(0, nonEmptyItems.get(0).split(":")[1]);
            }//replace with extracted value
        }
        return itemLabel + (!TextUtils.isEmpty(itemLabel) ? ": " : "") + StringUtils.join(nonEmptyItems.toArray(), ",");
    }

    @NonNull
    public static org.smartregister.Context context() {
        return OpdLibrary.getInstance().context();
    }

    @Nullable
    public static OpdMetadata metadata() {
        return OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
    }

    @Nullable
    public static String getIntentValue(@Nullable Intent data, @NonNull String key) {
        return data == null ? null : data.hasExtra(key) ? data.getStringExtra(key) : null;
    }

    public static void saveImageAndCloseOutputStream(Bitmap image, File outputFile) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outputFile);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        image.compress(compressFormat, 100, os);
    }

    @NonNull
    public static String convertDate(@NonNull Date date, @NonNull String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        return simpleDateFormat.format(date);
    }

    @Nullable
    public static Date convertStringToDate(@NonNull String pattern, @NonNull String dateString) {
        Date date = null;
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);

        if (!TextUtils.isEmpty(dateString) && !TextUtils.isEmpty(pattern)) {
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }
        return date;
    }

    @NotNull
    public static String getClientAge(String dobString, String translatedYearInitial) {
        String age = dobString;
        if (dobString.contains(translatedYearInitial)) {
            String extractedYear = dobString.substring(0, dobString.indexOf(translatedYearInitial));
            int year = dobString.contains(translatedYearInitial) ? Integer.parseInt(extractedYear) : 0;
            if (year >= 5) {
                age = extractedYear;
            }
        }
        return age;
    }

    @NonNull
    public static Intent buildFormActivityIntent(JSONObject jsonForm, HashMap<String, String> parcelableData, Context context) {
        Intent intent = new Intent(context, OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getOpdFormActivity());
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setWizard(false);
        form.setName("");
        String encounterType = jsonForm.optString(OpdJsonFormUtils.ENCOUNTER_TYPE);
        if (encounterType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
            form.setName(OpdConstants.EventType.DIAGNOSIS_AND_TREAT);
            form.setWizard(true);
        }
        form.setHideSaveLabel(true);
        form.setPreviousLabel("");
        form.setNextLabel("");
        form.setHideNextButton(false);
        form.setHidePreviousButton(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        if (parcelableData != null) {
            for (String intentKey : parcelableData.keySet()) {
                intent.putExtra(intentKey, parcelableData.get(intentKey));
            }
        }
        return intent;
    }

    @Nullable
    public static JSONObject getJsonFormToJsonObject(@NonNull String formName) {
        if (getFormUtils() == null) {
            return null;
        }

        return getFormUtils().getFormJson(formName);
    }


    @Nullable
    private static FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(OpdLibrary.getInstance().context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }


    @NonNull
    public static String keyToValueConverter(String keys) {
        if (!TextUtils.isEmpty(keys)) {
            String cleanKey;

            //If this contains html then don't capitalize it because it fails and the output is in lowercase
            if (keys.contains("<") && keys.contains(">")) {
                cleanKey = keys;
            } else {
                cleanKey = WordUtils.capitalizeFully(cleanValue(keys), ',');
            }

            return cleanKey.replaceAll("_", " ");
        } else {
            return "";
        }
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }

    public static HashMap<String, HashMap<String, String>> buildRepeatingGroupTests(@NonNull JSONObject stepJsonObject) throws JSONException {
        return buildRepeatingGroupTests(stepJsonObject, OpdConstants.JSON_FORM_KEY.TESTS_REPEATING_GROUP);
    }

    public static HashMap<String, HashMap<String, String>> buildRepeatingGroupTests(@NonNull JSONObject stepJsonObject, String field) {
        ArrayList<String> keysArrayList = new ArrayList<>();
        JSONArray fields = stepJsonObject.optJSONArray(OpdJsonFormUtils.FIELDS);
        JSONObject jsonObject = JsonFormUtils.getFieldJSONObject(fields, field);
        HashMap<String, HashMap<String, String>> repeatingGroupMap = new HashMap<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(JsonFormConstants.VALUE);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject valueField = jsonArray.optJSONObject(i);
                    String fieldKey = valueField.optString(JsonFormConstants.KEY);
                    keysArrayList.add(fieldKey);
                }

                for (int k = 0; k < fields.length(); k++) {
                    JSONObject valueField = fields.optJSONObject(k);
                    String fieldKey = valueField.optString(JsonFormConstants.KEY);
                    String fieldValue = valueField.optString(JsonFormConstants.VALUE);

                    if (fieldKey.contains("_")) {
                        fieldKey = fieldKey.substring(0, fieldKey.lastIndexOf("_"));
                        if (keysArrayList.contains(fieldKey) && StringUtils.isNotBlank(fieldValue) && !fieldValue.equals(OpdConstants.TYPE_OF_TEXT_LABEL)) {
                            String fieldKeyId = valueField.optString(JsonFormConstants.KEY).substring(fieldKey.length() + 1);
                            HashMap<String, String> hashMap = repeatingGroupMap.get(fieldKeyId) == null ? new HashMap<>() : repeatingGroupMap.get(fieldKeyId);
                            hashMap.put(fieldKey, fieldValue);
                            repeatingGroupMap.put(fieldKeyId, hashMap);
                        }
                    }
                }
            }
        }
        return repeatingGroupMap;
    }

    public static Map<String, String> getClientDemographicDetails(@NonNull String baseEntityId) {
        try {
            return OpdLibrary.getInstance().context().getEventClientRepository()
                    .rawQuery(OpdLibrary.getInstance().context().getEventClientRepository().getReadableDatabase(),
                            "select * from " + metadata().getTableName() +
                                    " where " + OpdDbConstants.Column.Client.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public static HashMap<String, String> getInjectableFields(@NonNull String formName, @NonNull String caseId) {
        Map<String, String> detailsMap = getClientDemographicDetails(caseId);
        HashMap<String, String> injectedValues = new HashMap<>();
        if (formName.equals(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
            if (detailsMap != null) {
                injectedValues.put(OpdConstants.ClientMapKey.GENDER, detailsMap.get(OpdConstants.ClientMapKey.GENDER));
                String strDob = detailsMap.get(OpdDbConstants.Column.Client.DOB);
                String age = "";
                if (StringUtils.isNotBlank(strDob)) {
                    age = String.valueOf(Utils.getAgeFromDate(strDob));
                }
                injectedValues.put(OpdConstants.JSON_FORM_KEY.AGE, age);
            }
            Map<String, String> opdCheckInMap = OpdLibrary.getInstance().getCheckInRepository().getLatestCheckIn(caseId);
            if (!opdCheckInMap.isEmpty()) {
                injectedValues.put("visit_id", opdCheckInMap.get(OpdDbConstants.Column.OpdCheckIn.VISIT_ID));
            }
        }
        return injectedValues;
    }

    @NotNull
    public static String cleanStringArray(@Nullable String stringArray) {
        if (StringUtils.isNotBlank(stringArray)) {
            return stringArray.replace("[", "")
                    .replace("]", "").replaceAll("\"", "");
        }
        return "";
    }

    @NonNull
    public static String cleanTestName(@NonNull String testName) {
        String result = "";
        if ("specify".equals(testName) || "other".equals(testName) || "status".equals(testName)) {
            result = "";
        } else {
            result = testName + " ";
        }
        return result;
    }

    public static String createTestName(@NonNull String key) {
        String result = removeHyphen(key
                .replace(OpdConstants.DIAGNOSTIC_TEST_RESULT, ""))
                .trim();
        if (StringUtils.isNotBlank(result)) {
            return result;
        } else {
            return "status";
        }
    }

    public static String removeHyphen(@Nullable String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replaceAll("_", " ");
        }
        return "";
    }

    public static JSONArray addOpenMrsEntityId(@NonNull String diagnosisType,
                                               @NonNull JSONArray jsonArray) {
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


    public static String getTodaysDate() {
        return convertDateFormat(DateTime.now());
    }

    public static String reverseHyphenSeperatedValues(String rawString, String outputSeparator) {
        if (StringUtils.isNotBlank(rawString)) {
            String resultString = rawString;
            String[] tokenArray = resultString.trim().split("-");
            ArrayUtils.reverse(tokenArray);
            resultString = StringUtils.join(tokenArray, outputSeparator);
            return resultString;
        }
        return "";
    }

    public static HashMap<String, List<String>> getDateToEventIdMap(List<ProfileHistory> historyList) {
        HashMap<String, List<String>> map = new HashMap<>();
        // Reverse the list order so that the latest edited  values are used
        ArrayList<ProfileHistory> reverseHistoryList = new ArrayList<>(historyList);
        Collections.reverse(reverseHistoryList);
        for (ProfileHistory profileHistory : reverseHistoryList) {
            String date = profileHistory.getEventDate();
            List<String> visitIds = new ArrayList<>();
            if (map.containsKey(date)) {
                List<String> oldIds = map.get(date);
                if (oldIds != null)
                    visitIds.addAll(oldIds);
                visitIds.add(profileHistory.getID());
            } else {
                visitIds.add(profileHistory.getID());
            }
            map.put(date, visitIds);
        }
        return map;
    }

    public static void injectGroupMap(JSONObject jsonObject) throws JSONException {
        JSONObject step = jsonObject.getJSONObject(STEP1);
        JSONArray fields = step.optJSONArray(OpdJsonFormUtils.FIELDS);
        for (int i=0; i<fields.length(); i++) {
            JSONObject object = fields.getJSONObject(i);
            String type = object.optString(TYPE);
            if (type.equalsIgnoreCase(REPEATING_GROUP)) {
                String key = object.optString(KEY);
                HashMap<String, HashMap<String, String>> buildRepeatingGroupTestsConducted = buildRepeatingGroupTests(step, key);
                String repeatingGroupKey = generateRepeatingGroupKey(key);
                populateJsonWithRepeatingGroupMap(buildRepeatingGroupTestsConducted, fields, step, repeatingGroupKey, jsonObject);
            }
        }
    }

    public static String generateRepeatingGroupKey(String key) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key) + "Key";
    }

    private static void populateJsonWithRepeatingGroupMap(HashMap<String, HashMap<String, String>> buildRepeatingGroupTests,
                                                          JSONArray fields, JSONObject step, String key, JSONObject jsonObject) throws JSONException{
        if (!buildRepeatingGroupTests.isEmpty()) {
            String strTest = gson.toJson(buildRepeatingGroupTests);
            JSONObject repeatingGroupObj = new JSONObject();
            repeatingGroupObj.put(JsonFormConstants.KEY, key);
            repeatingGroupObj.put(JsonFormConstants.VALUE, strTest);
            repeatingGroupObj.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
            if (fields != null) {
                fields.put(repeatingGroupObj);
                step.put(OpdJsonFormUtils.FIELDS, fields);
                jsonObject.put(STEP1, step);
            }
        }
    }

    public static Map<String, NativeFormFieldProcessor> getFieldProcessorMap() {
        Map<String, NativeFormFieldProcessor> fieldProcessorMap = new HashMap<>();
        fieldProcessorMap.put(OpdConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, (event, fieldJsonObject) -> {
            JSONArray valuesJsonArray;
            try {
                valuesJsonArray = new JSONArray(fieldJsonObject.optString(VALUE));
                for (int i = 0; i < valuesJsonArray.length(); i++) {
                    JSONObject jsonValObject = valuesJsonArray.optJSONObject(i);
                    String fieldType = jsonValObject.optString(OPENMRS_ENTITY);
                    String fieldCode = fieldJsonObject.optString(OPENMRS_ENTITY_ID);
                    String parentCode = fieldJsonObject.optString(OPENMRS_ENTITY_PARENT);
                    String value = jsonValObject.optString(OPENMRS_ENTITY_ID);
                    String humanReadableValues = jsonValObject.optString(AllConstants.TEXT);
                    String formSubmissionField = fieldJsonObject.optString(KEY);
                    event.addObs(new Obs(fieldType, AllConstants.TEXT, fieldCode, parentCode, Collections.singletonList(value),
                            Collections.singletonList(humanReadableValues), "", formSubmissionField));
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        });
        return fieldProcessorMap;
    }
}
