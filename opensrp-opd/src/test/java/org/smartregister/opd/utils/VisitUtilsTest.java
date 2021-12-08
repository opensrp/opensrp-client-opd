package org.smartregister.opd.utils;

import com.vijay.jsonwizard.utils.FormUtils;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.client.utils.constants.JsonFormConstants;
import org.smartregister.domain.Obs;
import org.smartregister.opd.BaseFragmentTest;
import org.smartregister.domain.Event;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.HIVStatus;
import org.smartregister.opd.model.Visit;
import org.smartregister.opd.model.VisitDetail;

import java.util.ArrayList;
import java.util.Date;


public class VisitUtilsTest extends BaseFragmentTest {

    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testEventToVisit() {
        Event event = (Event) new Event()
                .withBaseEntityId("638fe721-6267-47fe-be21-38066b3eb05b")
                .withIdentifiers(null)
                .withFormSubmissionId("25b8b395-6f84-4337-a77d-53d9982efdf5")
                .withChildLocationId("Bilila Health Centre")
                .withEntityType("ec_client")
                .withEventDate(new DateTime())
                .withEventType(OpdConstants.OpdModuleEventConstants.OPD_CHECK_IN)
                .withProviderId("meso")
                .withDateCreated(new DateTime());
        ArrayList<Object> obs1Values = new ArrayList<>();
        obs1Values.add("New");
        Obs obs1 = new Obs()
                .withFieldCode("visit_type")
                .withFieldDataType("text")
                .withFormSubmissionField("visit_type")
                .withFieldType("formsubmissionField")
                .addToHumanReadableValuesList(new ArrayList<>())
                .withValues(obs1Values);

        ArrayList<Object> obs2Values = new ArrayList<>();
        obs2Values.add("yes");
        Obs obs2 = new Obs()
                .withFieldCode("appointment_scheduled")
                .withFieldDataType("text")
                .withFormSubmissionField("appointment_scheduled")
                .withFieldType("formsubmissionField")
                .addToHumanReadableValuesList(new ArrayList<>())
                .withValues(obs2Values);

        ArrayList<Obs> obsArray = new ArrayList<>();
        obsArray.add(obs1);
        obsArray.add(obs2);
        event.setObs(obsArray);

        Visit visit = VisitUtils.eventToVisit(event);

        Assert.assertEquals(2, visit.getVisitDetails().size());
        Assert.assertEquals("New", visit.getVisitDetails().get("visit_type").get(0).getDetails());
        Assert.assertEquals("yes", visit.getVisitDetails().get("appointment_scheduled").get(0).getDetails());
    }

    @Test
    public void testGetTranslatedVisitTypeName() throws Exception {
        Context context = Mockito.spy(Context.class);
        PowerMockito.doReturn(context).when(OpdUtils.class, "context");
        PowerMockito.doReturn(RuntimeEnvironment.application).when(context).applicationContext();

        Assert.assertEquals("CHECK IN", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_CHECK_IN));
        Assert.assertEquals("VITAL/DANGER SIGNS", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_VITAL_DANGER_SIGNS_CHECK));
        Assert.assertEquals("PRELIMINARY DIAGNOSIS", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS));
        Assert.assertEquals("FINAL DIAGNOSIS AND TREATMENT", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_TREATMENT));
        Assert.assertEquals("LABORATORY TESTS AND RESULTS", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_LABORATORY));
        Assert.assertEquals("PHARMACY", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_PHARMACY));
        Assert.assertEquals("FINAL OUTCOME", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_FINAL_OUTCOME ));
        Assert.assertEquals(null, VisitUtils.getTranslatedVisitTypeName(""));
    }

    @Test
    public void testIsValidDate() {
        boolean isValid = VisitUtils.isValidDate("10-10-2020 12:13:44:100");
        Assert.assertTrue(isValid);
        boolean isNotValid = VisitUtils.isValidDate("");
        Assert.assertFalse(isNotValid);
    }

    @Test
    public void testDateFormat() {
        String formattedDate = VisitUtils.getFormattedDate(VisitUtils.getSourceDateFormat(), VisitUtils.getSaveDateFormat(), "10-10-2020");
        Assert.assertEquals("2020-10-10", formattedDate);
        String formattedDate2 = VisitUtils.getFormattedDate(VisitUtils.getSourceDateFormat(), VisitUtils.getSaveDateFormat(), "1628758829399");
        Assert.assertEquals("2021-08-12", formattedDate2);
    }


    @Test
    public void testAddPreviousVisitHivStatus() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"count\":\"1\",\"encounter_type\":\"OPD_Diagnosis\",\"entity_id\":\"\",\"step1\":{\"title\":\"Diagnosis\",\"fields\":[{\"key\":\"medical_conditions\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"hint\":\"Previous medical conditions\",\"label\":\"Previous medical conditions\",\"label_text_style\":\"bold\",\"exclusive\":[\"none\"],\"options\":[{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"none\",\"text\":\"None\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"allergies\",\"text\":\"Allergies\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"77AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"mental_diseases\",\"text\":\"Mental diseases\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"neoplasms\",\"text\":\"Neoplasms\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"121375AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"asthma\",\"text\":\"Asthma\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119481AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"diabetes\",\"text\":\"Diabetes\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"139071AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"cardiovascular_diseases\",\"text\":\"Cardiovascular diseases\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"119235AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"endocrine_diseases\",\"text\":\"Endocrine diseases\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"117399AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hypertension\",\"text\":\"Hypertension\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"127417AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"rheumatism\",\"text\":\"Rheumatoid arthritis\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"126511AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"sickle_cell_diseases\",\"text\":\"Sickle cell diseases\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"138571AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hiv\",\"text\":\"HIV\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163521AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"history_of_surgery\",\"text\":\"History of surgery\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"117855AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"epilepsy\",\"text\":\"Epilepsy\"},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"other\",\"text\":\"Other(Specify)\"}]},{\"key\":\"medical_conditions_specify\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Specify other condition\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_relevance_rules.yml\"}}}},{\"key\":\"hiv_tested_date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"HIV diagnosis date\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_relevance_rules.yml\"}}}},{\"key\":\"hiv_diagnosis_date_unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165224AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"yes\",\"text\":\"HIV diagnosis date unknown?\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1009988AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_relevance_rules.yml\"}}}},{\"key\":\"hiv_prev_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"HIV test results\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\\"\"},{\"key\":\"negative\",\"text\":\"Negative\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"unknown\",\"text\":\"Unknown\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_relevance_rules.yml\"}}}},{\"key\":\"hiv_prev_pos_art\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160119AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Is the client taking Antiretroviral treatment (ART)?\",\"label_text_style\":\"bold\",\"type\":\"native_radio\",\"options\":[{\"key\":\"yes\",\"text\":\"Yes\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"no\",\"text\":\"No\",\"value\":false,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_relevance_rules.yml\"}}}},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"138571AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hiv_positive\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#FF0000\",\"v_required\":{\"value\":false},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/opd_diagnosis_calculation.yml\"}}}}]},\"baseEntityId\":\"d8c3e0bd-bfd1-448c-9236-ff887f56820d\",\"global\":{}}");
        String baseEntityId = "d8c3e0bd-bfd1-448c-9236-ff887f56820d";
        HIVStatus hivStatus = new HIVStatus();

        // date Unknown
        VisitDetail visitDetail = new VisitDetail();
        visitDetail.setCreatedAt(new Date());
        visitDetail.setUpdatedAt(new Date());
        visitDetail.setVisitId("a5c8a72c-667f-4feb-8e4b-2452292b0da8");
        visitDetail.setDetails("1009988AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetail.setHumanReadable("HIV diagnosis date unknown?");
        visitDetail.setParentCode("165224AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetail.setVisitDetailsId("8ed0a455-3824-4365-95da-84e1535fe2bf");
        visitDetail.setVisitKey("hiv_diagnosis_date_unknown");
        hivStatus.setLastDiagnosisDateUnknown(visitDetail);
        // medical condition
        VisitDetail visitDetailMedical = new VisitDetail();
        visitDetailMedical.setCreatedAt(new Date());
        visitDetailMedical.setUpdatedAt(new Date());
        visitDetailMedical.setVisitId("a5c8a72c-667f-4feb-8e4b-2452292b0da8");
        visitDetailMedical.setDetails("138571AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetailMedical.setHumanReadable("HIV");
        visitDetailMedical.setParentCode("1628AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetailMedical.setVisitDetailsId("fdea2267-b74e-473c-91bb-315506d71692");
        visitDetailMedical.setVisitKey("medical_conditions");
        hivStatus.setMedicalCondition(visitDetailMedical);
        // taking art
        VisitDetail visitDetailArt = new VisitDetail();
        visitDetailArt.setCreatedAt(new Date());
        visitDetailArt.setUpdatedAt(new Date());
        visitDetailArt.setVisitId("a5c8a72c-667f-4feb-8e4b-2452292b0da8");
        visitDetailArt.setDetails("1138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetailArt.setHumanReadable("yes");
        visitDetailArt.setParentCode("");
        visitDetailArt.setVisitDetailsId("56c7649b-eeb5-4628-bf84-0d924dc55bcc");
        visitDetailArt.setVisitKey("hiv_prev_pos_art");
        hivStatus.setTakingART(visitDetailArt);
        // test result
        VisitDetail visitDetailTest = new VisitDetail();
        visitDetailTest.setCreatedAt(new Date());
        visitDetailTest.setUpdatedAt(new Date());
        visitDetailTest.setVisitId("a5c8a72c-667f-4feb-8e4b-2452292b0da8");
        visitDetailTest.setDetails("703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        visitDetailTest.setHumanReadable("positive");
        visitDetailTest.setParentCode("");
        visitDetailTest.setVisitDetailsId("f138a168-eed8-43cb-a233-3eb1147b5621");
        visitDetailTest.setVisitKey("hiv_prev_status");
        hivStatus.setTestResult(visitDetailTest);

        PowerMockito.doReturn(hivStatus).when(VisitDao.class, "getLastHIVStatusForClient", Mockito.anyString());
        PowerMockito.doReturn(true).when(VisitDao.class, "hasPreviousHIVStatus", Mockito.anyString());

        VisitUtils.addPreviousVisitHivStatus(jsonObject, baseEntityId);

        JSONArray fields = FormUtils.getMultiStepFormFields(jsonObject);
        for (int i = 0; i < fields.length(); ++i) {
            JSONObject field = fields.getJSONObject(i);
            if (field.get(JsonFormConstants.KEY).equals("hiv_diagnosis_date_unknown")) {
                Assert.assertEquals("yes", field.getJSONArray(JsonFormConstants.VALUE).get(0));
            } else if (field.get(JsonFormConstants.KEY).equals("medical_conditions")) {
                Assert.assertEquals("hiv", field.getJSONArray(JsonFormConstants.VALUE).get(0));
            } else if (field.get(JsonFormConstants.KEY).equals("hiv_prev_pos_art")) {
                Assert.assertEquals("yes", field.get(JsonFormConstants.VALUE));
            } else if (field.get(JsonFormConstants.KEY).equals("hiv_prev_status")) {
                Assert.assertEquals("positive", field.get(JsonFormConstants.VALUE));
            }
        }
    }
}
