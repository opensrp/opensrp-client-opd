package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdTreatmentDetailDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class OpdTreatmentDetailRepository extends BaseRepository implements OpdTreatmentDetailDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "("
            + OpdDbConstants.Column.OpdTreatmentDetail.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE_SPECIFY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.MEDICINE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.DOSAGE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.DURATION + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.FREQUENCY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.NOTE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.PROPERTY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.SPECIAL_INSTRUCTIONS + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatmentDetail.CREATED_AT + " VARCHAR NULL )";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL
            + "_" + OpdDbConstants.Column.OpdTreatmentDetail.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL +
            "(" + OpdDbConstants.Column.OpdTreatmentDetail.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL
            + "_" + OpdDbConstants.Column.OpdTreatmentDetail.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL +
            "(" + OpdDbConstants.Column.OpdTreatmentDetail.VISIT_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.opd.pojo.OpdTreatmentDetail opdTreatmentDetail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.BASE_ENTITY_ID, opdTreatmentDetail.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.MEDICINE, opdTreatmentDetail.getMedicine());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.DOSAGE, opdTreatmentDetail.getDosage());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.DURATION, opdTreatmentDetail.getDuration());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.FREQUENCY, opdTreatmentDetail.getFrequency());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.NOTE, opdTreatmentDetail.getNote());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.VISIT_ID, opdTreatmentDetail.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.PROPERTY, opdTreatmentDetail.getProperty());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.SPECIAL_INSTRUCTIONS, opdTreatmentDetail.getSpecialInstructions());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE, opdTreatmentDetail.getTreatmentType());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.TREATMENT_TYPE_SPECIFY, opdTreatmentDetail.getTreatmentTypeSpecify());
        contentValues.put(OpdDbConstants.Column.OpdTreatmentDetail.CREATED_AT, opdTreatmentDetail.getCreatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_TREATMENT_DETAIL, null, contentValues);
        return rows != -1;
    }

    @Override
    public org.smartregister.opd.pojo.OpdTreatmentDetail findOne(org.smartregister.opd.pojo.OpdTreatmentDetail opdTreatmentDetailDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(org.smartregister.opd.pojo.OpdTreatmentDetail opdTreatmentDetailDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<org.smartregister.opd.pojo.OpdTreatmentDetail> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
