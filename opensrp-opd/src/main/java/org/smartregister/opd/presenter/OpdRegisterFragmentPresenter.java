package org.smartregister.opd.presenter;

import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdRegisterFragmentContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

import org.apache.commons.lang3.StringUtils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.opd.pojos.QueryTable;
import org.smartregister.opd.pojos.InnerJoinObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class OpdRegisterFragmentPresenter implements OpdRegisterFragmentContract.Presenter {

    protected Set<View> visibleColumns = new TreeSet<>();
    private WeakReference<OpdRegisterFragmentContract.View> viewReference;
    private OpdRegisterFragmentContract.Model model;
    private RegisterConfiguration config;
    private String viewConfigurationIdentifier;

    public OpdRegisterFragmentPresenter(OpdRegisterFragmentContract.View view, OpdRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        QueryTable[] queryTables = new QueryTable[2];
        QueryTable childQueryTable = new QueryTable();
        childQueryTable.setTableName("ec_child");
        childQueryTable.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "gender",
                "home_address",
                "'Child' AS register_type"
        });

        queryTables[0] = childQueryTable;

        QueryTable womanQueryTable = new QueryTable();
        womanQueryTable.setTableName("ec_mother");
        womanQueryTable.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "'Female' AS gender",
                "home_address",
                "'ANC' AS register_type"
        });
        queryTables[1] = womanQueryTable;
        String countSelect = model.countSelect(queryTables);

        InnerJoinObject[] innerJoinObjects = new InnerJoinObject[1];
        InnerJoinObject childTableInnerJoinMotherTable = new InnerJoinObject();
        childTableInnerJoinMotherTable.setFirstTable(childQueryTable);

        QueryTable innerJoinMotherTable = new QueryTable();
        innerJoinMotherTable.setTableName("ec_mother");
        innerJoinMotherTable.setColNames(new String[]{
                "first_name AS mother_first_name",
                "last_name AS mother_last_name",
                "middle_name AS mother_middle_name"
        });

        childTableInnerJoinMotherTable.innerJoinOn("ec_child.relational_id = ec_mother.base_entity_id");
        childTableInnerJoinMotherTable.innerJoinTable(innerJoinMotherTable);
        innerJoinObjects[0] = childTableInnerJoinMotherTable;

        String mainSelect = model.mainSelect(innerJoinObjects, new QueryTable[]{innerJoinMotherTable});

        getView().initializeQueryParams("ec_child", countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    private void setVisibleColumns(Set<View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected OpdRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {

            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getMainCondition() {
        return String.format("1 = 1");
        //return String.format(" %s is null AND %s", DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter());
    }

    @Override
    public String getMainCondition(String tableName) {
        //return String.format(" %s is null AND %s", tableName + "." + DBConstants.KEY.DATE_REMOVED, ChildDBConstants.childAgeLimitFilter(tableName));
        return String.format("1 = 1");
    }

    @Override
    public String getDefaultSortQuery() {
        //return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";// AND "+ChildDBConstants.childAgeLimitFilter();
        return "last_interacted_with DESC";
    }

    @Override
    public String getDueFilterCondition() {
        //return getMainCondition() + " AND " + ChildDBConstants.childDueFilter();
        return getMainCondition();
    }

    public void setModel(OpdRegisterFragmentContract.Model model) {
        this.model = model;
    }

    @Override
    public String getWhereInQuery() {
        QueryTable[] tableCols = new QueryTable[2];
        QueryTable childTableCol = new QueryTable();
        childTableCol.setTableName("ec_child");
        childTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "gender",
                "dob",
                "home_address",
                "'Child' AS register_type",
                "relational_id AS relationalid"
        });

        tableCols[0] = childTableCol;

        QueryTable womanTableCol = new QueryTable();
        womanTableCol.setTableName("ec_mother");
        womanTableCol.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "'Female' AS gender",
                "dob",
                "home_address",
                "'ANC' AS register_type",
                "NULL AS mother_first_name",
                "NULL AS mother_last_name",
                "NULL AS mother_middle_name",
                "relationalid"
        });
        tableCols[1] = womanTableCol;

        InnerJoinObject[] tablesWithInnerJoins = new InnerJoinObject[1];
        InnerJoinObject tableColsInnerJoin = new InnerJoinObject();
        tableColsInnerJoin.setFirstTable(childTableCol);

        QueryTable innerJoinMotherTable = new QueryTable();
        innerJoinMotherTable.setTableName("ec_mother");
        innerJoinMotherTable.setColNames(new String[]{
                "first_name AS mother_first_name",
                "last_name AS mother_last_name",
                "middle_name AS mother_middle_name"
        });
        tableColsInnerJoin.innerJoinOn("ec_child.relational_id = ec_mother.base_entity_id");
        tableColsInnerJoin.innerJoinTable(innerJoinMotherTable);
        tablesWithInnerJoins[0] = tableColsInnerJoin;

        return model.mainSelectWhereIdsIn(tablesWithInnerJoins, new QueryTable[]{womanTableCol});
    }
}
