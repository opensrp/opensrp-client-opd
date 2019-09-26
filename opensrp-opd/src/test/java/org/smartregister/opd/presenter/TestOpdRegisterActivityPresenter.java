package org.smartregister.opd.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.contract.OpdRegisterActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class TestOpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {

    public TestOpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void startForm(String formName, String entityId, String metaData, String locationId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }
}
