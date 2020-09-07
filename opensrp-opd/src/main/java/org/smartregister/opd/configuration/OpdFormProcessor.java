package org.smartregister.opd.configuration;

import android.content.Intent;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public interface OpdFormProcessor<T> {
    T processForm(@NonNull JSONObject jsonObject, @NonNull Intent data) throws JSONException;
}
