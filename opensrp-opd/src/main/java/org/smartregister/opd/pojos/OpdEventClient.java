package org.smartregister.opd.pojos;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class OpdEventClient {

    private Event event;
    private Client client;

    public OpdEventClient(Client client, Event event) {
        this.client = client;
        this.event = event;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
