package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;

public class OpdRegisterQueryProviderTest extends OpdRegisterQueryProviderContract{

    @NonNull
    @Override
    public String getObjectIdsQuery(@Nullable String filters) {
        return null;
    }

    @NonNull
    @Override
    public String[] countExecuteQueries(@Nullable String filters) {
        return new String[0];
    }

    @NonNull
    @Override
    public String mainSelectWhereIDsIn() {
        return null;
    }
}