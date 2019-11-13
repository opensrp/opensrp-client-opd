package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.OpdVisitSummary;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 *
 */
public interface OpdProfileVisitsFragmentContract {

    interface Presenter {

        @Nullable
        OpdProfileVisitsFragmentContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);

        void loadVisits(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void populateWrapperDataAndFacts(@NonNull List<OpdVisitSummary> opdVisitSummaries, ArrayList<Pair<YamlConfigWrapper, Facts>> items);

        interface OnFinishedCallback {

            void onFinished(@NonNull List<OpdVisitSummary> opdVisitSummaries, ArrayList<Pair<YamlConfigWrapper, Facts>> items);
        }

        interface OnVisitsLoadedCallback {

            void onVisitsLoaded(@NonNull List<OpdVisitSummary> opdVisitSummaries);
        }
    }

    interface View {

        String getString(@StringRes int resId);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(@NonNull String baseEntityId, boolean isForEdit);

        void fetchVisits(@NonNull String baseEntityId, @NonNull Presenter.OnVisitsLoadedCallback onVisitsLoadedCallback);
    }
}