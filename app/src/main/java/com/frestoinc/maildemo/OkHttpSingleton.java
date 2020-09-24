package com.frestoinc.maildemo;

import android.os.Build;

import com.frestoinc.maildemo.activesync.ASCommandResponse;
import com.frestoinc.maildemo.activesync.ASWBXML;
import com.frestoinc.maildemo.activesync.CommandParameter;
import com.frestoinc.maildemo.activesync.Device;

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

/**
 * Created by frestoinc on 14,November,2019 for MailDemo.
 */
public class OkHttpSingleton {

    private static OkHttpSingleton INSTANCE;

    private OkHttpClient client;

    private String server = null;
    private byte[] wbxmlBytes = null;
    private String xmlString = null;
    private String requestLine = null;
    private String command = null;
    private String user = null;
    private String pwd = null;
    private long policyKey = 0;
    private CommandParameter[] parameters = null;

    private OkHttpSingleton(String user, String pwd, String server) {
        setUser(user);
        setPwd(pwd);
        setServer(server);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(getLoggingInterceptor());
        setAuthenticator(builder);
        builder.connectTimeout(10, TimeUnit.SECONDS); // connect timeout
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        client = builder.build();
    }

    /**
     * OkHttp Singleton.
     *
     * @return single instance of OkHttp.
     */
    public static OkHttpSingleton getInstance(String user, String pwd, String server) {
        if (INSTANCE == null) {
            INSTANCE = new OkHttpSingleton(user, pwd, server);
        }
        return INSTANCE;
    }

    private Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    private void setAuthenticator(OkHttpClient.Builder builder) {
        builder.authenticator((route, response) -> {
            String credentials = Credentials.basic(getUser(), getPwd());
            return response.request().newBuilder().header("Authorization", credentials).build();
        });
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Request getRequest(byte[] data) throws Exception {
        String uriString = String.format("%s/Microsoft-Server-ActiveSync?%s",
                getServer(), getRequestLine());
        return new Request.Builder()
                .url(uriString)
                .post(RequestBody.create(MediaType
                        .parse("application/vnd.ms-sync.wbxml"), data))
                //.header("Authorization", "Basic " + getEncodedCredentials())
                .header("Content-Type", "application/vnd.ms-sync.wbxml")
                .header("MS-ASProtocolVersion", "16.0")
                .header("X-MS-PolicyKey", ((Long) getPolicyKey()).toString())
                .build();
    }

    protected ASCommandResponse wrapHttpResponse(Response response) throws Exception {
        return new ASCommandResponse(response);
    }

    private String buildRequestLine() throws Exception {
        if (getCommand() == null) {
            throw new Exception("ASCommandRequest: Command not initialized.");
        }
        if (getUser() == null) {
            throw new Exception("ASCommandRequest: User not initialized.");
        }

        // Generate a plain-text request line.
        String requestLine = (String.format("Cmd=%s&User=%s&DeviceId=%s&DeviceType=%s",
                URLEncoder.encode(getCommand(), "UTF-8"), URLEncoder.encode(getUser(), "UTF-8"),
                URLEncoder.encode(getDevice().getDeviceID(), "UTF-8"), URLEncoder.encode(getDevice().getDeviceType(), "UTF-8")));
        if (getParameters() != null) {
            for (int i = 0; i < parameters.length; i++) {
                requestLine = String.format("%s&%s=%s", getRequestLine(), getParameters()[i].parameter,
                        URLEncoder.encode(getParameters()[i].value, "UTF-8"));
            }
        }
        return requestLine;
    }

    private byte[] encodeXmlString(String xmlString) throws Exception {
        ASWBXML encoder = new ASWBXML();
        encoder.loadXml(xmlString);
        return encoder.getBytes();
    }

    public long getPolicyKey() {
        return policyKey;
    }

    public void setPolicyKey(long policyKey) {
        this.policyKey = policyKey;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getRequestLine() throws Exception {
        // Generate on demand
        return buildRequestLine();
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    public byte[] getWbxmlBytes() {
        return wbxmlBytes;
    }

    public void setWbxmlBytes(byte[] wbxmlBytes) {
        this.wbxmlBytes = wbxmlBytes;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String xmlString) throws Exception {
        this.xmlString = xmlString;
        wbxmlBytes = encodeXmlString(this.xmlString);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Device getDevice() {
        Device device = new Device();
        device.setDeviceID("Phone");
        device.setDeviceType("Mobile");
        device.setModel(Build.MODEL);
        return device;
    }

    public CommandParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(CommandParameter[] parameters) {
        this.parameters = parameters;
    }
}
