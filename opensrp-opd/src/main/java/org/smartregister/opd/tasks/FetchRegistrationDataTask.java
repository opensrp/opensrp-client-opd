package org.smartregister.opd.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.CoreLibrary;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdReverseJsonFormUtils;
import org.smartregister.util.FormUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

import timber.log.Timber;

public class FetchRegistrationDataTask extends AsyncTask<String, Void, String> {

    private WeakReference<OpdProfileActivityContract.View> viewWeakReference;
    private OnTaskComplete onTaskComplete;

    public FetchRegistrationDataTask(@NonNull WeakReference<OpdProfileActivityContract.View> viewWeakReference, @NonNull OnTaskComplete onTaskComplete) {
        this.viewWeakReference = viewWeakReference;

        this.onTaskComplete = onTaskComplete;
    }

    @Nullable
    protected String doInBackground(String... params) {
        Map<String, String> detailsMap = CoreLibrary.getInstance().context().detailsRepository().getAllDetailsForClient(params[0]);

        OpdProfileActivityContract.View view = viewWeakReference.get();

        if (view == null || view.getClient() == null || view.getClient().getDetails() == null || view.getClient().getDetails().get(OpdDbConstants.KEY.REGISTER_ID) == null) {
            return null;
        }

        detailsMap.put(OpdJsonFormUtils.OPENSRP_ID, view.getClient().getDetails().get(OpdDbConstants.KEY.REGISTER_ID));

        FormUtils formUtils = null;
        try {
            formUtils = new FormUtils(view.getContext());
            return OpdReverseJsonFormUtils.prepareJsonEditOpdRegistrationForm(detailsMap, Arrays.asList(OpdJsonFormUtils.OPENSRP_ID, OpdConstants.JSON_FORM_KEY.BHT_ID), formUtils);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    protected void onPostExecute(@Nullable String jsonForm) {
        onTaskComplete.onSuccess(jsonForm);
    }

    public interface OnTaskComplete {

        void onSuccess(@Nullable String jsonForm);
    }
}