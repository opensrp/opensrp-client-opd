package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.OpdProfileOverviewAdapter;
import org.smartregister.opd.contract.OpdProfileOverviewFragmentContract;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.OpdCheckIn;
import org.smartregister.opd.presenter.OpdProfileOverviewFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdProfileOverviewFragment extends BaseProfileFragment implements OpdProfileOverviewFragmentContract.View {

    private String baseEntityId;
    private OpdProfileOverviewFragmentContract.Presenter presenter;

    public static OpdProfileOverviewFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        OpdProfileOverviewFragment fragment = new OpdProfileOverviewFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new OpdProfileOverviewFragmentPresenter();
    }

    @Override
    protected void onCreation() {
        if (getActivity() != null) {
            baseEntityId = getActivity().getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
        }
    }

    @Override
    protected void onResumption() {
        if (baseEntityId != null) {
            presenter.loadOverviewFacts(baseEntityId, new OpdProfileOverviewFragmentContract.Presenter.OnFinishedCallback() {

                @Override
                public void onFinished(@Nullable OpdCheckIn checkIn, @Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal) {
                    if (getActivity() != null && facts != null) {
                        OpdProfileOverviewAdapter adapter = new OpdProfileOverviewAdapter(getActivity(), yamlConfigListGlobal, facts);
                        adapter.notifyDataSetChanged();
                        // set up the RecyclerView
                        RecyclerView recyclerView = getActivity().findViewById(R.id.profile_overview_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(adapter);
                    }
                }

            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }
}