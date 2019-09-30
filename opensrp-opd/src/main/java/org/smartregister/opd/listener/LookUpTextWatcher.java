package org.smartregister.opd.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.event.Listener;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.utils.OpdLookUpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookUpTextWatcher implements TextWatcher {
    private static Map<String, String> lookUpFields;

    private final View mView;
    private final JsonFormFragment formFragment;


    public LookUpTextWatcher(JsonFormFragment formFragment, View view) {
        this.formFragment = formFragment;
        mView = view;
        lookUpFields = new HashMap<>();

    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    public void afterTextChanged(Editable editable) {
        String text = (String) mView.getTag(com.vijay.jsonwizard.R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        String key = (String) mView.getTag(com.vijay.jsonwizard.R.id.key);

        boolean afterLookUp = (Boolean) mView.getTag(com.vijay.jsonwizard.R.id.after_look_up);
        if (afterLookUp) {
            mView.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            return;
        }

        if(lookUpFields.containsKey(key)){
            if(text.trim().isEmpty()){
                lookUpFields.remove(key);
                return;
            }
        }
        lookUpFields.put(key, text);

        Listener<List<CommonPersonObject>> listener = null;
        if (formFragment instanceof BaseOpdFormFragment) {
            BaseOpdFormFragment OpdFormFragment = (BaseOpdFormFragment) formFragment;
            listener = OpdFormFragment.lookUpListener();
        }

        OpdLookUpUtils.lookUp(OpdLibrary.getInstance().context(), lookUpFields, listener);

    }

}