package org.smartregister.opd.contract;


import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.opd.pojos.QueryTable;
import org.smartregister.opd.pojos.InnerJoinObject;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */


public interface OpdRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        String getMainCondition();

        String getMainCondition(String tableName);

        String getDefaultSortQuery();

        String getDueFilterCondition();

        String getWhereInQuery();

    }

    interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(@NonNull QueryTable[] queryTables);

        String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols);

        String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols);

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        JSONArray getJsonArray(Response<String> response);

    }


}
