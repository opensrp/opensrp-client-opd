package org.smartregister.opd.interactor;

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
}
