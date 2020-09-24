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

//
// Translated by CS2J (http://www.cs2j.com): 06/11/2015 15:37:33
//

package com.frestoinc.maildemo.activesync;

import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Response;

// This class represents the FolderSync command
// request specified in MS-ASCMD section 2.2.2.4.1.
public class ASFolderSyncRequest extends ASCommandRequest {
    private String syncKey = "0";

    public ASFolderSyncRequest() {
        setCommand("FolderSync");
    }

    public String getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(String value) {
        syncKey = value;
    }

    // This function generates an ASFolderSyncResponse from an
    // HTTP response.
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASFolderSyncResponse(response);
    }

    // This function generates the XML request body
    // for the FolderSync request.
    protected void generateXmlPayload() throws Exception {
        // If WBXML was explicitly set, use that
        if (getWbxmlBytes() != null) {
            return;
        }

        // Otherwise, use the properties to build the XML and then WBXML encode
        // it
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document folderSyncXML = builder.newDocument();

        // XmlDeclaration xmlDeclaration =
        // folderSyncXML.CreateXmlDeclaration("1.0", "utf-8", null);
        // folderSyncXML.insertBefore(xmlDeclaration, null);
        Node folderSyncNode = folderSyncXML.createElementNS(Namespaces.folderHierarchyNamespace,
                Xmlns.folderHierarchyXmlns + ":FolderSync");
        folderSyncXML.appendChild(folderSyncNode);
        if (TextUtils.isEmpty(syncKey)) {
            syncKey = "0";
        }

        Node syncKeyNode = folderSyncXML.createElementNS(Namespaces.folderHierarchyNamespace,
                Xmlns.folderHierarchyXmlns + ":SyncKey");
        syncKeyNode.setTextContent(syncKey);
        folderSyncNode.appendChild(syncKeyNode);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(folderSyncNode), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }

}
