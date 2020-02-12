package org.smartregister.opd.contract;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.HashMap;

public interface OpdFormContract {
    void startForm(@NonNull String formName, @NonNull String caseId, @NonNull String entityTable, @Nullable CommonPersonObjectClient commonPersonObjectClient);

    HashMap<String, String> getInjectedFields(@NonNull String formName, @NonNull String caseId);

    HashMap<String, String> getInjectedFields(@NonNull String formName, @NonNull CommonPersonObjectClient commonPersonObjectClient);

}
