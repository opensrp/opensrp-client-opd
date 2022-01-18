package org.smartregister.opd.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.app.Dialog;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ApplicationProvider;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDialog;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdConstants;

public class OpdMultiSelectDrugPickerTest extends BaseUnitTest {

    private OpdMultiSelectDrugPicker opdMultiSelectDrugPicker;

    @Before
    public void setUp() {
        opdMultiSelectDrugPicker = spy(new OpdMultiSelectDrugPicker());
    }

    @Test
    public void testHandleClickEventOnListDataShouldShowAdditionalDetailsDialogIfIsMedicinePicker() {
        MultiSelectItem multiSelectItem = new MultiSelectItem();
        doReturn(ApplicationProvider.getApplicationContext()).when(opdMultiSelectDrugPicker).getContext();
        opdMultiSelectDrugPicker.handleClickEventOnListData(multiSelectItem, OpdConstants.JSON_FORM_KEY.MEDICINE);
        Dialog dialog = ShadowDialog.getLatestDialog();
        assertNotNull(dialog);
        assertTrue(dialog instanceof AlertDialog);
    }

    @Test
    public void testOnClickSaveDrugShouldInvokeFormUpdate() {
        MultiSelectItem multiSelectItem = new MultiSelectItem();
        doReturn(RuntimeEnvironment.application).when(opdMultiSelectDrugPicker).getContext();
        doNothing().when(opdMultiSelectDrugPicker).writeAdditionalDetails(anyString(), anyString(), anyString(), any(MultiSelectItem.class), anyString());
        doNothing().when(opdMultiSelectDrugPicker).writeToForm(anyString());
        doReturn(mock(AlertDialog.class)).when(opdMultiSelectDrugPicker).getAlertDialog(anyString());

        opdMultiSelectDrugPicker.handleClickEventOnListData(multiSelectItem, OpdConstants.JSON_FORM_KEY.MEDICINE);
        Dialog dialog = ShadowDialog.getLatestDialog();
        Button buttonSaveDrug = dialog.findViewById(R.id.opd_btn_save_drug);
        assertNotNull(buttonSaveDrug);
        buttonSaveDrug.performClick();

        verify(opdMultiSelectDrugPicker).writeAdditionalDetails(eq(""), eq(""), eq(""), eq(multiSelectItem), eq("medicine"));
    }

    @Test
    public void testWriteAdditionalDetailsShouldUpdateFormDetails() throws JSONException {
        String duration = "20 days";
        String frequency = "1 x 2";
        String dosage = "one or two tables daily";
        String currentAdapterKey = "medicine";
        MultiSelectItem multiSelectItem = new MultiSelectItem();
        multiSelectItem.setValue("{\"key\":\"abortion\",\"text\":\"Abortion\",\"property\":{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}}");
        doReturn(ApplicationProvider.getApplicationContext()).when(opdMultiSelectDrugPicker).getContext();
        doNothing().when(opdMultiSelectDrugPicker).updateSelectedData(any(MultiSelectItem.class), eq(false), eq(currentAdapterKey));
        opdMultiSelectDrugPicker.writeAdditionalDetails(duration, dosage, frequency, multiSelectItem, currentAdapterKey);

        verify(opdMultiSelectDrugPicker).updateSelectedData(eq(multiSelectItem), eq(false), eq(currentAdapterKey));

        JSONObject jsonObject = new JSONObject(multiSelectItem.getValue());
        assertTrue(jsonObject.has(JsonFormConstants.MultiSelectUtils.META));
        JSONObject metaObject = jsonObject.getJSONObject(JsonFormConstants.MultiSelectUtils.META);
        assertEquals(duration, metaObject.getString(OpdConstants.JSON_FORM_KEY.DURATION));
        assertEquals(frequency, metaObject.getString(OpdConstants.JSON_FORM_KEY.FREQUENCY));
    }
}