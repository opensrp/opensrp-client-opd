package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.pojos.InnerJoinObject;
import org.smartregister.opd.pojos.QueryTable;
import org.smartregister.opd.utils.OpdRegisterQueryBuilder;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-23
 */

public abstract class OpdRegisterQueryProviderContract {

    @NonNull
    public abstract String getObjectIdsQuery(@Nullable String filters);

    @NonNull
    public abstract String[] countExecuteQueries(@Nullable String filters);

    @NonNull
    public abstract String mainSelectWhereIDsIn();

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
}
