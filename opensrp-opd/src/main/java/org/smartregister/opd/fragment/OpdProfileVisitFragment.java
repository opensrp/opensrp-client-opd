package org.smartregister.opd.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.OpdProfileVisitsAdapter;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.listener.OnSendActionToFragment;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.presenter.OpdProfileFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.fragment.BaseProfileFragment;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class OpdProfileVisitFragment extends BaseProfileFragment implements OpdProfileFragmentContract.View, OnSendActionToFragment {

    private RecyclerView recyclerView;
    private OpdProfileFragmentContract.Presenter presenter;
    private String baseEntityId;

    public static OpdProfileVisitFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        OpdProfileVisitFragment fragment = new OpdProfileVisitFragment();
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
    protected void onCreation() {
        initializePresenter();
        if (getArguments() != null) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getArguments()
                    .getSerializable(OpdConstants.IntentKey.CLIENT_OBJECT);

            if (commonPersonObjectClient != null) {
                baseEntityId = commonPersonObjectClient.getCaseId();
            }
        }
    }

    @Override
    protected void onResumption() {
        presenter.loadVisits(baseEntityId, new OpdProfileFragmentContract.Presenter.OnFinishedCallback() {
            @Override
            public void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries) {

                OpdProfileVisitsAdapter adapter = new OpdProfileVisitsAdapter(getActivity(), opdVisitSummaries);
                adapter.notifyDataSetChanged();

                // set up the RecyclerView
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy(false);
        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.opd_fragment_profile_visits, container, false);

        recyclerView = fragmentView.findViewById(R.id.rv_opdFragmentProfileVisit_recyclerView);
        return fragmentView;
    }

    @Override
    public void onActionReceive() {
        onResumption();
    }
}
