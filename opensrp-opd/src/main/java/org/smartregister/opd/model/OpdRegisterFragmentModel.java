package org.smartregister.opd.model;



import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterFragmentContract;
import org.smartregister.opd.pojos.QueryTable;
import org.smartregister.opd.pojos.InnerJoinObject;
import org.smartregister.opd.utils.ConfigHelper;
import org.smartregister.opd.utils.OpdRegisterQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterFragmentModel implements OpdRegisterFragmentContract.Model {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(OpdLibrary.getInstance().context().applicationContext());
    }

    @Override
    public ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
    }

    @Override
    public Set<View> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier);
    }

    @Override
    public String countSelect(@NonNull QueryTable[] queryTables) {
        StringBuilder query = new StringBuilder("SELECT count(rs.sub_count) FROM (");

        for (int i = 0; i < queryTables.length; i++) {
            QueryTable tableCol = queryTables[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTableCounts(tableCol.getTableName());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        query.append(") AS rs");

        return query.toString();
    }

    @Override
    public String mainSelect(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }


        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }



    @Override
    public String getFilterText(List<Field> list, String filterTitle) {
        List<Field> filterList = list;
        if (filterList == null) {
            filterList = new ArrayList<>();
        }

        String filter = filterTitle;
        if (filter == null) {
            filter = "";
        }
        return "<font color=#727272>" + filter + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
    }

    @Override
    public String getSortText(Field sortField) {
        String sortText = "";
        if (sortField != null) {
            if (StringUtils.isNotBlank(sortField.getDisplayName())) {
                sortText = "(Sort: " + sortField.getDisplayName() + ")";
            } else if (StringUtils.isNotBlank(sortField.getDbAlias())) {
                sortText = "(Sort: " + sortField.getDbAlias() + ")";
            }
        }
        return sortText;
    }

    @Override
    public JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public String mainSelectWhereIdsIn(@NonNull InnerJoinObject[] tableColsInnerJoins, @NonNull QueryTable[] tableCols) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < tableColsInnerJoins.length; i++) {
            InnerJoinObject tableColInnerJoin = tableColsInnerJoins[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableColInnerJoin);
            countQueryBuilder.mainCondition(tableColInnerJoin.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableColInnerJoin.getFirstTable().getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }


        for (int i = 0; i < tableCols.length; i++) {
            QueryTable tableCol = tableCols[i];

            OpdRegisterQueryBuilder countQueryBuilder = new OpdRegisterQueryBuilder();
            countQueryBuilder.SelectInitiateMainTable(tableCol.getTableName(), tableCol.getColNames());
            countQueryBuilder.mainCondition(tableCol.getMainCondition());

            String idCol = "_id";
            if (countQueryBuilder.getSelectquery().contains("JOIN")) {
                idCol = tableCol.getTableName() + ".id";
            }

            if (countQueryBuilder.getSelectquery().contains("WHERE")) {
                countQueryBuilder.addCondition(" AND ");

            } else {
                countQueryBuilder.addCondition(" WHERE ");
            }
            countQueryBuilder.addCondition("%s IN (%s)");
            countQueryBuilder.setSelectquery(countQueryBuilder.getSelectquery().replaceFirst("%s", idCol));

            if (query.length() != 0 || i != 0) {
                query.append(" UNION ALL ");
            }

            query.append(countQueryBuilder.getSelectquery());
        }

        return query.toString();
    }
}