package org.smartregister.opd.utils;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.domain.HIVStatus;
import org.smartregister.opd.exception.CheckInEventProcessException;
import org.smartregister.opd.model.Visit;
import org.smartregister.opd.model.VisitDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.ENCOUNTER_TYPE;
import static org.smartregister.opd.utils.OpdUtils.context;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

public class VisitUtils {
    private static String[] default_obs = {"start", "end", "deviceid", "subscriberid", "simserial", "phonenumber"};
    private static final SimpleDateFormat sdf = new SimpleDateFormat(OpdConstants.DateTimeFormat.yyyy_MM_dd, Locale.ENGLISH);


    public static Visit eventToVisit(Event event) {
        List<String> exceptions = Arrays.asList(default_obs);

        Visit visit = new Visit();
        visit.setVisitId(event.getFormSubmissionId());
        visit.setBaseEntityId(event.getBaseEntityId());
        visit.setDate(event.getEventDate().toDate());
        visit.setVisitType(event.getEventType());
        visit.setEventId(event.getEventId());
        visit.setFormSubmissionId(event.getFormSubmissionId());
        visit.setChildLocationId(event.getChildLocationId());
        visit.setLocationId(event.getLocationId());
        visit.setCreatedAt(event.getDateCreated() != null ? event.getDateCreated().toDate() : new Date());
        visit.setUpdatedAt(event.getDateEdited() != null ? event.getDateEdited().toDate() : visit.getCreatedAt());
        visit.setVisitGroup(sdf.format(event.getEventDate().toDate()));

        Map<String, List<VisitDetail>> details = new HashMap<>();
        if (event.getObs() != null) {
            for (org.smartregister.domain.Obs obs : event.getObs()) {
                if (!exceptions.contains(obs.getFormSubmissionField())) {
                    VisitDetail detail = new VisitDetail();
                    detail.setVisitDetailsId(UUID.randomUUID().toString());
                    detail.setVisitId(visit.getVisitId());
                    detail.setVisitKey(obs.getFormSubmissionField());
                    detail.setParentCode(obs.getParentCode());
                    detail.setDetails(getDetailsValue(detail, obs.getValues().toString()));
                    detail.setHumanReadable(getDetailsValue(detail, obs.getHumanReadableValues().toString()));
                    detail.setCreatedAt(event.getDateCreated() != null ? event.getDateCreated().toDate() : new Date());
                    detail.setUpdatedAt(event.getDateEdited() != null ? event.getDateEdited().toDate() : detail.getCreatedAt());

                    List<VisitDetail> currentList = details.get(detail.getVisitKey());
                    if (currentList == null)
                        currentList = new ArrayList<>();

                    currentList.add(detail);
                    details.put(detail.getVisitKey(), currentList);
                }
            }
        }

        visit.setVisitDetails(details);
        return visit;
    }

    public static void processVisit(EventClient baseEvent) {
        try {
            // delete existing events from the database
            OpdLibrary.getInstance().visitRepository().deleteVisit(baseEvent.getEvent().getFormSubmissionId());

            Visit visit = eventToVisit(baseEvent.getEvent());

            OpdLibrary.getInstance().visitRepository().addVisit(visit);
            if (visit.getVisitDetails() != null) {
                for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
                    if (entry.getValue() != null) {
                        for (VisitDetail detail : entry.getValue()) {
                            OpdLibrary.getInstance().visitDetailsRepository().addVisitDetails(detail);
                        }
                    }
                }
            }
            // Update the last interacted with of the user
            updateLastInteractedWith(baseEvent.getEvent(), visit.getVisitId());
            abortTransaction();

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void updateLastInteractedWith(@NonNull Event event, @NonNull String visitId) throws
            CheckInEventProcessException {
        String tableName = OpdUtils.metadata().getTableName();

        String lastInteractedWithDate = String.valueOf(new Date().getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdConstants.JSON_FORM_KEY.LAST_INTERACTED_WITH, lastInteractedWithDate);

        int recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                .update(tableName, contentValues, OpdConstants.KEY.BASE_ENTITY_ID + " = ?", new String[]{event.getBaseEntityId()});

        if (recordsUpdated < 1) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }

        // Update FTS
        CommonRepository commonrepository = OpdLibrary.getInstance().context().commonrepository(tableName);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(OpdConstants.JSON_FORM_KEY.LAST_INTERACTED_WITH, lastInteractedWithDate);

        boolean isUpdated = false;

        if (commonrepository.isFts()) {
            recordsUpdated = OpdLibrary.getInstance().getRepository().getWritableDatabase()
                    .update(CommonFtsObject.searchTableName(tableName), contentValues, CommonFtsObject.idColumn + " = ?", new String[]{event.getBaseEntityId()});
            isUpdated = recordsUpdated > 0;
        }

        if (!isUpdated) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }
    }

    public static void abortTransaction() {
        if (OpdLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            OpdLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    /* public static String getDetailsValue(VisitDetail detail, String val) {
         String clean_val = cleanString(val);
         if (detail.getVisitKey().contains("date")) {
             return getFormattedDate(getSourceDateFormat(), getSaveDateFormat(), clean_val);
         }

         return clean_val;
     }
 */
    public static String getDetailsValue(VisitDetail detail, String val) {
        String clean_val = cleanString(val);
        if (detail.getVisitKey().contains("date") && StringUtils.isNotBlank(clean_val) && isValidDate(clean_val)) {
            return getFormattedDate(getSourceDateFormat(), getSaveDateFormat(), clean_val);
        }

        return clean_val;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:ms");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static String getFormattedDate(SimpleDateFormat sourceDateFormat, SimpleDateFormat destDateFormat, String value) {
        try {
            Date date = sourceDateFormat.parse(value);
            return destDateFormat.format(date);
        } catch (ParseException e) {
            try {
                // fallback for long datetypes
                Date date = new Date(Long.parseLong(value));
                return destDateFormat.format(date);
            } catch (NumberFormatException | NullPointerException nfe) {
                Timber.e(e);
                Timber.e(nfe);
            }
            Timber.e(e);
        }
        return value;
    }

    public static SimpleDateFormat getSourceDateFormat() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    }

    public static SimpleDateFormat getSaveDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    public static String cleanString(String dirtyString) {
        if (StringUtils.isBlank(dirtyString))
            return "";

        return dirtyString.substring(1, dirtyString.length() - 1);
    }

    public static String getTranslatedVisitTypeName(String name) {
        switch (name) {
            case OpdConstants.OpdModuleEventConstants.OPD_CHECK_IN:
                return context().getStringResource(R.string.opd_check_in);
            case OpdConstants.OpdModuleEventConstants.OPD_VITAL_DANGER_SIGNS_CHECK:
                return context().getStringResource(R.string.vital_danger_signs);
            case OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS:
                return context().getStringResource(R.string.opd_diagnosis);
            case OpdConstants.OpdModuleEventConstants.OPD_TREATMENT:
                return context().getStringResource(R.string.opd_treatment);
            case OpdConstants.OpdModuleEventConstants.OPD_LABORATORY:
                return context().getStringResource(R.string.lab_reports);
            case OpdConstants.OpdModuleEventConstants.OPD_PHARMACY:
                return context().getStringResource(R.string.pharmacy);
            case OpdConstants.OpdModuleEventConstants.OPD_FINAL_OUTCOME:
                return context().getStringResource(R.string.final_outcome);
            case OpdConstants.OpdModuleEventConstants.OPD_SERVICE_CHARGE:
                return context().getStringResource(R.string.service_fee);
            default:
                return null;
        }
    }

    public static void addPreviousVisitHivStatus(JSONObject jsonObject, String baseEntityID) {
        try {
            if (jsonObject.getString(ENCOUNTER_TYPE).equals(OpdConstants.OpdModuleEventConstants.OPD_DIAGNOSIS)
                    && VisitDao.hasPreviousHIVStatus(baseEntityID)) {
                HIVStatus hivStatus = VisitDao.getLastHIVStatusForClient(baseEntityID);
                JSONArray fields = FormUtils.getMultiStepFormFields(jsonObject);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);
                    String key = field.getString(JsonFormConstants.KEY);
                    if (hivStatus.getLastDiagnosisDate() != null && key.equals(hivStatus.getLastDiagnosisDate().getVisitKey())) {
                        field.put(JsonFormConstants.VALUE, hivStatus.getLastDiagnosisDate().getDetails());
                    } else if (hivStatus.getLastDiagnosisDateUnknown() != null && key.equals(hivStatus.getLastDiagnosisDateUnknown().getVisitKey())) {
                        field.put(JsonFormConstants.VALUE, new JSONArray().put("yes"));
                    } else if (hivStatus.getTestResult() != null && key.equals(hivStatus.getTestResult().getVisitKey())) {
                        field.put(JsonFormConstants.VALUE, (hivStatus.getTestResult().getHumanReadable().toLowerCase()));
                    } else if (hivStatus.getTakingART() != null && key.equals(hivStatus.getTakingART().getVisitKey())) {
                        field.put(JsonFormConstants.VALUE, (hivStatus.getTakingART().getHumanReadable().toLowerCase()));
                    } else if (key.equals(hivStatus.getMedicalCondition().getVisitKey())) {
                        field.put(JsonFormConstants.VALUE, new JSONArray().put("hiv"));
                    }
                    fields.put(i, field);
                }
            }
        } catch (
                Exception e) {
            Timber.e(e, "NewOpdProfileOverviewFragmentPresenter -> addPreviousVisitHivStatus()");
        }
    }

}
