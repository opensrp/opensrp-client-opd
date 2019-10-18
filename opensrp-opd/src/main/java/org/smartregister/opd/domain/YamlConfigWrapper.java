package org.smartregister.opd.domain;


import android.support.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class YamlConfigWrapper {

    private String group;
    private String subGroup;
    private YamlConfigItem yamlConfigItem;
    private String testResults;

    public YamlConfigWrapper(@Nullable String group, @Nullable String subGroup, @Nullable YamlConfigItem yamlConfigItem) {
        this.group = group;
        this.subGroup = subGroup;
        this.yamlConfigItem = yamlConfigItem;
    }

    public YamlConfigWrapper(@Nullable String group, @Nullable String subGroup, @Nullable YamlConfigItem yamlConfigItem,
                             @Nullable String testResults) {
        this.group = group;
        this.subGroup = subGroup;
        this.yamlConfigItem = yamlConfigItem;
        this.testResults = testResults;
    }

    @Nullable
    public YamlConfigItem getYamlConfigItem() {
        return yamlConfigItem;
    }

    public void setYamlConfigItem(@Nullable YamlConfigItem yamlConfigItem) {
        this.yamlConfigItem = yamlConfigItem;
    }

    @Nullable
    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(@Nullable String subGroup) {
        this.subGroup = subGroup;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public void setGroup(@Nullable String group) {
        this.group = group;
    }

    @Nullable
    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(@Nullable String testResults) {
        this.testResults = testResults;
    }
}