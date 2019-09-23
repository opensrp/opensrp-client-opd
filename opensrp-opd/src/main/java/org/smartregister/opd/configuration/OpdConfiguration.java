package org.smartregister.opd.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdConfiguration {

    private Builder builder;

    private OpdConfiguration(@NonNull Builder builder) {
        this.builder = builder;

        setDefaults();
    }

    private void setDefaults() {
        if (builder.opdRegisterProviderMetadata == null) {
            builder.opdRegisterProviderMetadata = BaseOpdRegisterProviderMetadata.class;
        }
    }

    @NonNull
    public Class<? extends OpdRegisterProviderMetadata> getOpdRegisterProviderMetadata() {
        return builder.opdRegisterProviderMetadata;
    }

    @Nullable
    public Class<? extends OpdRegisterRowOptions> getOpdRegisterRowOptions() {
        return builder.opdRegisterRowOptions;
    }

    @NonNull
    public Class<? extends OpdRegisterQueryProviderContract> getOpdRegisterQueryProvider() {
        return builder.opdRegisterQueryProvider;
    }

    public static class Builder {

        @Nullable
        private Class<? extends OpdRegisterProviderMetadata> opdRegisterProviderMetadata;

        @Nullable
        private Class<? extends OpdRegisterRowOptions> opdRegisterRowOptions;

        @NonNull
        private Class<? extends OpdRegisterQueryProviderContract> opdRegisterQueryProvider;

        public Builder(@NonNull Class<? extends OpdRegisterQueryProviderContract> opdRegisterQueryProvider) {
            this.opdRegisterQueryProvider = opdRegisterQueryProvider;
        }

        public Builder setOpdRegisterProviderMetadata(@Nullable Class<? extends OpdRegisterProviderMetadata> opdRegisterProviderMetadata) {
            this.opdRegisterProviderMetadata = opdRegisterProviderMetadata;
            return this;
        }

        public Builder setOpdRegisterRowOptions(@Nullable Class<? extends OpdRegisterRowOptions> opdRegisterRowOptions) {
            this.opdRegisterRowOptions = opdRegisterRowOptions;
            return this;
        }

        public OpdConfiguration build() {
            return new OpdConfiguration(this);
        }
    }
}
