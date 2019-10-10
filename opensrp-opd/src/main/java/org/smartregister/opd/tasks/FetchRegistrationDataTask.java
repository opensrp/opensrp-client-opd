package org.smartregister.opd.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.smartregister.CoreLibrary;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdReverseJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;

public class FetchRegistrationDataTask extends AsyncTask<String, Integer, String> {

    private WeakReference<Activity> context;

    public FetchRegistrationDataTask(WeakReference<Activity> context){
        this.context =  context;
    }

    protected String doInBackground(String... params) {
        Map<String, String> detailsMap = CoreLibrary.getInstance().context().detailsRepository().getAllDetailsForClient(params[0]);
        return OpdReverseJsonFormUtils.prepareJsonOpdEditForm(detailsMap, Arrays.asList(OpdConstants.KEY.ZEIR_ID, OpdConstants.JSON_FORM_KEY.BHT_ID), context.get());
    }

    protected void onPostExecute(String client) {
        Intent intent = new Intent(context.get(), OpdUtils.metadata().getOpdFormActivity());
        Form formParam = new Form();
        formParam.setWizard(false);
        formParam.setHideSaveLabel(true);
        formParam.setNextLabel("");
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, client);
        context.get().startActivityForResult(intent, OpdJsonFormUtils.REQUEST_CODE_GET_JSON);
    }
}
