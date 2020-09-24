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

package com.frestoinc.maildemo.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.frestoinc.maildemo.activesync.EasFolderType;

@Entity(tableName = "eas_folders", indices = {@Index(value = {"id"}, unique = true)})
public class EasFolder {

    @PrimaryKey(autoGenerate = true)
    private int priKey;

    @NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @NonNull
    @ColumnInfo(name = "parent_id")
    private String parentId = "";

    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";

    @TypeConverters(EasFolderType.class)
    private EasFolderType type;

    @Ignore
    private String syncKey;

    public int getPriKey() {
        return priKey;
    }

    public void setPriKey(int priKey) {
        this.priKey = priKey;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getParentId() {
        return parentId;
    }

    public void setParentId(@NonNull String parentId) {
        this.parentId = parentId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(String syncKey) {
        this.syncKey = syncKey;
    }

    public EasFolderType getType() {
        return type;
    }

    public void setType(EasFolderType type) {
        this.type = type;
    }


}
