package org.smartregister.opd.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.interactor.OpdProfileInteractor;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileActivityPresenter implements OpdProfileActivityContract.Presenter, OpdProfileActivityContract.InteractorCallBack {

    private WeakReference<OpdProfileActivityContract.View> mProfileView;
    private OpdProfileActivityContract.Interactor mProfileInteractor;

    public OpdProfileActivityPresenter(OpdProfileActivityContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new OpdProfileInteractor(this);
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
}