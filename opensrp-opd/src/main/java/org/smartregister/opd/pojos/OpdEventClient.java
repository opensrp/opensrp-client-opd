package org.smartregister.opd.pojos;

import android.support.annotation.NonNull;

import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;

public class OpdEventClient {

    private Event event;
    private Client client;

    public OpdEventClient(@NonNull Client client,@NonNull  Event event) {
        this.client = client;
        this.event = event;
    }

    public Client getClient() {
        return client;
    }

    public Event getEvent() {
        return event;
    }
}
