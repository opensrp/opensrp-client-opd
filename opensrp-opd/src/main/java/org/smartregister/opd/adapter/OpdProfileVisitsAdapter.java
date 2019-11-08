package org.smartregister.opd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.opd.R;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdUtils;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdProfileVisitsAdapter extends RecyclerView.Adapter<OpdProfileVisitsAdapter.ViewHolder> {

    private List<OpdVisitSummary> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public OpdProfileVisitsAdapter(@NonNull Context context, @NonNull List<OpdVisitSummary> opdVisitSummaryList) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = opdVisitSummaryList;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.opd_profile_visit_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OpdVisitSummary opdVisitSummary = mData.get(position);

        if (opdVisitSummary.getVisitDate() != null) {
            holder.visitDate.setText(OpdUtils.convertDate(opdVisitSummary.getVisitDate(), OpdConstants.DateFormat.d_MMM_yyyy));
        }

        if (!TextUtils.isEmpty(opdVisitSummary.getTestName())) {
            setVisible(holder.testName, holder.testResult);
            holder.testName.setText(opdVisitSummary.getTestName());
            holder.testResult.setText(opdVisitSummary.getTestResult());
        } else {
            setGone(holder.testName, holder.testResult);
        }

        boolean isDiagnosis = !TextUtils.isEmpty(opdVisitSummary.getDiagnosis());
        setVisibility(isDiagnosis, holder.diagnosisLabel, holder.diagnosis);
        if (isDiagnosis) {
            holder.diagnosis.setText(opdVisitSummary.getDiagnosis());
        }

        boolean hasDiagnosisType = !TextUtils.isEmpty(opdVisitSummary.getDiagnosisType());
        setVisibility(hasDiagnosisType, holder.diagnosisTypeLabel, holder.diagnosisType);
        if (hasDiagnosisType) {
            holder.diagnosisType.setText(opdVisitSummary.getDiagnosisType());
        }

        boolean isDiseaseCode = !TextUtils.isEmpty(opdVisitSummary.getDisease());
        setVisibility(isDiseaseCode, holder.diseaseCodeLabel, holder.diseaseCode);
        if (isDiseaseCode) {
            holder.diseaseCode.setText(opdVisitSummary.getDisease());
        }

        boolean isTreatment = !TextUtils.isEmpty(opdVisitSummary.getTreatment());
        setVisibility(isTreatment, holder.treatmentLabel, holder.treatment);
        if (isTreatment) {
            holder.treatment.setText(opdVisitSummary.getTreatment());
        }

        boolean isDosage = !TextUtils.isEmpty(opdVisitSummary.getDosage());
        setVisibility(isDosage, holder.doseLabel, holder.dose);
        if (isDosage) {
            holder.dose.setText(opdVisitSummary.getDosage());
        }

        boolean isDuration = !TextUtils.isEmpty(opdVisitSummary.getDuration());
        setVisibility(isDuration, holder.durationLabel, holder.duration);
        if (isDuration) {
            holder.duration.setText(opdVisitSummary.getDuration());
        }
    }

    private void setVisibility(boolean isVisible, View... views) {
        for (View view: views) {
            view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void setVisible(View... views) {
        for (View view: views) {
            view.setVisibility(View.VISIBLE);
        }
    }


    private void setGone(View... views) {
        for (View view: views) {
            view.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView visitDate;
        private TextView testName;
        private TextView testResult;
        private TextView diagnosisLabel;
        private TextView diagnosis;
        private TextView diseaseCodeLabel;
        private TextView diseaseCode;
        private TextView treatmentLabel;
        private TextView treatment;
        private TextView doseLabel;
        private TextView dose;
        private TextView durationLabel;
        private TextView duration;
        private TextView diagnosisType;
        private TextView diseaseLabel;
        private TextView disease;
        private TextView diagnosisTypeLabel;

        ViewHolder(View itemView) {
            super(itemView);

            visitDate = itemView.findViewById(R.id.visit_row_date);
            testName = itemView.findViewById(R.id.visit_row_test_name);
            testResult = itemView.findViewById(R.id.visit_row_test_result);
            diagnosis = itemView.findViewById(R.id.visit_row_diagnosis);
            diagnosisLabel = itemView.findViewById(R.id.visit_row_diagnosis_label);
            diagnosisType = itemView.findViewById(R.id.visit_row_diagnosis_type);
            diagnosisTypeLabel = itemView.findViewById(R.id.visit_row_diagnosis_type_label);
            diseaseCodeLabel = itemView.findViewById(R.id.visit_row_disease_code_label);
            diseaseCode = itemView.findViewById(R.id.visit_row_disease_code);
//            disease = itemView.findViewById(R.id.visit_row_disease);
//            diseaseLabel = itemView.findViewById(R.id.visit_row_disease_label);
            treatmentLabel = itemView.findViewById(R.id.visit_row_treatment_label);
            treatment = itemView.findViewById(R.id.visit_row_treatment);
            doseLabel = itemView.findViewById(R.id.visit_row_dosage_label);
            dose = itemView.findViewById(R.id.visit_row_dosage);
            durationLabel = itemView.findViewById(R.id.visit_row_duration_label);
            duration = itemView.findViewById(R.id.visit_row_duration);


            parent = itemView;
        }
    }

}