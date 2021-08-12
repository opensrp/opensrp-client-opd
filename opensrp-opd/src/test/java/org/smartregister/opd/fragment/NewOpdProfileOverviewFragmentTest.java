package org.smartregister.opd.fragment;

import android.app.Application;

import androidx.fragment.app.FragmentHostCallback;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.BaseFragmentTest;
import org.smartregister.opd.R;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.ProfileAction;
import org.smartregister.opd.presenter.NewOpdProfileOverviewFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;


public class NewOpdProfileOverviewFragmentTest extends BaseFragmentTest {

    private NewOpdProfileOverviewFragment fragment;

    @Override
    public void setUp() {
        super.setUp();
        fragment = Mockito.spy(NewOpdProfileOverviewFragment.newInstance(null));
    }

    @Test
    public void verifyViewIds() {
        Assert.assertEquals(R.layout.new_opd_fragment_profile_overview, fragment.getRootLayout());
        Assert.assertEquals(R.id.opd_profile_overview_recycler, fragment.getRecyclerViewID());
        Assert.assertEquals(R.id.progress_bar, fragment.getProgressBarID());
    }

    @Test
    public void testLoadGlobals() throws Exception {
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

        HashMap<String, List<ProfileAction.ProfileActionVisit>> mapVisits = new HashMap<>();
        ArrayList<ProfileAction.ProfileActionVisit> visits = new ArrayList<>();
        ProfileAction.ProfileActionVisit visit = new ProfileAction.ProfileActionVisit();
        visit.setVisitID("abc123");
        visits.add(visit);
        mapVisits.put("OPD_treatment", visits);

        fragment.loadGlobals(mapVisits);
        HashMap<String, String> formGlobalValues = ReflectionHelpers.getField(fragment, "formGlobalValues");
        Assert.assertEquals(formGlobalValues.size(), globalKeys.size());

    }


    @Test
    public void testGetFormName() {
        Application app = RuntimeEnvironment.application;
        ProfileAction action;
        String formName;

        action = new ProfileAction(app.getString(R.string.opd_check_in), 0);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.OPD_CHECKIN);


        action = new ProfileAction(app.getString(R.string.vital_danger_signs), 1);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.VITAL_DANGER_SIGNS);

        action = new ProfileAction(app.getString(R.string.opd_diagnosis), 2);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.DIAGNOSIS);

        action = new ProfileAction(app.getString(R.string.lab_reports), 3);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.LAB_RESULTS);

        action = new ProfileAction(app.getString(R.string.opd_treatment), 4);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.TREATMENT);

        action = new ProfileAction(app.getString(R.string.pharmacy), 5);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.PHARMACY);

        action = new ProfileAction(app.getString(R.string.final_outcome), 6);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.FINAL_OUTCOME);

        action = new ProfileAction(app.getString(R.string.service_fee), 7);
        formName = fragment.getFormName(action);
        Assert.assertEquals(formName, OpdConstants.JsonForm.SERVICE_FEE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFormNameError() {
        ProfileAction action;
        action = new ProfileAction("", 99);
        fragment.getFormName(action);
    }


    @Test
    public void testLoadPresenter() {
        ReflectionHelpers.setField(fragment, "presenter", null);
        fragment.loadPresenter();
        NewOpdProfileOverviewFragmentPresenter presenter = ReflectionHelpers.getField(fragment, "presenter");
        Assert.assertNotNull(presenter);

    }

    @Test
    public void testAttachGlobals() {
        JSONObject form = new JSONObject();
        fragment.attachGlobals(form, "");
        Assert.assertTrue(form.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL));
    }

    @Test
    public void testOnStartCallable() throws Exception {
        FragmentHostCallback host = Mockito.mock(FragmentHostCallback.class);
        ReflectionHelpers.setField(host, "mContext", RuntimeEnvironment.application);

        ReflectionHelpers.setField(fragment, "mHost", host);

        PowerMockito.doReturn(new HashMap<String, List<ProfileAction.ProfileActionVisit>>()).when(VisitDao.class, "getVisitsToday", Mockito.anyString());

        Callable<List<ProfileAction>> callable = fragment.onStartCallable(null);
        List<ProfileAction> actions = callable.call();
        Assert.assertEquals(actions.size(), 8);
    }

}
