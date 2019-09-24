package org.smartregister.opd.pojos;

public class OpdMetadata {
    private String formName;

    private String tableName;

    private String motherTableName;

    private String registerEventType;

    private String updateEventType;

    private String outOfCatchmentServiceEventType;

    private String config;

    private String childCareGiverRelationKey;

    private String outOfCatchmentFormName;

    private Class opdFormActivity;

    private Class profileActivity;

    private boolean formWizardValidateRequiredFieldsBefore;

    public OpdMetadata(String formName, String tableName, String motherTableName, String registerEventType, String updateEventType, String outOfCatchmentServiceEventType, String config, String childCareGiverRelationKey, String outOfCatchmentFormName, Class opdFormActivity, Class profileActivity, boolean formWizardValidateRequiredFieldsBefore) {
        this.formName = formName;
        this.tableName = tableName;
        this.motherTableName = motherTableName;
        this.registerEventType = registerEventType;
        this.updateEventType = updateEventType;
        this.outOfCatchmentServiceEventType = outOfCatchmentServiceEventType;
        this.config = config;
        this.childCareGiverRelationKey = childCareGiverRelationKey;
        this.outOfCatchmentFormName = outOfCatchmentFormName;
        this.opdFormActivity = opdFormActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMotherTableName() {
        return motherTableName;
    }

    public void setMotherTableName(String motherTableName) {
        this.motherTableName = motherTableName;
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

    public String getOutOfCatchmentServiceEventType() {
        return outOfCatchmentServiceEventType;
    }

    public void setOutOfCatchmentServiceEventType(String outOfCatchmentServiceEventType) {
        this.outOfCatchmentServiceEventType = outOfCatchmentServiceEventType;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getChildCareGiverRelationKey() {
        return childCareGiverRelationKey;
    }

    public void setChildCareGiverRelationKey(String childCareGiverRelationKey) {
        this.childCareGiverRelationKey = childCareGiverRelationKey;
    }

    public String getOutOfCatchmentFormName() {
        return outOfCatchmentFormName;
    }

    public void setOutOfCatchmentFormName(String outOfCatchmentFormName) {
        this.outOfCatchmentFormName = outOfCatchmentFormName;
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
}
