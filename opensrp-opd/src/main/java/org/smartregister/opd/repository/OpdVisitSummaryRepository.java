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
                OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID,

                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.TYPE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.CODE,
                OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS_SAME,

                OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.DISCHARGED_HOME,
                OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.DISCHARGED_ALIVE,
                OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.REFERRAL,
                OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.REFERRAL_LOCATION,
                OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.REFERRAL_LOCATION_SPECIFY,

                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.TEST_TYPE,
                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.TEST_NAME,
                OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.RESULT,

                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.DOSAGE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.FREQUENCY,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.DURATION,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.NOTE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE_SPECIFY,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.MEDICINE,
                OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.SPECIAL_INSTRUCTIONS};
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
                    + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "." + OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TEST_CONDUCTED + " ON "
                    + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " = " + OpdDbConstants.Table.OPD_TEST_CONDUCTED + "." + OpdDbConstants.Column.OpdTestConducted.VISIT_ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_OUTCOME + " ON "
                    + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID + " = " + OpdDbConstants.Table.OPD_OUTCOME + "." + OpdDbConstants.Column.OpdOutcome.VISIT_ID +
                    " LEFT JOIN " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + " ON "
                    + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "." + OpdDbConstants.Column.OpdTreatmentDetail.VISIT_ID + " = " + OpdDbConstants.Table.OPD_VISIT + "." + OpdDbConstants.Column.OpdVisit.ID +
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
                            if (test != null && StringUtils.isNotBlank(test.getType())) {
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
        opdVisitModel.setDiagnosis(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS)));
        opdVisitModel.setDiagnosisType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.TYPE)));
        opdVisitModel.setDiseaseCode(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.CODE)));
        opdVisitModel.setDisease(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE)));
        opdVisitModel.setIsDiagnosisSame(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS_SAME)));

        opdVisitModel.setSpecialInstructions(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.SPECIAL_INSTRUCTIONS)));
        opdVisitModel.setTreatmentType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE)));
        opdVisitModel.setTreatmentTypeSpecify(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE_SPECIFY)));

        opdVisitModel.setDischargedAlive(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdOutcome.DISCHARGED_HOME)));
        opdVisitModel.setDischargedHome(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdOutcome.DISCHARGED_HOME)));
        opdVisitModel.setReferral(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdOutcome.REFERRAL)));
        opdVisitModel.setReferralLocation(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdOutcome.REFERRAL_LOCATION)));
        opdVisitModel.setReferralLocationSpecify(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdOutcome.REFERRAL_LOCATION_SPECIFY)));

        String medicine = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.MEDICINE));

        OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
        treatment.setMedicine(medicine);
        treatment.setDosage(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.DOSAGE)));
        treatment.setDuration(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.DURATION)));
        treatment.setFrequency(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatmentDetail.FREQUENCY)));


        String testType = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST_TYPE));
        String testName = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST_NAME));
        String testResult = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.RESULT));

        if (StringUtils.isNotBlank(testType)) {
            OpdVisitSummary.Test testObj = new OpdVisitSummaryResultModel.Test();
            testObj.setType(testType);
            testObj.setName(testName);
            testObj.setResult(testResult);
            opdVisitModel.setTest(testObj);
        }

        opdVisitModel.setTreatment(treatment);

        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));
        return opdVisitModel;
    }


}
