package org.smartregister.opd.domain;

import org.smartregister.opd.model.VisitDetail;

public class HIVStatus {

    private VisitDetail lastDiagnosisDate;
    private VisitDetail lastDiagnosisDateUnknown;
    private VisitDetail testResult;
    private VisitDetail medicalCondition;
    private VisitDetail takingART;

    public VisitDetail getLastDiagnosisDate() {
        return lastDiagnosisDate;
    }

    public void setLastDiagnosisDate(VisitDetail lastDiagnosisDate) {
        this.lastDiagnosisDate = lastDiagnosisDate;
    }

    public VisitDetail getLastDiagnosisDateUnknown() {
        return lastDiagnosisDateUnknown;
    }

    public void setLastDiagnosisDateUnknown(VisitDetail lastDiagnosisDateUnknown) {
        this.lastDiagnosisDateUnknown = lastDiagnosisDateUnknown;
    }

    public VisitDetail getTestResult() {
        return testResult;
    }

    public void setTestResult(VisitDetail testResult) {
        this.testResult = testResult;
    }

    public VisitDetail getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(VisitDetail medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public VisitDetail getTakingART() {
        return takingART;
    }

    public void setTakingART(VisitDetail takingART) {
        this.takingART = takingART;
    }
}
