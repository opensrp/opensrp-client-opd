package org.smartregister.opd.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.ViewPagerAdapter;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.fragment.ProfileOverviewFragment;
import org.smartregister.opd.fragment.ProfileVisitFragment;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.view.activity.BaseProfileActivity;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class OpdProfileActivity extends BaseProfileActivity implements OpdProfileActivityContract.View {

    private TextView nameView;
    private TextView ageView;
    private TextView genderView;
    private TextView ancIdView;
    private ImageView imageView;

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        String baseEntityId = getIntent().getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID);
        ((OpdProfileActivityPresenter) presenter).refreshProfileView(baseEntityId);

        CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) getIntent()
                .getSerializableExtra(OpdConstants.IntentKey.CLIENT_OBJECT);
        ((OpdProfileActivityPresenter) presenter).refreshProfileTopSection(commonPersonObjectClient.getColumnmaps());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());

    }

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
}