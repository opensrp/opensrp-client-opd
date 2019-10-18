package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 *
 */
public interface OpdProfileFragmentContract {

    interface Presenter {

        @Nullable
        OpdProfileFragmentContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);
    }

    interface View {


    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);
    }
}