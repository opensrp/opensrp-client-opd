package org.smartregister.opd.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.CheckIn;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public interface CheckInDao {

    @Nullable
    CheckIn getLatestCheckIn(@NonNull String clientBaseEntityId);

}
