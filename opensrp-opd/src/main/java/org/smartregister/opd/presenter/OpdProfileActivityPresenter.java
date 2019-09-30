package org.smartregister.opd.presenter;



import android.content.Intent;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.interactor.ProfileInteractor;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileActivityPresenter implements OpdProfileActivityContract.Presenter, OpdProfileActivityContract.InteractorCallBack {
    private static final String TAG = OpdProfileActivityPresenter.class.getCanonicalName();

    private WeakReference<OpdProfileActivityContract.View> mProfileView;
    private OpdProfileActivityContract.Interactor mProfileInteractor;

    public OpdProfileActivityPresenter(OpdProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new ProfileInteractor(this);
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
            mProfileInteractor = null;
        }

    }

    @Override
    public void fetchProfileData(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, true);
    }

    @Override
    public void refreshProfileView(String baseEntityId) {
        mProfileInteractor.refreshProfileView(baseEntityId, false);
    }

    @Override
    public OpdProfileActivityContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        /*try {
            String jsonString = data.getStringExtra(Constants.INTENT_KEY.JSON);
            Log.d("JSONResult", jsonString);
            JSONObject form = new JSONObject(jsonString);
            getProfileView().showProgressDialog(
                    form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.CLOSE) ?
                            R.string.removing_dialog_title : R.string.saving_dialog_title);

            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.UPDATE_REGISTRATION)) {
                Pair<Client, Event> values = JsonFormUtils.processRegistrationForm(allSharedPreferences, jsonString);
                mProfileInteractor.saveRegistration(values, jsonString, true, this);
            } else if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.CLOSE)) {
                mProfileInteractor.removeWomanFromANCRegister(jsonString, allSharedPreferences.fetchRegisteredANM());
            } else {
                getProfileView().hideProgressDialog();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }*/
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //Overriden
    }


    @Override
    public void onNoUniqueId() {
        getProfileView().displayToast(R.string.no_openmrs_id);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        this.refreshProfileView(getProfileView().getIntentString(OpdConstants.IntentKey.BASE_ENTITY_ID));
        getProfileView().hideProgressDialog();
        //getProfileView().displayToast(isEdit ? R.string.registration_info_updated : R.string.new_registration_saved);
    }

    @Override
    public void refreshProfileTopSection(@NonNull Map<String, String> client) {
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

    @Override
    public HashMap<String, String> saveFinishForm(@NonNull Map<String, String> client) {
        //return contactInteractor.finalizeContactForm(client);
        return null;
    }
}