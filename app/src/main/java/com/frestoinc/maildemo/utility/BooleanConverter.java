package com.frestoinc.maildemo.utility;

import androidx.room.TypeConverter;

/**
 * Created by frestoinc on 19,December,2019 for MailDemo.
 */
public class BooleanConverter {

    @TypeConverter
    public int fromBoolean(boolean b) {
        return b ? 1 : 0;
    }

    @TypeConverter
    public boolean fromInt(int i) {
        return i == 1;
    }
}
