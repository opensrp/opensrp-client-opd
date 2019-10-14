package org.smartregister.opd.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseOpdRegisterActivityInteractor implements OpdRegisterActivityContract.Interactor {

    private AppExecutors appExecutors;


    public BaseOpdRegisterActivityInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    BaseOpdRegisterActivityInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack) {

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    @Override
    public void saveRegistration(List<OpdEventClient> opdEventClientList, String jsonString, RegisterParams registerParams, OpdRegisterActivityContract.InteractorCallBack callBack) {

    }

    @Override
    public void saveEvent(@NonNull final Event event, @NonNull final OpdRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveEventInDb(event);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onEventSaved();
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }


    private void saveEventInDb(@NonNull Event event) {
        try {
            CoreLibrary.getInstance().context().getEventClientRepository().addEvent(event.getBaseEntityId()
                    , new JSONObject(JsonFormUtils.gson.toJson(event)));
        } catch (JSONException e) {
            Timber.e(e);
        }

        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);

        try {
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @NonNull
    public ECSyncHelper getSyncHelper() {
        return OpdLibrary.getInstance().getEcSyncHelper();
    }

    @NonNull
    public AllSharedPreferences getAllSharedPreferences() {
        return OpdLibrary.getInstance().context().allSharedPreferences();
    }

    @NonNull
    public ClientProcessorForJava getClientProcessorForJava() {
        return DrishtiApplication.getInstance().getClientProcessor();
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        return OpdLibrary.getInstance().getUniqueIdRepository();
    }

}