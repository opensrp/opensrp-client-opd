package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;

import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.tasks.FetchProfileDataTask;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class ProfileInteractor implements OpdProfileActivityContract.Interactor {
    private OpdProfileActivityContract.Presenter mProfilePresenter;

    public ProfileInteractor(OpdProfileActivityContract.Presenter loginPresenter) {
        this.mProfilePresenter = loginPresenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfilePresenter = null;
        }
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }


    public OpdProfileActivityContract.View getProfileView() {
        return mProfilePresenter.getProfileView();
    }
}
