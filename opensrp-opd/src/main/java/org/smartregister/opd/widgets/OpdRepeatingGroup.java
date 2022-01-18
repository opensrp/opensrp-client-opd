package org.smartregister.opd.widgets;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.domain.WidgetArgs;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.widgets.RepeatingGroupFactory;

import org.json.JSONObject;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdConstants;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OpdRepeatingGroup extends RepeatingGroupFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener, boolean popup) throws Exception {
        List<View> viewList = getParentViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
        View root = viewList.isEmpty() ? null : viewList.get(0);
        if (root != null) {
            final WidgetArgs widgetArgs = new WidgetArgs()
                    .withStepName(stepName)
                    .withContext(context)
                    .withFormFragment(formFragment)
                    .withJsonObject(jsonObject)
                    .withListener(listener)
                    .withPopup(popup);

            AtomicInteger count = new AtomicInteger(1);
            Button repeatingGroupIntermediateBtn = root.findViewById(R.id.repeating_group_intermediate_btn);
            if (jsonObject.has(OpdConstants.KEY.REPEATING_GROUP_BUTTON_TEXT)) {
                repeatingGroupIntermediateBtn.setText(jsonObject.optString(OpdConstants.KEY.REPEATING_GROUP_BUTTON_TEXT));
            }
            if (jsonObject.has(OpdConstants.KEY.REPEATING_GROUP_BUTTON_TEXT_COLOR)) {
                repeatingGroupIntermediateBtn.setTextColor(Color.parseColor(jsonObject.optString(OpdConstants.KEY.REPEATING_GROUP_BUTTON_TEXT_COLOR)));
            }
            MaterialEditText referenceEditText = root.findViewById(R.id.reference_edit_text);
            ImageButton doneButton = root.findViewById(com.vijay.jsonwizard.R.id.btn_repeating_group_done);
            repeatingGroupIntermediateBtn.setOnClickListener(v -> {
                referenceEditText.setText(String.valueOf(count.incrementAndGet()));
                addOnDoneAction(referenceEditText, doneButton, widgetArgs);
            });
        }
        return viewList;
    }

    @VisibleForTesting
    protected List<View> getParentViewsFromJson(@NonNull String stepName, @NonNull Context context, @NonNull JsonFormFragment formFragment, @NonNull JSONObject jsonObject, @NonNull CommonListener listener, boolean popup) throws Exception {
        return super.getViewsFromJson(stepName, context, formFragment, jsonObject, listener, popup);
    }

    @Override
    protected int getLayout() {
        return R.layout.opd_native_form_repeating_group;
    }
}
