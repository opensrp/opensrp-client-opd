package org.smartregister.opd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseOpdFormFragmentTest {

    @Test
    public void startActivityOnLookUpShouldCallStartActivity() {
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BaseOpdFormFragment baseOpdFormFragment = new BaseOpdFormFragment();

        FragmentHostCallback host = Mockito.mock(FragmentHostCallback.class);

        ReflectionHelpers.setField(baseOpdFormFragment, "mHost", host);
        baseOpdFormFragment.startActivityOnLookUp(client);

        Mockito.verify(host, Mockito.times(1))
                .onStartActivityFromFragment(Mockito.any(Fragment.class)
                        , Mockito.any(Intent.class)
                        , Mockito.eq(-1)
                        , Mockito.nullable(Bundle.class));
    }

    @Test
    public void onItemClickShouldCallStartActivityOnLookupWithTheCorrectClient() {
        CommonPersonObjectClient client = Mockito.mock(CommonPersonObjectClient.class);

        BaseOpdFormFragment baseOpdFormFragment = Mockito.spy(new BaseOpdFormFragment());
        Mockito.doNothing().when(baseOpdFormFragment).startActivityOnLookUp(Mockito.any(CommonPersonObjectClient.class));

        AlertDialog alertDialog = Mockito.mock(AlertDialog.class);
        Mockito.doReturn(true).when(alertDialog).isShowing();
        Mockito.doNothing().when(alertDialog).dismiss();
        ReflectionHelpers.setField(baseOpdFormFragment, "alertDialog", alertDialog);

        View clickedView = Mockito.mock(View.class);
        Mockito.doReturn(client).when(clickedView).getTag();

        // The actual method call
        baseOpdFormFragment.onItemClick(clickedView);

        // Verification
        Mockito.verify(baseOpdFormFragment, Mockito.times(1))
                .startActivityOnLookUp(Mockito.eq(client));
    }
}