package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
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

        void fetchProfileData(@NonNull String baseEntityId);

        void refreshProfileView(@NonNull String baseEntityId);

        void refreshProfileTopSection(@NonNull Map<String, String> client);

        HashMap<String, String> saveFinishForm(@NonNull Map<String, String> client);
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(@NonNull String fullName);

        void setProfileID(@NonNull String registerId);

        void setProfileAge(@NonNull String age);

        void setProfileGender(@NonNull String gender);

        void setProfileImage(@NonNull String baseEntityId);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);
    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEdit);
    }
}