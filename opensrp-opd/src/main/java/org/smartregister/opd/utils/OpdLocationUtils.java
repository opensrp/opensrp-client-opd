package org.smartregister.opd.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OpdLocationUtils {

    @NotNull
    public static ArrayList<String> getLocationLevels() {
        ArrayList<String> allLevels = new ArrayList<>();
        allLevels.add("Country");
        allLevels.add("Province");
        allLevels.add("Department");
        allLevels.add("Health Facility");
        allLevels.add("Zone");
        allLevels.add("Residential Area");
        allLevels.add("Facility");
        return allLevels;
    }

    @NotNull
    public static ArrayList<String> getHealthFacilityLevels() {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");
        healthFacilities.add("Department");
        healthFacilities.add("Health Facility");
        healthFacilities.add("Facility");
        return healthFacilities;
    }

}
