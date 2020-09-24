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

public class ASFirstSyncKeyRequest extends ASCommandRequest {

    private String syncKey = null;
    private String folderId = null;

    public ASFirstSyncKeyRequest() {
        this.setCommand("Sync");
    }

    public String getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(String syncKey) {
        this.syncKey = syncKey;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    @Override
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASFirstSyncKeyResponse(response);
    }

    @Override
    protected void generateXmlPayload() throws Exception {
        if (getWbxmlBytes() != null) {
            return;
        }
        // Otherwise, use the properties to build the XML and then WBXML encode
        // it
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document syncXML = builder.newDocument();

        Node syncNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Sync");
        syncXML.appendChild(syncNode);

        Node collectionsNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Collections");
        syncNode.appendChild(collectionsNode);

        Node collectionNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Collection");
        collectionsNode.appendChild(collectionNode);

        Node syncKeyNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":SyncKey");
        syncKeyNode.setTextContent(getSyncKey() == null ? "0" : getSyncKey());
        collectionNode.appendChild(syncKeyNode);

        Node collectionIdNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":CollectionId");
        collectionIdNode.setTextContent(getFolderId());
        collectionNode.appendChild(collectionIdNode);

        Node deletesAsMovesNode = syncXML.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":DeletesAsMoves");
        deletesAsMovesNode.setTextContent("1");
        collectionNode.appendChild(deletesAsMovesNode);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(syncXML), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }
}
