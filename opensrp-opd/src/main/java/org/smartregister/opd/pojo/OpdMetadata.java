package org.smartregister.opd.pojo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.utils.DefaultOpdLocationUtils;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.ArrayList;
import java.util.Set;

public class OpdMetadata {

    private String opdRegistrationFormName;

    private String tableName;

    private String registerEventType;

    private String updateEventType;

    private String config;

    private Class<? extends BaseOpdFormActivity> opdFormActivity;

    private Class<? extends BaseOpdProfileActivity> profileActivity;

    private boolean formWizardValidateRequiredFieldsBefore;

    private ArrayList<String> locationLevels;

    private ArrayList<String> healthFacilityLevels;

    private Set<String> fieldsWithLocationHierarchy;

    private String lookUpQueryForOpdClient = String.format("select id as _id, %s, %s, %s, %s, %s, %s, %s, national_id from " + getTableName() + " where [condition] ", OpdConstants.KEY.RELATIONALID, OpdConstants.KEY.FIRST_NAME,
            OpdConstants.KEY.LAST_NAME, OpdConstants.KEY.GENDER, OpdConstants.KEY.DOB, OpdConstants.KEY.BASE_ENTITY_ID, OpdDbConstants.KEY.OPENSRP_ID);

    public OpdMetadata(@NonNull String opdRegistrationFormName, @NonNull String tableName, @NonNull String registerEventType, @NonNull String updateEventType,
                       @NonNull String config, @NonNull Class opdFormActivity, @Nullable Class profileActivity, boolean formWizardValidateRequiredFieldsBefore) {
        this.opdRegistrationFormName = opdRegistrationFormName;
        this.tableName = tableName;
        this.registerEventType = registerEventType;
        this.updateEventType = updateEventType;
        this.config = config;
        this.opdFormActivity = opdFormActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    public String getOpdRegistrationFormName() {
        return opdRegistrationFormName;
    }

    public void setOpdRegistrationFormName(String opdRegistrationFormName) {
        this.opdRegistrationFormName = opdRegistrationFormName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRegisterEventType() {
        return registerEventType;
    }

    public void setRegisterEventType(String registerEventType) {
        this.registerEventType = registerEventType;
    }

    public String getUpdateEventType() {
        return updateEventType;
    }

    public void setUpdateEventType(String updateEventType) {
        this.updateEventType = updateEventType;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Class getOpdFormActivity() {
        return opdFormActivity;
    }

    public void setOpdFormActivity(Class opdFormActivity) {
        this.opdFormActivity = opdFormActivity;
    }

    public Class getProfileActivity() {
        return profileActivity;
    }

    public void setProfileActivity(Class profileActivity) {
        this.profileActivity = profileActivity;
    }

    public boolean isFormWizardValidateRequiredFieldsBefore() {
        return formWizardValidateRequiredFieldsBefore;
    }

    public void setFormWizardValidateRequiredFieldsBefore(boolean formWizardValidateRequiredFieldsBefore) {
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    @NonNull
    public ArrayList<String> getLocationLevels() {
        if (locationLevels == null) {
            locationLevels = DefaultOpdLocationUtils.getLocationLevels();
        }

        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    @NonNull
    public ArrayList<String> getHealthFacilityLevels() {
        if (healthFacilityLevels == null) {
            healthFacilityLevels = DefaultOpdLocationUtils.getHealthFacilityLevels();
        }

        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }

    public String getLookUpQueryForOpdClient() {
        return lookUpQueryForOpdClient;
    }

    public void setLookUpQueryForOpdClient(String lookUpQueryForOpdClient) {
        this.lookUpQueryForOpdClient = lookUpQueryForOpdClient;
    }

    public Set<String> getFieldsWithLocationHierarchy() {
        return fieldsWithLocationHierarchy;
    }

    public void setFieldsWithLocationHierarchy(Set<String> fieldsWithLocationHierarchy) {
        this.fieldsWithLocationHierarchy = fieldsWithLocationHierarchy;
    }
}
