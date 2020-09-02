package org.smartregister.opd.configuration;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
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

public class OpdCloseFormProcessing implements OpdFormProcessor<List<Event>> {

    @Override
    public List<Event> processForm(@NonNull JSONObject jsonObject, @NonNull Intent data) throws JSONException {

        ArrayList<Event> eventList = new ArrayList<>();

        JSONArray fieldsArray = OpdUtils.generateFieldsFromJsonForm(jsonObject);
        FormTag formTag = OpdJsonFormUtils.formTag(Utils.getAllSharedPreferences());

        JSONObject metadata = jsonObject.getJSONObject(OpdJsonFormUtils.METADATA);

        String baseEntityId = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.BASE_ENTITY_ID);
        String entityTable = OpdUtils.getIntentValue(data, OpdConstants.IntentKey.ENTITY_TABLE);
        Event closeOpdEvent = JsonFormUtils.createEvent(fieldsArray, metadata, formTag, baseEntityId, OpdConstants.EventType.OPD_CLOSE, entityTable);
        OpdJsonFormUtils.tagSyncMetadata(closeOpdEvent);
        eventList.add(closeOpdEvent);

        processWomanDiedEvent(fieldsArray, closeOpdEvent);

        return eventList;
    }

    protected void processWomanDiedEvent(JSONArray fieldsArray, Event event) throws JSONException {
        if (OpdConstants.KEY.DIED.equals(JsonFormUtils.getFieldValue(fieldsArray, OpdConstants.JSON_FORM_KEY.OPD_CLOSE_REASON))) {
            event.setEventType(OpdConstants.EventType.DEATH);
            createDeathEventObject(event, fieldsArray);
        }
    }

    private void createDeathEventObject(Event event, JSONArray fieldsArray) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        EventClientRepository db = OpdLibrary.getInstance().eventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        String dateOfDeath = JsonFormUtils.getFieldValue(fieldsArray, OpdConstants.JSON_FORM_KEY.DATE_OF_DEATH);
        client.put(OpdConstants.JSON_FORM_KEY.DEATH_DATE, StringUtils.isNotBlank(dateOfDeath) ? OpdUtils.reverseHyphenSeperatedValues(dateOfDeath, "-") : OpdUtils.getTodaysDate());
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(OpdConstants.JSON_FORM_KEY.DEATH_DATE_APPROX, false);

        JSONObject attributes = client.getJSONObject(OpdConstants.JSON_FORM_KEY.ATTRIBUTES);
        attributes.put(OpdConstants.JSON_FORM_KEY.DATE_REMOVED, OpdUtils.getTodaysDate());

        db.addorUpdateClient(event.getBaseEntityId(), client);

        db.addEvent(event.getBaseEntityId(), eventJson);

        Event updateClientDetailsEvent = (Event) new Event().withBaseEntityId(event.getBaseEntityId())
                .withEventDate(DateTime.now().toDate()).withEventType(OpdUtils.metadata().getUpdateEventType()).withLocationId(event.getLocationId())
                .withProviderId(event.getLocationId()).withEntityType(event.getEntityType())
                .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());

        JSONObject eventJsonUpdateClientEvent = new JSONObject(JsonFormUtils.gson.toJson(updateClientDetailsEvent));

        db.addEvent(event.getBaseEntityId(), eventJsonUpdateClientEvent);
    }
}

