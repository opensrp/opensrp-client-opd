package org.smartregister.opd.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdRegisterProviderMetadata;
import org.smartregister.opd.holders.FooterViewHolder;
import org.smartregister.opd.utils.ConfigurationInstancesHelper;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.opd.R;
import org.smartregister.opd.holders.OpdRegisterViewHolder;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterProvider implements RecyclerViewProvider<OpdRegisterViewHolder> {
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    private OpdRegisterProviderMetadata opdRegisterProviderMetadata;

    public OpdRegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;

        // Get the configuration
        this.opdRegisterProviderMetadata = ConfigurationInstancesHelper.newInstance(OpdLibrary.getInstance()
                .getOpdConfiguration()
                .getOpdRegisterProviderMetadata());
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public OpdRegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.opd_register_list_row, parent, false);

        /*
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {

            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }*/

        return new OpdRegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {

        Map<String, String> patientColumnMaps = pc.getColumnmaps();

        if (opdRegisterProviderMetadata.isClientHaveGuardianDetails(patientColumnMaps)) {
            viewHolder.showCareGiverName();

            String parentFirstName = opdRegisterProviderMetadata.getGuardianFirstName(patientColumnMaps);
            String parentLastName = opdRegisterProviderMetadata.getGuardianLastName(patientColumnMaps);
            String parentMiddleName = opdRegisterProviderMetadata.getGuardianMiddleName(patientColumnMaps);

            String parentName = context.getResources().getString(R.string.care_giver_initials)
                    + ": "
                    + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
            fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
        } else {
            viewHolder.removeCareGiverName();
        }

        String firstName = opdRegisterProviderMetadata.getClientFirstName(patientColumnMaps);
        String middleName = opdRegisterProviderMetadata.getClientMiddleName(patientColumnMaps);
        String lastName = opdRegisterProviderMetadata.getClientLastName(patientColumnMaps);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(opdRegisterProviderMetadata.getDob(patientColumnMaps));
        //dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " + WordUtils.capitalize(OpdUtils.getTranslatedDate(dobString, context)));
        String registerType = opdRegisterProviderMetadata.getRegisterType(patientColumnMaps);

        if (registerType != null) {
            viewHolder.showRegisterType();
            fillValue(viewHolder.tvRegisterType, registerType);
        } else {
            viewHolder.hideRegisterType();
        }

        setAddressAndGender(pc, viewHolder);
        addButtonClickListeners(client, viewHolder);
    }

    public void populateIdentifierColumn(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        //fillValue(viewHolder.ancId, String.format(context.getString(R.string.unique_id_text), uniqueId));
    }

    public static void fillValue(@Nullable TextView v, @NonNull String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    public void setAddressAndGender(CommonPersonObjectClient pc, OpdRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = pc.getColumnmaps();
        String address = opdRegisterProviderMetadata.getHomeAddress(patientColumnMaps);
        String gender = opdRegisterProviderMetadata.getGender(patientColumnMaps);

        fillValue(viewHolder.textViewGender, gender);

        if (TextUtils.isEmpty(address)) {
            viewHolder.removePersonLocation();
        } else {
            viewHolder.showPersonLocation();
            fillValue(viewHolder.tvLocation, address);
        }
    }

    public void addButtonClickListeners(SmartRegisterClient client, OpdRegisterViewHolder viewHolder) {
        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(patient, client);
    }

    public void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        //view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL);
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View.OnClickListener getPaginationClickListener() {
        return paginationClickListener;
    }

    public void setPaginationClickListener(View.OnClickListener paginationClickListener) {
        this.paginationClickListener = paginationClickListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}