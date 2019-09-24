package org.smartregister.opd.model;

import org.json.JSONObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojos.OpdEventClient;
import org.smartregister.opd.utils.JsonFormUtils;
import org.smartregister.util.FormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterActivityModel implements OpdRegisterActivityContract.Model {
    private FormUtils formUtils;
    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
        }
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        if (viewIdentifiers != null) {
            ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
        }
    }

    @Override
    public void saveLanguage(String language) {
        // TODO Save Language
        //Map<String, String> langs = getAvailableLanguagesMap();
        //Utils.saveLanguage(Utils.getKeyByValue(langs, language));
    }

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public List<OpdEventClient> processRegistration(String jsonString, FormTag formTag) {
        List<OpdEventClient> opdEventClientList = new ArrayList<>();
        OpdEventClient opdEventClient = JsonFormUtils.processOpdDetailsForm(jsonString, formTag);
        if (opdEventClient == null) {
            return null;
        }
        opdEventClientList.add(opdEventClient);
        return opdEventClientList;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

    public FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(OpdLibrary.getInstance().context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

    public void setFormUtils(FormUtils formUtils) {
        this.formUtils = formUtils;
    }
}
