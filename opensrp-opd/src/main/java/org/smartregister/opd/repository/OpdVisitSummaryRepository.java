package org.smartregister.opd.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-31
 */

public class OpdVisitSummaryRepository extends BaseRepository {

    public OpdVisitSummaryRepository(Repository repository) {
        super(repository);
    }

    @NonNull
    public List<OpdVisitSummary> getOpdVisitSummaries(@NonNull String baseEntityId) {

        ArrayList<OpdVisitSummary> opdVisitSummaries = new ArrayList<>();

        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            String query = String.format("SELECT %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s FROM %s " +
                            "LEFT JOIN %s ON %s.%s = %s.%s " +
                            "INNER JOIN %s ON %s.%s = %s.%s " +
                            "LEFT JOIN %s ON %s.%s = %s.%s WHERE %s.%s = '%s' ORDER BY %s.%s DESC"
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.VISIT_DATE
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.TEST
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.RESULT
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.TYPE
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.CODE
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.DISEASE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.MEDICINE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.DOSAGE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.DURATION
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.VISIT_ID
                    , OpdDbConstants.Table.OPD_DIAGNOSIS
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.VISIT_ID
                    , OpdDbConstants.Table.OPD_TREATMENT
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.VISIT_ID
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.VISIT_DATE
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        opdVisitSummaries.add(getVisitSummaryResult(mCursor));
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

        return opdVisitSummaries;
    }

    public OpdVisitSummary getVisitSummaryResult(@NonNull Cursor cursor) {
        OpdVisitSummary opdVisitSummary = new OpdVisitSummary();

        opdVisitSummary.setTestName(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST)));
        opdVisitSummary.setTestResult(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.RESULT)));
        opdVisitSummary.setDiagnosis(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS)));
        opdVisitSummary.setDiagnosisType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.TYPE)));
        opdVisitSummary.setDiseaseCode(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.CODE)));
        opdVisitSummary.setDisease(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DISEASE)));
        opdVisitSummary.setTreatment(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.MEDICINE)));
        opdVisitSummary.setDosage(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DOSAGE)));
        opdVisitSummary.setDuration(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DURATION)));
        opdVisitSummary.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));

        return opdVisitSummary;
    }
}
