package org.smartregister.opd.sample.activity;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.json.JSONObject;
import org.smartregister.opd.activity.BaseOpdRegisterActivity;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.opd.sample.fragment.OpdRegisterFragment;
import org.smartregister.opd.sample.presenter.OpdRegisterActivityPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-18
 */

public class OpdRegisterActivity extends BaseOpdRegisterActivity {


    @Override
    protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model) {
        return new OpdRegisterActivityPresenter(view, model);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OpdRegisterFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public void startRegistration() {

    }
}
