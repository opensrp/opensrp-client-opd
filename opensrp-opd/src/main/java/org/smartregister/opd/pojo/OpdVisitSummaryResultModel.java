package org.smartregister.opd.pojo;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-13
 */
public class OpdVisitSummaryResultModel {

    private OpdVisitSummary.Test test;
    private String diagnosis;
    private String diseaseCode;
    private OpdVisitSummary.Treatment treatment;
    private String dosage;
    private String duration;
    private Date visitDate;
    private String disease;
    private String diagnosisType;

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    public OpdVisitSummary.Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(OpdVisitSummary.Treatment treatment) {
        this.treatment = treatment;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public static class Treatment {

        private String medicine;
        private String dosage;
        private String duration;
        private String frequency;

        public String getMedicine() {
            return medicine;
        }

        public void setMedicine(String medicine) {
            this.medicine = medicine;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }
    }

    public static class Test {
        private String name;
        private String result;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
