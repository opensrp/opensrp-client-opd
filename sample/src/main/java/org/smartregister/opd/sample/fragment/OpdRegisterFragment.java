package org.smartregister.opd.sample.fragment;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.sample.activity.OpdRegisterActivity;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-20
 */

public class OpdRegisterFragment extends BaseOpdRegisterFragment {

    @Override
    protected void startRegistration() {
        ((OpdRegisterActivity) getActivity()).startFormActivity(OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getFormName(),null,null);
    }

    @Override
    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // Do nothing
        Timber.i("Client Action button was clicked on OPD Register for client: %s", new Gson().toJson(commonPersonObjectClient));
    }

    @Override
    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // Do nothing
        Timber.i("Client was clicked on OPD Register: %s", new Gson().toJson(commonPersonObjectClient));
    }
}
