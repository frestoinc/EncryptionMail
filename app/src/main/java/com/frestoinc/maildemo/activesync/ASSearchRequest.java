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

public class ASSearchRequest extends ASCommandRequest {

    private String collectinId;
    private String reference;
    private boolean option = false;
    private int status;

    public ASSearchRequest() {
        this.setCommand("Search");
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCollectinId() {
        return collectinId;
    }

    public void setCollectinId(String collectinId) {
        this.collectinId = collectinId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOption() {
        return option;
    }

    public void setOption(boolean option) {
        this.option = option;
    }

    protected ASSearchResponse wrapHttpResponse(Response response)
            throws Exception {
        return new ASSearchResponse(response);
    }

    protected void generateXmlPayload() throws Exception {
        if (getWbxmlBytes() != null) {
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document searchXml = builder.newDocument();

        Node searchNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Search");
        searchXml.appendChild(searchNode);

        Node storeNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Store");
        searchNode.appendChild(storeNode);

        Node nameNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Name");
        nameNode.setTextContent("Mailbox");
        storeNode.appendChild(nameNode);

        Node queryNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Query");
        storeNode.appendChild(queryNode);

        Node andNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":And");
        queryNode.appendChild(andNode);

        Node collectionIdNode = searchXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":CollectionId");
        collectionIdNode.setTextContent(getCollectinId());
        andNode.appendChild(collectionIdNode);

        Node freeTextNode = searchXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":FreeText");
        freeTextNode.setTextContent(getReference());
        andNode.appendChild(freeTextNode);

        if (this.option) {
            generateOptionEmailXml(storeNode);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(searchXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }

    private void generateOptionEmailXml(Node rootNode) {
        Node optionsNode = rootNode.getOwnerDocument().createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Options");
        rootNode.appendChild(optionsNode);

        Node rebuildNode = rootNode.getOwnerDocument().createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":RebuildResults");
        optionsNode.appendChild(rebuildNode);

        Node deepTraversalNode = rootNode.getOwnerDocument().createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":DeepTraversal");
        optionsNode.appendChild(deepTraversalNode);

        Node rangeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Range");
        rangeNode.setTextContent("0-0");
        optionsNode.appendChild(rangeNode);

    /*Node bodyPartPreferenceNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
        Xmlns.airSyncBaseXmlns + ":BodyPartPreference");
    optionsNode.appendChild(bodyPartPreferenceNode);

    Node typeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
        Xmlns.airSyncBaseXmlns + ":Type");
    typeNode.setTextContent("2");
    bodyPartPreferenceNode.appendChild(typeNode);

    Node truncationSizeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
        Xmlns.airSyncBaseXmlns + ":TruncationSize");
    truncationSizeNode.setTextContent("1024");
    bodyPartPreferenceNode.appendChild(truncationSizeNode);

    Node previewNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
        Xmlns.airSyncBaseXmlns + ":Preview");
    previewNode.setTextContent("120");
    bodyPartPreferenceNode.appendChild(previewNode);

    Node mimeSupportNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
        Xmlns.airSyncXmlns + ":MIMESupport");
    mimeSupportNode.setTextContent("1");
    optionsNode.appendChild(mimeSupportNode);*/
    }
}
