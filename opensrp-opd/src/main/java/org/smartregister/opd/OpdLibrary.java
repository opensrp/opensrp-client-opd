package org.smartregister.opd;

import android.support.annotation.NonNull;

import org.smartregister.Context;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.helper.AncRulesEngineHelper;
import org.smartregister.opd.repository.CheckInRepository;
import org.smartregister.opd.repository.VisitRepository;
import org.smartregister.opd.utils.FilePath;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdLibrary {

    private final Context context;

    private static OpdLibrary instance;
    private OpdConfiguration opdConfiguration;
    private final Repository repository;

    private UniqueIdRepository uniqueIdRepository;
    private CheckInRepository checkInRepository;
    private VisitRepository visitRepository;

    private Yaml yaml;

    private AncRulesEngineHelper ancRulesEngineHelper;

    protected OpdLibrary(@NonNull Context context, @NonNull OpdConfiguration opdConfiguration, @NonNull Repository repository) {
        this.context = context;
        this.opdConfiguration = opdConfiguration;
        this.repository = repository;

        // Initialize configs processor
        initializeYamlConfigs();
    }

    public static void init(Context context, @NonNull Repository repository, @NonNull OpdConfiguration opdConfiguration) {
        if (instance == null) {
            instance = new OpdLibrary(context, opdConfiguration, repository);
        }
    }

    @NonNull
    public Context context() {
        return context;
    }

    public static OpdLibrary getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance does not exist!!! Call "
                    + OpdLibrary.class.getName()
                    + ".init method in the onCreate method of "
                    + "your Application class");
        }
        return instance;
    }

    @NonNull
    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    @NonNull
    public CheckInRepository getCheckInRepository() {
        if (checkInRepository == null) {
            checkInRepository = new CheckInRepository(getRepository());
        }

        return checkInRepository;
    }

    @NonNull
    public VisitRepository getVisitRepository() {
        if (visitRepository == null) {
            visitRepository = new VisitRepository(getRepository());
        }

        return visitRepository;
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }

    @NonNull
    public OpdConfiguration getOpdConfiguration() {
        return opdConfiguration;
    }

    private void initializeYamlConfigs() {
        Constructor constructor = new Constructor(YamlConfig.class);
        TypeDescription customTypeDescription = new TypeDescription(YamlConfig.class);
        customTypeDescription.addPropertyParameters(YamlConfigItem.FIELD_CONTACT_SUMMARY_ITEMS, YamlConfigItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
    }

    public Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(
                DrishtiApplication.getInstance().getApplicationContext().getAssets().open((FilePath.FOLDER.CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }

    public AncRulesEngineHelper getAncRulesEngineHelper() {
        if (ancRulesEngineHelper == null) {
            ancRulesEngineHelper = new AncRulesEngineHelper(context.applicationContext().getApplicationContext());
        }
        return ancRulesEngineHelper;
    }
}
