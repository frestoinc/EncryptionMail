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

import com.frestoinc.maildemo.data.model.GalContact;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

public class ASGalResponse extends ASCommandResponse {

    private Document responseXml = null;
    private int status = 0;
    private List<GalContact> list;

    public ASGalResponse(Response response) throws Exception {
        super(response);
        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);

            setStatus();
            if (getStatus() == 1) {
                setContacts();
            }
        }
    }

    public int getStatus() {
        return status;
    }

    public List<GalContact> getList() {
        return list;
    }

    private void setStatus() throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode = (Node) xpath.evaluate(".//Search:Search//Search:Status",
                responseXml, XPathConstants.NODE);
        if (statusNode != null) {
            status = Integer.parseInt(statusNode.getTextContent());
        }
    }

    private void setContacts() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node resultNode = (Node) xpath.evaluate(".//Search:Search//Search:Response//"
                + "Search:Store//Search:Result", responseXml, XPathConstants.NODE);

        list = new ArrayList<>();
        while (resultNode != null) {
            Node propertiesNode = resultNode.getFirstChild();
            while (propertiesNode != null) {
                GalContact contacts = new GalContact();
                Node childNode = propertiesNode.getFirstChild();
                while (childNode != null) {
                    switch (childNode.getNodeName()) {
                        case "gal:DisplayName":
                            contacts.setDisplayName(childNode.getTextContent());
                            break;
                        case "gal:EmailAddress":
                            contacts.setEmailAddress(childNode.getTextContent());
                            break;
                        default:
                            break;
                    }
                    childNode = childNode.getNextSibling();
                }
                if (contacts.getDisplayName() != null && contacts.getEmailAddress() != null) {
                    list.add(contacts);
                }
                propertiesNode = propertiesNode.getNextSibling();
            }
            resultNode = resultNode.getNextSibling();
        }
    }
}
