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
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

public class ASMailStatusResponse extends ASCommandResponse {

    private int status = -1;
    private Document responseXml = null;
    private String error = "";

    public ASMailStatusResponse(Response response) throws Exception {
        super(response);
        if (response.code() == HttpURLConnection.HTTP_OK) {
            setStatus(1);
        }
        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);
            setErrorStatus();
        }
    }

    private void setErrorStatus() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node dataNode = (Node) xpath.evaluate(".//ComposeMail:*//ComposeMail:Status",
                responseXml, XPathConstants.NODE);

        if (dataNode != null && dataNode.getTextContent() != null) {
            setStatus(Integer.parseInt(dataNode.getTextContent()));
            parseError();
        }
    }

    public int getStatus() {
        return status;
    }

    private void setStatus(int i) {
        this.status = i;
    }

    public String getError() {
        return error;
    }

    private void parseError() {
        switch (status) {
            case 102:
                error = "Malformed WBXML";
                break;
            case 103:
                error = "Malformed XML.";
                break;
            case 118:
                error = "Message was previously sent";
                break;
            case 119:
                error = "Message not able to parse recipient";
                break;
            case 120:
                error = "Message submission failed";
                break;
            case 107:
                error = "Message contains invalid MIME";
                break;
            case 110:
                error = "Server Error";
                break;
            default:
                error = "Unknown error";
                break;
        }
    }
}
