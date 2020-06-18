package org.smartregister.opd.interactor;

import net.sqlcipher.Cursor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.presenter.OpdProfileActivityPresenter;
import org.smartregister.opd.provider.OpdRegisterQueryProviderTest;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.HashMap;
import java.util.List;

public class OpdProfileInteractorTest extends BaseTest {

    @Mock
    private OpdLibrary opdLibrary;

    private OpdMetadata opdMetadata;

    private OpdProfileInteractor opdProfileInteractor;

    private String baseEntityId = "3223-sdfsew32-er";

    @Mock
    private Context opensrpContext;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private ClientProcessorForJava clientProcessorForJava;

    @Mock
    private ECSyncHelper ecSyncHelper;

    private String registrationForm = "{\"count\":\"1\",\"encounter_type\":\"Opd Registration\",\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Opd Registration\",\"fields\":[{\"key\":\"photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the person\"},{\"key\":\"OPENSRP_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"OPENSRP_ID\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"MER ID\",\"value\":\"0\",\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid MER ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the MER ID\"}},{\"key\":\"national_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"national_id\",\"type\":\"edit_text\",\"hint\":\"National ID\",\"edit_type\":\"name\",\"look_up\":true,\"v_required\":{\"value\":false,\"err\":\"Please enter the patient's national id\"}},{\"key\":\"bht_mid\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"bht_mid\",\"type\":\"barcode\",\"barcode_type\":\"qrcode\",\"hint\":\"BHT ID\",\"value\":\"0\",\"scanButtonText\":\"Scan Barcode\",\"look_up\":true,\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid BHT ID\"},\"v_required\":{\"value\":false,\"err\":\"Please enter a valid BHT ID\"}},{\"key\":\"opd_reg_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"annual_serial_number\",\"type\":\"edit_text\",\"hint\":\"Annual Serial Number\",\"edit_type\":\"name\"},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"entity_id\":\"\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":true,\"err\":\"Please enter a first name\"}},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":true,\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"}},{\"key\":\"dob_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Date of birth (DOB)\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"},\"relevance\":{\"step1:dob_unknown\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"false\\\")\"}}},{\"key\":\"dob_unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"dob_unknown\",\"type\":\"check_box\",\"hint\":\"DOB unknown checkbox\",\"label\":\"\",\"options\":[{\"key\":\"isDobUnknown\",\"text\":\"DOB unknown?\",\"value\":\"false\"}]},{\"key\":\"age_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"edit_text\",\"hint\":\"Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid Age\"},\"relevance\":{\"step1:dob_unknown\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"true\\\")\"}}},{\"key\":\"age_calculated\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/registration_calculation_rules.yml\"}}}},{\"key\":\"age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/registration_calculation_rules.yml\"}}}},{\"key\":\"dob_calculated\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"opd/registration_calculation_rules.yml\"}}}},{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"hidden\",\"value\":\"\"},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Gender\",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Gender\"}},{\"key\":\"home_address\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"tree\":[],\"value\":\"Lombwa Outreach\",\"hint\":\"Address/Location\",\"v_required\":{\"value\":false,\"err\":\"Please enter the Home Address\"}},{\"key\":\"phone_number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"phone_number\",\"type\":\"edit_text\",\"hint\":\"Phone number\",\"v_regex\":{\"value\":\"([0][0-9]{9})|\\\\s*\",\"err\":\"Number must begin with 0 and must be a total of 10 digits in length\"}},{\"key\":\"reminders\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"reminders\",\"openmrs_data_type\":\"select one\",\"type\":\"check_box\",\"options\":[{\"key\":\"isEnrolledInSmsMessages\",\"text\":\"Enroll in Sms Messages\",\"value\":\"false\"}]}]}}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opdProfileInteractor = new OpdProfileInteractor(Mockito.mock(OpdProfileActivityPresenter.class));
        opdMetadata = new OpdMetadata(OpdConstants.JSON_FORM_KEY.NAME
                , OpdDbConstants.KEY.TABLE
                , OpdConstants.EventType.OPD_REGISTRATION
                , OpdConstants.EventType.UPDATE_OPD_REGISTRATION
                , OpdConstants.CONFIG
                , Class.class
                , Class.class
                , true);
    }

    @Test
    public void testSaveRegistrationShouldPassCorrectArguments() throws Exception {
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        event.setFormSubmissionId("32323-sdse2");
        OpdEventClient opdEventClient = new OpdEventClient(new Client(baseEntityId), event);
        RegisterParams registerParams = new RegisterParams();
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.when(opdLibrary.context()).thenReturn(opensrpContext);
        Mockito.doReturn(clientProcessorForJava).when(opdLibrary).getClientProcessorForJava();
        Mockito.when(opdLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        long timeStamp = 1592220733;
        Mockito.when(allSharedPreferences.fetchLastUpdatedAtDate(0)).thenReturn(timeStamp);
        Whitebox.invokeMethod(opdProfileInteractor, "saveRegistration", opdEventClient, registrationForm, registerParams);
        ArgumentCaptor<List<String>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(ecSyncHelper).getEvents(listArgumentCaptor.capture());
        Assert.assertEquals(event.getFormSubmissionId(), listArgumentCaptor.getValue().get(0));
        Mockito.verify(allSharedPreferences).saveLastUpdatedAtDate(Mockito.eq(timeStamp));
    }

    @Test
    public void testRetrieveUpdatedClientShouldReturnValidClient() {
        OpdConfiguration opdConfiguration = new OpdConfiguration.Builder(OpdRegisterQueryProviderTest.class)
                .setOpdMetadata(opdMetadata)
                .build();
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.when(opdLibrary.getOpdConfiguration()).thenReturn(opdConfiguration);
        Mockito.when(opdLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.commonrepository(opdMetadata.getTableName())).thenReturn(commonRepository);
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.moveToFirst()).thenReturn(true);
        Mockito.when(commonRepository.rawCustomQueryForAdapter(Mockito.anyString())).thenReturn(cursor);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(OpdDbConstants.KEY.FIRST_NAME, "John");
        hashMap.put(OpdDbConstants.KEY.LAST_NAME, "Doe");
        CommonPersonObject commonPersonObject = new CommonPersonObject(baseEntityId,
                "relationalId", hashMap, "");
        Mockito.when(commonRepository.getCommonPersonObjectFromCursor(cursor)).thenReturn(commonPersonObject);
        CommonPersonObjectClient commonPersonObjectClient = opdProfileInteractor.retrieveUpdatedClient(baseEntityId);
        Assert.assertEquals(baseEntityId, commonPersonObjectClient.getCaseId());
        Assert.assertEquals(hashMap.get(OpdDbConstants.KEY.FIRST_NAME) + " " + hashMap.get(OpdDbConstants.KEY.LAST_NAME), commonPersonObjectClient.getName());
        Assert.assertNotNull(commonPersonObjectClient.getDetails());
        Assert.assertNotNull(commonPersonObjectClient.getColumnmaps());
    }

    @Test
    public void testRetrieveUpdatedClientShouldReturnNull() {
        OpdConfiguration opdConfiguration = new OpdConfiguration.Builder(OpdRegisterQueryProviderTest.class)
                .setOpdMetadata(opdMetadata)
                .build();
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);
        Mockito.when(opdLibrary.getOpdConfiguration()).thenReturn(opdConfiguration);
        Mockito.when(opdLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.commonrepository(opdMetadata.getTableName())).thenReturn(commonRepository);
        Cursor cursor = Mockito.mock(Cursor.class);
        Mockito.when(cursor.moveToFirst()).thenReturn(false);
        Mockito.when(commonRepository.rawCustomQueryForAdapter(Mockito.anyString())).thenReturn(cursor);
        CommonPersonObjectClient commonPersonObjectClient = opdProfileInteractor.retrieveUpdatedClient(baseEntityId);
        Assert.assertNull(commonPersonObjectClient);
    }


    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }
}