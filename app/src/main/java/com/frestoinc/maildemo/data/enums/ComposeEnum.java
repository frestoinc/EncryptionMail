package com.frestoinc.maildemo.data.enums;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frestoinc on 25,December,2019 for MailDemo.
 */
public enum ComposeEnum {

    @SerializedName("New")
    NEW(0),
    @SerializedName("Reply")
    REPLY(1),
    @SerializedName("Reply All")
    REPLY_ALL(2),
    @SerializedName("Forward")
    FORWARD(3);

    private static final Map map = new HashMap();

    static {
        for (ComposeEnum composeEnum : ComposeEnum.values()) {
            map.put(composeEnum.code, composeEnum);
        }
    }

    private Integer code;

    ComposeEnum(Integer code) {
        this.code = code;
    }

    public static Integer getStatusCode(ComposeEnum composeEnum) {
        if (composeEnum != null) {
            return composeEnum.code;
        }
        return null;
    }

    public static ComposeEnum valueOf(Integer code) {
        return (ComposeEnum) map.get(code);
    }
}


