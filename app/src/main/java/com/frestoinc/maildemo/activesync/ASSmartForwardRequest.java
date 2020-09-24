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

public class ASSmartForwardRequest extends ASCommandRequest {

    private String clientId = null;
    private String mimeContent = null;
    private String folderId = null;
    private String itemId = null;


    public ASSmartForwardRequest() {
        this.setCommand("SmartForward");
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMimeContent() {
        return mimeContent;
    }

    public void setMimeContent(String mimeContent) {
        this.mimeContent = mimeContent;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    protected ASMailStatusResponse wrapHttpResponse(Response response)
            throws Exception {
        return new ASMailStatusResponse(response);
    }

    @Override
    protected void generateXmlPayload() throws Exception {
        if (getWbxmlBytes() != null) {
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document itemOperationsXml = builder.newDocument();

        Node smartForwardNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":SmartForward");
        itemOperationsXml.appendChild(smartForwardNode);

        Node clientIdNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":ClientId");
        clientIdNode.setTextContent(getClientId());
        smartForwardNode.appendChild(clientIdNode);

        Node saveItemsNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":SaveInSentItems");
        smartForwardNode.appendChild(saveItemsNode);

        Node sourceNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":Source");
        smartForwardNode.appendChild(sourceNode);

        Node folderIdNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":FolderId");
        folderIdNode.setTextContent(getFolderId());
        sourceNode.appendChild(folderIdNode);

        Node itemIdNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":ItemId");
        itemIdNode.setTextContent(getItemId());
        sourceNode.appendChild(itemIdNode);

        Node mimeNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":MIME");
        //mimeNode.appendChild(itemOperationsXml.createCDATASection(getMimeContent()));
        mimeNode.setTextContent(getMimeContent());
        smartForwardNode.appendChild(mimeNode);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(itemOperationsXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }
}
