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

public class ASGalRequest extends ASCommandRequest {

    private String query;

    public ASGalRequest() {
        setCommand("Search");
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASGalResponse(response);
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

        Node searchNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Search");
        itemOperationsXml.appendChild(searchNode);

        Node storeNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Store");
        searchNode.appendChild(storeNode);

        Node nameNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Name");
        nameNode.setTextContent("GAL");
        storeNode.appendChild(nameNode);

        Node queryNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Query");
        queryNode.setTextContent(getQuery());
        storeNode.appendChild(queryNode);

        Node optionNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Options");
        storeNode.appendChild(optionNode);

        Node rangeNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":Range");
        rangeNode.setTextContent("0-5");
        optionNode.appendChild(rangeNode);

        Node rebuildResultsNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":RebuildResults");
        optionNode.appendChild(rebuildResultsNode);

        Node deepTraversalNode = itemOperationsXml.createElementNS(Namespaces.searchNamespace,
                Xmlns.searchXmlns + ":DeepTraversal");
        optionNode.appendChild(deepTraversalNode);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(itemOperationsXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }
}
