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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

// This class represents the Provision command
// response specified in MS-ASPROV section 2.2.
public class ASProvisionResponse extends ASCommandResponse {
    private boolean isPolicyLoaded;
    private ASPolicy policy;
    private int status = 0;

    public ASProvisionResponse(Response response) throws Exception {
        super(response);
        policy = new ASPolicy();
        isPolicyLoaded = policy.loadXml(getXmlString());
        setStatus();
    }

    public boolean getIsPolicyLoaded() {
        return isPolicyLoaded;
    }

    public ASPolicy getPolicy() {
        return policy;
    }

    public int getStatus() {
        return status;
    }

    // This function parses the response XML for
    // the Status element under the Provision element
    // and sets the status property according to the
    // value.
    private void setStatus() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(getXmlString()));
        Document xmlDoc = builder.parse(is);
        XPath xpath = XPathFactory.newInstance().newXPath();
        // NamespaceContext nsContext = xmlDoc.lookupNamespaceUri(prefix);
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if (prefix.equals("provision")) {
                    return "Provision";
                } else {
                    return "";
                }
            }

            @Override
            public String getPrefix(String namespaceUri) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceUri) {
                throw new UnsupportedOperationException();
            }
        });

        Node statusNode = (Node) xpath
                .evaluate(".//provision:Provision/provision:Status", xmlDoc, XPathConstants.NODE);
        if (statusNode != null) {
            String statusStr = statusNode.getTextContent();
            status = Integer.parseInt(statusStr);
        }
    }
}
