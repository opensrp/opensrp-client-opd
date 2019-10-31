package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.OpdVisitSummary;

import java.util.List;


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

        void loadVisits(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        interface OnFinishedCallback {

            void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries);
        }
    }

    interface View {


    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);

        void fetchVisits(@NonNull String baseEntityId, @NonNull Presenter.OnFinishedCallback onFinishedCallback);
    }
}