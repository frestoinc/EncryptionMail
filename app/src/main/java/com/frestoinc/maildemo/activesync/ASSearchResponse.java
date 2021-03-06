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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

public class ASSearchResponse extends ASCommandResponse {

    private Document responseXml = null;
    private int status = 0;

    public ASSearchResponse(Response response) throws Exception {
        super(response);

        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);

            setStatus();
        }
    }


    public int getStatus() {
        return status;
    }

    private void setStatus() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode =
                (Node) xpath.evaluate(".//Search:Search//Search:Status",
                        responseXml, XPathConstants.NODE);
        if (statusNode != null)
            status = Integer.parseInt(statusNode.getTextContent());
    }

    public String getLongId() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node longIdNode =
                (Node) xpath.evaluate(".//Search:Search//Search:Response//"
                                + "Search:Store//Search:Result//Search:LongId",
                        responseXml, XPathConstants.NODE);
        if (longIdNode != null && longIdNode.getTextContent() != null) {
            return longIdNode.getTextContent();
        }
        return null;
    }

    public String getNativeBodyType() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node nativeBodyTypeNode =
                (Node) xpath.evaluate(".//Search:Search//Search:Response//"
                                + "Search:Store//Search:Result//Search:Properties//AirSyncBase:NativeBodyType",
                        responseXml, XPathConstants.NODE);
        if (nativeBodyTypeNode != null && nativeBodyTypeNode.getTextContent() != null) {
            return nativeBodyTypeNode.getTextContent();
        }
        return null;
    }
}
//Search:Response