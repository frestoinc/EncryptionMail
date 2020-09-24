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

import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

public class ASFirstSyncKeyResponse extends ASCommandResponse {

    private SyncStatus status = SyncStatus.None;
    private Document responseXml = null;
    private String syncKey = null;

    public ASFirstSyncKeyResponse(Response response) throws Exception {
        super(response);
        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);
            setStatus();
            setSyncKey();
        }
    }

    public SyncStatus getStatus() {
        return status;
    }

    public String getSyncKey() {
        return syncKey;
    }

    private void setSyncKey() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node syncKeyNode = (Node) xpath.evaluate(".//AirSync:Sync//AirSync:SyncKey", responseXml, XPathConstants.NODE);
        if (syncKeyNode != null) {
            this.syncKey = syncKeyNode.getTextContent();
        }
    }

    private void setStatus() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode = (Node) xpath.evaluate(".//AirSync:Sync//AirSync:Status", responseXml, XPathConstants.NODE);
        if (statusNode != null) {
            this.status = SyncStatus.value(Integer.parseInt((statusNode.getTextContent())));
        }
    }

    public enum SyncStatus {
        // This enumeration covers the possible Status
        // values for FolderSync responses.
        None,
        Success,
        __dummyEnum__2,
        InvalidSyncKey,
        ProtocolError,
        ServerError,
        ClientServerConversionError,
        ServerOverwriteConflict,
        ObjectNotFound,
        SyncCannotComplete,
        __dummyEnum__10,
        __dummyEnum__11,
        FolderHierarchyOutOfDate,
        PartialSyncNotValid,
        InvalidDelayValue,
        InvalidSync,
        Retry;

        public static SyncStatus value(int i) {
            if (i >= 0 && i <= values().length) {
                return values()[i];
            }
            return null;
        }
    }
}
