package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-19
 */

public class BaseOpdRegisterProviderMetadata implements OpdRegisterProviderMetadata {


    @NonNull
    @Override
    public String getGuardianFirstName(@NonNull Map<String, String> columnMaps) {
        return null;
    }

    @NonNull
    @Override
    public String getGuardianMiddleName(@NonNull Map<String, String> columnMaps) {
        return null;
    }

    @NonNull
    @Override
    public String getGuardianLastName(@NonNull Map<String, String> columnMaps) {
        return null;
    }

    @Override
    public boolean isClientHaveGuardianDetails(@NonNull Map<String, String> columnMaps) {
        return false;
    }

    @Nullable
    @Override
    public String getRegisterType(@NonNull Map<String, String> columnMaps) {
        return null;
    }

    @NonNull
    @Override
    public String getHomeAddress(@NonNull Map<String, String> columnMaps) {
        return null;
    }

    @NonNull
    @Override
    public String getGender(@NonNull Map<String, String> columnMaps) {
        return null;
    }
}
