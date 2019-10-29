package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.opd.dao.OpdDetailsDao;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdDetails;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdDetailsRepository extends BaseRepository implements OpdDetailsDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_DETAILS + "("
            + OpdDetails.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + OpdDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDetails.PENDING_DIAGNOSE_AND_TREAT + " BOOLEAN NOT NULL, "
            + OpdDetails.CURRENT_VISIT_START_DATE + " DATETIME, "
            + OpdDetails.CURRENT_VISIT_END_DATE + " DATETIME, "
            + OpdDetails.CURRENT_VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), UNIQUE(" + OpdDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    public OpdDetailsRepository(Repository repository) {
        super(repository);
    }

    private String[] columns = new String[]{
            OpdDetails.ID,
            OpdDetails.BASE_ENTITY_ID,
            OpdDetails.PENDING_DIAGNOSE_AND_TREAT,
            OpdDetails.CURRENT_VISIT_START_DATE,
            OpdDetails.CURRENT_VISIT_END_DATE,
            OpdDetails.CURRENT_VISIT_ID,
            OpdDetails.CREATED_AT
    };

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }

    @NonNull
    public ContentValues createValuesFor(@NonNull org.smartregister.opd.pojos.OpdDetails opdDetails) {
        ContentValues contentValues = new ContentValues();

        if (opdDetails.getId() != 0) {
            contentValues.put(OpdDetails.ID, opdDetails.getId());
        }

        contentValues.put(OpdDetails.BASE_ENTITY_ID, opdDetails.getBaseEntityId());
        contentValues.put(OpdDetails.PENDING_DIAGNOSE_AND_TREAT, opdDetails.isPendingDiagnoseAndTreat());

        if (opdDetails.getCurrentVisitStartDate() != null) {
            contentValues.put(OpdDetails.CURRENT_VISIT_START_DATE, OpdUtils.convertDate(opdDetails.getCurrentVisitStartDate(), OpdDbConstants.DATE_FORMAT));
        }

        if (opdDetails.getCurrentVisitEndDate() != null) {
            contentValues.put(OpdDetails.CURRENT_VISIT_END_DATE, OpdUtils.convertDate(opdDetails.getCurrentVisitEndDate(), OpdDbConstants.DATE_FORMAT));
        }

        contentValues.put(OpdDetails.CURRENT_VISIT_ID, opdDetails.getCurrentVisitId());
        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.opd.pojos.OpdDetails opdDetails) {
        ContentValues contentValues = createValuesFor(opdDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(OpdDbConstants.Table.OPD_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Override
    public org.smartregister.opd.pojos.OpdDetails findOne(@NonNull org.smartregister.opd.pojos.OpdDetails opdDetails) {
        if (opdDetails.getCurrentVisitId() != null && opdDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(OpdDbConstants.Table.OPD_DETAILS, columns, OpdDetails.BASE_ENTITY_ID + "=? and " + OpdDetails.CURRENT_VISIT_ID + "=?",
                    new String[]{opdDetails.getBaseEntityId(), opdDetails.getCurrentVisitId()}, null, null, null, "1");
            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.moveToNext()) {
                opdDetails = new org.smartregister.opd.pojos.OpdDetails();
                opdDetails.setId(cursor.getInt(0));
                opdDetails.setBaseEntityId(cursor.getString(1));
                opdDetails.setPendingDiagnoseAndTreat((cursor.getInt(2) == 1));

                opdDetails.setCurrentVisitStartDate(OpdUtils
                        .convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(3)));
                opdDetails.setCurrentVisitEndDate(OpdUtils
                        .convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(4)));
                opdDetails.setCurrentVisitId(cursor.getString(5));
                opdDetails.setCreatedAt(OpdUtils
                        .convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(6)));
                cursor.close();
            }

        }
        return opdDetails;
    }

    @Override
    public boolean delete(org.smartregister.opd.pojos.OpdDetails opdDetails) {
        return false;
    }

    @Override
    public List<org.smartregister.opd.pojos.OpdDetails> findAll() {
        return null;
    }
}
