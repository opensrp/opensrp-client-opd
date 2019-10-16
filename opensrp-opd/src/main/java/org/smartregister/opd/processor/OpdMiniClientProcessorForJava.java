package org.smartregister.opd.processor;

import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Obs;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.exception.CheckInEventProcessException;
import org.smartregister.opd.pojos.CheckIn;
import org.smartregister.opd.pojos.OpdDetails;
import org.smartregister.opd.pojos.Visit;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.sync.ClientProcessorForJava;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.database.SQLiteException;

import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-01
 */

public class OpdMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private static OpdMiniClientProcessorForJava instance;

    private HashSet<String> eventTypes = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT, Locale.US);

    public OpdMiniClientProcessorForJava(Context context) {
        super(context);
    }

    public static OpdMiniClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new OpdMiniClientProcessorForJava(context);
        }

        return instance;
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(OpdConstants.EventType.CHECK_IN);
        }

        return eventTypes;

    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {
        Event event = eventClient.getEvent();

        if (event.getEventType().equals(OpdConstants.EventType.CHECK_IN)) {

            try {
                processCheckIn(event, eventClient.getClient());
                CoreLibrary.getInstance().context().getEventClientRepository().markEventAsProcessed(eventClient.getEvent().getFormSubmissionId());
            } catch (CheckInEventProcessException ex) {
                Timber.e(ex);
            }
        }
    }

    protected void processCheckIn(@NonNull Event event, @NonNull Client client) throws CheckInEventProcessException {
        HashMap<String, String> keyValues = new HashMap<>();

        List<Obs> obs = event.getObs();

        for (Obs observation: obs) {
            String key = observation.getFormSubmissionField();
            List<Object> values = observation.getValues();

            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (value != null) {
                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (value != null) {
                    keyValues.put(key, value);
                    continue;
                }
            }
        }

        Map<String, String> eventDetailsMap = event.getDetails();

        String visitId = eventDetailsMap.get(OpdConstants.Event.CheckIn.Detail.VISIT_ID);
        String visitDateString = eventDetailsMap.get(OpdConstants.Event.CheckIn.Detail.VISIT_DATE);

        Date visitDate = null;

        try {
            visitDate = dateFormat.parse(visitDateString);
        } catch (ParseException e) {
            Timber.e(e);

            visitDate = event.getEventDate().toDate();
        }

        if (visitDate != null) {


            // Create the visit first
            Visit visit = new Visit();
            visit.setId(visitId);
            visit.setBaseEntityId(event.getBaseEntityId());
            visit.setLocationId(event.getLocationId());
            visit.setProviderId(event.getProviderId());
            visit.setCreatedAt(new Date());
            visit.setVisitDate(visitDate);

            // Start transaction
            OpdLibrary.getInstance().getRepository().getWritableDatabase().beginTransaction();

            boolean saved = OpdLibrary.getInstance().getVisitRepository().addVisit(visit);
            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("Visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            CheckIn checkIn = generateCheckInRecordFromCheckInEvent(event, client, keyValues, visitId, visitDate);
            saved = OpdLibrary.getInstance().getCheckInRepository(). addCheckIn(checkIn);

            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("CheckIn for visit with id %s could not be saved in the db. Fail operation failed", visitId));
            }

            //TODO: Make sure this does not override opd details which are latest

            // Update the detail
            OpdDetails opdDetails = generateOpdDetailsFromCheckInEvent(event, visitId, visitDate);
            saved = OpdLibrary.getInstance().getOpdDetailsRepository().addOrUpdateOpdDetails(opdDetails);

            if (!saved) {
                abortTransaction();
                throw new CheckInEventProcessException(String.format("OPD Details for visit with id %s updating status of client %s could not be saved in the db. Fail operation failed", visitId, event.getBaseEntityId()));
            }

            // Update the last interacted with of the user
            try {
                updateLastInteractedWith(event, visit);
            } catch (SQLiteException ex) {
                abortTransaction();
                throw new CheckInEventProcessException("An error occurred saving last_interacted_with");
            }

            commitSuccessfulTransaction();
        } else {
            throw new CheckInEventProcessException(String.format("Check-in with visit id %s could not be processed because it the visitDate is null", visitId));
        }
    }

    private void updateLastInteractedWith(@NonNull Event event, Visit visit) throws CheckInEventProcessException {
        String tableName = event.getEntityType();
        String lastInteractedWithDate = String.valueOf(new Date().getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_interacted_with", lastInteractedWithDate);

        int recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                .update(tableName,  contentValues,"base_entity_id = ?", new String[]{event.getBaseEntityId()});

        if (recordsUpdated < 1) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visit.getId()
                    , event.getBaseEntityId()
                    , tableName));
        }

        // Update FTS
        CommonRepository commonrepository = CoreLibrary.getInstance().context().commonrepository(tableName);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("last_interacted_with", lastInteractedWithDate);

        boolean isUpdated = false;
        String fieldName = "base_entity_id";
        if ("ec_child".equals(tableName)) {
            fieldName = "object_id";
        }

        if (commonrepository.isFts()) {
            recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                    .update(CommonFtsObject.searchTableName(tableName),  contentValues,fieldName + " = ?", new String[]{event.getBaseEntityId()});
            isUpdated = recordsUpdated > 0;
        }

        if (!isUpdated) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visit.getId()
                    , event.getBaseEntityId()
                    , tableName));
        }
    }

    private void abortTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void commitSuccessfulTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().setTransactionSuccessful();
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    @NonNull
    private OpdDetails generateOpdDetailsFromCheckInEvent(@NonNull Event event, String visitId, Date visitDate) {
        OpdDetails opdDetails = new OpdDetails();
        opdDetails.setBaseEntityId(event.getBaseEntityId());
        opdDetails.setCurrentVisitId(visitId);
        opdDetails.setCurrentVisitStartDate(visitDate);
        opdDetails.setCurrentVisitEndDate(null);
        opdDetails.setCreatedAt(new Date());

        // Set Pending diagnose and treat if we have not lapsed the max check-in duration in minutes set in the opd library configuration
        if (visitDate != null) {
            long timeDifferenceInMinutes = ((new Date().getTime()) - visitDate.getTime())/(60 * 1000);
            opdDetails.setPendingDiagnoseAndTreat(timeDifferenceInMinutes <= OpdLibrary.getInstance().getOpdConfiguration().getMaxCheckInDurationInMinutes());
        }

        return opdDetails;
    }

    @NonNull
    private CheckIn generateCheckInRecordFromCheckInEvent(@NonNull Event event, @NonNull Client client, HashMap<String, String> keyValues, String visitId, Date visitDate) {
        CheckIn checkIn = new CheckIn();
        checkIn.setVisitId(visitId);
        checkIn.setPregnancyStatus(keyValues.get(OpdConstants.JsonFormField.PREGNANCY_STATUS));
        checkIn.setHasHivTestPreviously(keyValues.get(OpdConstants.JsonFormField.HIV_TESTED));
        checkIn.setHivResultsPreviously(keyValues.get(OpdConstants.JsonFormField.HIV_PREVIOUS_STATUS));
        checkIn.setIsTakingArt(keyValues.get(OpdConstants.JsonFormField.IS_PATIENT_TAKING_ART));
        checkIn.setCurrentHivResult(keyValues.get(OpdConstants.JsonFormField.CURRENT_HIV_STATUS));
        checkIn.setVisitType(keyValues.get(OpdConstants.JsonFormField.VISIT_TYPE));
        checkIn.setAppointmentScheduledPreviously(keyValues.get(OpdConstants.JsonFormField.APPOINTMENT_DUE));
        checkIn.setAppointmentDueDate(keyValues.get(OpdConstants.JsonFormField.APPOINTMENT_DUE_DATE));
        checkIn.setEventId(event.getEventId());
        checkIn.setBaseEntityId(client.getBaseEntityId());
        checkIn.setUpdatedAt(new Date().getTime());

        if (visitDate != null) {
            checkIn.setCreatedAt(visitDate.getTime());
        }

        return checkIn;
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        return false;
    }
}