package com.frestoinc.maildemo.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.frestoinc.maildemo.data.enums.ClassificationEnum;
import com.frestoinc.maildemo.data.enums.CryptoEnum;
import com.frestoinc.maildemo.di.scope.AppScope;
import com.frestoinc.maildemo.utility.Constants;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by frestoinc on 16,December,2019 for MailDemo.
 */
@AppScope
public class AppPreferenceHelper implements PreferenceHelper {

    private SharedPreferences sp;

    @Inject
    public AppPreferenceHelper(Context ctx) {
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Timber.e(sp == null ? "sp is null" : "sp no null");
    }

    @Override
    public void clearAll() {
        sp.edit().clear().apply();
    }

    @Override
    public ClassificationEnum getClassification() {
        return ClassificationEnum.getStatusCode(sp.getString(Constants.CLASSIFICATION_TYPE, "Unclassified"));
    }

    @Override
    public void setClassification(ClassificationEnum classification) {
        sp.edit().putString(Constants.CLASSIFICATION_TYPE, classification.toString()).apply();
    }

    @Override
    public CryptoEnum getEncryption() {
        return CryptoEnum.getStatusCode(sp.getString(Constants.ENCRYPT_TYPE, "Normal"));
    }

    @Override
    public void setEncryption(CryptoEnum encryption) {
        sp.edit().putString(Constants.ENCRYPT_TYPE, encryption.toString()).apply();
    }

    @Override
    public boolean getIsEcc() {
        return sp.getBoolean(Constants.ALGO_TYPE, true);
    }

    @Override
    public void setisEcc(boolean bool) {
        sp.edit().putBoolean(Constants.ALGO_TYPE, bool).apply();
    }

}
