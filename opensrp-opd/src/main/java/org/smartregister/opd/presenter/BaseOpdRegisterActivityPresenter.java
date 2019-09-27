package org.smartregister.opd.presenter;


import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.RegisterParams;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public abstract class BaseOpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter, OpdRegisterActivityContract.InteractorCallBack {
    private WeakReference<OpdRegisterActivityContract.View> viewReference;
    private OpdRegisterActivityContract.Interactor interactor;
    private OpdRegisterActivityContract.Model model;

    public BaseOpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        viewReference = new WeakReference<>(view);
        this.model = model;
    }

    public void setModel(OpdRegisterActivityContract.Model model) {
        this.model = model;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy\
        if (!isChangingConfiguration) {
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    private OpdRegisterActivityContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        getView().displayToast(language + " selected");
    }

    @Override
    public void saveForm(String jsonString, RegisterParams registerParams) {

    }
}