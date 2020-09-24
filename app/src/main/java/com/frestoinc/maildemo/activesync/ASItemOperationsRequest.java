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

public class ASItemOperationsRequest extends ASCommandRequest {

    private String longId = null;
    private String serverId = null;
    private String collectionId = null;
    private String fileReference = null;
    private ASItemOperationsResponse.ItemOperationEnum ioEnum;
    private boolean withOption = false;

    public ASItemOperationsRequest(ASItemOperationsResponse.ItemOperationEnum e) {
        this.setCommand("ItemOperations");
        this.ioEnum = e;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getLongId() {
        return longId;
    }

    public void setLongId(String longId) {
        this.longId = longId;
    }

    public boolean isWithOption() {
        return withOption;
    }

    public void setWithOption(boolean withOption) {
        this.withOption = withOption;
    }

    protected ASItemOperationsResponse wrapHttpResponse(Response response)
            throws Exception {
        return new ASItemOperationsResponse(response, ioEnum);
    }

    protected void generateXmlPayload() throws Exception {
        if (getWbxmlBytes() != null) {
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document itemOperationsXml = builder.newDocument();

        Node itemOperationsNode = itemOperationsXml.createElementNS(Namespaces.itemOperationsNamespace,
                Xmlns.itemOperationsXmlns + ":ItemOperations");
        itemOperationsXml.appendChild(itemOperationsNode);

        Node fetchNode = itemOperationsXml.createElementNS(Namespaces.itemOperationsNamespace,
                Xmlns.itemOperationsXmlns + ":Fetch");
        itemOperationsNode.appendChild(fetchNode);

        Node storeNode = itemOperationsXml.createElementNS(Namespaces.itemOperationsNamespace,
                Xmlns.itemOperationsXmlns + ":Store");
        storeNode.setTextContent("Mailbox");
        fetchNode.appendChild(storeNode);

        if (getCollectionId() != null) {
            Node collectionIdNode = itemOperationsXml.createElementNS(Namespaces.airSyncNamespace,
                    Xmlns.airSyncXmlns + ":CollectionId");
            collectionIdNode.setTextContent(getCollectionId());
            fetchNode.appendChild(collectionIdNode);
        }

        if (getServerId() != null) {
            Node serverIdNode = itemOperationsXml.createElementNS(Namespaces.airSyncNamespace,
                    Xmlns.airSyncXmlns + ":ServerId");
            serverIdNode.setTextContent(getServerId());
            fetchNode.appendChild(serverIdNode);
        }

        if (getFileReference() != null) {
            Node fileReferenceNode = itemOperationsXml.createElementNS(Namespaces.airSyncBaseNamespace,
                    Xmlns.airSyncBaseXmlns + ":FileReference");
            fileReferenceNode.setTextContent(getFileReference());
            fetchNode.appendChild(fileReferenceNode);
        }

        if (getLongId() != null) {
            Node longIdReferenceNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                    Xmlns.searchXmlns + ":LongId");
            longIdReferenceNode.setTextContent(getLongId());
            fetchNode.appendChild(longIdReferenceNode);
        }

        if (this.withOption) {
            generateOptionEmailXml(fetchNode);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(itemOperationsXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }

    private void generateOptionEmailXml(Node rootNode) {
        Node optionsNode = rootNode.getOwnerDocument()
                .createElementNS(Namespaces.itemOperationsNamespace, Xmlns.itemOperationsXmlns + ":Options");
        rootNode.appendChild(optionsNode);

        //MIMESupport = 0 => NEVER SEND
        //MIMESupport = 1 => SEND MIME FOR S/MIME ONLY
        //MIMESupport = 2 => SEND FOR ALL
        Node mimeSupportNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":MIMESupport");
        mimeSupportNode.setTextContent(getFileReference() != null ? "1" : "2");
        optionsNode.appendChild(mimeSupportNode);

        Node bodyPreferenceNode = rootNode.getOwnerDocument()
                .createElementNS(Namespaces.airSyncBaseNamespace, Xmlns.airSyncBaseXmlns + ":BodyPreference");
        optionsNode.appendChild(bodyPreferenceNode);

        //type = 1 => PLAIN TEXT
        //type = 2 => HTML RESPONSE
        //type = 3 => RICH TEXT FORMAT
        //type = 4 => FULL MIME RESPONSE
        Node typeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":Type");
        switch (this.ioEnum) {
            case GET_BODY:
                typeNode.setTextContent("2");
                break;
            case GET_ATTACHMENT:
                typeNode.setTextContent("1");
                break;
            case GET_FULLBODY:
                typeNode.setTextContent("4");
                break;
            default:
                break;
        }
        bodyPreferenceNode.appendChild(typeNode);

        Node rmNode = rootNode.getOwnerDocument().createElementNS(Namespaces.rightsManagementNamespace,
                Xmlns.rightsManagementXmlns + ":RightsManagementSupport");
        rmNode.setTextContent("1");
        optionsNode.appendChild(rmNode);
    }
}
