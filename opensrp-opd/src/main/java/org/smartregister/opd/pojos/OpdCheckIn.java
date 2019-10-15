package org.smartregister.opd.pojos;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdCheckIn {

    private int id;
    private String eventId;
    private int visitId;
    private String baseEntityId;
    private String pregnancyStatus;
    private String hasHivTestPreviously;
    private String hivResultsPreviously;
    private String isTakingArt;
    private String currentHivResult;
    private String visitType;
    private String appointmentScheduledPreviously;
    private String appointmentDueDate;
    private int updatedAt;
    private int createdAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getPregnancyStatus() {
        return pregnancyStatus;
    }

    public void setPregnancyStatus(String pregnancyStatus) {
        this.pregnancyStatus = pregnancyStatus;
    }

    public String getHasHivTestPreviously() {
        return hasHivTestPreviously;
    }

    public void setHasHivTestPreviously(String hasHivTestPreviously) {
        this.hasHivTestPreviously = hasHivTestPreviously;
    }

    public String getHivResultsPreviously() {
        return hivResultsPreviously;
    }

    public void setHivResultsPreviously(String hivResultsPreviously) {
        this.hivResultsPreviously = hivResultsPreviously;
    }

    public String getIsTakingArt() {
        return isTakingArt;
    }

    public void setIsTakingArt(String isTakingArt) {
        this.isTakingArt = isTakingArt;
    }

    public String getCurrentHivResult() {
        return currentHivResult;
    }

    public void setCurrentHivResult(String currentHivResult) {
        this.currentHivResult = currentHivResult;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getAppointmentScheduledPreviously() {
        return appointmentScheduledPreviously;
    }

    public void setAppointmentScheduledPreviously(String appointmentScheduledPreviously) {
        this.appointmentScheduledPreviously = appointmentScheduledPreviously;
    }

    public String getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public void setAppointmentDueDate(String appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }
}
