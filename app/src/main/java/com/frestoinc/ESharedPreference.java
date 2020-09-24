package com.frestoinc;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

class ESharedPreference {

    private Context ctx;
    private SharedPreferences sharedPreferences;

    private ESharedPreference(Context ctx) {
        this.ctx = ctx;
        initSp();
    }

    private KeyGenParameterSpec getSpec() {
        return new KeyGenParameterSpec.Builder(MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                KeyProperties.PURPOSE_ENCRYPT)
                .setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
    }

    private MasterKey getMasterKey() throws GeneralSecurityException, IOException {
        return new MasterKey.Builder(ctx)
                .setKeyGenParameterSpec(getSpec())
                .setUserAuthenticationRequired(false)
                .build();
    }

    private SharedPreferences getSharedPreferences() {
        return EncryptedSharedPreferences.create(
                ctx,
                "encrypted_gtriip",
                getMasterKey(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
    }

    private void initSp() {
        this.sharedPreferences = getSharedPreferences();

    }

    public String readKey(String key) {
        return sharedPreferences.getString(key, "");
    }
}
