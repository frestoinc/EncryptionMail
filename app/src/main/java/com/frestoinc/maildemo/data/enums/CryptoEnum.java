package com.frestoinc.maildemo.data.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frestoinc on 09,January,2020 for MailDemo.
 */
public enum CryptoEnum {

    @SerializedName("Normal")
    Normal("Normal"),
    @SerializedName("Sign")
    Sign("Sign"),
    @SerializedName("SignAndEncrypt")
    SignAndEncrypt("SignAndEncrypt");

    private static final Map map = new HashMap();

    static {
        for (CryptoEnum cryptoEnum : CryptoEnum.values()) {
            map.put(cryptoEnum.code, cryptoEnum);
        }
    }

    private String code;

    CryptoEnum(String code) {
        this.code = code;
    }

    public static String getStatusCode(CryptoEnum cryptoEnum) {
        if (cryptoEnum != null) {
            return cryptoEnum.code;
        }
        return null;
    }

    public static CryptoEnum getStatusCode(String string) {
        return (CryptoEnum) map.get(string);
    }
}
