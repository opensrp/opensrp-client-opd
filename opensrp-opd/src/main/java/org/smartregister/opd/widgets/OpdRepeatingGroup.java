package org.smartregister.opd.widgets;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;

import org.json.JSONObject;
import org.smartregister.opd.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OpdRepeatingGroup extends RepeatingGroupFactory {



    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> viewList = super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        View root = viewList.isEmpty() ? null : viewList.get(0);
        AtomicInteger count = new AtomicInteger(1);
        if(root != null) {
            Button repeatingGroupIntermediateBtn = viewList.get(0).findViewById(R.id.repeating_group_intermediate_btn);
            MaterialEditText referenceEditText = viewList.get(0).findViewById(R.id.reference_edit_text);
            repeatingGroupIntermediateBtn.setOnClickListener(v -> {
                referenceEditText.setText(String.valueOf(count.incrementAndGet()));
                addOnDoneAction(referenceEditText);
            });
        }
        return viewList;
    }


}
