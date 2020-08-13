package org.smartregister.opd.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.smartregister.opd.utils.OpdConstants.EventType.OPD_CLOSE;
import static org.smartregister.opd.utils.OpdJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;
import static org.smartregister.util.JsonFormUtils.getFieldValue;

public class OpdCloseFormProcessing implements OpdFormProcessor<List<Event>> {

    @Override
    public List<Event> processForm(@NonNull JSONObject jsonObject, @NonNull Intent data) throws JSONException {

        ArrayList<Event> eventList = new ArrayList<>();

        JSONArray fieldsArray = OpdUtils.generateFieldsFromJsonForm(jsonObject);
        FormTag formTag = OpdJsonFormUtils.formTag(Utils.getAllSharedPreferences());

        JSONObject metadata = jsonObject.getJSONObject(METADATA);

        String baseEntityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.ENTITY_TABLE);
        Event closeOpdEvent = JsonFormUtils.createEvent(fieldsArray, metadata, formTag, baseEntityId, OPD_CLOSE, entityTable);
        OpdJsonFormUtils.tagSyncMetadata(closeOpdEvent);
        eventList.add(closeOpdEvent);

        processWomanDiedEvent(fieldsArray, closeOpdEvent);

        return eventList;
    }

    protected void processWomanDiedEvent(JSONArray fieldsArray, Event event) throws JSONException {
        if ("died".equals(getFieldValue(fieldsArray, "opd_close_reason"))) {
            event.setEventType(OpdConstants.EventType.DEATH);
            createDeathEventObject(event);
        }
    }

    private void createDeathEventObject(Event event) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        EventClientRepository db = OpdLibrary.getInstance().eventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(OpdConstants.JSON_FORM_KEY.DEATH_DATE_APPROX, false);

        db.addorUpdateClient(event.getBaseEntityId(), client);

        db.addEvent(event.getBaseEntityId(), eventJson);

        Event updateClientDetailsEvent = (Event) new Event().withBaseEntityId(event.getBaseEntityId())
                .withEventDate(DateTime.now().toDate()).withEventType(OpdUtils.metadata().getUpdateEventType()).withLocationId(event.getLocationId())
                .withProviderId(event.getLocationId()).withEntityType(event.getEntityType())
                .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());

        JSONObject eventJsonUpdateClientEvent = new JSONObject(JsonFormUtils.gson.toJson(updateClientDetailsEvent));

        db.addEvent(event.getBaseEntityId(), eventJsonUpdateClientEvent);
    }
}

