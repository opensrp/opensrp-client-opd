package org.smartregister.opd.contract;

import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.listener.OnSendActionToFragment;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.view.contract.BaseProfileContract;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public interface OpdProfileActivityContract {

    interface Presenter extends BaseProfileContract.Presenter {

        @Nullable
        OpdProfileActivityContract.View getProfileView();

        void refreshProfileTopSection(@NonNull Map<String, String> client);

        void startForm(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient);

        void startFormActivity(@Nullable JSONObject form, @NonNull String caseId, @NonNull String entityTable);

        void saveVisitOrDiagnosisForm(String eventType, Intent data);

        HashMap<String, String> saveFinishForm(@NonNull Map<String, String> client);

        void onUpdateRegistrationBtnCLicked(@NonNull String baseEntityId);
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(@NonNull String fullName);

        void setProfileID(@NonNull String registerId);

        void setProfileAge(@NonNull String age);

        void setProfileGender(@NonNull String gender);

        void setProfileImage(@NonNull String baseEntityId);

        void openDiagnoseAndTreatForm();

        void openCheckInForm();

        void startFormActivity(JSONObject form, HashMap<String, String> intentKeys);

        OnSendActionToFragment getActionListenerForVisitFragment();

        OnSendActionToFragment getActionListenerForProfileOverview();

        @Nullable
        String getString(@StringRes int resId);


        @NonNull
        Context getContext();

        void startActivityForResult(@NonNull Intent intent, int requestCode);

    }

    interface Interactor {

        void fetchSavedDiagnosisAndTreatmentForm(@NonNull String baseEntityId, @NonNull String entityTable);

        void onDestroy(boolean isChangingConfiguration);
    }

    interface InteractorCallBack {

        void onRegistrationSaved(boolean isEdit);

        void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm, @NonNull String caseId, @NonNull String entityTable);

    }
}