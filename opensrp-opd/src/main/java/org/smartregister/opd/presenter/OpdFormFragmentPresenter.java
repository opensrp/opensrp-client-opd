package org.smartregister.opd.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.opd.utils.Constants;

public class OpdFormFragmentPresenter extends JsonFormFragmentPresenter {

    public OpdFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();
    }


    public boolean intermediatePage() {
        return this.mStepDetails != null && this.mStepDetails.has(Constants.JSON_FORM_EXTRA.NEXT);
    }
}
