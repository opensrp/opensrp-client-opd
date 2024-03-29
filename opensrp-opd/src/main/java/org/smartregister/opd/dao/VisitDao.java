package org.smartregister.opd.dao;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.dao.AbstractDao;
import org.smartregister.opd.R;
import org.smartregister.opd.domain.HIVStatus;
import org.smartregister.opd.domain.ProfileAction;
import org.smartregister.opd.domain.ProfileHistory;
import org.smartregister.opd.model.VisitDetail;
import org.smartregister.opd.utils.OpdConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.smartregister.opd.repository.VisitDetailsRepository.CREATED_AT;
import static org.smartregister.opd.repository.VisitDetailsRepository.DETAILS;
import static org.smartregister.opd.repository.VisitDetailsRepository.HUMAN_READABLE;
import static org.smartregister.opd.repository.VisitDetailsRepository.PARENT_CODE;
import static org.smartregister.opd.repository.VisitDetailsRepository.UPDATED_AT;
import static org.smartregister.opd.repository.VisitDetailsRepository.VISIT_DETAILS_ID;
import static org.smartregister.opd.repository.VisitDetailsRepository.VISIT_ID;
import static org.smartregister.opd.repository.VisitDetailsRepository.VISIT_KEY;
import static org.smartregister.opd.utils.OpdUtils.context;

import android.database.Cursor;

public class VisitDao extends AbstractDao {

    /***
     * Returns a map of all the visits completed today.
     *
     * @return
     */
    public static Map<String, List<ProfileAction.ProfileActionVisit>> getVisitsToday(String baseEntityId) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(OpdConstants.DateTimeFormat.yyyy_MM_dd, Locale.ENGLISH);
        String todayDate = sdfDate.format(new Date());

        String sql = "SELECT * FROM opd_client_visits where visit_group = '"
                + todayDate +
                "' and base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";
        Map<String, List<ProfileAction.ProfileActionVisit>> visitMap = new HashMap<>();

        SimpleDateFormat sdfTime = new SimpleDateFormat(OpdConstants.DateTimeFormat.hh_mm, Locale.ENGLISH);
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT"));

        DataMap<Void> dataMap = cursor -> {
            String visitType = getCursorValue(cursor, "visit_type");
            List<ProfileAction.ProfileActionVisit> visits = visitMap.get(visitType);
            if (visits == null) visits = new ArrayList<>();

            ProfileAction.ProfileActionVisit actionVisit = new ProfileAction.ProfileActionVisit();
            actionVisit.setVisitID(getCursorValue(cursor, "visit_id"));

            Date visitCreateDate = new Date(Long.parseLong(getCursorValue(cursor, "created_at")));
            actionVisit.setVisitTime(sdfTime.format(visitCreateDate));

            visits.add(actionVisit);
            visitMap.put(visitType, visits);

            return null;
        };

        readData(sql, dataMap);

        return visitMap;
    }


    public static Boolean getSeenToday(String baseEntityId) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(OpdConstants.DateTimeFormat.yyyy_MM_dd, Locale.ENGLISH);
        String todayDate = sdfDate.format(new Date());


        String sql = "SELECT * FROM opd_client_visits where visit_group = '"
                + todayDate +
                "' and base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";
        Map<String, List<ProfileAction.ProfileActionVisit>> visitMap = new HashMap<>();

//        SimpleDateFormat sdfTime = new SimpleDateFormat(OpdConstants.DateTimeFormat.hh_mm, Locale.ENGLISH);

        DataMap<Void> dataMap = cursor -> {
            String visitType = getCursorValue(cursor, "visit_type");
            List<ProfileAction.ProfileActionVisit> visits = visitMap.get(visitType);
            if (visits == null) visits = new ArrayList<>();
            visitMap.put(visitType, visits);

            return null;
        };

        readData(sql, dataMap);

        return visitMap.size() > 0;
    }

    public static JSONObject fetchEventAsJson(String formSubmissionId) throws JSONException {
        String sql = "SELECT json FROM Event where formSubmissionId = '" + formSubmissionId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "json");
        return new JSONObject(readSingleValue(sql, dataMap));
    }

    public static String fetchEventByFormSubmissionId(String formSubmissionId) {
        String sql = "SELECT json FROM Event where formSubmissionId = '" + formSubmissionId + "'";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "json");
        return readSingleValue(sql, dataMap);
    }

    public static List<ProfileHistory> getVisitHistory(String baseEntityId) {

        SimpleDateFormat sdfDate = new SimpleDateFormat(OpdConstants.DateTimeFormat.dd_MMM_yyyy, Locale.ENGLISH);
        String todayDate = sdfDate.format(new Date());
        sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat sdfTime = new SimpleDateFormat(OpdConstants.DateTimeFormat.hh_mm, Locale.ENGLISH);
        sdfTime.setTimeZone(TimeZone.getTimeZone("GMT"));

        String sql = "SELECT * FROM opd_client_visits where base_entity_id = '" + baseEntityId + "' order by created_at desc , updated_at desc";

        DataMap<ProfileHistory> dataMap = cursor -> {

            ProfileHistory history = new ProfileHistory();

            Date visitCreateDate = new Date(Long.parseLong(getCursorValue(cursor, "created_at")));
            history.setID(getCursorValue(cursor, "form_submission_id"));
            String date = sdfDate.format(visitCreateDate);
            history.setEventDate(date.equals(todayDate) ? context().getStringResource(R.string.today) : date);
            history.setEventTime(sdfTime.format(visitCreateDate));
            history.setEventType(getCursorValue(cursor, "visit_type"));

            return history;
        };

        return readData(sql, dataMap);
    }

    public static Map<String, String> getSavedKeysForVisit(String visitId) {
        Map<String, String> visitMap = new HashMap<>();
        String sql = "SELECT visit_key, details,  human_readable_details FROM opd_client_visit_details WHERE visit_id = '" + visitId + "'";

        DataMap<Void> dataMap = cursor -> {
            String key = getCursorValue(cursor, "visit_key");
            String value = "";
            String humanReadableValue = getCursorValue(cursor, "human_readable_details");
            if (humanReadableValue == null || humanReadableValue.isEmpty()) {
                value = getCursorValue(cursor, "details");
            } else {
                value = humanReadableValue;
            }
            if (visitMap.containsKey(key)) {
                String oldValue = visitMap.get(key);
                value = oldValue + ", " + value;
            }
            visitMap.put(key, value);
            return null;
        };
        readData(sql, dataMap);
        return visitMap;
    }

    public static String getDateStringForId(String formSubmissionId) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(OpdConstants.DateTimeFormat.dd_MMM_yyyy, Locale.ENGLISH);
        String todayDate = sdfDate.format(new Date());

        String sql = "SELECT created_At FROM opd_client_visits where form_submission_id = '" + formSubmissionId + "'";
        List<String> returnDate = new ArrayList<>();

        DataMap<Void> dataMap = cursor -> {

            Date visitCreateDate = new Date(Long.parseLong(getCursorValue(cursor, "created_at")));
            sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = sdfDate.format(visitCreateDate);
            returnDate.add(date.equals(todayDate) ? context().getStringResource(R.string.today) : date);
            return null;
        };
        readData(sql, dataMap);
        return returnDate.get(0);
    }

    public static HIVStatus getLastHIVStatusForClient(String baseEntityId) {
        HIVStatus hivStatus = new HIVStatus();
        String sql = "SELECT * FROM opd_client_visit_details " +
                "WHERE visit_id IN (" +
                  getVisitIdQuery(baseEntityId) +
                ")";

        DataMap<Void> dataMap = cursor -> {
            String key = getCursorValue(cursor, "visit_key");
            populateHivStatus(hivStatus, key, cursor);
            return null;
        };
        readData(sql, dataMap);
        return hivStatus;
    }

    private static void populateHivStatus(HIVStatus hivStatus, String key, Cursor cursor) {
        switch (key) {
            case "hiv_tested_date":
                hivStatus.setLastDiagnosisDate(readCursorForVisitDetail(cursor));
                break;
            case "medical_conditions":
                hivStatus.setMedicalCondition(readCursorForVisitDetail(cursor));
                break;
            case "hiv_prev_status":
                hivStatus.setTestResult(readCursorForVisitDetail(cursor));
                break;
            case "hiv_diagnosis_date_unknown":
                hivStatus.setLastDiagnosisDateUnknown(readCursorForVisitDetail(cursor));
                break;
            case "hiv_prev_pos_art":
                hivStatus.setTakingART(readCursorForVisitDetail(cursor));
                break;
            default:
                // Do nothing
                break;
        }

    }

    private static VisitDetail readCursorForVisitDetail(Cursor cursor) {
        VisitDetail detail = new VisitDetail();
        detail.setVisitDetailsId(getCursorValue(cursor, VISIT_DETAILS_ID));
        detail.setVisitId(getCursorValue(cursor, VISIT_ID));
        detail.setVisitKey(getCursorValue(cursor, VISIT_KEY));
        detail.setParentCode(getCursorValue(cursor, PARENT_CODE));
        detail.setDetails(getCursorValue(cursor, DETAILS));
        detail.setHumanReadable(getCursorValue(cursor, HUMAN_READABLE));
        detail.setUpdatedAt(new Date(Long.parseLong(getCursorValue(cursor, UPDATED_AT))));
        detail.setCreatedAt(new Date(Long.parseLong(getCursorValue(cursor, CREATED_AT))));
        return detail;
    }

    public static boolean hasPreviousHIVStatus(String baseEntityId) {
        String sql = "SELECT * FROM opd_client_visit_details " +
                "WHERE visit_id IN (" +
                    getVisitIdQuery(baseEntityId) +
                ") AND visit_key =='medical_conditions' AND human_readable_details = 'HIV'";

        DataMap<Boolean> dataMap = cursor -> cursor.getCount() > 0;
        return readData(sql, dataMap).size() > 0;
    }

    private static String getVisitIdQuery(String baseEntityId) {
        return "SELECT visit_id " +
                "FROM opd_client_visits " +
                "WHERE base_entity_id = '" + baseEntityId + "' " +
                "AND visit_type = 'OPD_Diagnosis'" +
                "ORDER BY updated_at DESC " +
                "LIMIT 1";
    }
}

