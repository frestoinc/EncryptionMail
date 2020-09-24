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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Response;

// This class represents the Sync command request
public class ASSyncRequest extends ASCommandRequest {

  private List<FolderInfo> folderList;
  private int wait = 0;
  // 1 - 59 minutes
  private int heartBeatInterval = 0;
  // 60 - 3540 seconds
  private int windowSize = 0;
  // 1 - 512 changes
  private boolean isPartial = false;

  public ASSyncRequest() {
    this.setCommand("Sync");
    this.folderList = new ArrayList<>();
  }

  public int getWait() {
    return wait;
  }

  public void setWait(int value) {
    wait = value;
  }

  public int getHeartBeatInterval() {
    return heartBeatInterval;
  }

  public void setHeartBeatInterval(int value) {
    heartBeatInterval = value;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int value) {
    windowSize = value;
  }

  public boolean getIsPartial() {
    return isPartial;
  }

  public void setIsPartial(boolean value) {
    isPartial = value;
  }

  public List<FolderInfo> getFolders() {
    return folderList;
  }

  public void setFolderList(List<FolderInfo> folderList) {
    this.folderList = folderList;
  }

  // This function generates an ASSyncResponse from an
  // HTTP response.
  @Override
  protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
    return new ASSyncResponse(response);
  }

  // This function generates the XML request body
  // for the Sync request.
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
    Document syncXml = builder.newDocument();

    // XmlDeclaration xmlDeclaration = syncXml.CreateXmlDeclaration("1.0",
    // "utf-8", null);
    // syncXml.InsertBefore(xmlDeclaration, null);

    Node syncNode = syncXml
            .createElementNS(Namespaces.airSyncNamespace, Xmlns.airSyncXmlns + ":Sync");
    syncXml.appendChild(syncNode);
    // Only add a collections node if there are folders in the request.
    // If omitting, there should be a Partial element.
    if (folderList.isEmpty() && !isPartial) {
      throw new Exception("Sync requests must specify collections or include the Partial element.");
    }

    if (!folderList.isEmpty()) {
      Node collectionsNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":Collections");
      syncNode.appendChild(collectionsNode);
      for (FolderInfo folder : folderList) {
        Node collectionNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":Collection");
        collectionsNode.appendChild(collectionNode);
        Node syncKeyNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":SyncKey");
        syncKeyNode.setTextContent(folder.syncKey);
        collectionNode.appendChild(syncKeyNode);
        Node collectionIdNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                Xmlns.airSyncXmlns + ":CollectionId");
        collectionIdNode.setTextContent(folder.id);
        collectionNode.appendChild(collectionIdNode);
        // To override "ghosting", you must include a Supported element
        // here.
        // This only applies to calendar items and contacts
        // NOT IMPLEMENTED
        // If folder is set to permanently delete items, then add a
        // DeletesAsMoves
        // element here and set it to false.
        // Otherwise, omit. Per MS-ASCMD, the absence of this element is
        // the same as true.
        if (folder.areDeletesPermanent) {
          Node deletesAsMovesNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                  Xmlns.airSyncXmlns + ":DeletesAsMoves");
          deletesAsMovesNode.setTextContent("1");
          collectionNode.appendChild(deletesAsMovesNode);
        }

        // In almost all cases the GetChanges element can be omitted.
        // It only makes sense to use it if SyncKey != 0 and you don't
        // want changes from the server for some reason.
        if (folder.areChangesIgnored) {
          Node getChangesNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                  Xmlns.airSyncXmlns + ":GetChanges");
          getChangesNode.setTextContent("1");
          collectionNode.appendChild(getChangesNode);
        }

        // If there's a folder-level window size, include it
        if (folder.windowSize > 0) {
          Node folderWindowSizeNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                  Xmlns.airSyncXmlns + ":WindowSize");
          folderWindowSizeNode.setTextContent(((Integer) folder.windowSize).toString());
          collectionNode.appendChild(folderWindowSizeNode);
        }

        // If the folder is set to conversation mode, specify that
        if (folder.useConversationMode) {
          Node conversationModeNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
                  Xmlns.airSyncXmlns + ":ConversationMode");
          conversationModeNode.setTextContent("1");
          collectionNode.appendChild(conversationModeNode);
        }

        // Include sync options for the folder
        // Note that you can include two Options elements, but the 2nd
        // one is for SMS
        // SMS is not implemented at this time, so we'll only include
        // one.
        if (folder.options != null) {
          generateOptionsXml(folder.options, collectionNode);

        }
      }
    }

    // Include client-side changes
    // TODO: Implement client side changes on the Folder object
    // if (folder.Commands != null)
    // {
    // folder.GenerateCommandsXml(collectionNode);
    // }
    // If a wait period was specified, include it here
    if (wait > 0) {
      Node waitNode = syncXml
              .createElementNS(Namespaces.airSyncNamespace, Xmlns.airSyncXmlns + ":Wait");
      waitNode.setTextContent(((Integer) wait).toString());
      syncNode.appendChild(waitNode);
    }

    // If a heartbeat interval period was specified, include it here
    if (heartBeatInterval > 0) {
      Node heartBeatIntervalNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":HeartbeatInterval");
      heartBeatIntervalNode.setTextContent(((Integer) heartBeatInterval).toString());
      syncNode.appendChild(heartBeatIntervalNode);
    }

    // If a windows size was specified, include it here
    if (windowSize > 0) {
      Node windowSizeNode = syncXml.createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":WindowSize");
      windowSizeNode.setTextContent(((Integer) windowSize).toString());
      syncNode.appendChild(windowSizeNode);
    }

    // If this request contains a partial list of collections, include the
    // Partial element
    if (isPartial) {
      Node partialNode = syncXml
              .createElementNS(Namespaces.airSyncNamespace, Xmlns.airSyncXmlns + ":Partial");
      syncNode.appendChild(partialNode);
    }

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(syncXml), new StreamResult(writer));
    String output = writer.getBuffer().toString();
    setXmlString(output);
  }

  // This function generates an <Options> node for a Sync
  // command based on the settings for this folder.
  public void generateOptionsXml(FolderInfo.FolderSyncOptions options, Node rootNode) {
    Node optionsNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
            Xmlns.airSyncXmlns + ":Options");
    rootNode.appendChild(optionsNode);

    if (options.FilterType != FolderInfo.SyncFilterType.NoFilter) {
      Node filterTypeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":FilterType");
      int filterTypeAsInteger = options.FilterType.ordinal();
      filterTypeNode.setTextContent(((Integer) filterTypeAsInteger).toString());
      optionsNode.appendChild(filterTypeNode);
    }

    if (options.Class != null) {
      Node classNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":Class");
      classNode.setTextContent(options.Class);
      optionsNode.appendChild(classNode);
    }

    if (options.BodyPreference != null && options.BodyPreference.length > 0 && options.BodyPreference[0] != null) {
      Node bodyPreferenceNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
              Xmlns.airSyncBaseXmlns + ":BodyPreference");
      optionsNode.appendChild(bodyPreferenceNode);

      Node typeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
              Xmlns.airSyncBaseXmlns + ":Type");
      int typeAsInteger = options.BodyPreference[0].Type.ordinal();
      typeNode.setTextContent(((Integer) typeAsInteger).toString());
      bodyPreferenceNode.appendChild(typeNode);

      if (options.BodyPreference[0].TruncationSize > 0) {
        Node truncationSizeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":TruncationSize");
        truncationSizeNode.setTextContent(((Long) options.BodyPreference[0].TruncationSize).toString());
        bodyPreferenceNode.appendChild(truncationSizeNode);
      }

      if (options.BodyPreference[0].AllOrNone) {
        Node allOrNoneNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":AllOrNone");
        allOrNoneNode.setTextContent("1");
        bodyPreferenceNode.appendChild(allOrNoneNode);
      }

      if (options.BodyPreference[0].Preview > -1) {
        Node previewNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":Preview");
        previewNode.setTextContent(((Integer) options.BodyPreference[0].Preview).toString());
        bodyPreferenceNode.appendChild(previewNode);
      }
    }

    if (options.BodyPartPreference != null) {
      Node bodyPartPreferenceNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
              Xmlns.airSyncBaseXmlns + ":BodyPartPreference");
      optionsNode.appendChild(bodyPartPreferenceNode);

      Node typeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
              Xmlns.airSyncBaseXmlns + ":Type");
      int typeAsInteger = options.BodyPartPreference.Type.ordinal();
      typeNode.setTextContent(((Integer) typeAsInteger).toString());
      bodyPartPreferenceNode.appendChild(typeNode);

      if (options.BodyPartPreference.TruncationSize > 0) {
        Node truncationSizeNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":TruncationSize");
        truncationSizeNode.setTextContent(((Long) options.BodyPreference[0].TruncationSize).toString());
        bodyPartPreferenceNode.appendChild(truncationSizeNode);
      }

      if (options.BodyPartPreference.AllOrNone) {
        Node allOrNoneNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":AllOrNone");
        allOrNoneNode.setTextContent("1");
        bodyPartPreferenceNode.appendChild(allOrNoneNode);
      }

      if (options.BodyPartPreference.Preview > -1) {
        Node previewNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncBaseNamespace,
                Xmlns.airSyncBaseXmlns + ":Preview");
        previewNode.setTextContent(((Integer) options.BodyPreference[0].Preview).toString());
        bodyPartPreferenceNode.appendChild(previewNode);
      }
    }

    if (options.MimeSupportLevel != FolderInfo.MimeSupport.NeverSendMime) {
      Node mimeSupportNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":MIMESupport");
      int mimeSupportLevelAsInteger = options.MimeSupportLevel.ordinal();
      mimeSupportNode.setTextContent(((Integer) mimeSupportLevelAsInteger).toString());
      optionsNode.appendChild(mimeSupportNode);
    }

    if (options.MimeTruncation != FolderInfo.MimeTruncationType.NoTruncate) {
      Node mimeTruncationNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":MIMETruncation");
      int mimeTruncationAsInteger = options.MimeTruncation.ordinal();
      mimeTruncationNode.setTextContent(((Integer) mimeTruncationAsInteger).toString());
      optionsNode.appendChild(mimeTruncationNode);
    }

    if (options.MaxItems > -1) {
      Node maxItemsNode = rootNode.getOwnerDocument().createElementNS(Namespaces.airSyncNamespace,
              Xmlns.airSyncXmlns + ":MaxItems");
      maxItemsNode.setTextContent(((Integer) options.MaxItems).toString());
      optionsNode.appendChild(maxItemsNode);
    }

    if (options.IsRightsManagementSupported) {
      Node rightsManagementSupportNode = rootNode.getOwnerDocument().createElementNS(
              Namespaces.rightsManagementNamespace, Xmlns.rightsManagementXmlns + ":RightsManagementSupport");
      rightsManagementSupportNode.setTextContent("1");
      optionsNode.appendChild(rightsManagementSupportNode);
    }
  }

}
