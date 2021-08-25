package org.smartregister.opd.widgets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdConstants;

import timber.log.Timber;

public class OpdMultiSelectDrugPicker extends MultiSelectListFactory implements TextWatcher {

    private Button opd_btn_save_drug;

    @Override
    protected void handleClickEventOnListData(@NonNull MultiSelectItem multiSelectItem, String key) {
        // Make sure its not called for other multi-select fields
        if (key.equals(OpdConstants.JSON_FORM_KEY.MEDICINE) || key.equals(OpdConstants.JSON_FORM_KEY.MEDICINE_PHARMACY))
            createAdditionalDetailsDialog(multiSelectItem, key);
        else
            super.handleClickEventOnListData(multiSelectItem, key);
    }

    private void createAdditionalDetailsDialog(@NonNull final MultiSelectItem multiSelectItem, final String currentAdapterKey) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.opd_drug_instructions_layout, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        opd_btn_save_drug = view.findViewById(R.id.opd_btn_save_drug);

        TextView txtSelectedItemInMultiSelectList = view.findViewById(R.id.txtSelectedItemInMultiSelectList);
        txtSelectedItemInMultiSelectList.setText(multiSelectItem.getText());

        final EditText edtTreatmentDuration = view.findViewById(R.id.edtTreatmentDuration);
        final EditText edtTreatmentDosage = view.findViewById(R.id.edtTreatmentDosage);
        final EditText edtTreatmentFrequency = view.findViewById(R.id.edtTreatmentFrequency);

        edtTreatmentDosage.addTextChangedListener(this);
        edtTreatmentDuration.addTextChangedListener(this);
        edtTreatmentFrequency.addTextChangedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.FullScreenDialogStyle);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        opd_btn_save_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editableTreatmentDuration = edtTreatmentDuration.getText();
                Editable editableTreatmentDosage = edtTreatmentDosage.getText();
                Editable editableTreatmentFrequency = edtTreatmentFrequency.getText();

                String duration = "";
                if (editableTreatmentDuration != null) {
                    duration = editableTreatmentDuration.toString();
                }

                String dosage = "";
                if (editableTreatmentDuration != null) {
                    dosage = editableTreatmentDosage.toString();
                }

                String frequency = "";
                if (editableTreatmentFrequency != null) {
                    frequency = editableTreatmentFrequency.toString();
                }


                writeAdditionalDetails(duration, dosage, frequency, multiSelectItem, currentAdapterKey);

                writeToForm(currentAdapterKey);

                alertDialog.dismiss();

                getAlertDialog(currentAdapterKey).dismiss();
            }
        });
    }

    protected void writeAdditionalDetails(@NonNull String duration, @NonNull String dosage, @NonNull String frequency, @NonNull MultiSelectItem multiSelectItem, String currentAdapterKey) {
        String multiSelectValue = multiSelectItem.getValue();
        String msg = String.format(getContext().getString(R.string.opd_drug_instructions_text), dosage, duration, frequency);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = new JSONObject(multiSelectValue);
            JSONObject jsonAdditionalObject = new JSONObject();
            jsonAdditionalObject.put(OpdConstants.JSON_FORM_KEY.DURATION, duration);
            jsonAdditionalObject.put(OpdConstants.JSON_FORM_KEY.DOSAGE, dosage);
            jsonAdditionalObject.put(OpdConstants.JSON_FORM_KEY.FREQUENCY, frequency);
            jsonAdditionalObject.put(JsonFormConstants.MultiSelectUtils.INFO, msg);
            jsonObject.put(JsonFormConstants.MultiSelectUtils.META, jsonAdditionalObject);
        } catch (JSONException e) {
            Timber.e(e);
        }

        multiSelectItem.setValue(jsonObject.toString());
        updateSelectedData(multiSelectItem, false, currentAdapterKey);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            if (opd_btn_save_drug != null) {
                opd_btn_save_drug.setTextColor(getContext().getResources().getColor(R.color.primary_text));
            }
        } else {
            if (opd_btn_save_drug != null) {
                opd_btn_save_drug.setTextColor(getContext().getResources().getColor(R.color.light_grey));
            }
        }
    }
}
