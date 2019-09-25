package org.smartregister.opd.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.event.Listener;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.adapter.ClientLookUpListAdapter;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;
import org.smartregister.opd.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Utils.getValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpdFormFragment extends JsonFormFragment implements ClientLookUpListAdapter.ClickListener {

    private boolean lookedUp = false;
    private Snackbar snackbar = null;
    private AlertDialog alertDialog = null;
    private ClientLookUpListAdapter clientLookUpListAdapter;

    public static OpdFormFragment getFormFragment(String stepName) {
        OpdFormFragment jsonFormFragment = new OpdFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected OpdFormFragmentPresenter createPresenter() {
        return new OpdFormFragmentPresenter(this, OpdFormInteractor.getOpdInteractorInstance());
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        super.updateVisibilityOfNextAndSave(next, save);
        Form form = getForm();
        if (form != null && form.isWizard() &&
                !OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().isFormWizardValidateRequiredFieldsBefore()) {
            this.getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }
    }

    private Form getForm() {
        return this.getActivity() != null && this.getActivity() instanceof JsonFormActivity ?
                ((JsonFormActivity) this.getActivity()).getForm() : null;
    }

    private final Listener<List<CommonPersonObject>> lookUpListener =
            new Listener<List<CommonPersonObject>>() {
                @Override
                public void onEvent(List<CommonPersonObject> data) {
                    if (!lookedUp) {
                        showClientLookUp(data);
                    }
                }
            };

    private void showClientLookUp(List<CommonPersonObject> data) {
        if (!data.isEmpty()) {
            showInfoSnackBar(data);
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void showInfoSnackBar(final List<CommonPersonObject> data) {
        snackbar = Snackbar.make(getMainView(), getActivity().getString(R.string.client_matches, data.size()),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.tap_to_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResultDialog(data);
            }
        });
        show(snackbar, 30000);
    }

    private void updateResultDialog(final List<CommonPersonObject> data) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.lookup_results, null);
        RecyclerView recyclerView = view.findViewById(R.id.lookup_recyclerview);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);
        alertDialog = builder.create();
        //
        clientLookUpListAdapter = new ClientLookUpListAdapter(data, getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(clientLookUpListAdapter);
        clientLookUpListAdapter.notifyDataSetChanged();
        clientLookUpListAdapter.setOnClickListener(this);
        //
        alertDialog.show();
    }

    private void lookupDialogDismissed(CommonPersonObjectClient client) {
        if (client != null) {

            Map<String, List<View>> lookupMap = getLookUpMap();
            if (lookupMap.containsKey(Constants.CLIENT_TYPE)) {
                List<View> lookUpViews = lookupMap.get(Constants.CLIENT_TYPE);
                if (lookUpViews != null && !lookUpViews.isEmpty()) {

                    for (View view : lookUpViews) {

                        String key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
                        String text = "";

                        if (StringUtils.containsIgnoreCase(key, Constants.KEY.FIRST_NAME)) {
                            text = getValue(client.getColumnmaps(), Constants.KEY.FIRST_NAME, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, Constants.KEY.LAST_NAME)) {
                            text = getValue(client.getColumnmaps(), Constants.KEY.LAST_NAME, true);
                        }

                        if (view instanceof MaterialEditText) {
                            MaterialEditText materialEditText = (MaterialEditText) view;
                            materialEditText.setEnabled(false);
                            materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, true);
                            materialEditText.setText(text);
                            materialEditText.setInputType(InputType.TYPE_NULL);
                            toggleEditText(materialEditText, false);
                        }
                    }

                    Map<String, String> metadataMap = new HashMap<>();
                    metadataMap.put(Constants.KEY.ENTITY_ID, Constants.CLIENT_TYPE);
                    metadataMap
                            .put(Constants.KEY.VALUE, getValue(client.getColumnmaps(), Constants.KEY.BASE_ENTITY_ID, false));

                    writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                    lookedUp = true;
                    clearView();
                }
            }
        }
    }

    private void clearView() {
        snackbar = Snackbar.make(getMainView(), R.string.undo_lookup, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                clearMotherLookUp();
            }
        });
        show(snackbar, 30000);
    }

    private void clearMotherLookUp() {
        Map<String, List<View>> lookupMap = getLookUpMap();
        if (lookupMap.containsKey(Constants.CLIENT_TYPE)) {
            List<View> lookUpViews = lookupMap.get(Constants.CLIENT_TYPE);
            if (lookUpViews != null && !lookUpViews.isEmpty()) {
                for (View view : lookUpViews) {
                    if (view instanceof MaterialEditText) {
                        MaterialEditText materialEditText = (MaterialEditText) view;
                        materialEditText.setEnabled(true);
                        toggleEditText(materialEditText, true);
                        materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
                        materialEditText.setText("");
                    }
                }

                Map<String, String> metadataMap = new HashMap<>();
                metadataMap.put(Constants.KEY.ENTITY_ID, "");
                metadataMap.put(Constants.KEY.VALUE, "");

                writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                lookedUp = false;
            }
        }
    }

    private void toggleEditText(MaterialEditText materialEditText, boolean status) {
        materialEditText.setEnabled(status);
    }

    private void show(final Snackbar snackbar, int duration) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.accent);

        final Button actionView = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.white));

        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });
//        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);

    }
    public Listener<List<CommonPersonObject>> lookUpListener() {
        return lookUpListener;
    }

    public void validateActivateNext() {
        if (!isVisible()) { //form fragment is initializing or not the last page
            return;
        }

        Form form = getForm();
        if (form == null || !form.isWizard()) {
            return;
        }

        ValidationStatus validationStatus = null;
        for (View dataView : getJsonApi().getFormDataViews()) {
            validationStatus = getPresenter().validate(this, dataView, false);
            if (!validationStatus.isValid()) {
                break;
            }
        }

        if (validationStatus != null && validationStatus.isValid()) {
            if (!getPresenter().intermediatePage()) {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(true);
            }
        } else {
            if (!getPresenter().intermediatePage()) {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
            }
        }
    }

    public OpdFormFragmentPresenter getPresenter() {
        return (OpdFormFragmentPresenter) presenter;
    }

    @Override
    public void onItemClick(View view) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }

            if (client != null) {
                lookupDialogDismissed(client);
            }
        }
    }

}
