package org.smartregister.opd.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import net.sqlcipher.database.SQLiteDatabase;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.domain.ProfileHistory;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.repository.OpdCheckInRepository;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(RobolectricTestRunner.class)
public class OpdUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private OpdConfiguration opdConfiguration;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private OpdCheckInRepository opdCheckInRepository;

    @Mock
    private EventClientRepository eventClientRepository;

    @Mock
    private OpdMetadata opdMetadata;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", OpdUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", OpdUtils.convertDate(date, OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(OpdUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", OpdUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(OpdUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();
        Mockito.doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();

        assertEquals(opdMetadata, OpdUtils.metadata());

        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge() {
        assertEquals("13", OpdUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", OpdUtils.getClientAge("4m", "y"));
        assertEquals("5", OpdUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", OpdUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", OpdUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", OpdUtils.getClientAge("6d", "y"));
    }

    @Test
    public void isTemplateShouldReturnFalseIfStringDoesNotContainMatchingBraces() {
        assertFalse(OpdUtils.isTemplate("{ This is a sytling brace"));
        assertFalse(OpdUtils.isTemplate("This is display text"));
    }

    @Test
    public void isTemplateShouldReturnTrueIfStringContainsMatchingBraces() {
        assertTrue(OpdUtils.isTemplate("Project Name: {project_name}"));
    }

    @Test
    public void buildActivityFormIntentShouldCreateIntentWithWizardEnabledWhenEncounterTypeIsDiagnosisAndTreat() throws JSONException {
        // Mock calls to OpdLibrary
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();
        Mockito.doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();
        Mockito.doReturn(BaseOpdFormActivity.class).when(opdMetadata).getOpdFormActivity();

        JSONObject jsonForm = new JSONObject();
        jsonForm.put(OpdJsonFormUtils.ENCOUNTER_TYPE, OpdConstants.EventType.DIAGNOSIS_AND_TREAT);

        HashMap<String, String> parcelableData = new HashMap<>();
        String baseEntityId = "89283-23dsd-23sdf";
        parcelableData.put(OpdConstants.IntentKey.BASE_ENTITY_ID, baseEntityId);

        Intent actualResult = OpdUtils.buildFormActivityIntent(jsonForm, parcelableData, Mockito.mock(Context.class));
        Form form = (Form) actualResult.getSerializableExtra(JsonFormConstants.JSON_FORM_KEY.FORM);

        assertTrue(form.isWizard());
        assertEquals(OpdConstants.EventType.DIAGNOSIS_AND_TREAT, form.getName());
        assertEquals(baseEntityId, actualResult.getStringExtra(OpdConstants.IntentKey.BASE_ENTITY_ID));
    }

    @Test
    public void buildRepeatingGroupTests() throws JSONException {
        String strStep1JsonObject = "{\"fields\":[{\"key\":\"tests_repeating_group\",\"type\":\"repeating_group\",\"value\"" +
                ":[{\"key\":\"diagnostic_test\"},{\"key\":\"diagnostic_result_specify\"}]}," +
                "{\"key\":\"diagnostic_test_128040f1b4034311b34b6ea65a81d3aa\",\"values\":[\"Ultra sound\"],\"value\":\"Ultra sound\"}," +
                "{\"key\":\"diagnostic_result_specify_128040f1b4034311b34b6ea65a81d3aa\",\"value\":\"wer\"}]}";
        JSONObject step1JsonObject = new JSONObject(strStep1JsonObject);
        HashMap<String, HashMap<String, String>> repeatingGroupNum = OpdUtils.buildRepeatingGroupTests(step1JsonObject);
        assertEquals(1, repeatingGroupNum.size());
    }

    @Test
    public void testGetInjectableFieldsShouldPopulateMapCorrectly() {
        String baseEntityId = "234-234";
        String visitId = "343-ertret-3";
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        org.smartregister.Context context = Mockito.mock(org.smartregister.Context.class);
        Mockito.doReturn(opdConfiguration).when(opdLibrary).getOpdConfiguration();
        opdMetadata.setTableName("ec_client");
        Mockito.doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();
        Mockito.when(opdLibrary.getCheckInRepository()).thenReturn(opdCheckInRepository);
        Mockito.when(opdLibrary.context()).thenReturn(context);
        Mockito.doReturn(sqLiteDatabase).when(eventClientRepository).getReadableDatabase();
        Mockito.when(context.getEventClientRepository()).thenReturn(eventClientRepository);
        ArrayList<HashMap<String, String>> maps = new ArrayList<>();
        HashMap<String, String> detailsMap = new HashMap<>();
        detailsMap.put(OpdConstants.ClientMapKey.GENDER, "Female");
        detailsMap.put(OpdDbConstants.Column.Client.DOB, "");
        maps.add(detailsMap);

        HashMap<String, String> checkInDetailsMap = new HashMap<>();
        checkInDetailsMap.put(OpdDbConstants.Column.OpdCheckIn.VISIT_ID, visitId);
        Mockito.when(opdCheckInRepository.getLatestCheckIn(baseEntityId)).thenReturn(checkInDetailsMap);

        Mockito.when(eventClientRepository.rawQuery(sqLiteDatabase, "select * from " + opdMetadata.getTableName() +
                " where " + OpdDbConstants.Column.Client.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1")).thenReturn(maps);

        String formName = OpdConstants.Form.OPD_DIAGNOSIS_AND_TREAT;
        HashMap<String, String> hashMap = OpdUtils.getInjectableFields(formName, baseEntityId);
        assertEquals(hashMap.get(OpdConstants.JSON_FORM_KEY.AGE), "");
        assertEquals(hashMap.get(OpdConstants.ClientMapKey.GENDER), "Female");
        assertEquals(hashMap.get(OpdDbConstants.Column.OpdCheckIn.VISIT_ID), visitId);

        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void testAddOpenMrsEntityIdShouldAddIdIfPresent() throws JSONException {
        String diseaseStr = "{\"openmrsentity\":\"\",\"property\":{\"presumed-id\":\"233AAAAAA\",\"code\":\"code_1\",\"confirmed-id\":null},\"openmrsentityid\":\"\",\"text\":\"Polio* (1)\",\"openmrsentityparent\":\"\",\"key\":\"code_1\"}";
        JSONObject jsonObject = new JSONObject(diseaseStr);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        JSONArray resultArray = OpdUtils.addOpenMrsEntityId("presumed", jsonArray);
        JSONObject resultObject = resultArray.getJSONObject(0);
        assertEquals("233AAAAAA", resultObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID));
    }

    @Test
    public void testInjectValueMap() throws JSONException {
        String form = "{\"count\":\"1\",\"encounter_type\":\"OPD_Laboratory\",\"step1\":{\"title\":\"Lab\",\"fields\":[{\"key\":\"tests_repeating_group_count\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"\",\"value\":2,\"text\":\"# of tests\"},{\"key\":\"diagnostic_test_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"The type of test conducted\",\"type\":\"spinner\",\"values\":[\"Pregnancy Test\",\"Ultra sound\",\"Malaria - Microscopy\",\"HIV test\",\"Syphilis Test - VDRL\",\"Hep B test\",\"Hep C test\",\"Blood Type test\",\"TB Screening\",\"Blood Glucose test (random plasma glucose test)\",\"Midstream urine Gram-staining\",\"Malaria - MRDT\",\"TB Gene Xpert\",\"TB smear microscopy\",\"TB urine LAM\",\"Urine dipstick\",\"Hemocue (haemoglobinometer)\",\"HIV Viral Load\",\"HIV EID\",\"HIV test - Rapid Test\",\"Other(specify)\"],\"keys\":[\"pregnancy_test\",\"ultra_sound\",\"malaria_microscopy\",\"hiv_test\",\"syphilis_vdrl\",\"hep_b\",\"hep_c\",\"blood_type\",\"tb_screening\",\"blood_glucose_random_plasma_glucose_test\",\"midstream_urine_gram_staining\",\"malaria_mrdt\",\"tb_gene_xpert\",\"tb_smear_microscopy\",\"tb_urine_lam\",\"urine_dipstick\",\"hemocue_haemoglobinometer\",\"hiv_viral_load\",\"hiv_eid\",\"hiv_test_rapid\",\"other\"],\"step\":\"step1\",\"is-rule-check\":true,\"value\":\"hep_b\"},{\"key\":\"diagnostic_test_result_other_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Specify the result of the test\",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the result of the test\"},\"is_visible\":false},{\"key\":\"diagnostic_test_result_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"The result of the test conducted\",\"type\":\"spinner\",\"values\":[\"Positive\",\"Negative\",\"Inconclusive\"],\"is_visible\":true,\"value\":\"Positive\"},{\"key\":\"diagnostic_test_result_blood_type_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"The result of the test conducted\",\"type\":\"spinner\",\"values\":[\"A(Positive)\",\"B(Positive)\",\"AB(Positive)\",\"O(Positive)\",\"O(Negative)\",\"A(Negative)\",\"B(Negative)\",\"AB(Negative)\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_tb_gene_xpert_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Test Result\",\"type\":\"spinner\",\"values\":[\"MTB Detected & RR Not detected\",\"RR Detected & MTB Not Detected\",\"MTB Not Detected\",\"Error/Indeterminate\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_tb_smear_microscopy_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Test Result\",\"type\":\"spinner\",\"values\":[\"Negative\",\"Scanty\",\"1+\",\"2+\",\"3+\"],\"keys\":[\"negative\",\"scanty\",\"1+\",\"2+\",\"3+\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_urine_dipstick_nitrites_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Urine dipstick result - nitrites\",\"type\":\"spinner\",\"values\":[\"None\",\"+\",\"++\",\"+++\",\"++++\"],\"keys\":[\"none\",\"+\",\"++\",\"+++\",\"++++\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_urine_dipstick_leukocytes_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Urine dipstick result - leukocytes\",\"type\":\"spinner\",\"values\":[\"None\",\"+\",\"++\",\"+++\",\"++++\"],\"keys\":[\"none\",\"+\",\"++\",\"+++\",\"++++\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_urine_dipstick_protein_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Urine dipstick result - protein\",\"type\":\"spinner\",\"values\":[\"None\",\"+\",\"++\",\"+++\",\"++++\"],\"keys\":[\"none\",\"+\",\"++\",\"+++\",\"++++\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_urine_dipstick_glucose_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"Urine dipstick result - glucose\",\"type\":\"spinner\",\"values\":[\"None\",\"+\",\"++\",\"+++\",\"++++\"],\"keys\":[\"none\",\"+\",\"++\",\"+++\",\"++++\"],\"is_visible\":false},{\"key\":\"diagnostic_test_result_hiv_viral_load_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"HIV Viral Load\",\"type\":\"spinner\",\"values\":[\"Detectable\",\"Undetectable\"],\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"diagnostic_test_result_hiv_viral_load_no_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"HIV Viral Load - Detectable\",\"type\":\"edit_text\",\"is_visible\":false},{\"key\":\"diagnostic_test_result_glucose_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"The result of the test conducted\",\"type\":\"edit_text\",\"edit_type\":\"number\",\"v_regex\":{\"value\":\"^[0-9]+(\\\\.)[0-9]+?$\",\"err\":\"Please enter a valid result\"},\"is_visible\":false},{\"key\":\"diagnostic_test_result_specify_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"hint\":\"The result of the test conducted\",\"type\":\"edit_text\",\"is_visible\":false},{\"key\":\"spacer_ebabd87976f3464e97052f7d77ac68a5\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"40sp\"},{\"key\":\"tests_repeating_group\",\"type\":\"repeating_group\",\"value\":[{\"key\":\"diagnostic_test\"},{\"key\":\"diagnostic_result_specify\"}]},{\"key\":\"diagnostic_test_128040f1b4034311b34b6ea65a81d3aa\",\"values\":[\"Ultra sound\"],\"value\":\"Ultra sound\"},{\"key\":\"diagnostic_result_specify_128040f1b4034311b34b6ea65a81d3aa\",\"value\":\"wer\"}]},\"baseEntityId\":\"d8c3e0bd-bfd1-448c-9236-ff887f56820d\"}";
        JSONObject formObject = new JSONObject(form);
        OpdUtils.injectGroupMap(formObject);
        JSONArray fields = formObject.getJSONObject("step1").getJSONArray("fields");
        JSONObject repeatingGroup = fields.optJSONObject(fields.length() - 1);
        assertNotNull(repeatingGroup);
    }

    @Test
    public void testDateToEventMap() {
        List<ProfileHistory> history = new ArrayList<>();

        ProfileHistory profileHistory1 = new ProfileHistory();
        profileHistory1.setID("4ea2ff13-ed0d-42a7-8fee-e3a82dff8d02");
        profileHistory1.setEventDate("10 Aug 2021");
        profileHistory1.setEventTime("03:54");
        profileHistory1.setEventType("OPD_Check_in");
        history.add(profileHistory1);

        ProfileHistory profileHistory2 = new ProfileHistory();
        profileHistory2.setID("3ih43ih4-ed0d-42a7-8fee-e3a82dff8d02");
        profileHistory2.setEventDate("10 Aug 2021");
        profileHistory2.setEventTime("02:44");
        profileHistory2.setEventType("OPD_Diagnosis");
        history.add(profileHistory2);

        HashMap<String, List<String>> map = OpdUtils.getDateToEventIdMap(history);

        assertNotNull(map.get("10 Aug 2021"));
        assertEquals(2, map.get("10 Aug 2021").size());
        assertEquals("4ea2ff13-ed0d-42a7-8fee-e3a82dff8d02", map.get("10 Aug 2021").get(1));
        assertEquals("3ih43ih4-ed0d-42a7-8fee-e3a82dff8d02", map.get("10 Aug 2021").get(0));
    }


    @Test
    public void testReverseHyphenSeperatedValues() {
        String dateOfDeath = "10-Aug-2021";
        String reverse = OpdUtils.reverseHyphenSeperatedValues(dateOfDeath, "-");
        assertEquals("2021-Aug-10", reverse);
    }

    @Test
    public void testSetHtmlText() {
        TextView textView = Mockito.mock(TextView.class);
        OpdUtils.setTextAsHtml(textView, "<html>some text</html>");
        Mockito.verify(textView, Mockito.times(1)).setText(Mockito.any(Spanned.class));
    }

}