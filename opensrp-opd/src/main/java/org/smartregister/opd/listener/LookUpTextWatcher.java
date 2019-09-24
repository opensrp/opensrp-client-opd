package org.smartregister.opd.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.event.Listener;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.fragment.OpdFormFragment;
import org.smartregister.opd.pojos.EntityLookUp;
import org.smartregister.opd.utils.Constants;
import org.smartregister.opd.utils.LookUpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookUpTextWatcher implements TextWatcher {
    private static Map<String, EntityLookUp> lookUpMap;

    private final View mView;
    private final JsonFormFragment formFragment;
    private final String mEntityId;


    public LookUpTextWatcher(JsonFormFragment formFragment, View view, String entityId) {
        this.formFragment = formFragment;
        mView = view;
        mEntityId = entityId;
        lookUpMap = new HashMap<>();

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

        EntityLookUp entityLookUp = new EntityLookUp();
        if (lookUpMap.containsKey(mEntityId)) {
            entityLookUp = lookUpMap.get(mEntityId);
        }

        if (StringUtils.isBlank(text)) {
            if (entityLookUp.containsKey(key)) {
                entityLookUp.remove(key);
            }
        } else {
            entityLookUp.put(key, text);
        }

        lookUpMap.put(mEntityId, entityLookUp);


        Listener<List<CommonPersonObject>> listener = null;
        if (formFragment instanceof OpdFormFragment) {
            OpdFormFragment OpdFormFragment = (OpdFormFragment) formFragment;
            listener = OpdFormFragment.lookUpListener();
        }

        if (mEntityId.equalsIgnoreCase(Constants.CLIENT_TYPE)) {
            LookUpUtils.lookUp(OpdLibrary.getInstance().context(), lookUpMap.get(mEntityId), listener, null);
        }

    }

}