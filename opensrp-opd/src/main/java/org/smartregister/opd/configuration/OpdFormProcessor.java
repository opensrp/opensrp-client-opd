package org.smartregister.opd.configuration;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public interface OpdFormProcessor<T> {
    T processForm(JSONObject jsonObject, Intent data) throws JSONException;
}
