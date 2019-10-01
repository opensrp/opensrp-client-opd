package org.smartregister.opd.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.opd.utils.OpdConstants;

public class OpdFormFragmentPresenter extends JsonFormFragmentPresenter {

    public OpdFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    public boolean isIntermediatePage() {
        return this.mStepDetails != null && this.mStepDetails.has(OpdConstants.JSON_FORM_EXTRA.NEXT);
    }
}
