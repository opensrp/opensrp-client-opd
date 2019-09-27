package org.smartregister.opd.sample.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class OpdRegisterActivityPresenter extends BaseOpdRegisterActivityPresenter {

    public OpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        super(view, model);
    }

    @Override
    public void saveForm(String jsonString, RegisterParams registerParams) {
        // Do nothing
    }

    @Override
    public void startForm(String formName, String entityId, String metaData, String locationId) {
        // Do nothing
    }

    @Override
    public void onNoUniqueId() {
        // Do nothing
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        // Do nothing
    }
}
