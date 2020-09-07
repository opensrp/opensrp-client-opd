package org.smartregister.opd.pojo;

import androidx.annotation.Nullable;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-13
 */
public class OpdVisitSummaryResultModel {

    private OpdVisitSummary.Test test;

    private String diagnosisType;
    private String diagnosis;
    private String diseaseCode;
    private String isDiagnosisSame;

    private String treatmentType;
    private String treatmentTypeSpecify;
    private String specialInstructions;
    private OpdVisitSummary.Treatment treatment;

    private String dischargedAlive;
    private String dischargedHome;
    private String referral;
    private String referralLocation;
    private String referralLocationSpecify;


    private Date visitDate;
    private String disease;

    public String getReferralLocationSpecify() {
        return referralLocationSpecify;
    }

    public void setReferralLocationSpecify(String referralLocationSpecify) {
        this.referralLocationSpecify = referralLocationSpecify;
    }

    public String getDischargedAlive() {
        return dischargedAlive;
    }

    public void setDischargedAlive(String dischargedAlive) {
        this.dischargedAlive = dischargedAlive;
    }

    public String getDischargedHome() {
        return dischargedHome;
    }

    public void setDischargedHome(String dischargedHome) {
        this.dischargedHome = dischargedHome;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getReferralLocation() {
        return referralLocation;
    }

    public void setReferralLocation(String referralLocation) {
        this.referralLocation = referralLocation;
    }

    public Test getTest() {
        return test;
    }

    public String getIsDiagnosisSame() {
        return isDiagnosisSame;
    }

    public void setIsDiagnosisSame(String isDiagnosisSame) {
        this.isDiagnosisSame = isDiagnosisSame;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getTreatmentTypeSpecify() {
        return treatmentTypeSpecify;
    }

    public void setTreatmentTypeSpecify(String treatmentTypeSpecify) {
        this.treatmentTypeSpecify = treatmentTypeSpecify;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
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
        private String type;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null) {
                return false;
            }
            String key = ((Test) obj).getName();
            return this.getName().equals(key);
        }
    }
}
