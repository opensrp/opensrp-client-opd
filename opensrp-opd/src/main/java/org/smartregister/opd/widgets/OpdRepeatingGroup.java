package org.smartregister.opd.widgets;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;

import org.json.JSONObject;
import org.smartregister.opd.R;

import java.util.List;

public class OpdRepeatingGroup extends RepeatingGroupFactory {


    int count = 1;

    @Override
    protected void addOnDoneAction(TextView textView) {
        super.addOnDoneAction(textView);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> viewList = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        Button repeating_group_intemediate_btn = viewList.get(0).findViewById(R.id.repeating_group_intermediate_btn);
        MaterialEditText referenceEditText = viewList.get(0).findViewById(R.id.reference_edit_text);
        repeating_group_intemediate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceEditText.setText(String.valueOf(++count));
                addOnDoneAction(referenceEditText);
            }
        });
        return viewList;
    }



}
