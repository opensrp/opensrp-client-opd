package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.utils.AppExecutors;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileFragmentInteractor implements OpdProfileFragmentContract.Interactor {

    private OpdProfileFragmentContract.Presenter mProfileFrgamentPresenter;
    private AppExecutors appExecutors;

    public OpdProfileFragmentInteractor(@NonNull OpdProfileFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        // Todo: We will have an implementation for refresh view
    }

    @Override
    public void fetchVisits(@NonNull final String baseEntityId, @NonNull final OpdProfileFragmentContract.Presenter.OnFinishedCallback onFinishedCallback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<OpdVisitSummary> summaries = OpdLibrary.getInstance().getOpdVisitSummaryRepository().getOpdVisitSummaries(baseEntityId);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        onFinishedCallback.onFinished(summaries);
                    }
                });
            }
        });
    }

    @Nullable
    public OpdProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}