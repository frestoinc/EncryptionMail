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

// This class represents a Provision command request
// as specified in MS-ASPROV section 2.2.

public class ASProvisionRequest extends ASCommandRequest {

    private boolean isAcknowledgement = false;
    private boolean isRemoteWipe = false;
    private int status = 0;
    private Device provisionDevice = null;

    public ASProvisionRequest() {
        setCommand("Provision");
    }

    public boolean getIsAcknowledgement() {
        return isAcknowledgement;
    }

    public void setIsAcknowledgement(boolean value) {
        isAcknowledgement = value;
    }

    public boolean getIsRemoteWipe() {
        return isRemoteWipe;
    }

    public void setIsRemoteWipe(boolean value) {
        isRemoteWipe = value;
    }

    public Device getProvisionDevice() {
        return provisionDevice;
    }

    public void setProvisionDevice(Device value) {
        provisionDevice = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int value) {
        status = value;
    }

    // This function generates an ASProvisionResponse from an
    // HTTP response.
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASProvisionResponse(response);
    }

    // This function generates the XML request body
    // for the Provision request.
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
        Document provisionXML = builder.newDocument();

        // XmlDeclaration xmlDeclaration =
        // provisionXML.CreateXmlDeclaration("1.0", "utf-8", null);
        // provisionXML.InsertBefore(xmlDeclaration, null);
        Node provisionNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                Xmlns.provisionXmlns + ":Provision");
        provisionXML.appendChild(provisionNode);
        // If this is a remote wipe acknowledgment, use
        // the remote wipe acknowledgment format
        // specified in MS-ASPROV section 3.1.5.1.2.2.
        if (isRemoteWipe) {
            // Build response to RemoteWipe request
            Node remoteWipeNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                    Xmlns.provisionXmlns + ":RemoteWipe");
            provisionNode.appendChild(remoteWipeNode);
            // Always return success for remote wipe
            Node statusNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                    Xmlns.provisionXmlns + ":Status");
            statusNode.setTextContent("1");
            remoteWipeNode.appendChild(statusNode);
        } else {
            // The other two possibilities here are
            // an initial request or an acknowledgment
            // of a policy received in a previous Provision
            // response.
            if (!isAcknowledgement) {
                // A DeviceInformation node is only included in the initial
                // request.
                if (provisionDevice != null) {
                    Node deviceNode =
                            provisionXML.importNode(provisionDevice.getDeviceInformationNode(), true);
                    provisionNode.appendChild(deviceNode);
                }
            }

            // These nodes are included in both scenarios.
            Node policiesNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                    Xmlns.provisionXmlns + ":Policies");
            provisionNode.appendChild(policiesNode);
            Node policyNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                    Xmlns.provisionXmlns + ":Policy");
            policiesNode.appendChild(policyNode);
            Node policyTypeNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                    Xmlns.provisionXmlns + ":PolicyType");
            String policyType = "MS-EAS-Provisioning-WBXML";
            policyTypeNode.setTextContent(policyType);
            policyNode.appendChild(policyTypeNode);
            if (isAcknowledgement) {
                // Need to also include policy key and status
                // when acknowledging
                Node policyKeyNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                        Xmlns.provisionXmlns + ":PolicyKey");
                policyKeyNode.setTextContent(((Long) getPolicyKey()).toString());
                policyNode.appendChild(policyKeyNode);
                Node statusNode = provisionXML.createElementNS(Namespaces.provisionNamespace,
                        Xmlns.provisionXmlns + ":Status");
                statusNode.setTextContent(((Integer) status).toString());
                policyNode.appendChild(statusNode);
            }

        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(provisionXML), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        setXmlString(output);
    }

}
