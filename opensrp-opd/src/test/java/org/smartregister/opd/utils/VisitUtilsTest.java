package org.smartregister.opd.utils;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.domain.Obs;
import org.smartregister.opd.BaseFragmentTest;
import org.smartregister.domain.Event;
import org.smartregister.opd.model.Visit;

import java.util.ArrayList;


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
        Assert.assertEquals("DIAGNOSIS", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS));
        Assert.assertEquals("TREATMENT", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_TREATMENT));
        Assert.assertEquals("LABORATORY TESTS AND RESULTS", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_LABORATORY));
        Assert.assertEquals("PHARMACY", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_PHARMACY));
        Assert.assertEquals("FINAL OUTCOME", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_FINAL_OUTCOME ));
        Assert.assertEquals("SERVICE FEE", VisitUtils.getTranslatedVisitTypeName(OpdConstants.OpdModuleEventConstants.OPD_SERVICE_CHARGE  ));
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
        String formattedDate3 = VisitUtils.getFormattedDate(VisitUtils.getSourceDateFormat(), VisitUtils.getSaveDateFormat(), null);
        Assert.assertNull(formattedDate3);
    }
}
