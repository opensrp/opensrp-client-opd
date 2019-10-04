package org.smartregister.opd.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.ViewPagerAdapter;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.fragment.ProfileVisitFragment;
import org.smartregister.opd.fragment.ProfileOverviewFragment;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class OpdProfileActivity extends BaseProfileActivity implements OpdProfileActivityContract.View {

    public static final String CLOSE_ANC_RECORD = "Close ANC Record";
    private TextView nameView;
    private TextView ageView;
    private TextView genderView;
    private TextView ancIdView;
    private ImageView imageView;
    private String phoneNumber;
    private HashMap<String, String> detailMap;
    private String buttonAlertStatus;

    @Override
    protected void initializePresenter() {
        presenter = new OpdProfileActivityPresenter(this);
    }

    @Override
    protected void setupViews() {

        super.setupViews();

        ageView = findViewById(R.id.textview_detail_two);
        genderView = findViewById(R.id.textview_detail_three);
        ancIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);

        getButtonAlertStatus();
    }

    private void getButtonAlertStatus() {
        /*detailMap = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);

        buttonAlertStatus = Utils.processContactDoneToday(detailMap.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE),
                Constants.ALERT_STATUS.ACTIVE.equals(detailMap.get(DBConstants.KEY.CONTACT_STATUS)) ?
                        Constants.ALERT_STATUS.IN_PROGRESS : "");*/
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ProfileOverviewFragment profileOverviewFragment = ProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        ProfileVisitFragment profileVisitsFragment = ProfileVisitFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileOverviewFragment, this.getString(R.string.overview));
        adapter.addFragment(profileVisitsFragment, this.getString(R.string.visits));

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected void fetchProfileData() {
        String baseEntityId = getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
        ((OpdProfileActivityPresenter) presenter).fetchProfileData(baseEntityId);
        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                .getSerializableExtra(OpdConstants.IntentKey.CLIENT_OBJECT);
        ((OpdProfileActivityPresenter) presenter).refreshProfileTopSection(commonPersonObjectClient.getColumnmaps());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            finish();
        }

        /*else {

            String contactButtonText = getString(R.string.start_contact);

            if (buttonAlertStatus.equals(Constants.ALERT_STATUS.TODAY)) {

                contactButtonText = String.format(getString(R.string.contact_recorded_today_no_break),
                        Utils.getTodayContact(detailMap.get(DBConstants.KEY.NEXT_CONTACT)));

            } else if (buttonAlertStatus.equals(Constants.ALERT_STATUS.IN_PROGRESS)) {

                contactButtonText = String.format(getString(R.string.continue_contact),
                        Integer.valueOf(detailMap.get(DBConstants.KEY.NEXT_CONTACT)));
            }


            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            arrayAdapter.add(getString(R.string.call));
            arrayAdapter.add(contactButtonText);
            arrayAdapter.add(getString(R.string.close_anc_record));

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String textClicked = arrayAdapter.getItem(which);
                    if (textClicked != null) {
                        switch (textClicked) {
                            case Constants.START_CONTACT:
                            case Constants.CONTINUE_CONTACT:
                                continueToContact();
                                break;
                            case CLOSE_ANC_RECORD:
                                JsonFormUtils.launchANCCloseForm(OpdProfileActivity.this);
                                break;
                            default:
                                if (textClicked.startsWith("Continue")) {
                                    continueToContact();
                                }

                                break;
                        }
                    }

                    dialog.dismiss();
                }

            });
            builderSingle.show();
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void continueToContact() {
        /*if (!buttonAlertStatus.equals(Constants.ALERT_STATUS.TODAY)) {

            String baseEntityId = detailMap.get(DBConstants.KEY.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, OpdProfileActivity.this);
            }
        }*/
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        return true;
    }*/

    @Override
    protected void onResumption() {
        super.onResumption();
        String baseEntityId = getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
        ((OpdProfileActivityPresenter) presenter).refreshProfileView(baseEntityId);

        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                .getSerializableExtra(OpdConstants.IntentKey.CLIENT_OBJECT);
        ((OpdProfileActivityPresenter) presenter).refreshProfileTopSection(commonPersonObjectClient.getColumnmaps());
        registerEventBus();
    }

    @Override
    public void onPause() {
        //EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            ((OpdProfileActivityPresenter) presenter).processFormDetailsSave(data, allSharedPreferences);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {

            String formMetadata = JsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getWomanClient());
            try {

                JsonFormUtils.startFormForEdit(this, JsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);

            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshProfileTopSection(ClientDetailsFetchedEvent event) {
        if (event != null && !event.isEditMode()) {
            Utils.removeStickyEvent(event);
            ((ProfilePresenter) presenter).refreshProfileTopSection(event.getWomanClient());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void removePatient(PatientRemovedEvent event) {
        if (event != null) {
            Utils.removeStickyEvent(event);
            hideProgressDialog();
            finish();
        }
    }*/

    @Override
    public void setProfileName(@NonNull String fullName) {
        this.patientName = fullName;
        nameView.setText(fullName);
    }

    @Override
    public void setProfileID(@NonNull String registerId) {
        ancIdView.setText(String.format(getString(R.string.id_detail), registerId));
    }

    @Override
    public void setProfileAge(@NonNull String age) {
        ageView.setText(String.format(getString(R.string.age_details), age));

    }

    @Override
    public void setProfileGender(@NonNull String gender) {
        genderView.setText(String.format(getString(R.string.gender_details), gender));
    }


    @Override
    public void setProfileImage(@NonNull String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, R.drawable.avatar_woman);
    }

    @NonNull
    protected Intent getTelephoneIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
    }

    protected boolean isPermissionGranted() {
        return PermissionUtils.isPermissionGranted(this, Manifest.permission.READ_PHONE_STATE,
                PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);
    }

    protected void registerEventBus() {
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View view) {
        /*if (view.getId() == R.id.profile_overview_due_button) {

            String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, getActivity());
            }

        } else {
            super.onClick(view);
        }*/
        super.onClick(view);
    }

    private Activity getActivity() {
        return this;

    }
}