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

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;
import timber.log.Timber;

public class ASItemOperationsResponse extends ASCommandResponse {

    private Document responseXml = null;
    private byte[] attachmentData = null;
    private byte[] message = null;
    private int status = 0;

    public ASItemOperationsResponse(Response response, ItemOperationEnum ioEnum) throws Exception {
        super(response);
        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);
            setStatus();
            switch (ioEnum) {
                case GET_BODY:
                    setHTMLMessage();
                    break;
                case GET_FULLBODY:
                    setFullMessage();
                    break;
                case GET_ATTACHMENT:
                    setAttachment();
                    break;
                default:
                    break;
            }

        }
    }

    public byte[] getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public byte[] getAttachmentData() {
        return attachmentData;
    }

    private void setStatus() throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode =
                (Node) xpath.evaluate(".//ItemOperations:ItemOperations//ItemOperations:Status",
                        responseXml, XPathConstants.NODE);
        if (statusNode != null) {
            status = Integer.parseInt(statusNode.getTextContent());
        }
    }

    private void setHTMLMessage() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node dataNode = (Node) xpath.evaluate(".//ItemOperations:ItemOperations//ItemOperations:Response//ItemOperations:Fetch//ItemOperations:Properties"
                + "//AirSyncBase:Body//AirSyncBase:Data", responseXml, XPathConstants.NODE);
        if (dataNode != null) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            if (dataNode.getTextContent().length() > 0) {
                for (int j = 0; j < dataNode.getTextContent().length(); j++) {
                    sb.append(dataNode.getTextContent().charAt(j));
                }
            }
            Timber.e("string type: %s", sb.toString());
            message = sb.toString().getBytes();
        }
    }

    private void setFullMessage() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node dataNode = (Node) xpath.evaluate(".//ItemOperations:ItemOperations//ItemOperations:Response//ItemOperations:Fetch//ItemOperations:Properties"
                + "//AirSyncBase:Body//AirSyncBase:Data", responseXml, XPathConstants.NODE);
        if (dataNode != null) {
            message = dataNode.getTextContent().getBytes(StandardCharsets.UTF_8);
        }
    }

    private void setAttachment() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node dataNode = (Node) xpath.evaluate(".//ItemOperations:ItemOperations//"
                        + "ItemOperations:Response//ItemOperations:Fetch//ItemOperations:Properties"
                        + "//ItemOperations:Data",
                responseXml, XPathConstants.NODE);

        if (dataNode != null && dataNode.getTextContent() != null) {
            attachmentData = dataNode.getTextContent().getBytes(StandardCharsets.UTF_8);
        }
    }

    public enum ItemOperationEnum {
        GET_BODY,
        GET_FULLBODY,
        GET_ATTACHMENT,
    }
}
