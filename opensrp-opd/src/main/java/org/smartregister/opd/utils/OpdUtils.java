package org.smartregister.opd.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.pojos.OpdMetadata;
import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdUtils extends org.smartregister.util.Utils {

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

    public static int getProfileImageResourceIDentifier() {
        return R.mipmap.ic_launcher;
    }

    public static Date dobStringToDate(String dobString) {
        DateTime dateTime = dobStringToDateTime(dobString);
        if (dateTime != null) {
            return dateTime.toDate();
        }
        return null;
    }

    public static DateTime dobStringToDateTime(String dobString) {
        try {
            if (StringUtils.isBlank(dobString)) {
                return null;
            }
            return new DateTime(dobString);

        } catch (Exception e) {
            return null;
        }
    }

    public static org.smartregister.Context context() {
        return OpdLibrary.getInstance().context();
    }

    public static OpdMetadata metadata() {
        return OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();
    }
}
