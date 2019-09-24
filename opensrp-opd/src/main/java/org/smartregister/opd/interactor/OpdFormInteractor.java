package org.smartregister.opd.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.opd.widgets.OpdEditTextFactory;

public class OpdFormInteractor extends JsonFormInteractor {

    private static OpdFormInteractor OPD_INTERACTOR_INSTANCE = new OpdFormInteractor();

    private OpdFormInteractor(){
        super();
    }

    public static JsonFormInteractor getOpdInteractorInstance(){
        return OPD_INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new OpdEditTextFactory());
    }
}
