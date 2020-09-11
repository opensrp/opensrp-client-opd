package org.smartregister.opd.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentHostCallback;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.BaseRobolectricUnitTest;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.configuration.BaseOpdRegisterProviderMetadata;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.configuration.OpdRegisterQueryProviderContract;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.utils.OpdConstants;

import java.util.HashMap;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

public class BaseOpdFormFragmentTest extends BaseRobolectricUnitTest {

    private BaseOpdFormFragment formFragment;

    @Mock
    private OpdLibrary opdLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        formFragment = Mockito.spy(BaseOpdFormFragment.class);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void startActivityOnLookUpShouldCallStartActivity() {
        OpdConfiguration opdConfiguration = new OpdConfiguration.Builder(OpdRegisterQueryProvider.class)
                .setOpdRegisterProviderMetadata(BaseOpdRegisterProviderMetadata.class)
                .setOpdMetadata(new OpdMetadata("form-name"
                        , "table-name"
                        , "register-event-type"
                        , "update-event-type"
                        , "config"
                        , BaseOpdFormActivity.class
                        , BaseOpdProfileActivity.class
                        , false))
                .build();

        Activity activity = Robolectric.setupActivity(FragmentActivity.class);

        Mockito.doReturn(activity).when(formFragment).getActivity();

        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();

        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);

        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);


        FragmentHostCallback host = Mockito.mock(FragmentHostCallback.class);

        ReflectionHelpers.setField(formFragment, "mHost", host);
        formFragment.startActivityOnLookUp(client);

        Mockito.verify(host, Mockito.times(1))
                .onStartActivityFromFragment(Mockito.any(Fragment.class)
                        , Mockito.any(Intent.class)
                        , Mockito.eq(-1)
                        , Mockito.nullable(Bundle.class));
    }

    @Test
    public void onItemClickShouldCallStartActivityOnLookupWithTheCorrectClient() {
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        Mockito.doNothing().when(formFragment).startActivityOnLookUp(Mockito.any(CommonPersonObjectClient.class));

        AlertDialog alertDialog = Mockito.mock(AlertDialog.class);
        Mockito.doReturn(true).when(alertDialog).isShowing();
        Mockito.doNothing().when(alertDialog).dismiss();
        ReflectionHelpers.setField(formFragment, "alertDialog", alertDialog);

        View clickedView = Mockito.mock(View.class);
        Mockito.doReturn(client).when(clickedView).getTag();

        // The actual method call
        formFragment.onItemClick(clickedView);

        // Verification
        Mockito.verify(formFragment, Mockito.times(1))
                .startActivityOnLookUp(Mockito.eq(client));
    }

    @Test
    public void testFinishWithResultShouldAddParcelableDataToIntentBeforeActivityFinish() {
        String baseEntityId = "342-rw3424";
        String table = "ec_client";
        HashMap<String, String> parcelableData = new HashMap<>();
        parcelableData.put(OpdConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);
        parcelableData.put(OpdConstants.IntentKey.ENTITY_TABLE, table);

        BaseOpdFormActivity baseOpdFormActivity = Mockito.spy(Robolectric
                .buildActivity(BaseOpdFormActivity.class, null).get());

        Mockito.doReturn(parcelableData).when(baseOpdFormActivity).getParcelableData();

        Mockito.doReturn(baseOpdFormActivity).when(formFragment).getActivity();

        Intent intent = new Intent();

        formFragment.finishWithResult(intent);

        Assert.assertEquals(baseEntityId, intent.getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID));
        Assert.assertEquals(table, intent.getStringExtra(OpdConstants.IntentKey.ENTITY_TABLE));

        Mockito.verify(baseOpdFormActivity, Mockito.times(1))
                .setResult(Mockito.eq(Activity.RESULT_OK), Mockito.eq(intent));

        Mockito.verify(baseOpdFormActivity, Mockito.times(1))
                .finish();


    }

    static class OpdRegisterQueryProvider extends OpdRegisterQueryProviderContract {

        @NonNull
        @Override
        public String getObjectIdsQuery(@Nullable String filters, @Nullable String mainCondition) {
            return null;
        }

        @NonNull
        @Override
        public String[] countExecuteQueries(@Nullable String filters, @Nullable String mainCondition) {
            return new String[0];
        }

        @NonNull
        @Override
        public String mainSelectWhereIDsIn() {
            return null;
        }
    }
}