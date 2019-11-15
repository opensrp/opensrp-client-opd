package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.MultiSelectOptionsDao;
import org.smartregister.opd.pojos.OpdMultiSelectOption;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdMultiSelectOptions;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdMultiSelectOptionsRepository extends BaseRepository implements MultiSelectOptionsDao {

    private String[] columns = new String[]{
            OpdMultiSelectOptions.ID,
            OpdMultiSelectOptions.TYPE,
            OpdMultiSelectOptions.JSON,
            OpdMultiSelectOptions.VERSION,
            OpdMultiSelectOptions.CREATED_AT
    };

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_MULTI_SELECT_LIST_OPTION + "("
            + OpdMultiSelectOptions.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + OpdMultiSelectOptions.TYPE + " VARCHAR NOT NULL, "
            + OpdMultiSelectOptions.JSON + " VARCHAR NOT NULL, "
            + OpdMultiSelectOptions.VERSION + " VARCHAR NOT NULL, "
            + OpdMultiSelectOptions.APP_VERSION + " VARCHAR NOT NULL, "
            + OpdMultiSelectOptions.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), " +
            "UNIQUE(" + OpdMultiSelectOptions.TYPE + "," + OpdMultiSelectOptions.VERSION + ") ON CONFLICT REPLACE)";


    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }

    public OpdMultiSelectOptionsRepository(Repository repository) {
        super(repository);
    }

    @Override
    public boolean saveOrUpdate(OpdMultiSelectOption opdMultiSelectOption) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdMultiSelectOptions.TYPE, opdMultiSelectOption.getType());
        contentValues.put(OpdMultiSelectOptions.VERSION, opdMultiSelectOption.getVersion());
        contentValues.put(OpdMultiSelectOptions.JSON, opdMultiSelectOption.getJson());
        contentValues.put(OpdMultiSelectOptions.APP_VERSION, opdMultiSelectOption.getAppVersion());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_MULTI_SELECT_LIST_OPTION, null, contentValues);
        return rows != -1;
    }

    @Override
    public OpdMultiSelectOption findOne(OpdMultiSelectOption opdMultiSelectOption) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(OpdDbConstants.Table.OPD_MULTI_SELECT_LIST_OPTION, columns,
                OpdMultiSelectOptions.TYPE + " = ? and " + OpdMultiSelectOptions.VERSION + " = ?",
                new String[]{opdMultiSelectOption.getType(), opdMultiSelectOption.getVersion()}, null, null, OpdMultiSelectOptions.ID + " DESC", "1");
        if (cursor.getCount() == 0) {
            return null;
        }
        OpdMultiSelectOption multiSelectOption = null;
        if (cursor.moveToNext()) {
            multiSelectOption = new OpdMultiSelectOption(cursor.getInt(0),
                    cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4));

            cursor.close();
        }
        return multiSelectOption;
    }

    @Override
    public boolean delete(OpdMultiSelectOption opdMultiSelectOption) {
        throw new NotImplementedException("delete");
    }

    @Override
    public List<OpdMultiSelectOption> findAll() {
        throw new NotImplementedException("findAll");
    }

    @Override
    public OpdMultiSelectOption getLatest(String key) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(OpdDbConstants.Table.OPD_MULTI_SELECT_LIST_OPTION, columns, OpdMultiSelectOptions.TYPE + " = ? ",
                new String[]{key}, null, null, OpdMultiSelectOptions.ID + " DESC", "1");
        if (cursor.getCount() == 0) {
            return null;
        }
        OpdMultiSelectOption multiSelectOption = null;
        if (cursor.moveToNext()) {
            multiSelectOption = new OpdMultiSelectOption(cursor.getInt(0),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(1), null);
            cursor.close();
        }
        return multiSelectOption;
    }
}
