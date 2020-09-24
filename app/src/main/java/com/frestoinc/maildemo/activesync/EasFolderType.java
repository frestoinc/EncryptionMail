/*
 *
 *
 *  * Copyright (C) 2006 The Android Open Source Project
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.frestoinc.maildemo.activesync;

import androidx.room.TypeConverter;

public enum EasFolderType {
    None(0),
    UserCreated(1),
    DefaultInbox(2),
    DefaultDrafts(3),
    DefaultDeleted(4),
    DefaultSent(5),
    DefaultOutbox(6),
    DefaultTasks(7),
    DefaultCalendar(8),
    DefaultContacts(9),
    DefaultNotes(10),
    DefaultJournal(11),
    UserCreatedMail(12),
    UserCreatedCalendar(13),
    UserCreatedContacts(14),
    UserCreatedTasks(15),
    UserCreatedjournal(16),
    UserCreatedNotes(17),
    UnknownFolder(18),
    RecipientInformationCache(19);

    private Integer code;

    EasFolderType(Integer code) {
        this.code = code;
    }

    @TypeConverter
    public static EasFolderType getType(Integer numeral) {
        for (EasFolderType f : values()) {
            if (f.code.equals(numeral)) {
                return f;
            }
        }
        return null;
    }

    @TypeConverter
    public static Integer getStatusInt(EasFolderType type) {
        if (type != null) {
            return type.code;
        }
        return null;
    }

    public static EasFolderType valueOf(int type) {
        EasFolderType[] vals = values();
        if (type >= 0 && type < vals.length)
            return vals[type];
        return null;
    }

    public static String valueOf(EasFolderType type) {
        switch (type) {
            case UserCreated:
                return "UserCreated";
            case DefaultInbox:
                return "DefaultInbox";
            case DefaultDrafts:
                return "DefaultDrafts";
            case DefaultDeleted:
                return "DefaultDeleted";
            case DefaultSent:
                return "DefaultSent";
            case DefaultOutbox:
                return "DefaultOutbox";
            case DefaultTasks:
                return "DefaultTasks";
            case DefaultCalendar:
                return "DefaultCalendar";
            case DefaultContacts:
                return "DefaultContacts";
            case DefaultNotes:
                return "DefaultNotes";
            case DefaultJournal:
                return "DefaultJournal";
            case UserCreatedMail:
                return "UserCreatedMail";
            case UserCreatedCalendar:
                return "UserCreatedCalendar";
            case UserCreatedContacts:
                return "UserCreatedContacts";
            case UserCreatedTasks:
                return "UserCreatedTasks";
            case UserCreatedjournal:
                return "UserCreatedjournal";
            case UserCreatedNotes:
                return "UserCreatedNotes";
            case UnknownFolder:
                return "UnknownFolder";
            default:
                return "None";
        }
    }
}