package org.smartregister.opd.tasks;

import android.os.AsyncTask;

import org.smartregister.util.Utils;

import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class FetchProfileDataTask extends AsyncTask<String, Integer, Map<String, String>> {

    private boolean isForEdit;

    public FetchProfileDataTask(boolean isForEdit) {
        this.isForEdit = isForEdit;
    }

    protected Map<String, String> doInBackground(String... params) {
        /*String baseEntityId = params[0];
        return PatientRepository.getWomanProfileDetails(baseEntityId);*/
        return null;
    }

    protected void onPostExecute(Map<String, String> client) {
        //Utils.postStickyEvent(new ClientDetailsFetchedEvent(client, isForEdit));
    }
}