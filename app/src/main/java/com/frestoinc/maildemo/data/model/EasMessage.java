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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.frestoinc.maildemo.utility.BooleanConverter;

@Entity(tableName = "eas_messages", indices = {@Index(value = {"priKey"}, unique = true)})
public class EasMessage {

    @PrimaryKey(autoGenerate = true)
    private int priKey;

    @ColumnInfo(name = "folder_id")
    private String folderId;

    @ColumnInfo(name = "classification")
    private String classification;

    @ColumnInfo(name = "server_id")
    private String serverId;

    @ColumnInfo(name = "to")
    private String to;

    @ColumnInfo(name = "cc")
    private String cc;

    @ColumnInfo(name = "from")
    private String from;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "date_received")
    private String dateReceived;

    @ColumnInfo(name = "display_to")
    private String displayTo;

    @ColumnInfo(name = "thread_topic")
    private String threadTopic;

    @ColumnInfo(name = "importance")
    private int importance;

    @TypeConverters(BooleanConverter.class)
    private boolean isRead;

    @ColumnInfo(name = "conversation_id")
    private String conversationId;

    @ColumnInfo(name = "has_attachment")
    private boolean hasAttachment;

    @ColumnInfo(name = "attachment_name")
    private String attachmentName;

    @ColumnInfo(name = "attachment_reference")
    private String attachmentReference;

    @ColumnInfo(name = "attachment_method")
    private String attachmentMethod;

    @ColumnInfo(name = "attachment_size")
    private int attachmentSize;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] message;

    @TypeConverters(BooleanConverter.class)
    private boolean isSync = false;

    @TypeConverters(BooleanConverter.class)
    private boolean isDecrypted = false;

    @TypeConverters(BooleanConverter.class)
    private boolean isEncrypted = false;

    public int getPriKey() {
        return priKey;
    }

    public void setPriKey(int priKey) {
        this.priKey = priKey;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getDisplayTo() {
        return displayTo;
    }

    public void setDisplayTo(String displayTo) {
        this.displayTo = displayTo;
    }

    public String getThreadTopic() {
        return threadTopic;
    }

    public void setThreadTopic(String threadTopic) {
        this.threadTopic = threadTopic;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean hasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentReference() {
        return attachmentReference;
    }

    public void setAttachmentReference(String attachmentReference) {
        this.attachmentReference = attachmentReference;
    }

    public String getAttachmentMethod() {
        return attachmentMethod;
    }

    public void setAttachmentMethod(String attachmentMethod) {
        this.attachmentMethod = attachmentMethod;
    }

    public int getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(int attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public boolean isDecrypted() {
        return isDecrypted;
    }

    public void setDecrypted(boolean decrypted) {
        isDecrypted = decrypted;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
