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

import com.frestoinc.maildemo.data.model.EasMessage;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// This class represents an <Add>, <Change>,
// <Delete>, or <SoftDelete> node in a
// Sync command response.
public class ServerSyncCommand {
    private EasSyncCommand.Type type = EasSyncCommand.Type.Invalid;
    private String serverId;
    private String itemClass;
    private Node appDataXml;

    public ServerSyncCommand(
            EasSyncCommand.Type commandType, String id, Node appData, String changedItemClass) {
        setType(commandType);
        setServerId(id);
        setAppDataXml(appData);
        setItemClass(changedItemClass);
    }

    public EasSyncCommand.Type getType() {
        return type;
    }

    public void setType(EasSyncCommand.Type type) {
        this.type = type;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getItemClass() {
        return itemClass;
    }

    public void setItemClass(String itemClass) {
        this.itemClass = itemClass;
    }

    public Node getAppDataXml() {
        return appDataXml;
    }

    public void setAppDataXml(Node appDataXml) {
        this.appDataXml = appDataXml;
    }

    public EasMessage getMessage() {
        if (getAppDataXml() == null) {
            return null;
        }
        EasMessage email = new EasMessage();
        email.setServerId(getServerId());
        Node node = getAppDataXml().getFirstChild();
        while (node != null) {
            String prefix = node.getPrefix();
            String namespace = node.getOwnerDocument().lookupNamespaceURI(prefix);
            switch (namespace) {
                case "Email": {
                    String nodeName = node.getLocalName();
                    switch (nodeName) {
                        case "To":
                            email.setTo(node.getTextContent());
                            break;
                        case "Cc":
                            email.setCc(node.getTextContent());
                            break;
                        case "From":
                            email.setFrom(node.getTextContent());
                            break;
                        case "Subject":
                            email.setSubject(node.getTextContent());
                            break;
                        case "DateReceived":
                            email.setDateReceived(node.getTextContent());
                            break;
                        case "DisplayTo":
                            email.setDisplayTo(node.getTextContent());
                            break;
                        case "ThreadTopic":
                            email.setThreadTopic(node.getTextContent());
                            break;
                        case "Importance":
                            email.setImportance(Integer.parseInt(node.getTextContent()));
                            break;
                        case "Read":
                            email.setRead(Boolean.parseBoolean(node.getTextContent()));
                            break;
                        default:
                            break;

                    }
                    break;
                }
                case "Email2": {
                    String nodeName = node.getLocalName();
                    if (nodeName.equals("ConversationId")) {
                        email.setConversationId(node.getTextContent());
                    }
                    break;
                }
                case "AirSyncBase": {
                    String nodeName = node.getLocalName();
                    switch (nodeName) {
                        case "Attachments":
                            Node attachmentsNode = node.getFirstChild();
                            if (attachmentsNode != null) {
                                String _prefix = attachmentsNode.getPrefix();
                                String _namespace = attachmentsNode.getOwnerDocument().lookupNamespaceURI(_prefix);
                                if ("AirSyncBase".equals(_namespace)) {
                                    NodeList attachmentNodeList = attachmentsNode.getChildNodes();
                                    for (int i = 0; i < attachmentNodeList.getLength(); i++) {
                                        Node _node = attachmentNodeList.item(i);
                                        String _nodeName = _node.getLocalName();
                                        switch (_nodeName) {
                                            case "DisplayName":
                                                email.setAttachmentName(_node.getTextContent());
                                                break;
                                            case "FileReference":
                                                email.setAttachmentReference(_node.getTextContent());
                                                break;
                                            case "Method":
                                                email.setAttachmentMethod(_node.getTextContent());
                                                break;
                                            case "EstimatedDataSize":
                                                email.setAttachmentSize(Integer.parseInt(_node.getTextContent()));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                email.setHasAttachment(true);
                                break;
                            }
                            break;
                        case "Body":
                            Node bodyNode = node.getFirstChild();
                            while (bodyNode != null) {
                                String bodyNodeName = bodyNode.getLocalName();
                                if (bodyNodeName.equals("Data")) {
                                    SpannableStringBuilder sb = new SpannableStringBuilder();
                                    if (node.getTextContent().length() > 0) {
                                        for (int j = 0; j < bodyNode.getTextContent().length(); j++) {
                                            sb.append(bodyNode.getTextContent().charAt(j));
                                        }
                                    }
                                    email.setMessage(sb.toString().getBytes());
                                }
                                bodyNode = bodyNode.getNextSibling();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
            node = node.getNextSibling();
        }
        return email;
    }

}
