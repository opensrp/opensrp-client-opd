package org.smartregister.opd.utils;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class OpdLookUpUtils {

    public static void lookUp(@NonNull final Context context, @NonNull  final Map<String, String> entityLookUp, @NonNull  final Listener<List<CommonPersonObject>> listener) {
        org.smartregister.util.Utils
                .startAsyncTask(new AsyncTask<Void, Void, List<CommonPersonObject>>() {
                    @Override
                    protected List<CommonPersonObject> doInBackground(Void... params) {
                        publishProgress();
                        return clientLookUp(context, entityLookUp);
                    }

                    @Override
                    protected void onPostExecute(List<CommonPersonObject> result) {
                        listener.onEvent(result);
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        //show progress bar
                    }
                }, null);
    }

    private static List<CommonPersonObject> clientLookUp(@Nullable Context context,@NonNull Map<String, String> entityLookUp) {
        List<CommonPersonObject> results = new ArrayList<>();
        if (context == null) {
            return results;
        }


        if (entityLookUp.isEmpty()) {
            return results;
        }

        String tableName = OpdUtils.metadata().getTableName();

        CommonRepository commonRepository = context.commonrepository(tableName);
        String query = lookUpQuery(entityLookUp, tableName);

        Cursor cursor = null;
        try {

            cursor = commonRepository.rawCustomQueryForAdapter(query);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    CommonPersonObject commonPersonObject = commonRepository.readAllcommonforCursorAdapter(cursor);
                    results.add(commonPersonObject);
                    cursor.moveToNext();
                }
            }


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return results;
    }

    private static String lookUpQuery(@NonNull Map<String, String> entityMap,@NonNull String tableName) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName,
                new String[]{OpdConstants.KEY.RELATIONALID, OpdConstants.KEY.OPENSRP_ID, OpdConstants.KEY.FIRST_NAME, OpdConstants.KEY.LAST_NAME,
                        AllConstants.ChildRegistrationFields.GENDER, OpdConstants.KEY.DOB,
                        OpdConstants.KEY.BASE_ENTITY_ID}

        );
        String query = queryBUilder.mainCondition(getMainConditionString(entityMap));
        return queryBUilder.Endquery(query);
    }

    private static String getMainConditionString(@NonNull Map<String, String> entityMap) {
        String mainConditionString = "";
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //first name, last name, bht id, national id
            String firstName = "first_name";
            String lastName = "last_name";
            String bht_id = "bht_mid";
            String national_id = "national_id";
            if (StringUtils.containsIgnoreCase(key, firstName)) {
                key = firstName;
            }

            if (StringUtils.containsIgnoreCase(key, lastName)) {
                key = lastName;
            }

            if (StringUtils.containsIgnoreCase(key, bht_id)) {
                key = bht_id;
            }

            if (StringUtils.containsIgnoreCase(key, national_id)) {
                key = national_id;
            }

            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + key + " Like '%" + value + "%'";
            } else {
                mainConditionString += " AND " + key + " Like '%" + value + "%'";

            }
        }

        return mainConditionString;
    }
}