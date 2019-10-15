package org.smartregister.opd.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.text.WordUtils;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class ContactJsonFormUtils extends FormUtils {

    @NonNull
    public static String keyToValueConverter(String keys) {
        if (keys != null) {
            String cleanKey = WordUtils.capitalizeFully(cleanValue(keys), ',');
            if (!TextUtils.isEmpty(keys)) {
                return cleanKey.replaceAll("_", " ");
            } else {
                return cleanKey;
            }
        } else {
            return "";
        }
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }
}