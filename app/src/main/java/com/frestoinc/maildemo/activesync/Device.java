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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

// This class represents a "device" and is used to
// generate a DeviceInformation Xml element, as specified
// in [MS-ASCMD] section 2.2.3.45.
public class Device {
    private String deviceID;
    private String deviceType;
    private String model;
    private String ImeiNumber;
    private String friendlyName;
    private String operatingSystem;
    private String operatingSystemLanguage;
    private String phoneNumber;
    private String mobileOperator;
    private String userAgent;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String value) {
        deviceID = value;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String value) {
        deviceType = value;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String value) {
        model = value;
    }

    public String getIMEI() {
        return ImeiNumber;
    }

    public void setIMEI(String value) {
        ImeiNumber = value;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String value) {
        friendlyName = value;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String value) {
        operatingSystem = value;
    }

    public String getOperatingSystemLanguage() {
        return operatingSystemLanguage;
    }

    public void setOperatingSystemLanguage(String value) {
        operatingSystemLanguage = value;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        phoneNumber = value;
    }

    public String getMobileOperator() {
        return mobileOperator;
    }

    public void setMobileOperator(String value) {
        mobileOperator = value;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String value) {
        userAgent = value;
    }

    // This function generates and returns an XmlNode for the
    // DeviceInformation element.
    public Node getDeviceInformationNode() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlDoc = builder.newDocument();

        Element deviceInfoElement = xmlDoc.createElementNS(Namespaces.settingsNamespace,
                Xmlns.settingsXmlns + ":DeviceInformation");
        xmlDoc.appendChild(deviceInfoElement);
        Element setElement = xmlDoc.createElementNS(Namespaces.settingsNamespace, Xmlns.settingsXmlns + ":Set");
        deviceInfoElement.appendChild(setElement);
        if (getModel() != null) {
            Element modelElement = xmlDoc.createElementNS(Namespaces.settingsNamespace, Xmlns.settingsXmlns + ":Model");
            modelElement.setTextContent(getModel());
            setElement.appendChild(modelElement);
        }

        if (getIMEI() != null) {
            Element imeielement = xmlDoc.createElementNS(Namespaces.settingsNamespace, Xmlns.settingsXmlns + ":IMEI");
            imeielement.setTextContent(getIMEI());
            setElement.appendChild(imeielement);
        }

        if (getFriendlyName() != null) {
            Element friendlyNameElement = xmlDoc.createElementNS(Namespaces.settingsNamespace,
                    Xmlns.settingsXmlns + ":FriendlyName");
            friendlyNameElement.setTextContent(getFriendlyName());
            setElement.appendChild(friendlyNameElement);
        }

        if (getOperatingSystem() != null) {
            Element operatingSystemElement = xmlDoc.createElementNS(Namespaces.settingsNamespace, Xmlns.settingsXmlns + ":OS");
            operatingSystemElement.setTextContent(getOperatingSystem());
            setElement.appendChild(operatingSystemElement);
        }

        if (getOperatingSystemLanguage() != null) {
            Element operatingSystemLanguageElement = xmlDoc.createElementNS(Namespaces.settingsNamespace,
                    Xmlns.settingsXmlns + ":OSLanguage");
            operatingSystemLanguageElement.setTextContent(getOperatingSystemLanguage());
            setElement.appendChild(operatingSystemLanguageElement);
        }

        if (getPhoneNumber() != null) {
            Element phoneNumberElement = xmlDoc.createElementNS(Namespaces.settingsNamespace,
                    Xmlns.settingsXmlns + ":PhoneNumber");
            phoneNumberElement.setTextContent(getPhoneNumber());
            setElement.appendChild(phoneNumberElement);
        }

        if (getMobileOperator() != null) {
            Element mobileOperatorElement = xmlDoc.createElementNS(Namespaces.settingsNamespace,
                    Xmlns.settingsXmlns + ":MobileOperator");
            mobileOperatorElement.setTextContent(getMobileOperator());
            setElement.appendChild(mobileOperatorElement);
        }

        if (getUserAgent() != null) {
            Element userAgentElement = xmlDoc.createElementNS(Namespaces.settingsNamespace, Xmlns.settingsXmlns + ":UserAgent");
            userAgentElement.setTextContent(getUserAgent());
            setElement.appendChild(userAgentElement);
        }

        return xmlDoc.getDocumentElement();
    }

}
