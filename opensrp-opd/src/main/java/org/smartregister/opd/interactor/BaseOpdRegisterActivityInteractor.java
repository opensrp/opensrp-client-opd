package org.smartregister.opd.interactor;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseOpdRegisterActivityInteractor implements OpdRegisterActivityContract.Interactor {

    protected AppExecutors appExecutors;
    private OpdRegisterActivityContract.Presenter presenter;


    public BaseOpdRegisterActivityInteractor(@NonNull OpdRegisterActivityContract.Presenter presenter) {
        this(presenter, new AppExecutors());
    }

    @VisibleForTesting
    BaseOpdRegisterActivityInteractor(@NonNull OpdRegisterActivityContract.Presenter presenter, AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.presenter = presenter;
    }

    @Override
    public void fetchSavedDiagnosisAndTreatmentForm(final @NonNull String baseEntityId, final @Nullable String entityTable, @NonNull final OpdRegisterActivityContract.InteractorCallBack interactorCallBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm = OpdLibrary
                        .getInstance()
                        .getOpdDiagnosisAndTreatmentFormRepository()
                        .findOne(new OpdDiagnosisAndTreatmentForm(baseEntityId));

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        interactorCallBack.onFetchedSavedDiagnosisAndTreatmentForm(diagnosisAndTreatmentForm, baseEntityId, entityTable);
                    }
                });
            }
        });
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack) {

    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        presenter = null;
    }

    @Override
    public void saveRegistration(List<OpdEventClient> opdEventClientList, String jsonString, RegisterParams registerParams, OpdRegisterActivityContract.InteractorCallBack callBack) {

    }

    @Override
    public void saveEvents(@NonNull final List<Event> events, @NonNull final OpdRegisterActivityContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (Event event : events) {
                    saveEventInDb(event);
                }

                processLatestUnprocessedEvents();

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
            CoreLibrary.getInstance()
                    .context()
                    .getEventClientRepository()
                    .addEvent(event.getBaseEntityId()
                            , new JSONObject(JsonFormUtils.gson.toJson(event)));
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void processLatestUnprocessedEvents() {
        // Process this event
        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);

        try {
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            getAllSharedPreferences().saveLastUpdatedAtDate(new Date().getTime());
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