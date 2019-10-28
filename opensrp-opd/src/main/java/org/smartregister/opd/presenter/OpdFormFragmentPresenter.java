package org.smartregister.opd.presenter;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.opd.fragment.BaseOpdFormFragment;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.utils.OpdConstants;

public class OpdFormFragmentPresenter extends JsonFormFragmentPresenter {

    public OpdFormFragmentPresenter(BaseOpdFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    public OpdFormFragmentPresenter(BaseOpdFormFragment formFragment) {
        super(formFragment, OpdFormInteractor.getInstance());
    }

    public boolean isIntermediatePage() {
        return this.mStepDetails != null && this.mStepDetails.has(OpdConstants.JSON_FORM_EXTRA.NEXT);
    }


}
