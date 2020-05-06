package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdMedicineDao;
import org.smartregister.opd.pojo.OpdTreatmentDetail;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class OpdTreatmentDetailRepository extends BaseRepository implements OpdMedicineDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL + "("
            + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE_SPECIFY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.MEDICINE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.DOSAGE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.DURATION + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.FREQUENCY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.NOTE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.PROPERTY + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.SPECIAL_INSTRUCTIONS + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " VARCHAR NOT NULL, " +
            "UNIQUE(" + OpdDbConstants.Column.OpdTreatment.VISIT_ID + "," + OpdDbConstants.Column.OpdTreatment.MEDICINE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL
            + "_" + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL +
            "(" + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL
            + "_" + OpdDbConstants.Column.OpdTreatment.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT_DETAIL +
            "(" + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull OpdTreatmentDetail opdTreatmentDetail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID, opdTreatmentDetail.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.MEDICINE, opdTreatmentDetail.getMedicine());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.DOSAGE, opdTreatmentDetail.getDosage());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.DURATION, opdTreatmentDetail.getDuration());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.FREQUENCY, opdTreatmentDetail.getFrequency());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.NOTE, opdTreatmentDetail.getNote());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.VISIT_ID, opdTreatmentDetail.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.PROPERTY, opdTreatmentDetail.getProperty());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.SPECIAL_INSTRUCTIONS, opdTreatmentDetail.getSpecialInstructions());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE, opdTreatmentDetail.getTreatmentType());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.TREATMENT_TYPE_SPECIFY, opdTreatmentDetail.getTreatmentTypeSpecify());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_TREATMENT_DETAIL, null, contentValues);
        return rows != -1;
    }

    @Override
    public OpdTreatmentDetail findOne(OpdTreatmentDetail opdTreatmentDetailDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(OpdTreatmentDetail opdTreatmentDetailDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<OpdTreatmentDetail> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
