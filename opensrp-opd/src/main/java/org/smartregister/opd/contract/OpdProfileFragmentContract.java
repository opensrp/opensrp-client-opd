package org.smartregister.opd.contract;

import android.support.annotation.NonNull;

import org.jeasy.rules.api.Facts;

import java.util.Map;

/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 *
 */
public interface OpdProfileFragmentContract {

    interface Presenter {

        @NonNull
        OpdProfileFragmentContract.View getProfileView();

        @NonNull
        Facts getImmediatePreviousContact(@NonNull Map<String, String> client, @NonNull String baseEntityId, @NonNull String contactNo);
    }

    interface View {


    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);
    }
}