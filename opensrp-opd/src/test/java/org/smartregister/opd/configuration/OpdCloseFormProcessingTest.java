package org.smartregister.opd.configuration;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class OpdCloseFormProcessingTest {

    private OpdCloseFormProcessing opdCloseFormProcessing;

    @Before
    public void setUp() {

        CoreLibrary coreLibrary = mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        OpdLibrary opdLibrary = mock(OpdLibrary.class);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", opdLibrary);

        opdCloseFormProcessing = new OpdCloseFormProcessing();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
        opdCloseFormProcessing = null;
    }

    @Test
    public void processOpdFormShouldReturnValidEventList() throws JSONException {
        String jsonString = "{\"encounter_type\":\"Opd Close\",\"entity_id\":\"\",\"metadata\":{\"encounter_location\":\"\"},\"step1\":{\"title\":\"Opd Close\",\"fields\":[{\"key\":\"opd_close_reason\",\"value\":\"died\"},{\"key\":\"date_of_death\",\"value\":\"09-08-2020\"},{\"key\":\"place_of death\",\"value\":\"Community\"},{\"key\":\"death_cause\",\"value\":\"Unknown\"}]}}";

        JSONObject clientObject = new JSONObject();
        clientObject.put(OpdConstants.JSON_FORM_KEY.ATTRIBUTES, clientObject);

        Context context = mock(Context.class);
        AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
        Intent intent = mock(Intent.class);
        EventClientRepository eventClientRepository = mock(EventClientRepository.class);
        OpdConfiguration opdConfiguration = mock(OpdConfiguration.class);
        OpdMetadata opdMetadata = mock(OpdMetadata.class);

        doReturn(context).when(CoreLibrary.getInstance()).context();
        doReturn(allSharedPreferences).when(context).allSharedPreferences();
        doReturn("").when(allSharedPreferences).fetchRegisteredANM();
        doReturn(0).when(OpdLibrary.getInstance()).getApplicationVersion();
        doReturn(0).when(OpdLibrary.getInstance()).getDatabaseVersion();
        doReturn(true).when(intent).hasExtra(eq(OpdConstants.IntentKey.BASE_ENTITY_ID));
        doReturn("3242-23-423-4-234-234").when(intent).getStringExtra(eq(OpdConstants.IntentKey.BASE_ENTITY_ID));
        doReturn(true).when(intent).hasExtra(eq(OpdConstants.IntentKey.ENTITY_TABLE));
        doReturn("ec_client").when(intent).getStringExtra(eq(OpdConstants.IntentKey.ENTITY_TABLE));
        doReturn(eventClientRepository).when(OpdLibrary.getInstance()).eventClientRepository();
        doReturn(clientObject).when(eventClientRepository).getClientByBaseEntityId(anyString());
        doReturn(opdConfiguration).when(OpdLibrary.getInstance()).getOpdConfiguration();
        doReturn(opdMetadata).when(opdConfiguration).getOpdMetadata();
        doReturn(OpdConstants.EventType.UPDATE_OPD_REGISTRATION).when(opdMetadata).getUpdateEventType();

        List<Event> events = opdCloseFormProcessing.processForm(new JSONObject(jsonString), intent);

        assertEquals(1, events.size());
        assertEquals(OpdConstants.EventType.DEATH, events.get(0).getEventType());
        assertEquals("3242-23-423-4-234-234", events.get(0).getBaseEntityId());
        assertEquals("ec_client", events.get(0).getEntityType());
    }
}
