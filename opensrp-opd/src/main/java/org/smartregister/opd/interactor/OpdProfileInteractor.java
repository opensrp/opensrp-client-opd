package org.smartregister.opd.interactor;

import android.support.annotation.NonNull;

import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.contract.OpdProfileActivityContract;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.utils.AppExecutors;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class OpdProfileInteractor implements OpdProfileActivityContract.Interactor {

    private OpdProfileActivityContract.Presenter mProfilePresenter;
    private AppExecutors appExecutors;

    public OpdProfileInteractor(@NonNull OpdProfileActivityContract.Presenter presenter) {
        this.mProfilePresenter = presenter;
        appExecutors = new AppExecutors();
    }

    @Override
    public void fetchSavedDiagnosisAndTreatmentForm(@NonNull final String baseEntityId, @NonNull final String entityTable) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm = OpdLibrary
                        .getInstance()
                        .getOpdDiagnosisAndTreatmentFormRepository()
                        .findOne(new OpdDiagnosisAndTreatmentForm(baseEntityId));

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mProfilePresenter instanceof OpdProfileActivityContract.InteractorCallBack) {
                            ((OpdProfileActivityContract.InteractorCallBack) mProfilePresenter)
                                    .onFetchedSavedDiagnosisAndTreatmentForm(diagnosisAndTreatmentForm, baseEntityId, entityTable);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        if (!isChangingConfiguration) {
            mProfilePresenter = null;
        }
    }
}
