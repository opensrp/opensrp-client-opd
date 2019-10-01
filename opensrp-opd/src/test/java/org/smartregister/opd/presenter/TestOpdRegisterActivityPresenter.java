package org.smartregister.opd.presenter;

import org.smartregister.opd.contract.OpdRegisterActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class TestOpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {

    public TestOpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

    }
}
