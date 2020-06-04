package org.smartregister.opd.processor;


import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.exception.CheckInEventProcessException;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.pojo.OpdDiagnosisDetail;
import org.smartregister.opd.pojo.OpdTestConducted;
import org.smartregister.opd.pojo.OpdTreatmentDetail;
import org.smartregister.opd.pojo.OpdVisit;
import org.smartregister.opd.repository.OpdDetailsRepository;
import org.smartregister.opd.repository.OpdDiagnosisDetailRepository;
import org.smartregister.opd.repository.OpdTestConductedRepository;
import org.smartregister.opd.repository.OpdTreatmentDetailRepository;
import org.smartregister.opd.repository.OpdVisitRepository;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashSet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpdLibrary.class)
public class OpdMiniClientProcessorForJavaTest extends BaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private OpdMiniClientProcessorForJava opdMiniClientProcessorForJava;
    @Mock
    private OpdLibrary opdLibrary;
    @Mock
    private OpdTreatmentDetailRepository opdTreatmentDetailRepository;
    @Mock
    private OpdTestConductedRepository opdTestConductedRepository;
    @Mock
    private OpdDiagnosisDetailRepository opdDiagnosisDetailRepository;
    @Mock
    private OpdDetailsRepository opdDetailsRepository;
    @Captor
    private ArgumentCaptor<OpdTreatmentDetail> opdTreatmentArgumentCaptor;
    @Captor
    private ArgumentCaptor<OpdDiagnosisDetail> opdDiagnosisArgumentCaptor;
    @Captor
    private ArgumentCaptor<OpdDetails> opdDetailsArgumentCaptor;
    @Captor
    private ArgumentCaptor<OpdTestConducted> opdTestConductedArgumentCaptor;
    private Event event;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opdMiniClientProcessorForJava = Mockito.spy(new OpdMiniClientProcessorForJava(Mockito.mock(Context.class)));
        event = new Event();
        event.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, "visitId");
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void processServiceDetail() throws Exception {

    }

    @Test
    public void processTreatment() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdTreatmentDetailRepository()).thenReturn(opdTreatmentDetailRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.MEDICINE);
        obs.setValue("Folic acid 5mg");
        obs.setHumanReadableValue("Folic acid 5mg");

        obs.setFieldDataType("text");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.MEDICINE);
        event.setEventDate(new DateTime());
        event.addObs(obs);
        event.addDetails(OpdConstants.JSON_FORM_KEY.ID, "id");
        event.addDetails(JsonFormConstants.VALUE, "[{\"key\":\"Bacteria Killer\",\"text\":\"Bacteria Killer\",\"property\":{\"meta\":{\"dosage\":\"er\",\"duration\":\"er\"}}}]");

        Client client = new Client("123");
        EventClient eventClient = new EventClient(event, client);
        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processTreatment", eventClient);
        Mockito.verify(opdTreatmentDetailRepository, Mockito.times(1)).saveOrUpdate(opdTreatmentArgumentCaptor.capture());
        Assert.assertEquals("er", opdTreatmentArgumentCaptor.getValue().getDuration());
        Assert.assertEquals("er", opdTreatmentArgumentCaptor.getValue().getDosage());
        Assert.assertEquals("Bacteria Killer", opdTreatmentArgumentCaptor.getValue().getMedicine());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getVisitId());
    }

    @Test
    public void processDiagnosis() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdDiagnosisDetailRepository()).thenReturn(opdDiagnosisDetailRepository);

        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
        obs.setFieldDataType("text");
        obs.setValue("");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
        obs.setHumanReadableValue("Bacterial Meningitis");
        event.addObs(obs);
        event.addDetails(OpdConstants.JSON_FORM_KEY.ID, "id");
        event.addDetails(JsonFormConstants.VALUE, "[{\"key\":\"Bacterial Meningitis\",\"text\":\"Bacterial Meningitis\",\"property\":{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}}]");

        Obs obs1 = new Obs();
        obs1.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSIS);
        obs1.setValue("diagnosis");
        obs1.setFieldDataType("text");
        obs1.setHumanReadableValue("");
        obs1.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSIS);
        event.addObs(obs1);

        Obs obs2 = new Obs();
        obs2.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        obs2.setValue("Confirmed");
        obs2.setFieldDataType("text");
        obs2.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        event.addObs(obs2);
        event.setEventDate(new DateTime());
        Client client = new Client("123");
        EventClient eventClient = new EventClient(event, client);
        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processDiagnosis", eventClient);
        Mockito.verify(opdDiagnosisDetailRepository, Mockito.times(1)).saveOrUpdate(opdDiagnosisArgumentCaptor.capture());
        Assert.assertEquals("diagnosis", opdDiagnosisArgumentCaptor.getValue().getDiagnosis());
        Assert.assertEquals("Bacterial Meningitis", opdDiagnosisArgumentCaptor.getValue().getDisease());
        Assert.assertEquals("Confirmed", opdDiagnosisArgumentCaptor.getValue().getType());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getVisitId());
    }


    @Test
    public void processTestConducted() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdTestConductedRepository()).thenReturn(opdTestConductedRepository);

        Obs obs2 = new Obs();
        obs2.setFormSubmissionField(OpdConstants.REPEATING_GROUP_MAP);
        obs2.setValue("{\"6bce8c38eac04a47a2438bcd79071d69\":{\"diagnostic_test\":\"TB Screening\",\"diagnostic_test_result_spinner\":\"Negative\"}}");
        obs2.setFieldDataType("text");
        obs2.setHumanReadableValue("");
        obs2.setFieldCode(OpdConstants.REPEATING_GROUP_MAP);

        event.addObs(obs2);
        event.addDetails("visitId", "visitId");
        event.setEventDate(new DateTime());
        event.addDetails(OpdConstants.JSON_FORM_KEY.ID, "id");

        Client client = new Client("123");
        EventClient eventClient = new EventClient(event, client);

        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processTestConducted", eventClient);

        Mockito.verify(opdTestConductedRepository, Mockito.times(1)).saveOrUpdate(opdTestConductedArgumentCaptor.capture());
        Assert.assertEquals("Negative", opdTestConductedArgumentCaptor.getValue().getResult());
        Assert.assertEquals("TB Screening", opdTestConductedArgumentCaptor.getValue().getTestType());
        Assert.assertEquals("visitId", opdTestConductedArgumentCaptor.getValue().getVisitId());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getCreatedAt());
    }


    @Test
    public void getEventTypesShouldReturnAtLeast6EventTypesAllStartingWithOpd() {
        HashSet<String> eventTypes = opdMiniClientProcessorForJava.getEventTypes();

        Assert.assertTrue(eventTypes.size() >= 6);
        for (String eventType : eventTypes) {
            Assert.assertTrue(eventType.startsWith("OPD"));
        }
    }

    @Test
    public void processEventClientShouldCallProcessCheckInWhenEvenTypeIsOPDCheckIn() throws Exception {
        String formSubmissionId = "submission-id";
        Event event = new Event().withEventType(OpdConstants.EventType.CHECK_IN).withFormSubmissionId(formSubmissionId);

        event.addDetails("d", "d");
        event.setEventDate(new DateTime());

        EventClient eventClient = new EventClient(event, new Client("base-entity-id"));
        ClientClassification clientClassification = Mockito.mock(ClientClassification.class);

        Mockito.doNothing()
                .when(opdMiniClientProcessorForJava)
                .processCheckIn(Mockito.any(EventClient.class), Mockito.any(ClientClassification.class));

        // Mock CoreLibrary calls to make they pass
        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        org.smartregister.Context contextMock = Mockito.mock(org.smartregister.Context.class);
        Mockito.doReturn(contextMock)
                .when(coreLibrary)
                .context();
        EventClientRepository eventClientRepository = Mockito.mock(EventClientRepository.class);
        Mockito.doReturn(eventClientRepository)
                .when(contextMock)
                .getEventClientRepository();

        opdMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), clientClassification);

        Mockito.verify(opdMiniClientProcessorForJava, Mockito.times(1))
                .processCheckIn(Mockito.any(EventClient.class), Mockito.any(ClientClassification.class));
        Mockito.verify(eventClientRepository, Mockito.times(1))
                .markEventAsProcessed(Mockito.eq(formSubmissionId));

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void processEventClientShouldThrowExceptionWhenClientIsNull() throws Exception {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Could not process this OPD Check-In Event because Client bei referenced by OPD Check-In event does not exist");

        Event event = new Event().withEventType(OpdConstants.EventType.CHECK_IN).withBaseEntityId("bei");
        event.addDetails("d", "d");

        EventClient eventClient = new EventClient(event, null);

        opdMiniClientProcessorForJava.processEventClient(eventClient, new ArrayList<Event>(), null);
    }

    @Test
    public void processCheckInShouldThrowExceptionWhenVisitIdIsNull() throws CheckInEventProcessException {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Check-in of event");

        String baseEntityId = "bei";

        Event event = new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new DateTime());
        event.addDetails("d", "d");

        Client client = new Client(baseEntityId);

        ClientClassification clientClassification = Mockito.mock(ClientClassification.class);
        opdMiniClientProcessorForJava.processCheckIn(new EventClient(event, client), clientClassification);
    }

    @Test
    public void processCheckInShouldThrowExceptionSavingVisitFails() throws CheckInEventProcessException {
        expectedException.expect(CheckInEventProcessException.class);
        expectedException.expectMessage("Could not process this OPD Check-In Event because Visit with id visit-id could not be saved in the db. Fail operation failed");

        String baseEntityId = "bei";

        Event event = new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new DateTime());

        Obs obsVisitId = new Obs();
        obsVisitId.setFormSubmissionField("visit_id");
        obsVisitId.setValue("visit-id");
        obsVisitId.setFieldDataType("text");
        obsVisitId.setHumanReadableValue("");
        obsVisitId.setFieldCode("visit_id");

        Obs obsVisitDate = new Obs();
        obsVisitDate.setFormSubmissionField("visit_date");
        obsVisitDate.setValue("2018-03-03 10:10:10");
        obsVisitDate.setFieldDataType("text");
        obsVisitDate.setHumanReadableValue("");
        obsVisitDate.setFieldCode("visit_date");

        event.addObs(obsVisitDate);
        event.addObs(obsVisitId);

        Client client = new Client(baseEntityId);

        //Mock OpdLibrary
        OpdLibrary opdLibrary = Mockito.mock(OpdLibrary.class);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        OpdVisitRepository opdVisitRepository = Mockito.mock(OpdVisitRepository.class);
        Mockito.doReturn(opdVisitRepository).when(opdLibrary).getVisitRepository();

        // Mock Repository
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(repository).when(opdLibrary).getRepository();
        Mockito.doReturn(database).when(repository).getWritableDatabase();

        // Mock OpdVisitRepository to return false
        Mockito.doReturn(false).when(opdVisitRepository).addVisit(Mockito.any(OpdVisit.class));

        ClientClassification clientClassification = Mockito.mock(ClientClassification.class);
        opdMiniClientProcessorForJava.processCheckIn(new EventClient(event, client), clientClassification);
    }

    @Test
    public void processOpdCloseVisitEvent() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        OpdDetails opdDetails = new OpdDetails("id", "id");
        PowerMockito.when(opdDetailsRepository.findOne(Mockito.any(OpdDetails.class))).thenReturn(opdDetails);
        PowerMockito.when(opdLibrary.getOpdDetailsRepository()).thenReturn(opdDetailsRepository);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);


        event.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, "id");
        event.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_END_DATE, "id");

        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processOpdCloseVisitEvent", event);

        Mockito.verify(opdDetailsRepository, Mockito.times(1))
                .saveOrUpdate(opdDetailsArgumentCaptor.capture());
        Assert.assertEquals("id", opdDetailsArgumentCaptor.getValue().getBaseEntityId());
        Assert.assertEquals("id", opdDetailsArgumentCaptor.getValue().getCurrentVisitId());
    }

    @Test
    public void getEventTypes() {
        Assert.assertNotNull(opdMiniClientProcessorForJava.getEventTypes());
    }
}