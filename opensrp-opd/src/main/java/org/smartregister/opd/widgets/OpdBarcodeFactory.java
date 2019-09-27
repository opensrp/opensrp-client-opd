package org.smartregister.opd.widgets;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.widgets.BarcodeFactory;

import org.json.JSONObject;
import org.smartregister.opd.listener.LookUpTextWatcher;

import java.util.List;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class OpdBarcodeFactory extends BarcodeFactory {

    JsonFormFragment formFragment;
    @Override
    protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, boolean popup) {
        this.formFragment = formFragment;
        return super.attachJson(stepName, context, formFragment, jsonObject, popup);
    }

    @Override
    protected void addOnBarCodeResultListeners(Context context, final MaterialEditText editText) {
        if (context instanceof JsonApi) {
            JsonApi jsonApi = (JsonApi) context;
            jsonApi.addOnActivityResultListener(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE,
                    new OnActivityResultListener() {
                        @Override
                        public void onActivityResult(int requestCode,
                                                     int resultCode, Intent data) {
                            if (requestCode == JsonFormConstants.BARCODE_CONSTANTS.BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
                                if (data != null) {
                                    Barcode barcode = data.getParcelableExtra(JsonFormConstants.BARCODE_CONSTANTS.BARCODE_KEY);
                                    Timber.d("Scanned QR Code %s ", barcode.displayValue);
                                    editText.addTextChangedListener(new LookUpTextWatcher(formFragment, editText));
                                    editText.setText(barcode.displayValue);
                                } else
                                    Timber.i("NO RESULT FOR QR CODE");
                            }
                        }
                    });
        }
    }
}
