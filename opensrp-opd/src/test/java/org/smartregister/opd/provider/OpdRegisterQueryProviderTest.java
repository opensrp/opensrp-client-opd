package org.smartregister.opd.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

public class OpdRegisterQueryProviderTest extends OpdRegisterQueryProviderContract {

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters) {
        return "";
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters) {
        return new String[]{""};
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return "";
    }
}
