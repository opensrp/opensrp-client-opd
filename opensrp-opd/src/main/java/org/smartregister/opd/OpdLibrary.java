package org.smartregister.opd;

import android.support.annotation.NonNull;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;
import org.smartregister.view.LocationPickerView;

import id.zelory.compressor.Compressor;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdLibrary {

    private final Context context;

    private static OpdLibrary instance;
    private OpdConfiguration opdConfiguration;
    private final Repository repository;
    private ECSyncHelper syncHelper;

    private UniqueIdRepository uniqueIdRepository;
    private int applicationVersion;
    private int databaseVersion;

    private EventClientRepository eventClientRepository;

    private ClientProcessorForJava clientProcessorForJava;
    private Compressor compressor;
    private LocationPickerView locationPickerView;

    protected OpdLibrary(@NonNull Context context, @NonNull OpdConfiguration opdConfiguration, @NonNull Repository repository) {
        this.context = context;
        this.opdConfiguration = opdConfiguration;
        this.repository = repository;
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

    public UniqueIdRepository getUniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository(getRepository());
        }
        return uniqueIdRepository;
    }

    public void setOpdConfiguration(OpdConfiguration opdConfiguration) {
        this.opdConfiguration = opdConfiguration;
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }


    @NonNull
    public ECSyncHelper getEcSyncHelper() {
        if (syncHelper == null) {
            syncHelper = ECSyncHelper.getInstance(context().applicationContext());
        }
        return syncHelper;
    }

    @NonNull
    public OpdConfiguration getOpdConfiguration() {
        return opdConfiguration;
    }


    public ClientProcessorForJava getClientProcessorForJava() {
        if (clientProcessorForJava == null) {
            clientProcessorForJava = ClientProcessorForJava.getInstance(context().applicationContext());
        }
        return clientProcessorForJava;
    }

    public void setClientProcessorForJava(ClientProcessorForJava clientProcessorForJava) {
        this.clientProcessorForJava = clientProcessorForJava;
    }

    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = Compressor.getDefault(context().applicationContext());
        }
        return compressor;
    }

    public LocationPickerView getLocationPickerView(android.content.Context context) {
        if (locationPickerView == null) {
            locationPickerView = new LocationPickerView(context);
            locationPickerView.init();
        }
        return locationPickerView;
    }

    public AppProperties getProperties() {
        return CoreLibrary.getInstance().context().getAppProperties();
    }

    public void setUniqueIdRepository(UniqueIdRepository uniqueIdRepository) {
        this.uniqueIdRepository = uniqueIdRepository;
    }

    public EventClientRepository getEventClientRepository() {
        return eventClientRepository;
    }

    public void setEventClientRepository(EventClientRepository eventClientRepository) {
        this.eventClientRepository = eventClientRepository;
    }

    public int getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(int applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }
}
