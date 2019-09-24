package org.smartregister.opd.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.fragment.OpdFormFragment;
import org.smartregister.opd.utils.Constants;
import org.smartregister.opd.utils.JsonFormUtils;
import org.smartregister.util.LangUtils;

import java.util.List;

import timber.log.Timber;

public class BaseOpdFormActivity extends JsonWizardFormActivity {
    private OpdFormFragment opdFormFragment;
    private String TAG = BaseOpdFormActivity.class.getCanonicalName();
    private boolean enableOnCloseDialog = true;
    private JSONObject form;

    @Override
    protected void attachBaseContext(android.content.Context base) {

        String language = LangUtils.getLanguage(base);
        super.attachBaseContext(LangUtils.setAppLocale(base, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            form = new JSONObject(currentJsonState());
        } catch (JSONException e) {
            Timber.e(e);
        }

        enableOnCloseDialog = getIntent().getBooleanExtra(Constants.FormActivity.EnableOnCloseDialog, true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            String et = form.getString(JsonFormUtils.ENCOUNTER_TYPE);

            confirmCloseTitle = getString(R.string.confirm_form_close);
            confirmCloseMessage = et.trim().toLowerCase().contains("update") ? this.getString(R.string.any_changes_you_make) : this.getString(R.string.confirm_form_close_explanation);

            setConfirmCloseTitle(confirmCloseTitle);
            setConfirmCloseMessage(confirmCloseMessage);


        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        opdFormFragment = OpdFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, opdFormFragment).commit();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        super.setSupportActionBar(toolbar);
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        if (OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().isFormWizardValidateRequiredFieldsBefore()) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup)
            throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity,
                openMrsEntityId, popup);
        if (OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().isFormWizardValidateRequiredFieldsBefore()) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        if (OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().isFormWizardValidateRequiredFieldsBefore()) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity,
                openMrsEntityId);
        if (OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().isFormWizardValidateRequiredFieldsBefore()) {
            validateActivateNext();
        }
    }

    /**
     * Conditionaly display the confirmation dialog
     */
    @Override
    public void onBackPressed() {
        if (enableOnCloseDialog) {
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                    .setMessage(confirmCloseMessage).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BaseOpdFormActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "No button on dialog in " + JsonFormActivity.class.getCanonicalName());
                        }
                    }).create();

            dialog.show();

        } else {
            BaseOpdFormActivity.this.finish();
        }
    }

    public void validateActivateNext() {
        Fragment fragment = getVisibleFragment();
        if (fragment != null && fragment instanceof OpdFormFragment) {
            ((OpdFormFragment) fragment).validateActivateNext();
        }
    }

    public Fragment getVisibleFragment() {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) return fragment;
            }
        }
        return null;
    }
}
