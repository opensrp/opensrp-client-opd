package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.dao.CheckInDao;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import org.smartregister.opd.utils.OpdDbConstants.Column.CheckIn;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class CheckInRepository extends BaseRepository implements CheckInDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.CHECK_IN + "("
            + CheckIn.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + CheckIn.EVENT_ID + " VARCHAR NOT NULL, "
            + CheckIn.VISIT_ID + " INT NOT NULL, "
            + CheckIn.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + CheckIn.PREGNANCY_STATUS + " VARCHAR, "
            + CheckIn.HAS_HIV_TEST_PREVIOUSLY + " VARCHAR NOT NULL, "
            + CheckIn.HIV_RESULTS_PREVIOUSLY + " VARCHAR, "
            + CheckIn.IS_TAKING_ART + " VARCHAR, "
            + CheckIn.CURRENT_HIV_RESULT + " VARCHAR NOT NULL, "
            + CheckIn.VISIT_TYPE + " VARCHAR NOT NULL, "
            + CheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY + " VARCHAR NOT NULL, "
            + CheckIn.APPOINTMENT_DUE_DATE + " INTEGER, "
            + CheckIn.CREATED_AT + " INTEGER NOT NULL, "
            + CheckIn.UPDATED_AT + " INTEGER NOT NULL, UNIQUE(" + CheckIn.VISIT_ID + ", " + CheckIn.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.CHECK_IN
            + "_" + CheckIn.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.CHECK_IN + "(" + CheckIn.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.CHECK_IN
            + "_" + CheckIn.VISIT_ID + "_index ON " + OpdDbConstants.Table.CHECK_IN + "(" + CheckIn.VISIT_ID + ");";
    private static final String INDEX_EVENT_ID = "CREATE INDEX " + OpdDbConstants.Table.CHECK_IN
            + "_" + CheckIn.EVENT_ID + "_index ON " + OpdDbConstants.Table.CHECK_IN + "(" + CheckIn.EVENT_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{CheckIn.ID
            , CheckIn.EVENT_ID
            , CheckIn.VISIT_ID
            , CheckIn.BASE_ENTITY_ID
            , CheckIn.PREGNANCY_STATUS
            , CheckIn.HAS_HIV_TEST_PREVIOUSLY
            , CheckIn.HIV_RESULTS_PREVIOUSLY
            , CheckIn.IS_TAKING_ART
            , CheckIn.CURRENT_HIV_RESULT
            , CheckIn.VISIT_TYPE
            , CheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY
            , CheckIn.APPOINTMENT_DUE_DATE
            , CheckIn.CREATED_AT
            , CheckIn.UPDATED_AT
    };

    public CheckInRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
        database.execSQL(INDEX_EVENT_ID);
    }

    public ContentValues createValuesFor(@NonNull org.smartregister.opd.pojos.CheckIn checkIn) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(CheckIn.ID, checkIn.getId());
        contentValues.put(CheckIn.EVENT_ID, checkIn.getEventId());
        contentValues.put(CheckIn.VISIT_ID, checkIn.getVisitId());
        contentValues.put(CheckIn.BASE_ENTITY_ID, checkIn.getBaseEntityId());
        contentValues.put(CheckIn.PREGNANCY_STATUS, checkIn.getPregnancyStatus());
        contentValues.put(CheckIn.HAS_HIV_TEST_PREVIOUSLY, checkIn.getHasHivTestPreviously());
        contentValues.put(CheckIn.HIV_RESULTS_PREVIOUSLY, checkIn.getHivResultsPreviously());
        contentValues.put(CheckIn.IS_TAKING_ART, checkIn.getIsTakingArt());
        contentValues.put(CheckIn.CURRENT_HIV_RESULT, checkIn.getCurrentHivResult());
        contentValues.put(CheckIn.VISIT_TYPE, checkIn.getVisitType());
        contentValues.put(CheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY, checkIn.getAppointmentScheduledPreviously());
        contentValues.put(CheckIn.APPOINTMENT_DUE_DATE, checkIn.getAppointmentDueDate());

        return contentValues;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.CheckIn getLatestCheckIn(@NonNull String clientBaseEntityId) {
        Cursor mCursor = null;
        org.smartregister.opd.pojos.CheckIn checkIn = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(clientBaseEntityId)) {
                mCursor = db.query(OpdDbConstants.Table.CHECK_IN, columns, CheckIn.BASE_ENTITY_ID + " = ?"
                        , new String[]{clientBaseEntityId}
                        , null
                        , null
                        , CheckIn.CREATED_AT + " DESC"
                        , "1");

                if (mCursor != null) {
                    if (mCursor.moveToNext()) {
                        checkIn = getCheckInResult(mCursor);
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

        return checkIn;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.CheckIn getCheckInByVisit(@NonNull int visitId) {

        Cursor mCursor = null;
        org.smartregister.opd.pojos.CheckIn checkIn = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (visitId != 0) {
                mCursor = db.query(OpdDbConstants.Table.CHECK_IN, columns, CheckIn.VISIT_ID + " = ?"
                        , new String[]{visitId + ""}
                        , null
                        , null
                        , CheckIn.CREATED_AT + " DESC"
                        , "1");

                if (mCursor != null) {
                    if (mCursor.moveToNext()) {
                        checkIn = getCheckInResult(mCursor);
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

        return checkIn;
    }

    @NonNull
    protected org.smartregister.opd.pojos.CheckIn getCheckInResult(@NonNull Cursor cursor) {
        org.smartregister.opd.pojos.CheckIn checkIn = new org.smartregister.opd.pojos.CheckIn();

        checkIn.setId(cursor.getInt(cursor.getColumnIndex(CheckIn.ID)));
        checkIn.setEventId(cursor.getString(cursor.getColumnIndex(CheckIn.EVENT_ID)));
        checkIn.setVisitId(cursor.getInt(cursor.getColumnIndex(CheckIn.VISIT_ID)));
        checkIn.setBaseEntityId(cursor.getString(cursor.getColumnIndex(CheckIn.BASE_ENTITY_ID)));
        checkIn.setPregnancyStatus(cursor.getString(cursor.getColumnIndex(CheckIn.PREGNANCY_STATUS)));
        checkIn.setHasHivTestPreviously(cursor.getString(cursor.getColumnIndex(CheckIn.HAS_HIV_TEST_PREVIOUSLY)));
        checkIn.setHivResultsPreviously(cursor.getString(cursor.getColumnIndex(CheckIn.HIV_RESULTS_PREVIOUSLY)));
        checkIn.setIsTakingArt(cursor.getString(cursor.getColumnIndex(CheckIn.IS_TAKING_ART)));
        checkIn.setCurrentHivResult(cursor.getString(cursor.getColumnIndex(CheckIn.CURRENT_HIV_RESULT)));
        checkIn.setVisitType(cursor.getString(cursor.getColumnIndex(CheckIn.VISIT_TYPE)));
        checkIn.setAppointmentScheduledPreviously(cursor.getString(cursor.getColumnIndex(CheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY)));
        checkIn.setAppointmentDueDate(cursor.getString(cursor.getColumnIndex(CheckIn.APPOINTMENT_DUE_DATE)));
        checkIn.setCreatedAt(cursor.getInt(cursor.getColumnIndex(CheckIn.CREATED_AT)));
        checkIn.setUpdatedAt(cursor.getInt(cursor.getColumnIndex(CheckIn.UPDATED_AT)));

        return checkIn;
    }
}
