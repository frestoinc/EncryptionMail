package com.frestoinc.maildemo.data.local.prefs;

import com.frestoinc.maildemo.data.enums.ClassificationEnum;
import com.frestoinc.maildemo.data.enums.CryptoEnum;

/**
 * Created by frestoinc on 16,December,2019 for MailDemo.
 */
public interface PreferenceHelper {

    void clearAll();

    ClassificationEnum getClassification();

    void setClassification(ClassificationEnum classification);

    CryptoEnum getEncryption();

    void setEncryption(CryptoEnum encryption);

    boolean getIsEcc();

    void setisEcc(boolean bool);

}
