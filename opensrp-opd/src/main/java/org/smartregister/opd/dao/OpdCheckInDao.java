package org.smartregister.opd.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojo.OpdCheckIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public interface OpdCheckInDao {

    @Nullable
    Map<String, String> getLatestCheckIn(@NonNull String clientBaseEntityId);

    @Nullable
    Map<String, String> getCheckInByVisit(@NonNull String visitId);

    boolean addCheckIn(@NonNull OpdCheckIn checkIn);

}
