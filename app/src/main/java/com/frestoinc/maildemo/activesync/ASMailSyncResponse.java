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

public class ASMailSyncResponse extends ASCommandResponse {


    private Document responseXml = null;
    private SyncStatus status = SyncStatus.None;

    public ASMailSyncResponse(Response response) throws Exception {
        super(response);
        // Sync responses can be empty
        if (!TextUtils.isEmpty(getXmlString())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(getXmlString()));
            responseXml = builder.parse(is);

            setStatus();
        }
    }

    public SyncStatus getStatus() {
        return status;
    }

    // This function gets the sync key for
    // a folder.
    public String getSyncKeyForFolder(String folderId) throws Exception {
        String folderSyncKey = "0";
        String collectionXPath = String.format(
                ".//AirSync:Collection[AirSync:CollectionId = \"%s\"]/AirSync:SyncKey", folderId);
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node syncKeyNode = (Node) xpath.evaluate(collectionXPath, responseXml, XPathConstants.NODE);
        if (syncKeyNode != null)
            folderSyncKey = syncKeyNode.getTextContent();
        return folderSyncKey;
    }

    // This function returns the new items (Adds)
    // for a folder.
    public List<ServerSyncCommand> getServerSyncsForFolder(String folderId) throws Exception {
        List<ServerSyncCommand> srvCommands = new ArrayList<>();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String collectionXPath = String.format(
                ".//AirSync:Collection[AirSync:CollectionId = \"%s\"]/AirSync:Commands/*",
                folderId);
        NodeList cmdNodes = (NodeList) xpath.evaluate(collectionXPath, responseXml, XPathConstants.NODESET);
        for (int i = 0, n = cmdNodes.getLength(); i < n; i++) {
            Node cmdNode = cmdNodes.item(i);
            String cmdTypeStr = cmdNode.getLocalName();
            EasSyncCommand.Type cmdType = EasSyncCommand.Type.Invalid;
            switch (cmdTypeStr) {
                case "Add":
                    cmdType = EasSyncCommand.Type.Add;
                    break;
                case "Change":
                    cmdType = EasSyncCommand.Type.Change;
                    break;
                case "Delete":
                    cmdType = EasSyncCommand.Type.Delete;
                    break;
                case "SoftDelete":
                    cmdType = EasSyncCommand.Type.SoftDelete;
                    break;
                default:
                    break;
            }
            getServerSync(srvCommands, cmdNode, cmdType);
        }
        return srvCommands;
    }

    private void getServerSync(List<ServerSyncCommand> srvCommands, Node cmdNode, EasSyncCommand.Type cmdType) throws Exception {
        Node node = cmdNode.getFirstChild();
        String serverId = null;
        Node applicationDataNode = null;
        while (node != null) {
            String prefix = node.getPrefix();

            if (node.getOwnerDocument().lookupNamespaceURI(prefix).equals("AirSync")) {
                String name = node.getNodeName();
                if (name.equals("ServerId")) {
                    serverId = node.getTextContent();
                } else if (name.equals("ApplicationData")) {
                    applicationDataNode = node;
                }
            }
            node = node.getNextSibling();
        }
        if (serverId != null && applicationDataNode != null) {
            ServerSyncCommand srvCommand =
                    new ServerSyncCommand(cmdType, serverId, applicationDataNode, null);
            srvCommands.add(srvCommand);
        }
    }

    // This function extracts the response status from the
    // XML and sets the status property.
    private void setStatus() throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Node statusNode =
                (Node) xpath.evaluate(".//AirSync:Sync//AirSync:Status", responseXml, XPathConstants.NODE);
        if (statusNode != null) {
            status = SyncStatus.value(Integer.parseInt((statusNode.getTextContent())));
        }
    }

    public enum SyncStatus {
        // This enumeration covers the possible Status
        // values for FolderSync responses.
        None,
        Success,
        __dummyEnum__2,
        InvalidSyncKey,
        ProtocolError,
        ServerError,
        ClientServerConversionError,
        ServerOverwriteConflict,
        ObjectNotFound,
        SyncCannotComplete,
        __dummyEnum__10,
        __dummyEnum__11,
        FolderHierarchyOutOfDate,
        PartialSyncNotValid,
        InvalidDelayValue,
        InvalidSync,
        Retry;

        public static SyncStatus value(int i) {
            if (i >= 0 && i <= values().length) {
                return values()[i];
            }
            return null;
        }
    }
}
