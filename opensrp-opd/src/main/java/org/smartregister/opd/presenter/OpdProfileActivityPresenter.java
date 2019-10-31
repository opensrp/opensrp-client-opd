package org.smartregister.opd.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.interactor.OpdProfileInteractor;
import org.smartregister.opd.listener.OpdEventActionCallBack;
import org.smartregister.opd.model.OpdProfileActivityModel;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.utils.AppExecutors;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.tasks.FetchRegistrationDataTask;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdEventUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileActivityPresenter implements OpdProfileActivityContract.Presenter, OpdProfileActivityContract.InteractorCallBack, OpdEventActionCallBack {

    private WeakReference<OpdProfileActivityContract.View> mProfileView;
    private OpdProfileActivityContract.Interactor mProfileInteractor;

    private OpdProfileActivityModel model;
    private JSONObject form = null;

    public OpdProfileActivityPresenter(OpdProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileInteractor(this);
        model = new OpdProfileActivityModel();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Nullable
    @Override
    public OpdProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        }

        return null;
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getProfileView() != null) {
            getProfileView().hideProgressDialog();
        }
    }

    @Override
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable) {
        try {
            if (diagnosisAndTreatmentForm != null) {
                form = new JSONObject(diagnosisAndTreatmentForm.getForm());
            }

            startFormActivity(form, caseId, entityTable);
        } catch (JSONException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable) {
        if (getProfileView() != null && form != null) {
            HashMap<String, String> intentKeys = new HashMap<>();
            intentKeys.put(OpdConstants.IntentKey.BASE_ENTITY_ID, caseId);
            intentKeys.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);
            (mProfileView.get()).startFormActivity(form, intentKeys);
        }
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
        OpdProfileActivityContract.View profileView = getProfileView();
        if (profileView != null) {
            profileView.setProfileName(client.get(OpdDbConstants.KEY.FIRST_NAME) + " " + client.get(OpdDbConstants.KEY.LAST_NAME));
                String translatedYearInitial = profileView.getString(R.string.abbrv_years);
            String dobString = client.get(OpdConstants.KEY.DOB);

            if (dobString != null) {
                String clientAge = OpdUtils.getClientAge(Utils.getDuration(dobString), translatedYearInitial);
                profileView.setProfileAge(clientAge);
            }

            try {
                profileView.setProfileGender(Utils.getValue(client, OpdDbConstants.KEY.GENDER, true));
            } catch (Exception e) {
                Timber.e(e);
                profileView.setProfileGender("");
            }

            profileView.setProfileID(Utils.getValue(client, OpdDbConstants.KEY.REGISTER_ID, false));
            profileView.setProfileImage(Utils.getValue(client, OpdDbConstants.KEY.ID, false));
        }
    }

    @Override
    public void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();
        injectedValues.put("patient_gender", clientMap.get("gender"));
        String entityTable = clientMap.get(OpdConstants.IntentKey.ENTITY_TABLE);

        startFormActivity(formName, commonPersonObjectClient.getCaseId(), entityTable, injectedValues);
    }

    public void startFormActivity(String formName, String caseId, String entityTable, HashMap<String, String> injectedValues) {
        if (mProfileView != null) {
            form = null;
            try {
                String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                form = model.getFormAsJson(formName, caseId, locationId, injectedValues);

                // Fetch saved form & continue editing
                if (formName.equals(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
                    mProfileInteractor.fetchSavedDiagnosisAndTreatmentForm(caseId, entityTable);
                } else {
                    startFormActivity(form, caseId, entityTable);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void saveVisitOrDiagnosisForm(String eventType, Intent data) {
        String jsonString = null;
        OpdEventUtils opdEventUtils = new OpdEventUtils(new AppExecutors());
        if (data != null) {
            jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
        }

        if (jsonString == null) {
            return;
        }

        if (eventType.equals(OpdConstants.EventType.CHECK_IN)) {
            try {
                Event opdVisitEvent = OpdLibrary.getInstance().processOpdCheckInForm(eventType, jsonString, data);
                opdEventUtils.saveEvents(Collections.singletonList(opdVisitEvent), this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        } else if (eventType.equals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT)) {
            try {
                List<Event> opdDiagnosisAndTreatment = OpdLibrary.getInstance().processOpdDiagnosisAndTreatmentForm(jsonString, data);
                opdEventUtils.saveEvents(opdDiagnosisAndTreatment, this);
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public HashMap<String, String> saveFinishForm(@NonNull Map<String, String> client) {
        return null;
    }

    @Override
    public void onOpdEventSaved() {
        if (mProfileView != null) {
            OpdProfileActivityContract.View view = mProfileView.get();

            view.getActionListenerForProfileOverview().onActionReceive();
            view.getActionListenerForVisitFragment().onActionReceive();
            view.hideProgressDialog();
        }
    }

    @Override
    public void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId) {
        Utils.startAsyncTask(new FetchRegistrationDataTask(new WeakReference<Context>(getProfileView().getContext()), new FetchRegistrationDataTask.OnTaskComplete() {
            @Override
            public void onSuccess(@Nullable String client) {
                Context context = getProfileView().getContext();

                OpdMetadata metadata = OpdUtils.metadata();
                if (metadata != null) {
                    Intent intent = new Intent(context, metadata.getOpdFormActivity());
                    Form formParam = new Form();
                    formParam.setWizard(false);
                    formParam.setHideSaveLabel(true);
                    formParam.setNextLabel("");
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
                    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, client);
                    getProfileView().startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
                }
            }
        }), new String[]{baseEntityId});
    }
}