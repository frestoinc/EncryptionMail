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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.Response;
import timber.log.Timber;

// This class represents a generic Exchange ActiveSync command response.
public class ASCommandResponse {
    private byte[] wbxmlBytes;
    private String xmlString;
    private int httpStatus;
    private String httpStatusMessage;

    public ASCommandResponse(Response response) throws Exception {
        httpStatus = response.code();
        httpStatusMessage = response.message();
        if (httpStatus != 200) {
            throw new Exception("HTTP error " + httpStatus + ": " + httpStatusMessage);
        }
        InputStream responseStream;
        if (response.body() != null) {
            responseStream = response.body().byteStream();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            byte[] byteBuffer = new byte[256];
            int count;
            count = responseStream.read(byteBuffer, 0, 256);
            while (count > 0) {
                bytes.write(byteBuffer, 0, count);
                count = responseStream.read(byteBuffer, 0, 256);
            }
            //wbxmlBytes contains message header
            wbxmlBytes = bytes.toByteArray();
            // Decode the WBXML
            xmlString = decodeWBXML(wbxmlBytes);

            largeLog(xmlString);
        }
    }


    private void largeLog(String content) {
        if (content.length() > 2000) {
            Timber.e(content.substring(0, 2000));
            largeLog(content.substring(2000));
        } else {
            Timber.e(content);
        }
    }

    public byte[] getWbxmlBytes() {
        return wbxmlBytes;
    }

    public String getXmlString() {
        return xmlString;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getHttpStatusMessage() {
        return httpStatusMessage;
    }

    // This function uses the ASWBXML class to decode
    // a WBXML stream into XML.
    private String decodeWBXML(byte[] wbxml) throws Exception {
        ASWBXML decoder = new ASWBXML();
        decoder.loadBytes(wbxml);
        return decoder.getXml();
    }
}
