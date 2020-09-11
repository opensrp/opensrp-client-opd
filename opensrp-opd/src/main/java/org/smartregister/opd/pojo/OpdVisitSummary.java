package org.smartregister.opd.pojo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-31
 */

public class OpdVisitSummary extends OpdVisitSummaryResultModel {

    private HashSet<String> diseases = new HashSet<>();
    private HashMap<String, Treatment> treatments = new HashMap<>();
    private HashMap<String, List<Test>> tests = new HashMap<>();

    public HashSet<String> getDiseases() {
        return diseases;
    }

    @Override
    public void setDisease(String disease) {
        super.setDisease(disease);

        addDisease(disease);
    }

    @Override
    public void setTreatment(Treatment treatment) {
        super.setTreatment(treatment);
        addTreatment(treatment);
    }

    public void addDisease(@NonNull String diseaseCode) {
        diseases.add(diseaseCode);
    }

    public HashMap<String, Treatment> getTreatments() {
        return treatments;
    }

    public void addTreatment(@NonNull Treatment treatment) {
        treatments.put(treatment.getMedicine(), treatment);
    }

    @Override
    public void setTest(Test test) {
        super.setTest(test);
        addTest(test);
    }

    public HashMap<String, List<Test>> getTests() {
        return tests;
    }

    public void addTest(@NonNull Test test) {
        List<Test> testList = tests.get(test.getType()) == null ? new ArrayList<>() : tests.get(test.getType());
        if (!testList.contains(test)) {
            testList.add(test);
            tests.put(test.getType(), testList);
        }
    }
}
