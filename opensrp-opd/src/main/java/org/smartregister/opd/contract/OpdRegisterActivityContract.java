package org.smartregister.opd.contract;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.pojos.RegisterParams;
import org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public interface OpdRegisterActivityContract {

    interface View extends BaseRegisterContract.View {
        OpdRegisterActivityContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void saveForm(String jsonString, RegisterParams registerParams);

        void startForm(String formName, String entityId, String metaData, String locationId);
    }

    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        List<OpdEventClient> processRegistration(String jsonString, FormTag formTag);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws Exception;

        String getInitials();

    }

    interface Interactor {
        void getNextUniqueId(Triple<String, String, String> triple, OpdRegisterActivityContract.InteractorCallBack callBack);

        void onDestroy(boolean isChangingConfiguration);

        void saveRegistration(List<OpdEventClient> opdEventClientList, String jsonString, RegisterParams registerParams, BaseOpdRegisterActivityPresenter opdRegisterActivityPresenter);
    }

    interface InteractorCallBack {

        void onNoUniqueId();

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);
    }
}