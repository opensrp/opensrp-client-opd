package org.smartregister.opd.utils;

import android.support.annotation.NonNull;

import org.smartregister.opd.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultOpdLocationUtils {

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

}
