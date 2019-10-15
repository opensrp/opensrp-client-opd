package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.presenter.OpdProfileFragmentPresenter;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class ProfileVisitFragment extends BaseProfileFragment implements OpdProfileFragmentContract.View {
    public static final String TAG = ProfileVisitFragment.class.getCanonicalName();

    private List<YamlConfigWrapper> lastContactDetails;
    private List<YamlConfigWrapper> lastContactTests;
    private TextView testsHeader;
    private LinearLayout lastContactLayout;
    private LinearLayout testLayout;
    private LinearLayout testsDisplayLayout;
    private ProfileContactsActionHandler profileContactsActionHandler = new ProfileContactsActionHandler();
    private OpdProfileFragmentContract.Presenter presenter;

    public static ProfileVisitFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ProfileVisitFragment fragment = new ProfileVisitFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new OpdProfileFragmentPresenter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializePresenter();
    }

    @Override
    protected void onCreation() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }
    }

    @Override
    protected void onResumption() {
        lastContactDetails = new ArrayList<>();
        lastContactTests = new ArrayList<>();
        if (testsDisplayLayout != null) {
            testsDisplayLayout.removeAllViews();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile_contacts, container, false);
        lastContactLayout = fragmentView.findViewById(R.id.last_contact_layout);
        TextView lastContactBottom = lastContactLayout.findViewById(R.id.last_contact_bottom);
        lastContactBottom.setOnClickListener(profileContactsActionHandler);

        testLayout = fragmentView.findViewById(R.id.test_layout);
        testsHeader = testLayout.findViewById(R.id.tests_header);
        TextView testsBottom = testLayout.findViewById(R.id.tests_bottom);
        testsBottom.setOnClickListener(profileContactsActionHandler);

        testsDisplayLayout = testLayout.findViewById(R.id.test_display_layout);

        return fragmentView;
    }

    /**
     * Handles the Click actions on any of the section in the page.
     */
    private class ProfileContactsActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.last_contact_bottom && ! lastContactDetails.isEmpty()) {
                //goToPreviousContacts();
            } else if (view.getId() == R.id.tests_bottom && ! lastContactTests.isEmpty()) {
                //goToPreviousContactsTests();
            }
        }

    }
}
