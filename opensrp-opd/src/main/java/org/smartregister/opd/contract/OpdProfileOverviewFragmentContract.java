package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojo.OpdCheckIn;
import org.smartregister.opd.pojo.OpdVisit;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

public interface OpdProfileOverviewFragmentContract {

    interface View {

    }

    interface Presenter {

        void loadOverviewFacts(@NonNull String baseEntityId, @NonNull OnFinishedCallback onFinishedCallback);

        void loadOverviewDataAndDisplay(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit, @NonNull final OnFinishedCallback onFinishedCallback);

        interface OnFinishedCallback {

            void onFinished(@Nullable Facts facts, @Nullable List<YamlConfigWrapper> yamlConfigListGlobal);
        }
    }

    interface Model {

        void fetchLastCheckAndVisit(@NonNull String baseEntityId, @NonNull OnFetchedCallback onFetchedCallback);

        interface OnFetchedCallback {

            void onFetched(@Nullable OpdCheckIn opdCheckIn, @Nullable OpdVisit opdVisit);
        }
    }
}
