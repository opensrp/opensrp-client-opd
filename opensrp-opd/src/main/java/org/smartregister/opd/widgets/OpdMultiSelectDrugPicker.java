package org.smartregister.opd.widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.opd.R;

import java.util.Arrays;

public class OpdMultiSelectDrugPicker extends MultiSelectListFactory implements TextWatcher {
    private Context context;
    private Button opd_btn_save_drug;

    @Override
    protected void handleClickEventOnListData(MultiSelectItem multiSelectItem, Context context) {
        this.context = context;
        createAdditionalDetailsDialog(multiSelectItem);
    }

    private void createAdditionalDetailsDialog(final MultiSelectItem multiSelectItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dosage_and_duration_layout, null);
        ImageView imgClose = view.findViewById(R.id.multiSelectListCloseDialog);
        opd_btn_save_drug = view.findViewById(R.id.opd_btn_save_drug);

        TextView txtMultiSelectListDialogTitle = view.findViewById(R.id.multiSelectListDialogTitle);
        TextView txtSelectedItemInMultiSelectList = view.findViewById(R.id.txtSelectedItemInMultiSelectList);
        txtSelectedItemInMultiSelectList.setText(multiSelectItem.getKey());
        final EditText edtTreatmentDuration = view.findViewById(R.id.edtTreatmentDosage);
        final EditText edtTreatmentDosage = view.findViewById(R.id.edtTreatmentDuration);
        edtTreatmentDosage.addTextChangedListener(this);
        edtTreatmentDuration.addTextChangedListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreenDialogStyle);
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
                String duration = edtTreatmentDuration.getText().toString();
                String dosage = edtTreatmentDosage.getText().toString();

                writeAdditionalDetails(duration, dosage, multiSelectItem);

                writeToForm(multiSelectItem);

                alertDialog.dismiss();

                getAlertDialog().dismiss();
            }
        });
    }

    private void writeAdditionalDetails(String duration, String dosage, MultiSelectItem multiSelectItem) {
        String multiSelectValue = multiSelectItem.getValue();
        String msg = "Dose: " + dosage + ", Duration: " + duration;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = new JSONObject(multiSelectValue);
            JSONObject jsonAdditionalObject = new JSONObject();
            jsonAdditionalObject.put("duration", duration);
            jsonAdditionalObject.put("dosage", dosage);
            jsonAdditionalObject.put("info", msg);
            jsonObject.put("meta", jsonAdditionalObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        multiSelectItem.setValue(jsonObject.toString());

        updateSelectedData(Arrays.asList(multiSelectItem), false);
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
                opd_btn_save_drug.setTextColor(Color.parseColor("#212121"));
            }
        } else {
            if (opd_btn_save_drug != null) {
                opd_btn_save_drug.setTextColor(Color.parseColor("#cccccc"));
            }
        }
    }

}
