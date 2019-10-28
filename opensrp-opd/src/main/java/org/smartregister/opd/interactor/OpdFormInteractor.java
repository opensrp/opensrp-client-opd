package org.smartregister.opd.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.widgets.OpdBarcodeFactory;
import org.smartregister.opd.widgets.OpdEditTextFactory;
import org.smartregister.opd.widgets.OpdMultiSelectDrugPicker;

public class OpdFormInteractor extends JsonFormInteractor {


    private OpdFormInteractor() {
        super();
    }

    public static JsonFormInteractor getInstance() {

        if (INSTANCE != null && !(INSTANCE instanceof OpdFormInteractor)) {
            INSTANCE = new OpdFormInteractor();
        }
        return INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new OpdEditTextFactory());
        map.put(JsonFormConstants.BARCODE, new OpdBarcodeFactory());
        map.put(OpdConstants.JsonFormWidget.MULTI_SELECT_DRUG_PICKER, new OpdMultiSelectDrugPicker());
    }
}
