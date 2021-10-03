package com.brennaswitzer.cookbook.payload;

import com.brennaswitzer.cookbook.domain.AuthProvider;
import lombok.val;

public enum ProviderType {

    LOCAL(AuthProvider.local),
    FACEBOOK(AuthProvider.facebook),
    GOOGLE(AuthProvider.google),
    GITHUB(AuthProvider.github);

    private final AuthProvider provider;

    ProviderType(AuthProvider provider) {
        this.provider = provider;
    }

    public static ProviderType fromAuthProvider(AuthProvider provider) {
        for (val p : values()) {
            if (p.provider == provider) {
                return p;
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized '%s' provider", provider));
    }

}
