package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.ProfileOverviewAdapter;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.CheckIn;
import org.smartregister.opd.pojos.Visit;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class ProfileOverviewFragment extends BaseProfileFragment {

    public static final String TAG = ProfileOverviewFragment.class.getCanonicalName();
    private List<YamlConfigWrapper> yamlConfigListGlobal;

    private Button dueButton;
    //private ButtonAlertStatus buttonAlertStatus;
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
        HashMap<String, String> clientDetails =
                (HashMap<String, String>) getActivity().getIntent().getSerializableExtra(OpdConstants.IntentKey.CLIENT_MAP);
        //buttonAlertStatus = Utils.getButtonAlertStatus(clientDetails, getString(R.string.contact_number_due));
        yamlConfigListGlobal = new ArrayList<>();
        baseEntityId = getActivity().getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
    }

    @Override
    protected void onResumption() {
        try {
            yamlConfigListGlobal = new ArrayList<>(); //This makes sure no data duplication happens
            Facts facts = new Facts();

            // TODO: Move this to a background thread once Benn has added his work with the AppExecutors
            Visit visit = OpdLibrary.getInstance().getVisitRepository().getLatestVisit(baseEntityId);

            if (visit != null) {
                CheckIn checkIn = OpdLibrary.getInstance().getCheckInRepository().getCheckInByVisit(visit.getId());

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

                for (YamlConfigItem configItem : configItems) {
                    if (OpdLibrary.getInstance().getAncRulesEngineHelper()
                            .getRelevance(facts, configItem.getRelevance())) {
                        yamlConfigList.add(new YamlConfigWrapper(null, null, configItem));
                        valueCount += 1;
                    }
                }

                if (valueCount > 0) {
                    yamlConfigListGlobal.addAll(yamlConfigList);

                }
            }

            /*Utils.processButtonAlertStatus(getActivity(), dueButton, dueButton, buttonAlertStatus);
            dueButton.setVisibility(View.VISIBLE);*/

            ProfileOverviewAdapter adapter = new ProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
            adapter.notifyDataSetChanged();
            // set up the RecyclerView
            RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void setDataFromCheckIn(@NonNull CheckIn checkIn, @NonNull Visit visit, @NonNull Facts facts) {
        facts.put("pregnancy_status", checkIn.getPregnancyStatus());
        facts.put("is_previously_tested_hiv", checkIn.getHasHivTestPreviously());
        facts.put("patient_on_art", checkIn.getIsTakingArt());
        facts.put("hiv_status", checkIn.getCurrentHivResult());
        facts.put("visit_type", checkIn.getVisitType());
        facts.put("previous_appointment", checkIn.getAppointmentScheduledPreviously());
        facts.put("date_of_appointment", checkIn.getAppointmentDueDate());

        String visitToAppointmentDateDuration = null;
        if (checkIn.getAppointmentDueDate() != null) {
            visitToAppointmentDateDuration = getVisitToAppointmentDateDuration(visit.getVisitDate(), checkIn.getAppointmentDueDate());
        }

        facts.put("visit_to_appointment_date", visitToAppointmentDateDuration);
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

    private void setTestData(@NonNull Facts facts) {
        facts.put("pregnancy_status", "Positive");
        facts.put("is_previously_tested_hiv", "Negative");
        facts.put("patient_on_art", "No");
        facts.put("hiv_status", "positive");
        facts.put("visit_type", "New");
        facts.put("previous_appointment", "No");
        facts.put("date_of_appointment", "Tue March 2019");
        facts.put("visit_to_appointment_date", "34 days");
    }

    private Iterable<Object> loadFile(String filename) throws IOException {
        return OpdLibrary.getInstance().readYaml(filename);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_profile_overview, container, false);
        /*dueButton = fragmentView.findViewById(R.id.profile_overview_due_button);
        if (!ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
            dueButton.setOnClickListener((ProfileActivity) getActivity());
        } else {
            dueButton.setEnabled(false);
        }*/
        return fragmentView;
    }
}