package org.smartregister.opd.presenter;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.contract.OpdRegisterActivityContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-24
 */

@RunWith(MockitoJUnitRunner.class)
public class BaseOpdRegisterActivityPresenterTest {


    @Test
    public void startFormShouldPassEntityTableAndBaseEntityIdToActivity() throws JSONException {
        OpdRegisterActivityContract.View view = Mockito.mock(OpdRegisterActivityContract.View.class);
        OpdRegisterActivityContract.Model model = Mockito.mock(OpdRegisterActivityContract.Model.class);

        BaseOpdRegisterActivityPresenter baseOpdRegisterActivityPresenter = new OpdRegisterActivityPresenter(view, model);

        Mockito.doReturn(new JSONObject()).when(model).getFormAsJson(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.nullable(HashMap.class));

        ReflectionHelpers.setField(baseOpdRegisterActivityPresenter, "viewReference", new WeakReference<OpdRegisterActivityContract.View>(view));
        baseOpdRegisterActivityPresenter.setModel(model);

        baseOpdRegisterActivityPresenter.startForm("check_in.json", "90923-dsfds",  "meta", "location-id", null, "ec_child");

        Mockito.verify(view, Mockito.times(1))
                .startFormActivity(Mockito.any(JSONObject.class), Mockito.any(HashMap.class));
    }
}