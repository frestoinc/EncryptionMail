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

import android.os.Build;

import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.data.model.GalContact;

import java.util.ArrayList;
import java.util.List;

public class EasConnection {

  private AccountUser accountUser;

  public AccountUser getAccountUser() {
    return accountUser;
  }

  public void setAccountUser(AccountUser accountUser) {
    this.accountUser = accountUser;
  }

  private Device getDevice() {
    Device device = new Device();
    device.setDeviceID("Phone");
    device.setDeviceType("Mobile");
    device.setModel(Build.MODEL);
    return device;
  }

  public long getPolicyKey() throws Exception {

    ASProvisionRequest provReq = new ASProvisionRequest();
    provReq.setAccountUser(getAccountUser());
    provReq.setDevice(getDevice());
    //provReq.setSslCredentials(getSslCredentials());
    provReq.setProvisionDevice(getDevice());
    long policyKey = 0;
    provReq.setPolicyKey(policyKey);

    ASProvisionResponse provRes = (ASProvisionResponse) provReq.getResponse();
    if (provRes.getStatus() != ProvisionStatus.Success) {
      throw new Exception(
              String.format("Error returned from initial provision request: status=%d", provRes.getStatus()));
    }

    if (provRes.getIsPolicyLoaded()) {
      if (provRes.getPolicy().getStatus() != ASPolicy.PolicyStatus.Success.ordinal()) {
        throw new Exception(String.format("Policy Error returned from initial provision request: status=%d",
                provRes.getPolicy().getStatus()));
      }

      if (provRes.getPolicy().getRemoteWipeRequested()) {
        ASProvisionRequest wipeAcknReq = new ASProvisionRequest();

        wipeAcknReq.setAccountUser(getAccountUser());
        //wipeAcknReq.setSslCredentials(getSslCredentials());
        wipeAcknReq.setDevice(getDevice());
        wipeAcknReq.setPolicyKey(policyKey);

        wipeAcknReq.setIsRemoteWipe(true);
        wipeAcknReq.setStatus(ProvisionStatus.Success);

        ASProvisionResponse wipeAckRes = (ASProvisionResponse) wipeAcknReq.getResponse();

        if (wipeAckRes.getStatus() != ProvisionStatus.Success) {
          throw new Exception(
                  String.format("Error returned from remote wipe Acknowledgment request: status=%d",
                          wipeAckRes.getStatus()));
        }
      } else {
        // The server has provided a policy
        // and a temporary policy key.
        // The client must acknowledge this policy
        // in order to get a permanent policy
        // key.

        ASProvisionRequest policyAckReq = new ASProvisionRequest();

        // Initialize the request with information
        // that applies to all requests.
        policyAckReq.setAccountUser(getAccountUser());
        policyAckReq.setDevice(getDevice());
        //policyAckReq.setSslCredentials(getSslCredentials());

        // Set the policy key to the temporary policy key from
        // the previous response.
        policyAckReq.setPolicyKey(provRes.getPolicy().getPolicyKey());

        // Initialize the Provision command-specific
        // information.
        policyAckReq.setIsAcknowledgement(true);
        // Indicate successful application of the policy.
        policyAckReq.setStatus(PolicyAcknowledgement.Success);

        // Send the request
        ASProvisionResponse policyAckRes = (ASProvisionResponse) policyAckReq.getResponse();

        if (policyAckRes.getStatus() == ProvisionStatus.Success && policyAckRes.getIsPolicyLoaded()) {
          // Save the permanent policy key for use
          // in subsequent command requests.
          policyKey = policyAckRes.getPolicy().getPolicyKey();
        } else {
          throw new Exception(String.format("Error returned from policy acknowledgment request: %s",
                  policyAckRes.getStatus()));
        }

      }
    }
    return policyKey;
  }

  public List<EasFolder> getFolders(long policyKey) throws Exception {

    String syncKey = "0";
    ASFolderSyncRequest request = new ASFolderSyncRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //syncFolderReq.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);
    request.setSyncKey(syncKey);

    ASFolderSyncResponse syncFolderRes = (ASFolderSyncResponse) request.getResponse();

    List<EasFolder> folders = new ArrayList<>();
    List<FolderInfo> folderList = syncFolderRes.getFolderList();
    for (FolderInfo fi : folderList) {
      EasFolder folder = new EasFolder();
      folder.setId(fi.id);
      folder.setParentId(fi.parentId);
      folder.setName(fi.name);
      folder.setType(EasFolderType.valueOf(fi.type));
      folders.add(folder);
    }
    return folders;
  }

  public String getFolderSyncKey(long policyKey, String folderId, int maxCount) throws Exception {

    ASSyncRequest request = new ASSyncRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //initSyncReq.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);
    request.setWindowSize(maxCount);
    FolderInfo fi = new FolderInfo();
    fi.id = folderId;

    request.getFolders().add(fi);
    // inbox.areChangesIgnored = true;

    ASSyncResponse initSyncRes = (ASSyncResponse) request.getResponse();
    if (initSyncRes.getStatus() != ASSyncResponse.SyncStatus.Success) {
      throw new Exception("Error returned from empty sync request: "
              + initSyncRes.getStatus().toString());
    }
    fi.syncKey = initSyncRes.getSyncKeyForFolder(fi.id);
    return fi.syncKey;
  }

  public String getFirstSyncKeyCommand(long policyKey, String folderId) throws Exception {

    ASFirstSyncKeyRequest request = new ASFirstSyncKeyRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);
    request.setFolderId(folderId);

    ASFirstSyncKeyResponse response = (ASFirstSyncKeyResponse) request.getResponse();
    if (response.getStatus() == ASFirstSyncKeyResponse.SyncStatus.Success) {
      if (response.getSyncKey() != null)
        return response.getSyncKey();
    }
    return null;
  }

  //todo check sync key
  public EasSyncCommand getMailSyncCommands(long policyKey, String folderId, String syncKey) throws Exception {

    ASMailSyncRequest request = new ASMailSyncRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //syncReq.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setSyncKey(syncKey);
    request.setFolderId(folderId);
    request.setOption(true);

    ASMailSyncResponse syncRes = (ASMailSyncResponse) request.getResponse();
    List<ServerSyncCommand> srvSyncs = syncRes.getServerSyncsForFolder(folderId);
    if (syncRes.getStatus() == ASMailSyncResponse.SyncStatus.Success) {
      syncKey = syncRes.getSyncKeyForFolder(folderId);
    } else if (syncRes.getStatus() != ASMailSyncResponse.SyncStatus.None) {
      throw new Exception("Error returned from sync request: " + syncRes.getStatus());
    }

    EasSyncCommand syncCommands = new EasSyncCommand();
    syncCommands.setSyncKey(syncKey);
    for (ServerSyncCommand srvSync : srvSyncs) {
      EasSyncCommand.Command syncCommand = syncCommands.add(
              srvSync.getServerId(), srvSync.getType());
      syncCommand.setMessage(srvSync.getMessage());
    }
    return syncCommands;
  }

  //todo review
  public byte[] getItemOperationsHMTLBodyCommand(long policyKey, String collectionid, String serverId) throws Exception {

    ASItemOperationsRequest request = new ASItemOperationsRequest(ASItemOperationsResponse.ItemOperationEnum.GET_BODY);

    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setWithOption(true);
    request.setServerId(serverId);
    request.setCollectionId(collectionid);

    ASItemOperationsResponse response = (ASItemOperationsResponse) request.getResponse();
    if (response.getStatus() == 1) {
      return response.getMessage();
    }
    return null;
  }

  public byte[] getItemOperationsFullBodyCommand(long policyKey, String collectionid, String serverId) throws Exception {

    ASItemOperationsRequest request = new ASItemOperationsRequest(ASItemOperationsResponse.ItemOperationEnum.GET_FULLBODY);

    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setWithOption(true);
    request.setServerId(serverId);
    request.setCollectionId(collectionid);

    ASItemOperationsResponse response = (ASItemOperationsResponse) request.getResponse();
    if (response.getStatus() == 1) {
      return response.getMessage();
    }

    return null;
  }

  public ASItemOperationsResponse getItemOperationsAttachmentCommand(long policyKey, String fileReference) throws Exception {

    ASItemOperationsRequest request = new ASItemOperationsRequest(ASItemOperationsResponse.ItemOperationEnum.GET_ATTACHMENT);

    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setFileReference(fileReference);

    return (ASItemOperationsResponse) request.getResponse();
  }

  public ASMailStatusResponse sendNewMailCommand(long policyKey, String clientId, String content) throws Exception {

    ASSendMailRequest request = new ASSendMailRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //smRequest.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setClientId(clientId);
    request.setMimeContent(content);

    return (ASMailStatusResponse) request.getResponse();
  }

  public ASMailStatusResponse sendReplyMailCommand(long policyKey, String clientId, String folderId,
                                                   String itemId, String content) throws Exception {
    ASSmartReplyRequest request = new ASSmartReplyRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setClientId(clientId);
    request.setFolderId(folderId);
    request.setItemId(itemId);
    request.setMimeContent(content);

    return (ASMailStatusResponse) request.getResponse();
  }

  public List<GalContact> getGalCommand(long policyKey, String query) throws Exception {

    ASGalRequest request = new ASGalRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);
    request.setQuery(query);

    ASGalResponse response = (ASGalResponse) request.getResponse();
    if (response.getStatus() == 1)
      return response.getList();
    return null;
  }

  public void getSettingsCommand(long policyKey) throws Exception {

    ASSettingsRequest request = new ASSettingsRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //request.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    ASSettingsResponse response = (ASSettingsResponse) request.getResponse();
    //Timber.d("response: %i", response.getStatus());
  }

  public int forwardMailCommand(long policyKey, String clientId, String content) throws Exception {

    ASSearchRequest request = new ASSearchRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //sRequest.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    request.setCollectinId(EasFolderType.valueOf(EasFolderType.DefaultInbox));
    request.setReference("Test multi normal message");
    request.setOption(true);

    ASSearchResponse searchResponse = (ASSearchResponse) request.getResponse();
    if (searchResponse.getStatus() == 1) {
      String longId = searchResponse.getLongId();
      return sendForwardMail(getDevice(), policyKey, clientId, longId, content);
    }
    return 0;
  }

  private int sendForwardMail(Device device, long policyKey, String clientId, String longId, String content) throws Exception {

    ASSmartForwardRequest request = new ASSmartForwardRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //sfRequest.setSslCredentials(getSslCredentials());
    request.setPolicyKey(policyKey);

    //todo
        /*sfRequest.setClientId(clientId);
        sfRequest.setMimeContent(content);*/

    ASMailStatusResponse response = (ASMailStatusResponse) request.getResponse();
    return response.getStatus();
  }

  public EasSyncCommand getFolderLastSyncKey(long policyKey, String folderId, String syncKey) throws Exception {
    EasSyncCommand syncCommands = new EasSyncCommand();
    syncCommands.setSyncKey(syncKey);
    while (true) {
      EasSyncCommand tmpSyncCommands = getFolderSyncCommands(policyKey, folderId, syncCommands.getSyncKey(), 512);
      syncCommands.setSyncKey(tmpSyncCommands.getSyncKey());
      if (tmpSyncCommands.allSize() == 0)
        break;
      syncCommands.getAdded().addAll(tmpSyncCommands.getAdded());
      syncCommands.getUpdated().addAll(tmpSyncCommands.getUpdated());
      syncCommands.getDeleted().addAll(tmpSyncCommands.getDeleted());
    }
    return syncCommands;
  }

  public EasSyncCommand getFolderSyncCommands(long policyKey, String folderId, String syncKey,
                                              int maxCount) throws Exception {

    ASSyncRequest request = new ASSyncRequest();
    request.setAccountUser(getAccountUser());
    request.setDevice(getDevice());
    //syncReq.setSslCredentials(getSslCredentials());

    request.setPolicyKey(policyKey);
    FolderInfo fi = new FolderInfo();
    fi.id = folderId;
    //fi.parentId = folder.getParentId();
    //fi.name = folder.getName();
    //fi.type = folder.getType().ordinal();
    fi.syncKey = syncKey;
    request.getFolders().add(fi);
    request.setWindowSize(maxCount);
    // inbox.useConversationMode = true;

    ASSyncResponse syncRes = (ASSyncResponse) request.getResponse();
    List<ServerSyncCommand> srvSyncs = syncRes.getServerSyncsForFolder(folderId);
    if (syncRes.getStatus() == ASSyncResponse.SyncStatus.Success) {
      syncKey = syncRes.getSyncKeyForFolder(folderId);
    } else if (syncRes.getStatus() != ASSyncResponse.SyncStatus.None) {
      throw new Exception("Error returned from sync reqeust: " + syncRes.getStatus());
    }

    EasSyncCommand syncCommands = new EasSyncCommand();
    syncCommands.setSyncKey(syncKey);
    for (ServerSyncCommand srvSync : srvSyncs) {
      EasSyncCommand.Command syncCommand = syncCommands.add(
              srvSync.getServerId(), srvSync.getType());
      syncCommand.setMessage(srvSync.getMessage());
    }
    return syncCommands;
  }
}
