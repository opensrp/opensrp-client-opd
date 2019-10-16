package org.smartregister.opd.presenter;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.interactor.BaseOpdRegisterActivityInteractor;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public abstract class BaseOpdRegisterActivityPresenter implements OpdRegisterActivityContract.Presenter, OpdRegisterActivityContract.InteractorCallBack {

    private WeakReference<OpdRegisterActivityContract.View> viewReference;
    private OpdRegisterActivityContract.Interactor interactor;
    private OpdRegisterActivityContract.Model model;

    public BaseOpdRegisterActivityPresenter(OpdRegisterActivityContract.View view, OpdRegisterActivityContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new BaseOpdRegisterActivityInteractor();
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
        if (initials != null && getView() != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Nullable
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

        if (getView() != null) {
            getView().displayToast(language + " selected");
        }
    }

    @Override
    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {

    }

    @Override
    public void saveVisitOrDiagnosisForm(@NonNull String eventType, String jsonString, @Nullable Intent data) {
        if (eventType.equals(OpdConstants.EventType.CHECK_IN)) {
            try {
                Event opdVisitEvent = OpdLibrary.getInstance().processOpdCheckInForm(eventType, jsonString, data);
                interactor.saveEvent(opdVisitEvent, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void onEventSaved() {
        if  (getView() != null) {
            getView().refreshList(FetchStatus.fetched);
            getView().hideProgressDialog();
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull String entityId, @NonNull String metaData
            , @NonNull String locationId, @Nullable HashMap<String, String> injectedFieldValues, @Nullable String entityTable) {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metaData, locationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = null;
        try {
            form = model.getFormAsJson(formName, entityId, locationId, injectedFieldValues);
        } catch (JSONException e) {
            Timber.e(e);
        }

        if (getView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(OpdConstants.IntentKey.BASE_ENTITY_ID, entityId);
            intentKeys.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);

            getView().startFormActivity(form, intentKeys);
        }
    }
}