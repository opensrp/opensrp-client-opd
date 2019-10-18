package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.contract.OpdProfileFragmentContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileFragmentInteractor implements OpdProfileFragmentContract.Interactor {

    private OpdProfileFragmentContract.Presenter mProfileFrgamentPresenter;

    public OpdProfileFragmentInteractor(@NonNull OpdProfileFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
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

    @Nullable
    public OpdProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}