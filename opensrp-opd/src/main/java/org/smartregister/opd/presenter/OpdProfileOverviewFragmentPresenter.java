package org.smartregister.opd.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jeasy.rules.api.Facts;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.model.OpdProfileOverviewFragmentModel;
import org.smartregister.opd.pojos.OpdCheckIn;
import org.smartregister.opd.pojos.OpdDetails;
import org.smartregister.opd.pojos.OpdVisit;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdFactsUtil;
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
    private CommonPersonObjectClient client;

    public OpdProfileOverviewFragmentPresenter() {
        model = new OpdProfileOverviewFragmentModel();
    }

    @Override
    public void loadOverviewFacts(@NonNull String baseEntityId, @NonNull final OnFinishedCallback onFinishedCallback) {
        model.fetchLastCheckAndVisit(baseEntityId, new OpdProfileOverviewFragmentContract.Model.OnFetchedCallback() {
            @Override
            public void onFetched(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails) {
                loadOverviewDataAndDisplay(opdCheckIn, opdVisit, opdDetails, onFinishedCallback);
            }
        });
    }

    @Override
    public void loadOverviewDataAndDisplay(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @Nullable OpdDetails opdDetails, @NonNull final OnFinishedCallback onFinishedCallback) {
        List<YamlConfigWrapper> yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
        Facts facts = new Facts();
        setDataFromCheckIn(opdCheckIn, opdVisit, opdDetails, facts);

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

    private void setDataFromCheckIn(@Nullable OpdCheckIn checkIn, @Nullable OpdVisit visit, @Nullable OpdDetails opdDetails, @NonNull Facts facts) {
        if (checkIn != null) {
            if (client != null && AllConstants.FEMALE_GENDER.equalsIgnoreCase(client.getColumnmaps().get("gender"))) {
                OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.PREGNANCY_STATUS, checkIn.getPregnancyStatus());
            }

            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.IS_PREVIOUSLY_TESTED_HIV, checkIn.getHasHivTestPreviously());
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.PATIENT_ON_ART, checkIn.getIsTakingArt());
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.HIV_STATUS, checkIn.getCurrentHivResult());
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.VISIT_TYPE, checkIn.getVisitType());
            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.PREVIOUS_APPOINTMENT, checkIn.getAppointmentScheduledPreviously());

            OpdFactsUtil.putNonNullFact(facts, OpdConstants.FactKey.ProfileOverview.DATE_OF_APPOINTMENT, checkIn.getAppointmentDueDate());
        } else {
            if (client != null) {
                if (AllConstants.FEMALE_GENDER.equalsIgnoreCase(client.getColumnmaps().get("gender"))) {
                    facts.put(OpdConstants.FactKey.ProfileOverview.PREGNANCY_STATUS, "Unknown");
                } else {
                    facts.put(OpdConstants.FactKey.ProfileOverview.HIV_STATUS, "Unknown");
                }
            }
        }

        Date latestValidCheckInDate = OpdLibrary.getInstance().getLatestValidCheckInDate();
        boolean shouldCheckIn = visit == null || latestValidCheckInDate.before(visit.getVisitDate()) || (opdDetails != null && opdDetails.getCurrentVisitEndDate() != null);

        facts.put(OpdDbConstants.Column.OpdDetails.PENDING_DIAGNOSE_AND_TREAT,  !shouldCheckIn);

        if (visit != null && visit.getVisitDate() != null && checkIn != null && checkIn.getAppointmentDueDate() != null) {
            facts.put(OpdConstants.FactKey.VISIT_TO_APPOINTMENT_DATE, getVisitToAppointmentDateDuration(visit.getVisitDate(), checkIn.getAppointmentDueDate()));
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

    public void setClient(@NonNull CommonPersonObjectClient client) {
        this.client = client;
    }
}
