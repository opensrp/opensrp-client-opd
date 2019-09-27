package org.smartregister.opd.interactor;

import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.tasks.FetchProfileDataTask;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class ProfileFragmentInteractor implements OpdProfileFragmentContract.Interactor {

    private OpdProfileFragmentContract.Presenter mProfileFrgamentPresenter;

    public ProfileFragmentInteractor(OpdProfileFragmentContract.Presenter presenter) {
        this.mProfileFrgamentPresenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfileFrgamentPresenter = null;
        }
    }

    @Override
    public void refreshProfileView(String baseEntityId, boolean isForEdit) {
        new FetchProfileDataTask(isForEdit).execute(baseEntityId);
    }


    public OpdProfileFragmentContract.View getProfileView() {
        return mProfileFrgamentPresenter.getProfileView();
    }
}