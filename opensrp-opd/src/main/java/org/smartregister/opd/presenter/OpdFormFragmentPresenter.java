package org.smartregister.opd.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.opd.fragment.BaseOpdFormFragment;

public class OpdFormFragmentPresenter extends JsonWizardFormFragmentPresenter {

    public OpdFormFragmentPresenter(BaseOpdFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    protected boolean moveToNextWizardStep() {
        final String nextStep = getFormFragment().getJsonApi().nextStep();
        if (!"".equals(nextStep)) {
            JsonFormFragment next = BaseOpdFormFragment.getFormFragment(nextStep);
            getView().hideKeyBoard();
            getView().transactThis(next);
            return true;
        }
        return false;
    }
}
