package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.Visit;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-01
 */

public class VisitRepository extends BaseRepository implements VisitDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.VISIT + "("
            + Visit.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + Visit.VISIT_DATE + " DATETIME NOT NULL,"
            + Visit.PROVIDER_ID + " VARCHAR NOT NULL,"
            + Visit.LOCATION_ID + " VARCHAR NOT NULL,"
            + Visit.BASE_ENTITY_ID + "VARCHAR NOT NULL,"
            + Visit.CREATED_AT + " INTEGER NOT NULL)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.VISIT
            + "_" + Visit.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.VISIT + "(" + Visit.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String INDEX_VISIT_DATE = "CREATE INDEX " + OpdDbConstants.Table.VISIT
            + "_" + Visit.VISIT_DATE + "_index ON " + OpdDbConstants.Table.VISIT + "(" + Visit.VISIT_DATE + " COLLATE NOCASE);";

    private String[] columns = new String[] {Visit.ID
            , Visit.VISIT_DATE
            , Visit.PROVIDER_ID
            , Visit.LOCATION_ID
            , Visit.BASE_ENTITY_ID
            , Visit.CREATED_AT};

    private SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);

    public VisitRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_DATE);
    }


    public ContentValues createValuesFor(@NonNull org.smartregister.opd.pojos.Visit visit) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(Visit.ID, visit.getId());
        contentValues.put(Visit.VISIT_DATE, dateFormat.format(visit.getVisitDate()));
        contentValues.put(Visit.PROVIDER_ID, visit.getProviderId());
        contentValues.put(Visit.LOCATION_ID, visit.getLocationId());
        contentValues.put(Visit.BASE_ENTITY_ID, visit.getBaseEntityId());
        contentValues.put(Visit.CREATED_AT, visit.getVisitDate().getTime());

        return contentValues;
    }

    @NonNull
    protected org.smartregister.opd.pojos.Visit getVisitResult(@NonNull Cursor cursor) {
        org.smartregister.opd.pojos.Visit visit = new org.smartregister.opd.pojos.Visit();

        visit.setId(cursor.getInt(cursor.getColumnIndex(Visit.ID)));
        visit.setVisitDate(new Date(cursor.getLong(cursor.getColumnIndex(Visit.VISIT_DATE))));
        visit.setProviderId(cursor.getString(cursor.getColumnIndex(Visit.PROVIDER_ID)));
        visit.setLocationId(cursor.getString(cursor.getColumnIndex(Visit.LOCATION_ID)));
        visit.setBaseEntityId(cursor.getString(cursor.getColumnIndex(Visit.BASE_ENTITY_ID)));
        visit.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(Visit.CREATED_AT))));

        return visit;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.Visit getLatestVisit(@NonNull String clientBaseEntityId) {

        Cursor mCursor = null;
        org.smartregister.opd.pojos.Visit visit = null;
        try {
            net.sqlcipher.database.SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(clientBaseEntityId)) {
                mCursor = db.query(OpdDbConstants.Table.VISIT, columns, Visit.BASE_ENTITY_ID + " = ?"
                        , new String[]{clientBaseEntityId}
                        , null
                        , null
                        , Visit.VISIT_DATE + " DESC"
                        , "1");

                if (mCursor != null) {
                    if (mCursor.moveToNext()) {
                        visit = getVisitResult(mCursor);
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

        return visit;
    }
}
