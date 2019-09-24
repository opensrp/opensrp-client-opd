package org.smartregister.opd.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONObject;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.fragment.BaseOpdRegisterFragment;
import org.smartregister.opd.model.OpdRegisterActivityModel;
import org.smartregister.opd.pojos.UpdateRegisterParams;
import org.smartregister.opd.presenter.OpdRegisterActivityPresenter;
import org.smartregister.opd.utils.Constants;
import org.smartregister.opd.utils.JsonFormUtils;
import org.smartregister.opd.utils.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseOpdRegisterActivity extends BaseRegisterActivity implements OpdRegisterActivityContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerBottomNavigation() {
        // Do nothing
    }

    @Override
    protected void initializePresenter() {
        presenter = new OpdRegisterActivityPresenter(this, new OpdRegisterActivityModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BaseOpdRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected void onResumption() {
        super.onResumption();
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        throw new IllegalArgumentException();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        //do nothing
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Timber.d("JSONResult : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().getRegisterEventType())) {
                    UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
                    updateRegisterParam.setEditMode(false);
                    updateRegisterParam.setFormTag(JsonFormUtils.formTag(Utils.context().allSharedPreferences()));

                   // showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, updateRegisterParam);
                } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().getOutOfCatchmentServiceEventType())) {

                   // showProgressDialog(R.string.saving_dialog_title);
//                    presenter().saveOutOfCatchmentService(jsonString,this);

                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void switchToBaseFragment() {
        Intent intent = new Intent(this, BaseOpdRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public OpdRegisterActivityContract.Presenter presenter() {
        return (OpdRegisterActivityContract.Presenter) presenter;
    }

    @Override
    public void startRegistration() {
        //do nothing
    }
}