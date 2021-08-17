package org.smartregister.opd.fragment;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.BaseFragmentTest;
import org.smartregister.opd.R;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.ProfileHistory;
import org.smartregister.opd.presenter.NewOpdProfileVisitsFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NewOpdProfileVisitsFragmentTest extends BaseFragmentTest {

    private NewOpdProfileVisitsFragment fragment;
    
    @Override
    public void setUp() {
        super.setUp();
        fragment = Mockito.spy(NewOpdProfileVisitsFragment.newInstance(null));
    }

    @Test
    public void verifyViewIds() {
        Assert.assertEquals(R.layout.new_opd_fragment_profile_overview, fragment.getRootLayout());
        Assert.assertEquals(R.id.opd_profile_overview_recycler, fragment.getRecyclerViewID());
        Assert.assertEquals(R.id.progress_bar, fragment.getProgressBarID());
    }

    @Test
    public void testPopulateGlobalsList() throws Exception {
        HashSet<String> globalKeys = new HashSet<>();
        globalKeys.add("opd_danger_signs_value");
        globalKeys.add("respiratory_rate");
        globalKeys.add("disease_code_primary");
        globalKeys.add("pregnancy_status");
        globalKeys.add("oximetry");
        globalKeys.add("medicine");
        globalKeys.add("disease_code_final_diagn");
        globalKeys.add("bp_systolic");
        globalKeys.add("opd_health_concerns_value");
        globalKeys.add("opd_danger_signs");
        globalKeys.add("bp_diastolic");
        globalKeys.add("diagnostic_test_lab_results");
        globalKeys.add("pulse_rate");
        globalKeys.add("current_weight");
        globalKeys.add("diagnostic_test_ordered");
        globalKeys.add("opd_health_concerns");
        globalKeys.add("convulsions_history");
        globalKeys.add("body_temp");
        globalKeys.add("height");
        globalKeys.add("medical_conditions");
        ReflectionHelpers.setField(fragment, "globalKeys", globalKeys);

        HashMap<String, List<String>> dateToIdMap = new HashMap<>();
        ArrayList<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        ids.add("4");
        ids.add("5");
        dateToIdMap.put("Today", ids);
        PowerMockito.doReturn(dateToIdMap).when(OpdUtils.class, "getDateToEventIdMap", Mockito.any());

        HashMap<String, String> savedValues = new HashMap<>();
        savedValues.put("tests_repeating_group_count", "3");
        savedValues.put("medicine_object", "\"[{\"key\":\"AA007840\",\"text\":\"Atenolol 50mg\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"pack_size\":null,\"product_code\":\"AA007840\",\"dispensing_unit\":\"Tablet\",\"meta\":{\"duration\":\"78\",\"dosage\":\"12\",\"frequency\":\"3456\",\"info\":\"Dose: 12, Duration: 78, Frequency: 3456\"}}},{\"key\":\"FF006300\",\"text\":\"Bandage, WOW 10cm x 4m long, when stretched\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"pack_size\":null,\"product_code\":\"FF006300\",\"dispensing_unit\":\"each\",\"meta\":{\"duration\":\"33\",\"dosage\":\"11\",\"frequency\":\"2244\",\"info\":\"Dose: 11, Duration: 33, Frequency: 2244\"}}}]");
        savedValues.put("test_ordered_avail", "yes");
        savedValues.put("diagnostic_test_ba1ed23029a44fd980784093a5c6f746", "ultra_sound");
        savedValues.put("diagnostic_test_24f8d3b0a73a49e9894c83d6d545b39f", "pregnancy_test");
        savedValues.put("repeatingGroupMap", "{\"24f8d3b0a73a49e9894c83d6d545b39f\":{\"diagnostic_test_result\":\"Negative\",\"diagnostic_test\":\"pregnancy_test\"},\"ba1ed23029a44fd980784093a5c6f746\":{\"diagnostic_test\":\"ultra_sound\",\"diagnostic_test_result_specify\":\"Ultra\"}}");
        savedValues.put("treatment_type", "Medicine, Suturing, Wound dressing, Foreign body removal");
        savedValues.put("medicine", "[{\"key\":\"AA007840\",\"text\":\"Atenolol 50mg\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"pack_size\":null,\"product_code\":\"AA007840\",\"dispensing_unit\":\"Tablet\",\"meta\":{\"duration\":\"78\",\"dosage\":\"12\",\"frequency\":\"3456\",\"info\":\"Dose: 12, Duration: 78, Frequency: 3456\"}}},{\"key\":\"FF006300\",\"text\":\"Bandage, WOW 10cm x 4m long, when stretched\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\",\"property\":{\"pack_size\":null,\"product_code\":\"FF006300\",\"dispensing_unit\":\"each\",\"meta\":{\"duration\":\"33\",\"dosage\":\"11\",\"frequency\":\"2244\",\"info\":\"Dose: 11, Duration: 33, Frequency: 2244\"}}}]");
        savedValues.put("diagnostic_test_result_specify_ba1ed23029a44fd980784093a5c6f746", "Ultra");
        savedValues.put("diagnostic_test_result_24f8d3b0a73a49e9894c83d6d545b39f", "Negative");
        PowerMockito.doReturn(savedValues).when(VisitDao.class, "getSavedKeysForVisit", Mockito.anyString());

        ArrayList<ProfileHistory> history = new ArrayList<>();
        history.add(new ProfileHistory());
        fragment.populateGlobalsList(history);

        Map<String, Map<String, String>> formGlobalValuesMap = ReflectionHelpers.getField(fragment, "formGlobalValuesMap");
        Assert.assertEquals(globalKeys.size(), formGlobalValuesMap.get("Today").size());

    }


    @Test
    public void testGetFormName() {
        ProfileHistory history;
        String formName;

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_CHECK_IN);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.OPD_CHECKIN, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_VITAL_DANGER_SIGNS_CHECK);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.VITAL_DANGER_SIGNS, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.DIAGNOSIS, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_TREATMENT);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.TREATMENT, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_LABORATORY);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.LAB_RESULTS, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_PHARMACY);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.PHARMACY, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_FINAL_OUTCOME);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.FINAL_OUTCOME, formName);

        history = new ProfileHistory().setEventType(OpdConstants.OpdModuleEventConstants.OPD_SERVICE_CHARGE);
        formName = fragment.getFormName(history);
        Assert.assertEquals(OpdConstants.JsonForm.SERVICE_FEE, formName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFormNameError() {
        fragment.getFormName(new ProfileHistory().setEventType(""));
    }


    @Test
    public void testLoadPresenter() {
        ReflectionHelpers.setField(fragment, "presenter", null);
        fragment.loadPresenter();
        NewOpdProfileVisitsFragmentPresenter presenter = ReflectionHelpers.getField(fragment, "presenter");
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testAttachGlobals() throws Exception {
        Map<String, Map<String, String>> formGlobalValuesMap = new HashMap<>();
        formGlobalValuesMap.put("Today", new HashMap<>());
        ReflectionHelpers.setField(fragment, "formGlobalValuesMap", formGlobalValuesMap);

        PowerMockito.doReturn("Today").when(VisitDao.class, "getDateStringForId", Mockito.any());
        JSONObject form = new JSONObject();
        fragment.attachGlobals(form, "");
        Assert.assertTrue(form.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL));
    }


}
