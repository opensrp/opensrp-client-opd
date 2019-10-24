package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdDetails;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdDetailsRepository extends BaseRepository {

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

    public boolean addOrUpdateOpdDetails(@NonNull org.smartregister.opd.pojos.OpdDetails opdDetails) {
        ContentValues contentValues = createValuesFor(opdDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(OpdDbConstants.Table.OPD_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }
}
