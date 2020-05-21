package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdDiagnosisDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class OpdDiagnosisDetailRepository extends BaseRepository implements OpdDiagnosisDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL + "("
            + OpdDbConstants.Column.OpdDiagnosisDetail.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.TYPE + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS_SAME + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.ICD10_CODE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.CODE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.DETAILS + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID + " VARCHAR NOT NULL, " +
            "UNIQUE(" + OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID + "," + OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE + "," + OpdDbConstants.Column.OpdDiagnosisDetail.TYPE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL
            + "_" + OpdDbConstants.Column.OpdDiagnosisDetail.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL +
            "(" + OpdDbConstants.Column.OpdDiagnosisDetail.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL
            + "_" + OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL +
            "(" + OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.opd.pojo.OpdDiagnosisDetail opdDiagnosisDetail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.BASE_ENTITY_ID, opdDiagnosisDetail.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS, opdDiagnosisDetail.getDiagnosis());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.TYPE, opdDiagnosisDetail.getType());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE, opdDiagnosisDetail.getDisease());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.ICD10_CODE, opdDiagnosisDetail.getIcd10Code());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.CODE, opdDiagnosisDetail.getCode());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS_SAME, opdDiagnosisDetail.getIsDiagnosisSame());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.DETAILS, opdDiagnosisDetail.getDetails());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisDetail.VISIT_ID, opdDiagnosisDetail.getVisitId());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_DIAGNOSIS_DETAIL, null, contentValues);
        return rows != -1;
    }

    @Override
    public org.smartregister.opd.pojo.OpdDiagnosisDetail findOne(org.smartregister.opd.pojo.OpdDiagnosisDetail opdDiagnosisDetail) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(org.smartregister.opd.pojo.OpdDiagnosisDetail opdDiagnosisDetail) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<org.smartregister.opd.pojo.OpdDiagnosisDetail> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
