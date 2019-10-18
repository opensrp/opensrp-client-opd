package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;

import org.smartregister.opd.contract.OpdProfileActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileInteractor implements OpdProfileActivityContract.Interactor {
    private OpdProfileActivityContract.Presenter mProfilePresenter;

    public OpdProfileInteractor(OpdProfileActivityContract.Presenter presenter) {
        this.mProfilePresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfilePresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        // Todo: Add code for refreshing the profile view
    }

    public OpdProfileActivityContract.View getProfileView() {
        return mProfilePresenter.getProfileView();
    }
}
