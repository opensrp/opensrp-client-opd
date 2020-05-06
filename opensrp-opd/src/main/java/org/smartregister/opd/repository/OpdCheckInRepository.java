package org.smartregister.opd.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.database.SQLiteException;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.dao.OpdCheckInDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdCheckIn;
import org.smartregister.repository.BaseRepository;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdCheckInRepository extends BaseRepository implements OpdCheckInDao {

    @Nullable
    @Override
    public Map<String, String> getLatestCheckIn(@NonNull String clientBaseEntityId) {
        try {
            if (StringUtils.isNotBlank(clientBaseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from " + OpdDbConstants.Table.OPD_CHECK_IN +
                                " where " + OpdCheckIn.BASE_ENTITY_ID + " = '" + clientBaseEntityId + "' order by " + OpdCheckIn.CREATED_AT + " DESC  limit 1").get(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Nullable
    @Override
    public Map<String, String> getCheckInByVisit(@NonNull String visitId) {
        try {
            if (StringUtils.isNotBlank(visitId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from " + OpdDbConstants.Table.OPD_CHECK_IN +
                                " where " + OpdCheckIn.VISIT_ID + " = '" + visitId + "' order by " + OpdCheckIn.CREATED_AT + " DESC  limit 1").get(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public boolean addCheckIn(@NonNull org.smartregister.opd.pojo.OpdCheckIn checkIn) throws SQLiteException {
        throw new NotImplementedException("replaced with the processEvent logic from core");
    }
}
