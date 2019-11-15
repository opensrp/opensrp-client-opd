package org.smartregister.opd.widgets;

import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojos.OpdMultiSelectOption;
import org.smartregister.opd.repository.OpdMultiSelectOptionsRepository;

import java.util.List;

import timber.log.Timber;

public class OpdMultiSelectList extends MultiSelectListFactory {

    @Override
    public List<MultiSelectItem> fetchData() {
        OpdMultiSelectOptionsRepository opdMultiSelectOptionsRepository = OpdLibrary.getInstance()
                .getOpdMultiSelectOptionsRepository();
        OpdMultiSelectOption multiSelectOption = opdMultiSelectOptionsRepository.getLatest(currentAdapterKey);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(multiSelectOption.getJson());
            return MultiSelectListUtils.processOptionsJsonArray(jsonArray);
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }
}
