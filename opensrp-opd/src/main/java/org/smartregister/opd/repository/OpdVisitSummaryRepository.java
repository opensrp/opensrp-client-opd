package org.smartregister.opd.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.pojo.OpdVisitSummaryResultModel;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-31
 */

public class OpdVisitSummaryRepository extends BaseRepository {


    public String[] visitSummaryColumns() {
        return new String[]{
                OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.VISIT_DATE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosis.DISEASE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosis.TYPE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosis.CODE,

                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.TEST_TYPE,
                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.TEST_NAME,
                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.RESULT,

                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.DOSAGE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.FREQUENCY,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.DURATION,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.NOTE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE_SPECIFY,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.MEDICINE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.SPECIAL_INSTRUCTIONS};
    }

    @NonNull
    public List<OpdVisitSummary> getOpdVisitSummaries(@NonNull String baseEntityId, int pageNo) {
        LinkedHashMap<String, OpdVisitSummary> opdVisitSummaries = new LinkedHashMap<>();

        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String[] visitIds = getVisitIds(baseEntityId, pageNo);
            String joinedIds = "'" + StringUtils.join(visitIds, "','") + "'";

            String query = "SELECT " + StringUtils.join(visitSummaryColumns(), ",") + " FROM " + OpdDbConstants.Table.OPD_VISIT +
                    " INNER JOIN " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + " ON "
                    + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosis.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TEST_CONDUCTED + " ON "
                    + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " = " + OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.VISIT_ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + " ON "
                    + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " WHERE " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID + " = '" + baseEntityId + "'"
                    + " AND " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " IN (" + joinedIds + ") " +
                    " ORDER BY " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.VISIT_DATE + " DESC";

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        OpdVisitSummary visitSummaryResult = getVisitSummaryResult(mCursor);
                        String dateString = (new SimpleDateFormat(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH)).format(visitSummaryResult.getVisitDate());

                        OpdVisitSummary existingOpdVisitSummary = opdVisitSummaries.get(dateString);
                        if (existingOpdVisitSummary != null) {
                            // Add any extra disease codes
                            String disease = visitSummaryResult.getDisease();
                            if (disease != null && !existingOpdVisitSummary.getDisease().contains(disease)) {
                                existingOpdVisitSummary.addDisease(disease);
                            }

                            // Add any extra treatments/medicines
                            OpdVisitSummary.Treatment treatment = visitSummaryResult.getTreatment();
                            if (treatment != null && treatment.getMedicine() != null && !existingOpdVisitSummary.getTreatments().containsKey(treatment.getMedicine())) {
                                existingOpdVisitSummary.addTreatment(treatment);
                            }

                            // Add any extra Tests
                            OpdVisitSummary.Test test = visitSummaryResult.getTest();
                            if (test != null && StringUtils.isNotBlank(test.getName()) && !existingOpdVisitSummary.getTests().containsKey(test.getName())) {
                                existingOpdVisitSummary.addTest(test);
                            }
                        } else {
                            opdVisitSummaries.put(dateString, visitSummaryResult);
                        }

                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return new ArrayList<>(opdVisitSummaries.values());
    }


    public int getVisitPageCount(@NonNull String baseEntityId) {
        Cursor mCursor = null;
        int pageCount = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = String.format("SELECT count(%s) FROM %s WHERE %s = '%s'"
                    , OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        int recordCount = mCursor.getInt(0);
                        pageCount = (int) Math.ceil(recordCount / 10d);
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return pageCount;
    }


    public String[] getVisitIds(@NonNull String baseEntityId, int pageNo) {
        ArrayList<String> visitIds = new ArrayList<>();
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            int offset = pageNo * 10;

            String query = String.format("SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s DESC LIMIT 10 OFFSET %d "
                    , OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , OpdDbConstants.Column.OpdVisit.VISIT_DATE
                    , offset
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        visitIds.add(mCursor.getString(0));
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return visitIds.toArray(new String[0]);
    }

    @NonNull
    public OpdVisitSummary getVisitSummaryResult(@NonNull Cursor cursor) {
        OpdVisitSummary opdVisitModel = new OpdVisitSummary();
        opdVisitModel.setDiagnosis(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS)));
        opdVisitModel.setDiagnosisType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.TYPE)));
        opdVisitModel.setDiseaseCode(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.CODE)));
        opdVisitModel.setDisease(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DISEASE)));

        String medicine = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.MEDICINE));

        OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
        treatment.setMedicine(medicine);
        treatment.setDosage(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DOSAGE)));
        treatment.setDuration(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DURATION)));
        treatment.setFrequency(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.FREQUENCY)));
        treatment.setSpecialInstructions(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.SPECIAL_INSTRUCTIONS)));
        treatment.setTreatmentType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE)));
        treatment.setTreatmentTypeSpecify(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE_SPECIFY)));
        opdVisitModel.setTreatment(treatment);


        String test = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST_NAME));
        String testType = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST_TYPE));
        String testResult = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.RESULT));

        if (StringUtils.isNotBlank(test) || StringUtils.isNotBlank(testResult)) {
            OpdVisitSummary.Test testObj = new OpdVisitSummaryResultModel.Test();
            testObj.setTestType(testType);
            testObj.setName(test);
            testObj.setResult(testResult);
            opdVisitModel.setTest(testObj);
        }

        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));

//        try {
//            JSONArray jsonArray = new JSONArray(new JSONArray(disease).optString(0));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                if (jsonObject != null) {
//                    opdVisitModel.addDisease(jsonObject.optString(JsonFormConstants.TEXT));
//                }
//            }
//        } catch (JSONException e) {
//            Timber.e(e);
//        }
//
//        String medicine = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.MEDICINE));
//
//        try {
//            JSONArray jsonArray = new JSONArray(new JSONArray(medicine).optString(0));
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.optJSONObject(i);
//                if (jsonObject != null) {
//                    OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
//                    treatment.setMedicine(jsonObject.optString(JsonFormConstants.TEXT));
//                    JSONObject propertyJsonObj = jsonObject.optJSONObject("property").optJSONObject("meta");
//                    treatment.setDosage(propertyJsonObj.optString("dosage"));
//                    treatment.setDuration(propertyJsonObj.optString("duration"));
//                    treatment.setFrequency(propertyJsonObj.optString("frequency"));
//                    opdVisitModel.setTreatment(treatment);
//                }
//            }
//        } catch (JSONException e) {
//            Timber.e(e);
//        }
//
//        try {
//            JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("test_obj")));
//            Iterator<String> jsonRepeatingGroupIdIterator = jsonObject.keys();
//            while (jsonRepeatingGroupIdIterator.hasNext()) {
//                OpdVisitSummary.Test test = new OpdVisitSummaryResultModel.Test();
//                JSONObject jsonObjectRepeatingGroupObj = jsonObject.optJSONObject(jsonRepeatingGroupIdIterator.next());
//                Iterator<String> testStringIterator = jsonObjectRepeatingGroupObj.keys();
//                StringBuilder testObj = new StringBuilder();
//                while (testStringIterator.hasNext()) {
//                    String resultKey = testStringIterator.next();
//                    if (OpdConstants.DIAGNOSTIC_TEST.equals(resultKey)) {
//                        test.setName(jsonObjectRepeatingGroupObj.optString(resultKey));
//                    }
//                    if (resultKey.startsWith(OpdConstants.DIAGNOSTIC_TEST_RESULT)) {
//                        String temp = OpdUtils.createTestName(resultKey);
//                        if (temp.equals("status") || temp.equals("specify")) {
//                            temp = "";
//                        } else {
//                            temp += " ";
//                        }
//
//                        testObj.append(temp).append(jsonObjectRepeatingGroupObj.optString(resultKey)).append("\n");
//                    }
//                }
//                test.setResult(testObj.toString());
//                opdVisitModel.setTest(test);
//            }
//        } catch (JSONException e) {
//            Timber.e(e);
//        }

        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));
        return opdVisitModel;
    }


}
