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

/*
<?xml version="1.0" encoding="UTF-8"?>
 <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:rm=
     "RightsManagement" xmlns="ComposeMail" targetNamespace="ComposeMail"
     elementFormDefault="qualified" attributeFormDefault="unqualified">
   <xs:include schemaLocation="ComposeMail.xsd"/>
   <xs:import namespace="RightsManagement" schemaLocation=
       "RightsManagement.xsd"/>
   <xs:element name="SendMail">
     <xs:complexType>
       <xs:all>
         <xs:element ref="ClientId"/>
         <xs:element ref="AccountId" minOccurs="0"/>
         <xs:element ref="SaveInSentItems" minOccurs="0"/>
         <xs:element ref="Mime"/>
         <xs:element ref="rm:TemplateID" minOccurs="0"/>
       </xs:all>
     </xs:complexType>
   </xs:element>
 </xs:schema>
 */
public class ASSendMailRequest extends ASCommandRequest {

    private String clientId = null;
    private String mimeContent = null;

    public ASSendMailRequest() {
        this.setCommand("SendMail");
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

        Node sendMailNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":SendMail");
        itemOperationsXml.appendChild(sendMailNode);

        Node clientIdNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":ClientId");
        //clientIdNode.appendChild(itemOperationsXml.createCDATASection(getClientId()));
        clientIdNode.setTextContent(getClientId());
        sendMailNode.appendChild(clientIdNode);

        Node saveItemsNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":SaveInSentItems");
        sendMailNode.appendChild(saveItemsNode);

        Node mimeNode = itemOperationsXml.createElementNS(Namespaces.composeMailNamespace,
                Xmlns.composeMailXmlns + ":MIME");
        mimeNode.appendChild(itemOperationsXml.createCDATASection(getMimeContent()));
        sendMailNode.appendChild(mimeNode);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(itemOperationsXml), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }
}
