package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.ProfileOverviewAdapter;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.OpdCheckIn;
import org.smartregister.opd.pojos.OpdVisit;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.DateUtil;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class ProfileOverviewFragment extends BaseProfileFragment {

    private List<YamlConfigWrapper> yamlConfigListGlobal;
    private String baseEntityId;

    public static ProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileOverviewFragment fragment = new ProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreation() {
        if (getActivity() != null) {
            yamlConfigListGlobal = new ArrayList<>();
            baseEntityId = getActivity().getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
        }
    }

    @Override
    protected void onResumption() {
        try {
            yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
            Facts facts = new Facts();

            // TODO: Move this to a background thread once Benn has added his work with the AppExecutors
            OpdVisit visit = OpdLibrary.getInstance().getVisitRepository().getLatestVisit(baseEntityId);

            if (visit != null) {
                OpdCheckIn checkIn = OpdLibrary.getInstance().getCheckInRepository().getCheckInByVisit(visit.getId());

                if (checkIn != null) {
                    setDataFromCheckIn(checkIn, visit, facts);
                }
            }

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

            if (getActivity() != null) {
                ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                adapter.notifyDataSetChanged();
                // set up the RecyclerView
                RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
            }

        } catch (IOException e) {
            Timber.e(e);
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
        facts.put("visit_to_appointment_date", getVisitToAppointmentDateDuration(visit.getVisitDate(), checkIn.getAppointmentDueDate()));
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

    private Iterable<Object> loadFile(String filename) throws IOException {
        return OpdLibrary.getInstance().readYaml(filename);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }
}