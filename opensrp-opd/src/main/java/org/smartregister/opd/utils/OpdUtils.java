package org.smartregister.opd.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.pojos.OpdMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdUtils extends org.smartregister.util.Utils {

    private static final String OTHER_SUFFIX = ", other]";

    public static String getTranslatedDate(String str_date, android.content.Context context) {
        return str_date
                .replace("d", context.getString(R.string.abbrv_days))
                .replace("w", context.getString(R.string.abbrv_weeks))
                .replace("m", context.getString(R.string.abbrv_months))
                .replace("y", context.getString(R.string.abbrv_years));
    }

    public static float convertDpToPixel(float dp, @NonNull Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String fillTemplate(String stringValue, Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = processValue(key, facts);
            stringValueResult = stringValueResult.replace("{" + key + "}", value).replaceAll(", $", "").trim();
        }
        //Remove unnecessary commas by cleaning the returned string
        return cleanValueResult(stringValueResult);
    }

    private static String processValue(String key, Facts facts) {
        String value = "";
        if (facts.get(key) instanceof String) {
            value = facts.get(key);
            if (value != null && value.endsWith(OTHER_SUFFIX)) {
                Object otherValue = value.endsWith(OTHER_SUFFIX) ? facts.get(key + ConstantsUtils.SuffixUtils.OTHER) : "";
                value = otherValue != null ?
                        value.substring(0, value.lastIndexOf(",")) + ", " + otherValue.toString() + "]" :
                        value.substring(0, value.lastIndexOf(",")) + "]";

            }
        }

        return ContactJsonFormUtils.keyToValueConverter(value);
    }

    private static String cleanValueResult(String result) {
        List<String> nonEmptyItems = new ArrayList<>();

        for (String item : result.split(",")) {
            if (item.length() > 1) {
                nonEmptyItems.add(item);
            }
        }
        //Get the first item that usually  has a colon and remove it form list, if list has one item append separator
        String itemLabel = "";
        if (!nonEmptyItems.isEmpty() && nonEmptyItems.get(0).contains(":")) {
            String[] separatedLabel = nonEmptyItems.get(0).split(":");
            itemLabel = separatedLabel[0];
            if (separatedLabel.length > 1) {
                nonEmptyItems.set(0, nonEmptyItems.get(0).split(":")[1]);
            }//replace with extracted value
        }
        return itemLabel + (!TextUtils.isEmpty(itemLabel) ? ": " : "") + StringUtils.join(nonEmptyItems.toArray(), ",");
    }

    @NonNull
    public static org.smartregister.Context context() {
        return OpdLibrary.getInstance().context();
    }

    @Nullable
    public static OpdMetadata metadata() {
        return OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
    }
}
