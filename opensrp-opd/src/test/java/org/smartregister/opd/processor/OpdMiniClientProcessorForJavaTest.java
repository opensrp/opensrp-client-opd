package org.smartregister.opd.processor;


import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.Obs;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojos.OpdDiagnosis;
import org.smartregister.opd.pojos.OpdServiceDetail;
import org.smartregister.opd.pojos.OpdTestConducted;
import org.smartregister.opd.pojos.OpdTreatment;
import org.smartregister.opd.repository.OpdDiagnosisRepository;
import org.smartregister.opd.repository.OpdServiceDetailRepository;
import org.smartregister.opd.repository.OpdTestConductedRepository;
import org.smartregister.opd.repository.OpdTreatmentRepository;
import org.smartregister.opd.utils.OpdConstants;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpdLibrary.class)
public class OpdMiniClientProcessorForJavaTest extends BaseTest {

    private OpdMiniClientProcessorForJava opdMiniClientProcessorForJava;

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private OpdServiceDetailRepository opdServiceDetailRepository;

    @Mock
    private OpdTreatmentRepository opdTreatmentRepository;

    @Mock
    private OpdTestConductedRepository opdTestConductedRepository;

    @Mock
    private OpdDiagnosisRepository opdDiagnosisRepository;

    @Captor
    private ArgumentCaptor<OpdServiceDetail> opdServiceDetailArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdTreatment> opdTreatmentArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdDiagnosis> opdDiagnosisArgumentCaptor;

    @Captor
    private ArgumentCaptor<OpdTestConducted> opdTestConductedArgumentCaptor;

    private Event event;

    private Client client;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opdMiniClientProcessorForJava = new OpdMiniClientProcessorForJava(Mockito.mock(Context.class));
        event = new Event();
        event.addDetails(OpdConstants.JSON_FORM_KEY.VISIT_ID, "visitId");
    }

    @Test
    public void processServiceDetail() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdServiceDetailRepository()).thenReturn(opdServiceDetailRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.SERVICE_FEE);
        obs.setValue("fee");
        obs.setFieldDataType("text");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.SERVICE_FEE);
        event.addObs(obs);
        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processServiceDetail", event);
        Mockito.verify(opdServiceDetailRepository, Mockito.times(1)).saveOrUpdate(opdServiceDetailArgumentCaptor.capture());
        Assert.assertEquals("fee", opdServiceDetailArgumentCaptor.getValue().getFee());
        Assert.assertEquals("visitId", opdServiceDetailArgumentCaptor.getValue().getVisitId());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getId());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdServiceDetailArgumentCaptor.getValue().getCreatedAt());
        Assert.assertEquals(event.getDetails().toString(), opdServiceDetailArgumentCaptor.getValue().getDetails());
    }

    @Test
    public void processTreatment() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdTreatmentRepository()).thenReturn(opdTreatmentRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.MEDICINE);
        obs.setValue("[{\"key\":\"Bacteria Killer\",\"property\":{\"meta\":{\"dosage\":\"er\",\"duration\":\"er\"}}}]");

        obs.setFieldDataType("text");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.MEDICINE);
        event.addObs(obs);

        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processTreatment", event);
        Mockito.verify(opdTreatmentRepository, Mockito.times(1)).saveOrUpdate(opdTreatmentArgumentCaptor.capture());
        Assert.assertEquals("er", opdTreatmentArgumentCaptor.getValue().getDuration());
        Assert.assertEquals("er", opdTreatmentArgumentCaptor.getValue().getDosage());
        Assert.assertEquals("Bacteria Killer", opdTreatmentArgumentCaptor.getValue().getMedicine());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdTreatmentArgumentCaptor.getValue().getId());
    }

    @Test
    public void processDiagnosis() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdDiagnosisRepository()).thenReturn(opdDiagnosisRepository);

        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
        obs.setValue("[{\"key\":\"Bacterial Meningitis\",\"property\":{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}}]");
        obs.setFieldDataType("text");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.DISEASE_CODE);
        event.addObs(obs);

        Obs obs1 = new Obs();
        obs1.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSIS);
        obs1.setValue("diagnosis");
        obs1.setFieldDataType("text");
        obs1.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSIS);
        event.addObs(obs1);

        Obs obs2 = new Obs();
        obs2.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        obs2.setValue("Confirmed");
        obs2.setFieldDataType("text");
        obs2.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSIS_TYPE);
        event.addObs(obs2);

        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processDiagnosis", event);
        Mockito.verify(opdDiagnosisRepository, Mockito.times(1)).saveOrUpdate(opdDiagnosisArgumentCaptor.capture());
        Assert.assertEquals("diagnosis", opdDiagnosisArgumentCaptor.getValue().getDiagnosis());
        Assert.assertEquals("Bacterial Meningitis", opdDiagnosisArgumentCaptor.getValue().getDisease());
        Assert.assertEquals("Confirmed", opdDiagnosisArgumentCaptor.getValue().getType());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdDiagnosisArgumentCaptor.getValue().getId());
    }


    @Test
    public void processTestConducted() throws Exception {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdTestConductedRepository()).thenReturn(opdTestConductedRepository);
        Obs obs = new Obs();
        obs.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        obs.setValue("diagnostic test result");
        obs.setFieldDataType("text");
        obs.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST_RESULT_SPINNER);
        event.addObs(obs);

        Obs obs1 = new Obs();
        obs1.setFormSubmissionField(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        obs1.setValue("diagnostic test");
        obs1.setFieldDataType("text");
        obs1.setFieldCode(OpdConstants.JSON_FORM_KEY.DIAGNOSTIC_TEST);
        event.addObs(obs1);

        Whitebox.invokeMethod(opdMiniClientProcessorForJava, "processTestConducted", event);

        Mockito.verify(opdTestConductedRepository, Mockito.times(1)).saveOrUpdate(opdTestConductedArgumentCaptor.capture());
        Assert.assertEquals("diagnostic test result", opdTestConductedArgumentCaptor.getValue().getResult());
        Assert.assertEquals("diagnostic test", opdTestConductedArgumentCaptor.getValue().getTest());
        Assert.assertEquals("visitId", opdTestConductedArgumentCaptor.getValue().getVisitId());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getCreatedAt());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getUpdatedAt());
        Assert.assertNotNull(opdTestConductedArgumentCaptor.getValue().getId());
    }
}
