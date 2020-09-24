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

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Response;

public class ASMailSyncRequest extends ASCommandRequest {

    private String syncKey = null;
    private String folderId = null;
    private boolean option = false;

    public ASMailSyncRequest() {
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

    public boolean isOption() {
        return option;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    @Override
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASMailSyncResponse(response);
    }

    @Override
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
        Document syncXml = builder.newDocument();

        // XmlDeclaration xmlDeclaration = syncXml.CreateXmlDeclaration("1.0",
        // "utf-8", null);
        // syncXml.InsertBefore(xmlDeclaration, null);

        Node syncNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Sync");
        syncXml.appendChild(syncNode);
        // Only add a collections node if there are folders in the request.
        // If omitting, there should be a Partial element.
        if (syncKey == null || TextUtils.isEmpty(syncKey)) {
            throw new Exception("Sync requests must be supplied with a sync key.");
        }

        Node collectionsNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Collections");
        syncNode.appendChild(collectionsNode);


        Node collectionNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Collection");
        collectionsNode.appendChild(collectionNode);

        Node syncKeyNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":SyncKey");
        syncKeyNode.setTextContent(getSyncKey());
        collectionNode.appendChild(syncKeyNode);

        Node collectionIdNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":CollectionId");
        collectionIdNode.setTextContent(getFolderId());
        collectionNode.appendChild(collectionIdNode);

        Node deletesAsMovesNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":DeletesAsMoves");
        deletesAsMovesNode.setTextContent("1");
        collectionNode.appendChild(deletesAsMovesNode);

        /*Node getChangesNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":GetChanges");
        //getChangesNode.setTextContent("1");
        collectionNode.appendChild(getChangesNode);*/

        /*Node folderWindowSizeNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":WindowSize");
        folderWindowSizeNode.setTextContent(((Integer) this.windowSize).toString());
        collectionNode.appendChild(folderWindowSizeNode);*/

        if (this.option) {
            generateOptionEmailXml(collectionNode);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(syncXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }

    private void generateOptionEmailXml(Node rootNode) {
        Node optionsNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Options");
        rootNode.appendChild(optionsNode);

        //MIMESupport = 0 => NEVER SEND
        //MIMESupport = 1 => SEND MIME FOR S/MIME ONLY
        //MIMESupport = 2 => SEND FOR ALL
        Node mimeSupportNode =
                rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
                        Xmlns.airSyncXmlns + ":MIMESupport");
        mimeSupportNode.setTextContent("1");
        optionsNode.appendChild(mimeSupportNode);

        Node mimeTruncationNode =
                rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
                        Xmlns.airSyncXmlns + ":MIMETruncation");
        mimeTruncationNode.setTextContent("0");
        optionsNode.appendChild(mimeTruncationNode);

        Node bodyPreferenceNode =
                rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                        Xmlns.airSyncBaseXmlns + ":BodyPreference");
        optionsNode.appendChild(bodyPreferenceNode);

        //type = 1 => PLAIN TEXT
        //type = 2 => HTML RESPONSE
        //type = 3 => RICH TEXT FORMAT
        //type = 4 => FULL MIME RESPONSE
        Node typeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":Type");
        typeNode.setTextContent("1");
        bodyPreferenceNode.appendChild(typeNode);

        Node truncationSizeNode =
                rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                        Xmlns.airSyncBaseXmlns + ":TruncationSize");
        truncationSizeNode.setTextContent("1000");
        bodyPreferenceNode.appendChild(truncationSizeNode);
    }
}
