package org.smartregister.opd.widgets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.vision.barcode.Barcode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.listener.LookUpTextWatcher;
import org.smartregister.opd.utils.OpdConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class OpdBarcodeFactoryTest extends BaseUnitTest {

    private OpdBarcodeFactory opdBarcodeFactory;

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private Context context;

    @Before
    public void setUp() {
        opdBarcodeFactory = spy(new OpdBarcodeFactory());
    }

    @Test
    public void testGetViewsFromJsonShouldUpdateForLookUpWithForm() throws Exception {
        assertFalse(ReflectionHelpers.getField(opdBarcodeFactory, "forLookUp"));

        JSONObject fieldJsonObject = new JSONObject();
        fieldJsonObject.put(OpdConstants.KEY.LOOK_UP, "true");

        String fieldKey = "fieldA";
        doReturn(new ArrayList<>()).when(opdBarcodeFactory)
                .getParentViewsFromJson(fieldKey, context,
                        jsonFormFragment, fieldJsonObject, commonListener, false);

        opdBarcodeFactory.getViewsFromJson(fieldKey, context,
                jsonFormFragment, fieldJsonObject, commonListener, false);

        assertTrue(ReflectionHelpers.getField(opdBarcodeFactory, "forLookUp"));
    }

    @Test
    public void testOnActivityResultListenerShouldUpdateEditTextView() {
        JsonFormActivity jsonFormActivity = spy(Robolectric.buildActivity(JsonFormActivity.class).get());
        MaterialEditText mockEditText = mock(MaterialEditText.class);
        HashMap<Integer, OnActivityResultListener> listenerHashMap = new HashMap<>();
        WhiteboxImpl.setInternalState(jsonFormActivity, "onActivityResultListeners", listenerHashMap);
        WhiteboxImpl.setInternalState(opdBarcodeFactory, "forLookUp", true);

        opdBarcodeFactory.addOnBarCodeResultListeners(jsonFormActivity, mockEditText);

        Intent intent = new Intent();
        Barcode barcode = new Barcode();
        barcode.displayValue = "10";
        intent.putExtra(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_KEY, barcode);
        listenerHashMap.get(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE).onActivityResult(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE, Activity.RESULT_OK, intent);

        verify(mockEditText).addTextChangedListener(any(LookUpTextWatcher.class));
        verify(mockEditText).setText(eq(barcode.displayValue));
        verify(mockEditText).setTag(eq(com.vijay.jsonwizard.R.id.after_look_up), eq(false));

        jsonFormActivity.finish();
    }

}