package org.smartregister.opd.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdEventUtils;
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

        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public void fetchProfileData(@NonNull String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, true);
    }

    @Override
    public void refreshProfileView(@NonNull String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, false);
    }

    @Nullable
    @Override
    public OpdProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //Overriden
    }

    @Override
    public void onNoUniqueId() {
        if (getProfileView() != null) {
            getProfileView().displayToast(R.string.no_openmrs_id);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        if (getProfileView() != null) {
            this.refreshProfileView(getProfileView().getIntentString(OpdConstants.IntentKey.BASE_ENTITY_ID));
            getProfileView().hideProgressDialog();
        }
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
        if (getProfileView() != null) {
            getProfileView()
                    .setProfileName(client.get(OpdDbConstants.KEY.FIRST_NAME) + " " + client.get(OpdDbConstants.KEY.LAST_NAME));
            getProfileView().setProfileAge(String.valueOf(Utils.getDuration(client.get(OpdDbConstants.KEY.DOB))));

            try {
                getProfileView().setProfileGender(Utils.getValue(client, OpdDbConstants.KEY.GENDER, true));
            } catch (Exception e) {
                Timber.e(e);
                getProfileView().setProfileGender("");
            }
            getProfileView().setProfileID(Utils.getValue(client, OpdDbConstants.KEY.REGISTER_ID, false));
            getProfileView().setProfileImage(Utils.getValue(client, OpdDbConstants.KEY.BASE_ENTITY_ID, false));
        }
    }

    @Nullable
    @Override
    public HashMap<String, String> saveFinishForm(@NonNull Map<String, String> client) {
        return null;
    }

    @Override
    public void startForm(String formName, CommonPersonObjectClient commonPersonObjectClient) {
        Map<String, String> clientMap = commonPersonObjectClient.getColumnmaps();
        HashMap<String, String> injectedValues = new HashMap<>();
        injectedValues.put("patient_gender", clientMap.get("gender"));
        String entityTable = clientMap.get(OpdConstants.IntentKey.ENTITY_TABLE);
        startFormActivity(formName, commonPersonObjectClient.getCaseId(), entityTable, injectedValues);
    }

    public void startFormActivity(String formName, String caseId, String entityTable, HashMap<String, String> injectedValues) {
        if (mProfileView != null) {
            JSONObject form = null;
            try {
                String locationId = OpdUtils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                form = model.getFormAsJson(formName, caseId, locationId, injectedValues);
                if (formName.equals(OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT)) {
                    OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(caseId);
                    if (OpdLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().findOne(opdDiagnosisAndTreatmentForm) != null) {
                        form = new JSONObject(OpdLibrary.getInstance().getOpdDiagnosisAndTreatmentFormRepository().findOne(opdDiagnosisAndTreatmentForm).getForm());
                    }
                }
            } catch (JSONException e) {
                Timber.e(e);
            }

            if (getProfileView() != null && form != null) {
                HashMap<String, String> intentKeys = new HashMap<>();
                intentKeys.put(OpdConstants.IntentKey.BASE_ENTITY_ID, caseId);
                intentKeys.put(OpdConstants.IntentKey.ENTITY_TABLE, entityTable);
                (mProfileView.get()).startFormActivity(form, intentKeys);
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
    public void onOpdEventSaved() {
        if (mProfileView != null) {
            mProfileView.get().getActionListenerForProfileOverview().onActionReceive();
            mProfileView.get().getActionListenerForVisitFragment().onActionReceive();
            mProfileView.get().hideProgressDialog();
        }
    }
}