package com.frestoinc.maildemo.data.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frestoinc on 10,January,2020 for MailDemo.
 */
public enum ClassificationEnum {

    @SerializedName("Unclassified")
    Unclassified("Unclassified"),
    @SerializedName("Restricted")
    Restricted("Restricted"),
    @SerializedName("Confidential")
    Confidential("Confidential"),
    @SerializedName("Secret")
    Secret("Secret");

    private static final Map map = new HashMap();

    static {
        for (ClassificationEnum classificationEnum : ClassificationEnum.values()) {
            map.put(classificationEnum.code, classificationEnum);
        }
    }

    private String code;

    ClassificationEnum(String code) {
        this.code = code;
    }

    public static String getStatusCode(ClassificationEnum classificationEnum) {
        if (classificationEnum != null) {
            return classificationEnum.code;
        }
        return null;
    }

    public static ClassificationEnum getStatusCode(String string) {
        return (ClassificationEnum) map.get(string);
    }
}
