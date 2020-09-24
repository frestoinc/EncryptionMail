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
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

// This class represents an Exchange
// ActiveSync policy.
public class ASPolicy {
    private int status = 0;
    private long policyKey = 0;
    private byte allowBlueTooth = 0;
    private boolean allowBrowser = false;
    private boolean allowCamera = false;
    private boolean allowConsumerEmail = false;
    private boolean allowDesktopSync = false;
    private boolean allowHtmlEmail = false;
    private boolean allowInternetSharing = false;
    private boolean allowIrDA = false;
    private boolean allowPOPIMAPEmail = false;
    private boolean allowRemoteDesktop = false;
    private boolean allowSimpleDevicePassword = false;
    private int allowSmimeEncryptionAlgorithmNegotiation = 0;
    private boolean allowSmimeSoftCerts = false;
    private boolean allowStorageCard = false;
    private boolean allowTextMessaging = false;
    private boolean allowUnsignedApplications = false;
    private boolean allowUnsignedInstallationPackages = false;
    private boolean allowWifi = false;
    private boolean alphanumericDevicePasswordRequired = false;
    private boolean attachmentsEnabled = false;
    private boolean devicePasswordEnabled = false;
    private long devicePasswordExpiration = 0;
    private long devicePasswordHistory = 0;
    private long maxAttachmentSize = 0;
    private long maxCalendarAgeFilter = 0;
    private long maxDevicePasswordFailedAttempts = 0;
    private long maxEmailAgeFilter = 0;
    private int maxEmailBodyTruncationSize = -1;
    private int maxEmailHtmlBodyTruncationSize = -1;
    private long maxInactivityTimeDeviceLock = 0;
    private byte minDevicePasswordComplexCharacters = 1;
    private byte minDevicePasswordLength = 1;
    private boolean passwordRecoveryEnabled = false;
    private boolean requireDeviceEncryption = false;
    private boolean requireEncryptedSmimeMessages = false;
    private int requireEncryptionSmimeAlgorithm = 0;
    private boolean requireManualSyncWhenRoaming = false;
    private int requireSignedSmimeAlgorithm = 0;
    private boolean requireSignedSmimeMessages = false;
    private boolean requireStorageCardEncryption = false;
    private String[] approvedApplicationList = null;
    private String[] unapprovedInROMApplicationList = null;
    private boolean remoteWipeRequested = false;
    private boolean hasPolicyInfo = false;

    public int getStatus() {
        return status;
    }

    public long getPolicyKey() {
        return policyKey;
    }

    public byte getAllowBlueTooth() {
        return allowBlueTooth;
    }

    public boolean getAllowBrowser() {
        return allowBrowser;
    }

    public boolean getAllowCamera() {
        return allowCamera;
    }

    public boolean getAllowConsumerEmail() {
        return allowConsumerEmail;
    }

    public boolean getAllowDesktopSync() {
        return allowDesktopSync;
    }

    public boolean getAllowHtmlEmail() {
        return allowHtmlEmail;
    }

    public boolean getAllowInternetSharing() {
        return allowInternetSharing;
    }

    public boolean getAllowIrDA() {
        return allowIrDA;
    }

    public boolean getAllowPOPIMAPEmail() {
        return allowPOPIMAPEmail;
    }

    public boolean getAllowRemoteDesktop() {
        return allowRemoteDesktop;
    }

    public boolean getAllowSimpleDevicePassword() {
        return allowSimpleDevicePassword;
    }

    public int getAllowSmimeEncryptionAlgorithmNegotiation() {
        return allowSmimeEncryptionAlgorithmNegotiation;
    }

    public boolean getAllowSmimeSoftCerts() {
        return allowSmimeSoftCerts;
    }

    public boolean getAllowStorageCard() {
        return allowStorageCard;
    }

    public boolean getAllowTextMessaging() {
        return allowTextMessaging;
    }

    public boolean getAllowUnsignedApplications() {
        return allowUnsignedApplications;
    }

    public boolean getAllowUnsignedInstallationPackages() {
        return allowUnsignedInstallationPackages;
    }

    public boolean getAllowWifi() {
        return allowWifi;
    }

    public boolean getAlphanumericDevicePasswordRequired() {
        return alphanumericDevicePasswordRequired;
    }

    public boolean getAttachmentsEnabled() {
        return attachmentsEnabled;
    }

    public boolean getDevicePasswordEnabled() {
        return devicePasswordEnabled;
    }

    public long getDevicePasswordExpiration() {
        return devicePasswordExpiration;
    }

    public long getDevicePasswordHistory() {
        return devicePasswordHistory;
    }

    public long getMaxAttachmentSize() {
        return maxAttachmentSize;
    }

    public long getMaxCalendarAgeFilter() {
        return maxCalendarAgeFilter;
    }

    public long getMaxDevicePasswordFailedAttempts() {
        return maxDevicePasswordFailedAttempts;
    }

    public long getMaxEmailAgeFilter() {
        return maxEmailAgeFilter;
    }

    public int getMaxEmailBodyTruncationSize() {
        return maxEmailBodyTruncationSize;
    }

    public int getMaxEmailHtmlBodyTruncationSize() {
        return maxEmailHtmlBodyTruncationSize;
    }

    public long getMaxInactivityTimeDeviceLock() {
        return maxInactivityTimeDeviceLock;
    }

    public byte getMinDevicePasswordComplexCharacters() {
        return minDevicePasswordComplexCharacters;
    }

    public byte getMinDevicePasswordLength() {
        return minDevicePasswordLength;
    }

    public boolean getPasswordRecoveryEnabled() {
        return passwordRecoveryEnabled;
    }

    public boolean getRequireDeviceEncryption() {
        return requireDeviceEncryption;
    }

    public boolean getRequireEncryptedSmimeMessages() {
        return requireEncryptedSmimeMessages;
    }

    public int getRequireEncryptionSmimeAlgorithm() {
        return requireEncryptionSmimeAlgorithm;
    }

    public boolean getRequireManualSyncWhenRoaming() {
        return requireManualSyncWhenRoaming;
    }

    public int getRequireSignedSmimeAlgorithm() {
        return requireSignedSmimeAlgorithm;
    }

    public boolean getRequireSignedSmimeMessages() {
        return requireSignedSmimeMessages;
    }

    public boolean getRequireStorageCardEncryption() {
        return requireStorageCardEncryption;
    }

    public String[] getApprovedApplicationList() {
        return approvedApplicationList;
    }

    public String[] getUnapprovedInRomApplicationList() {
        return unapprovedInROMApplicationList;
    }

    public boolean getRemoteWipeRequested() {
        return remoteWipeRequested;
    }

    public boolean getHasPolicyInfo() {
        return hasPolicyInfo;
    }

    // This function parses a Provision command
    // response (as specified in MS-ASPROV section 2.2)
    // and extracts the policy information.
    public boolean loadXml(String policyXml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(policyXml));
        Document xmlDoc = builder.parse(is);
        XPath xpath = XPathFactory.newInstance().newXPath();
        // NamespaceContext nsContext = xmlDoc.lookupNamespaceURI(prefix);
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

        // If this is a remote wipe, there's no
        // further parsing to do.
        Node remoteWipeNode = (Node) xpath.evaluate(
                ".//provision:RemoteWipe", xmlDoc, XPathConstants.NODE);
        if (remoteWipeNode != null) {
            remoteWipeRequested = true;
            return true;
        }

        // Find the policy.
        Node policyNode = (Node) xpath.evaluate(".//provision:Policy", xmlDoc, XPathConstants.NODE);
        if (policyNode == null) {
            return false;
        }

        Node policyTypeNode = (Node) xpath
                .evaluate("provision:PolicyType", policyNode, XPathConstants.NODE);
        if (policyTypeNode != null
                && policyTypeNode.getTextContent().equals("MS-EAS-Provisioning-WBXML")) {
            // Get the policy's status
            Node policyStatusNode = (Node) xpath
                    .evaluate("provision:Status", policyNode, XPathConstants.NODE);
            if (policyStatusNode != null) {
                status = Integer.parseInt(policyStatusNode.getTextContent());
            }

            // Get the policy key
            Node policyKeyNode = (Node) xpath
                    .evaluate("provision:PolicyKey", policyNode, XPathConstants.NODE);
            if (policyKeyNode != null) {
                policyKey = Long.parseLong(policyKeyNode.getTextContent());
            }

            // Get the contents of the policy
            Node provisioningDocNode = (Node) xpath.evaluate(".//provision:EASProvisionDoc", policyNode,
                    XPathConstants.NODE);
            if (provisioningDocNode != null) {
                hasPolicyInfo = true;
                NodeList childNodes = provisioningDocNode.getChildNodes();
                for (int i = 0, n = childNodes.getLength(); i < n; i++) {
                    Node policySettingNode = childNodes.item(i);
                    // Loop through the child nodes and
                    // set the corresponding property.
                    String name = policySettingNode.getLocalName();
                    switch (name) {
                        case ("AllowBluetooth"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowBlueTooth = Byte.parseByte(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowBrowser"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowBrowser = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowCamera"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowCamera = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowConsumerEmail"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowConsumerEmail = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowDesktopSync"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowDesktopSync = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowHtmlEmail"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowHtmlEmail = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowInternetSharing"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowInternetSharing = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowIrDA"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowIrDA = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowPOPIMAPEmail"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowPOPIMAPEmail = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowRemoteDesktop"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowRemoteDesktop = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowSimpleDevicePassword"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowSimpleDevicePassword = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowSmimeEncryptionAlgorithmNegotiation"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowSmimeEncryptionAlgorithmNegotiation = Integer
                                        .parseInt(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowSmimeSoftCerts"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowSmimeSoftCerts = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowStorageCard"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowStorageCard = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowTextMessaging"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowTextMessaging = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowUnsignedApplications"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowUnsignedApplications = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowUnsignedInstallationPackages"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowUnsignedInstallationPackages = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AllowWiFi"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                allowWifi = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("AlphanumericDevicePasswordRequired"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                alphanumericDevicePasswordRequired = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("ApprovedApplicationList"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                approvedApplicationList = parseAppList(policySettingNode);
                            }

                            break;
                        case ("AttachmentsEnabled"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                attachmentsEnabled = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("DevicePasswordEnabled"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                devicePasswordEnabled = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("DevicePasswordExpiration"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                devicePasswordExpiration = Long.parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("DevicePasswordHistory"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                devicePasswordHistory = Long.parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxAttachmentSize"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxAttachmentSize = Long.parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxCalendarAgeFilter"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxCalendarAgeFilter = Long.parseLong(policySettingNode.getTextContent());
                            }
                            break;
                        case ("MaxDevicePasswordFailedAttempts"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxDevicePasswordFailedAttempts = Long
                                        .parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxEmailAgeFilter"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxEmailAgeFilter = Long.parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxEmailBodyTruncationSize"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxEmailBodyTruncationSize = Integer.parseInt(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxEmailHtmlBodyTruncationSize"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxEmailHtmlBodyTruncationSize = Integer
                                        .parseInt(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MaxInactivityTimeDeviceLock"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                maxInactivityTimeDeviceLock = Long.parseLong(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MinDevicePasswordComplexCharacters"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                minDevicePasswordComplexCharacters = Byte
                                        .parseByte(policySettingNode.getTextContent());
                            }

                            break;
                        case ("MinDevicePasswordLength"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                minDevicePasswordLength = Byte.parseByte(policySettingNode.getTextContent());
                            }

                            break;
                        case ("PasswordRecoveryEnabled"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                passwordRecoveryEnabled = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireDeviceEncryption"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireDeviceEncryption = Boolean.parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireEncryptedSmimeMessages"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireEncryptedSmimeMessages = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireEncryptionSmimeAlgorithm"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireEncryptionSmimeAlgorithm = Integer
                                        .parseInt(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireManualSyncWhenRoaming"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireManualSyncWhenRoaming = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireSignedSmimeAlgorithm"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireSignedSmimeAlgorithm = Integer.parseInt(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireSignedSmimeMessages"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireSignedSmimeMessages = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("RequireStorageCardEncryption"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                requireStorageCardEncryption = Boolean
                                        .parseBoolean(policySettingNode.getTextContent());
                            }

                            break;
                        case ("UnapprovedInROMApplicationList"):
                            if (!TextUtils.isEmpty(policySettingNode.getTextContent())) {
                                unapprovedInROMApplicationList = parseAppList(policySettingNode);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        return true;
    }

    // This function parses the contents of the
    // ApprovedApplicationList and the UnapprovedInROMApplicationList
    // nodes.
    private String[] parseAppList(Node appListNode) {
        NodeList childNodes = appListNode.getChildNodes();
        String[] appList = new String[childNodes.getLength()];
        for (int i = 0, n = childNodes.getLength(); i < n; i++) {
            Node appNode = childNodes.item(i);
            appList[i] = appNode.getNodeValue();
        }
        return appList;
    }

    public enum EncryptionAlgorithm {
        TripleDES, DES, RC2_128bit, RC2_64bit, RC2_40bit
    }

    public enum SigningAlgorithm {
        SHA1, MD5
    }

    public enum CalendarAgeFilter {
        ALL, __dummyEnum__0, __dummyEnum__1, __dummyEnum__2, TWO_WEEKS, ONE_MONTH, THREE_MONTHS, SIX_MONTHS
    }

    public enum MailAgeFilter {
        ALL, ONE_DAY, THREE_DAYS, ONE_WEEK, TWO_WEEKS, ONE_MONTH
    }

    public enum PolicyStatus {
        None, Success, NoPolicyDefined, PolicyTypeUnknown, PolicyDataCorrupt, PolicyKeyMismatch
    }

}
