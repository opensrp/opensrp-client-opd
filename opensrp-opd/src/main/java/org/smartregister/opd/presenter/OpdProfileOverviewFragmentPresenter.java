package org.smartregister.opd.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.model.OpdProfileOverviewFragmentModel;
import org.smartregister.opd.pojo.OpdCheckIn;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.DateUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

public class OpdProfileOverviewFragmentPresenter implements OpdProfileOverviewFragmentContract.Presenter {

    private OpdProfileOverviewFragmentModel model;

    public OpdProfileOverviewFragmentPresenter() {
        model = new OpdProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchLastCheckAndVisit(baseEntityId, new OpdProfileOverviewFragmentContract.Model.OnFetchedCallback() {
            @Override
            public void onFetched(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit) {
                loadOverviewDataAndDisplay(opdCheckIn, opdVisit, onFinishedCallback);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
        Facts facts = new Facts();

        if (opdVisit != null && opdCheckIn != null) {
            setDataFromCheckIn(opdCheckIn, opdVisit, facts);
        }

        try {
            generateYamlConfigList(facts, yamlConfigListGlobal);
        } catch (IOException ioException) {
            Timber.e(ioException);
        }

        onFinishedCallback.onFinished(facts, yamlConfigListGlobal);
    }

    private void generateYamlConfigList(@NonNull Facts facts, @NonNull List<YamlConfigWrapper> yamlConfigListGlobal) throws IOException {
        Iterable<Object> ruleObjects = loadFile(FilePath.FILE.PROFILE_OVERVIEW);

        for (Object ruleObject : ruleObjects) {
            List<YamlConfigWrapper> yamlConfigList = new ArrayList<>();
            int valueCount = 0;

            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            if (yamlConfig.getGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(yamlConfig.getGroup(), null, null));
            }

            if (yamlConfig.getSubGroup() != null) {
                yamlConfigList.add(new YamlConfigWrapper(null, yamlConfig.getSubGroup(), null));
            }

            List<YamlConfigItem> configItems = yamlConfig.getFields();

            if (configItems != null) {

                for (YamlConfigItem configItem : configItems) {
                    String relevance = configItem.getRelevance();
                    if (relevance != null && OpdLibrary.getInstance().getOpdRulesEngineHelper()
                            .getRelevance(facts, relevance)) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }
            }

            if (valueCount > 0) {
                yamlConfigListGlobal.addAll(yamlConfigList);
            }
        }
    }

    private void setDataFromCheckIn(@NonNull OpdCheckIn checkIn, @NonNull OpdVisit visit, @NonNull Facts facts) {
        facts.put("pregnancy_status", checkIn.getPregnancyStatus());
        facts.put("is_previously_tested_hiv", checkIn.getHasHivTestPreviously());
        facts.put("patient_on_art", checkIn.getIsTakingArt());
        facts.put("hiv_status", checkIn.getCurrentHivResult());
        facts.put("visit_type", checkIn.getVisitType());
        facts.put("previous_appointment", checkIn.getAppointmentScheduledPreviously());
        facts.put("date_of_appointment", checkIn.getAppointmentDueDate());

        if (visit.getVisitDate() != null && checkIn.getAppointmentDueDate() != null) {
            facts.put("visit_to_appointment_date", getVisitToAppointmentDateDuration(visit.getVisitDate(), checkIn.getAppointmentDueDate()));
        }
    }

    private Iterable<Object> loadFile(@NonNull String filename) throws IOException {
        return OpdLibrary.getInstance().readYaml(filename);
    }

    @NonNull
    private String getVisitToAppointmentDateDuration(@NonNull Date visitDate, @NonNull String appointmentDueDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);
        try {
            Date appointmentDueDate = dateFormat.parse(appointmentDueDateString);

            return DateUtil.getDuration(appointmentDueDate.getTime() - visitDate.getTime());
        } catch (ParseException e) {
            Timber.e(e);
            return "";
        }
    }
}
