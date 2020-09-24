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

import com.frestoinc.maildemo.BuildConfig;
import com.frestoinc.maildemo.data.model.AccountUser;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

// This class represents a generic Exchange ActiveSync command request.
public class ASCommandRequest {

    private Device device = null;
    private AccountUser accountUser;
    private byte[] wbxmlBytes = null;
    private String xmlString = null;
    private String requestLine = null;
    private String command = null;
    private long policyKey = 0;
    private CommandParameter[] parameters = null;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public AccountUser getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(AccountUser accountUser) {
        this.accountUser = accountUser;
    }

    public byte[] getWbxmlBytes() {
        return wbxmlBytes;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String value) throws Exception {
        xmlString = value;
        // Loading XML causes immediate encoding
        wbxmlBytes = encodeXmlString(xmlString);
    }

    public String getRequestLine() throws Exception {
        // Generate on demand
        buildRequestLine();
        return requestLine;
    }

    public void setRequestLine(String value) {
        requestLine = value;
    }

    public String getCommand() {
        return command;
    }

    protected void setCommand(String value) {
        command = value;
    }

    public long getPolicyKey() {
        return policyKey;
    }

    public void setPolicyKey(long value) {
        policyKey = value;
    }

    public CommandParameter[] getCommandParameters() {
        return parameters;
    }

    // This function sends the request and returns
    // the response.
    public ASCommandResponse getResponse() throws Exception {
        generateXmlPayload();
        if (getAccountUser() == null) {
            throw new Exception("ASCommandRequest: Account Users not initialized.");
        }

        if (getDevice() == null) {
            throw new Exception("ASCommandRequest: Device not initialized.");
        }

        if (getWbxmlBytes() == null) {
            throw new Exception("ASCommandRequest: WbxmlBytes not initialized.");
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(getLoggingInterceptor());
        if (BuildConfig.DEBUG) {
            setAuthenticator(builder);
        }
        setTimeout(builder);

        OkHttpClient client = builder.build();

        Request request = getRequest();
        Response response = client.newCall(request).execute();
        ASCommandResponse asResponse = wrapHttpResponse(response);
        response.close();
        return asResponse;
    }

    private Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    private void setAuthenticator(OkHttpClient.Builder builder) {
        builder.authenticator((route, response) -> {
            String credentials = Credentials.basic(getAccountUser().getAccountemail(), getAccountUser().getAccountpwd());
            return response.request().newBuilder().header("Authorization", credentials).build();
        });
    }

    private Request getRequest() throws Exception {
        String uriString = String.format("%s/Microsoft-Server-ActiveSync?%s",
                getAccountUser().getAccountserver(), getRequestLine());
        return new Request.Builder()
                .url(uriString)
                .post(RequestBody.create(MediaType
                        .parse("application/vnd.ms-sync.wbxml"), getWbxmlBytes()))
                //.header("Authorization", "Basic " + getEncodedCredentials())
                .header("Content-Type", "application/vnd.ms-sync.wbxml")
                .header("MS-ASProtocolVersion", "16.0")
                .header("X-MS-PolicyKey", ((Long) getPolicyKey()).toString())
                .build();
    }

    private void setTimeout(OkHttpClient.Builder builder) {
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
    }

    // This function generates an ASCommandResponse from an
    // HTTP response.
    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASCommandResponse(response);
    }

    // This function builds a request line from the class properties.
    protected void buildRequestLine() throws Exception {
        setRequestLine(String.format("Cmd=%s&User=%s&DeviceId=%s&DeviceType=%s",
                URLEncoder.encode(getCommand(), "UTF-8"), URLEncoder.encode(getAccountUser().getAccountemail(), "UTF-8"),
                URLEncoder.encode(getDevice().getDeviceID(), "UTF-8"), URLEncoder.encode(getDevice().getDeviceType(), "UTF-8")));
        if (getCommandParameters() != null) {
            for (int i = 0; i < parameters.length; i++) {
                setRequestLine(
                        String.format("%s&%s=%s", getRequestLine(), getCommandParameters()[i].parameter,
                                URLEncoder.encode(getCommandParameters()[i].value, "UTF-8")));
            }
        }
    }

    // This function generates an XML payload.
    protected void generateXmlPayload() throws Exception {
    }

    // For the base class, this is a no-op.
    // Classes that extend this class to implement
    // commands override this function to generate
    // the XML payload based on the command's request schema
    // This function uses the ASWBXML class to decode
    // a WBXML stream into XML.
    private String decodeWbxml(byte[] wbxml) throws Exception {
        ASWBXML decoder = new ASWBXML();
        decoder.loadBytes(wbxml);
        return decoder.getXml();
    }

    // This function uses the ASWBXML class to encode
    // XML into a WBXML stream.
    private byte[] encodeXmlString(String xmlString) throws Exception {
        ASWBXML encoder = new ASWBXML();
        encoder.loadXml(xmlString);
        return encoder.getBytes();
    }
}
