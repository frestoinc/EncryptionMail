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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import okhttp3.Response;

// This class represents a FolderSync command
// response as specified in MS-ASCMD section 2.2.2.4.2.
public class ASFolderSyncResponse extends ASCommandResponse {
    private Document responseXml;
    private int status = 0;
    private String syncKey;

    public ASFolderSyncResponse(Response response) throws Exception {
        super(response);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(getXmlString()));
        responseXml = builder.parse(is);
        setStatus();
    }

    public int getStatus() {
        return status;
    }

    public String getSyncKey() {
        return syncKey;
    }

    // This function updates a folder tree based on the
    // changes received from the server in the response.
    public List<FolderInfo> getFolderList() throws Exception {
        List<FolderInfo> result = new ArrayList<>();
        XPath xpath = XPathFactory.newInstance().newXPath();
        // Process adds (new folders) first
        NodeList addNodes =
                (NodeList) xpath.evaluate(".//FolderHierarchy:Add", responseXml, XPathConstants.NODESET);
        for (int i = 0, n = addNodes.getLength(); i < n; i++) {
            Node addNode = addNodes.item(i);
            FolderInfo fi = new FolderInfo();
            nodeToFolderInfo(addNode, fi);
            fi.action = FolderInfo.Action.Added;
            result.add(fi);
        }
        // Then process deletes
        NodeList deleteNodes =
                (NodeList) xpath.evaluate(".//FolderHierarchy:Delete",
                        responseXml, XPathConstants.NODESET);
        for (int i = 0, n = deleteNodes.getLength(); i < n; i++) {
            Node deleteNode = deleteNodes.item(i);
            FolderInfo fi = new FolderInfo();
            nodeToFolderInfo(deleteNode, fi);
            fi.action = FolderInfo.Action.Deleted;
            result.add(fi);
        }
        // Finally process any updates to existing folders
        NodeList updateNodes =
                (NodeList) xpath.evaluate(".//FolderHierarchy:Update",
                        responseXml, XPathConstants.NODESET);
        for (int i = 0, n = updateNodes.getLength(); i < n; i++) {
            Node updateNode = updateNodes.item(i);
            FolderInfo fi = new FolderInfo();
            nodeToFolderInfo(updateNode, fi);
            fi.action = FolderInfo.Action.Updated;
            result.add(fi);
        }
        return result;
    }

    private void nodeToFolderInfo(Node node, FolderInfo fi) {
        node = node.getFirstChild();
        while (node != null) {
            String name = node.getLocalName();
            switch (name) {
                case "DisplayName":
                    fi.name = node.getTextContent();
                    break;
                case "ServerId":
                    fi.id = node.getTextContent();
                    break;
                case "ParentId":
                    fi.parentId = node.getTextContent();
                    break;
                case "Type":
                    fi.type = Integer.parseInt(node.getTextContent());
                    break;
                default:
                    break;
            }
            node = node.getNextSibling();
        }
    }

    // This function extracts the response status from the
    // XML and sets the status property.
    private void setStatus() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode =
                (Node) xpath.evaluate(".//FolderHierarchy:FolderSync/FolderHierarchy:Status", responseXml,
                        XPathConstants.NODE);
        if (statusNode != null) {
            status = Integer.parseInt(statusNode.getTextContent());
        }
        // Get sync key
        Node syncKeyNode =
                (Node) xpath.evaluate(".//FolderHierarchy:FolderSync/FolderHierarchy:SyncKey", responseXml,
                        XPathConstants.NODE);
        if (syncKeyNode != null) {
            syncKey = syncKeyNode.getTextContent();
        }
    }

}
