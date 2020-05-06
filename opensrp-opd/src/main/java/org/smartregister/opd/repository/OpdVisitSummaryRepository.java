package org.smartregister.opd.repository;

import android.support.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.pojo.OpdVisitSummaryResultModel;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
                OpdDbConstants.Table.OPD_DIAGNOSIS + "." + OpdDbConstants.Column.OpdDiagnosis.DISEASE,
                OpdDbConstants.Table.OPD_DIAGNOSIS + "." + OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS,
                OpdDbConstants.Table.OPD_DIAGNOSIS + "." + OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS_TYPE,
                OpdDbConstants.Table.OPD_DIAGNOSIS + "." + OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS_SAME,
                OpdDbConstants.Table.OPD_TEST + "." + "test_obj",
                OpdDbConstants.Table.OPD_TREATMENT + "." + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE,
                OpdDbConstants.Table.OPD_TREATMENT + "." + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE_SPECIFY,
                OpdDbConstants.Table.OPD_TREATMENT + "." + OpdDbConstants.Column.OpdTreatment.MEDICINE,
                OpdDbConstants.Table.OPD_TREATMENT + "." + OpdDbConstants.Column.OpdTreatment.SPECIAL_INSTRUCTIONS};
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
                    " INNER JOIN " + OpdDbConstants.Table.OPD_DIAGNOSIS + " ON "
                    + OpdDbConstants.Table.OPD_DIAGNOSIS + "." + OpdDbConstants.Column.OpdDiagnosis.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TEST + " ON "
                    + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " = " + OpdDbConstants.Table.OPD_TEST + "." + OpdDbConstants.Column.OpdTestConducted.VISIT_ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TREATMENT + " ON "
                    + OpdDbConstants.Table.OPD_TREATMENT + "." + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " WHERE " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID + " = '" + baseEntityId + "'"
                    + " AND " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " IN (" + joinedIds + ") " +
                    " ORDER BY " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.VISIT_DATE + " DESC";

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        OpdVisitSummary visitSummaryResult = getVisitSummaryResult(mCursor);
                        String dateString = (new SimpleDateFormat(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH)).format(visitSummaryResult.getVisitDate());
                        opdVisitSummaries.put(dateString, visitSummaryResult);

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
        opdVisitModel.setDiagnosisType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS_TYPE)));
        String disease = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DISEASE));
        try {
            JSONArray jsonArray = new JSONArray(new JSONArray(disease).optString(0));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject != null) {
                    opdVisitModel.addDisease(jsonObject.optString(JsonFormConstants.TEXT));
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        String medicine = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.MEDICINE));

        try {
            JSONArray jsonArray = new JSONArray(new JSONArray(medicine).optString(0));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject != null) {
                    OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
                    treatment.setMedicine(jsonObject.optString(JsonFormConstants.TEXT));
                    JSONObject propertyJsonObj = jsonObject.optJSONObject("property").optJSONObject("meta");
                    treatment.setDosage(propertyJsonObj.optString("dosage"));
                    treatment.setDuration(propertyJsonObj.optString("duration"));
                    treatment.setFrequency(propertyJsonObj.optString("frequency"));
                    opdVisitModel.setTreatment(treatment);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex("test_obj")));
            Iterator<String> jsonRepeatingGroupIdIterator = jsonObject.keys();
            while (jsonRepeatingGroupIdIterator.hasNext()) {
                OpdVisitSummary.Test test = new OpdVisitSummaryResultModel.Test();
                JSONObject jsonObjectRepeatingGroupObj = jsonObject.optJSONObject(jsonRepeatingGroupIdIterator.next());
                Iterator<String> testStringIterator = jsonObjectRepeatingGroupObj.keys();
                StringBuilder testObj = new StringBuilder();
                while (testStringIterator.hasNext()) {
                    String resultKey = testStringIterator.next();
                    if (OpdConstants.DIAGNOSTIC_TEST.equals(resultKey)) {
                        test.setName(jsonObjectRepeatingGroupObj.optString(resultKey));
                    }
                    if (resultKey.startsWith(OpdConstants.DIAGNOSTIC_TEST_RESULT)) {
                        testObj.append(OpdUtils.createTestName(resultKey)).append(" ").append(jsonObjectRepeatingGroupObj.optString(resultKey)).append("\n");
                    }
                }
                test.setResult(testObj.toString());
                opdVisitModel.setTest(test);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));
        return opdVisitModel;
    }


}
