package org.smartregister.opd.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.Visit;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-01
 */

public interface VisitDao {


    @Nullable
    Visit getLatestVisit(@NonNull String clientBaseEntityId);
}
