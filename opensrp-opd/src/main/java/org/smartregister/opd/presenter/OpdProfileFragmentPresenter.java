package org.smartregister.opd.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.interactor.OpdProfileFragmentInteractor;

import java.lang.ref.WeakReference;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileFragmentPresenter implements OpdProfileFragmentContract.Presenter {

    private WeakReference<OpdProfileFragmentContract.View> mProfileView;
    private OpdProfileFragmentContract.Interactor mProfileInteractor;

    public OpdProfileFragmentPresenter(@NonNull OpdProfileFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileFragmentInteractor(this);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (! isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void loadVisits(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback) {
        if (mProfileInteractor != null) {
            mProfileInteractor.fetchVisits(baseEntityId, onFinishedCallback);
        }
    }

    @Nullable
    @Override
    public OpdProfileFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }
}