package com.frestoinc.maildemo.ui.sharedviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frestoinc.maildemo.AuthResource;
import com.frestoinc.maildemo.SessionManager;
import com.frestoinc.maildemo.activesync.ASItemOperationsResponse;
import com.frestoinc.maildemo.activesync.ASMailStatusResponse;
import com.frestoinc.maildemo.activesync.EasConnection;
import com.frestoinc.maildemo.api.crypto.BcCrypto;
import com.frestoinc.maildemo.api.crypto.CryptoAttributes;
import com.frestoinc.maildemo.api.crypto.CustomKeyStore;
import com.frestoinc.maildemo.base.BaseViewModel;
import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.enums.ClassificationEnum;
import com.frestoinc.maildemo.data.enums.ComposeEnum;
import com.frestoinc.maildemo.data.enums.CryptoEnum;
import com.frestoinc.maildemo.data.local.DataManager;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by frestoinc on 23,December,2019 for MailDemo.
 */
public class MessageViewModel extends BaseViewModel {

    /**
     * Injected Session manager.
     */
    @Inject
    public SessionManager sessionManager;
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> shouldReadSmartCard = new MutableLiveData<>();
    private MutableLiveData<Boolean> shouldDoEncrypt = new MutableLiveData<>();
    private MutableLiveData<EasMessage> easMessage = new MutableLiveData<>();
    private MutableLiveData<List<String>> toList = new MutableLiveData<>();
    private MutableLiveData<List<String>> ccList = new MutableLiveData<>();
    private MutableLiveData<ComposeEnum> composeEnum = new MutableLiveData<>();
    private MutableLiveData<AuthResource> syncResource = new MutableLiveData<>();
    private MutableLiveData<MimeMessage> mimeMessage = new MutableLiveData<>();
    private MutableLiveData<CryptoAttributes> attributes = new MutableLiveData<>();
    private byte[] attachmentData;

    /**
     * Instantiates a new Message view model.
     *
     * @param schedulerProvider the scheduler provider
     * @param manager           the manager
     */
    @Inject
    public MessageViewModel(SchedulerProvider schedulerProvider, DataManager manager) {
        super(schedulerProvider, manager);
    }

    @Override
    public void setError(Throwable e) {
        if (syncResource != null) {
            syncResource.setValue(AuthResource.error(e.getMessage()));
            shouldReadSmartCard.setValue(false);
        }
        e.printStackTrace();
        error.setValue(e.getMessage());
    }

    /**
     * Gets error result.
     *
     * @return the error result
     */
    public LiveData<String> getErrorResult() {
        return error;
    }

    /**
     * Gets eas message.
     *
     * @return the eas message
     */
    public LiveData<EasMessage> getEasMessage() {
        return easMessage;
    }

    /**
     * Gets to list.
     *
     * @return the to list
     */
    public LiveData<List<String>> getToList() {
        return toList;
    }

    /**
     * Gets cc list.
     *
     * @return the cc list
     */
    public LiveData<List<String>> getCcList() {
        return ccList;
    }

    /**
     * Gets compose enum.
     *
     * @return the compose enum
     */
    public LiveData<ComposeEnum> getComposeEnum() {
        return composeEnum;
    }

    /**
     * Sets the global {@link ComposeEnum} when there is a change in state.
     *
     * @param cenum the cenum
     */
    public void setComposeEnum(ComposeEnum cenum) {
        composeEnum.setValue(cenum);
        setGlobalComposeEnum(cenum);
    }

    /**
     * Gets session manager.
     *
     * @return the session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * Gets sync resource.
     *
     * @return the sync resource
     */
    public LiveData<AuthResource> getSyncResource() {
        return syncResource;
    }

    /**
     * Gets is reading card.
     *
     * @return the is reading card
     */
    public LiveData<Boolean> getShouldReadSmartCard() {
        return shouldReadSmartCard;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public LiveData<CryptoAttributes> getAttributes() {
        return attributes;
    }

    /**
     * Gets should do encrypt.
     *
     * @return the should do encrypt
     */
    private LiveData<Boolean> getShouldDoEncrypt() {
        return shouldDoEncrypt;
    }

    private byte[] getAttachmentData() {
        return attachmentData;
    }

    private void setAttachmentData(byte[] attachmentData) {
        this.attachmentData = attachmentData;
    }

    /**
     * Gets global compose enum.
     *
     * @return the global compose enum
     */
    public ComposeEnum getGlobalComposeEnum() {
        return getSessionManager().getCachedMessageType().getValue();
    }

    private void setGlobalComposeEnum(ComposeEnum composeEnum) {
        MutableLiveData<ComposeEnum> liveData = new MutableLiveData<>();
        liveData.setValue(composeEnum);
        getSessionManager().setCachedMessageType(liveData);
    }

    /**
     * Sets global message.
     *
     * @param message the message
     */
    public void setGlobalMessage(EasMessage message) {
        MutableLiveData<EasMessage> liveData = new MutableLiveData<>();
        liveData.setValue(message);
        getSessionManager().setCachedMessage(liveData);
    }

    /**
     * Sets new message.
     */
    public void setNewMessage() {
        easMessage.setValue(new EasMessage());
    }

    /**
     * Gets global eas message.
     *
     * @return the global eas message
     */
    public EasMessage getGlobalEasMessage() {
        return getSessionManager().getCachedMessage().getValue();
    }

    /**
     * Gets account email.
     *
     * @return the account email
     */
    public String getAccountEmail() {
        return getSessionManager().getAccountUser().getValue().getAccountemail();
    }

    /**
     * Gets {@link EasMessage} from ActiveSync to set the HTML body part.
     * Followed by getting {@link com.frestoinc.maildemo.data.enums.ClassificationEnum}
     * from ActiveSync to set the classification
     *
     * @param message the message
     */
    public void getEasMessageFromAs(EasMessage message) {
        //todo merge
        syncResource.setValue(AuthResource.loading("Retrieving message from server..."));
        Single.fromCallable(() -> {
            EasConnection conn = getSessionManager().getEasConnection();
            long key = conn.getPolicyKey();
            return conn.getItemOperationsFullBodyCommand(key, message.getFolderId(), message.getServerId());
        })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<byte[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(byte[] bytes) {
                        //message.setMessage(bytes);
                        //getEasMessageClassification(message);
                        try {
                            parseFullBody(message, bytes);
                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                            setError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void getEasMessageClassification(EasMessage message) {
        syncResource.setValue(AuthResource.loading("Retrieving message from server..."));
        Single.fromCallable(() -> {
            EasConnection conn = getSessionManager().getEasConnection();
            long key = conn.getPolicyKey();
            return conn.getItemOperationsFullBodyCommand(
                    key, message.getFolderId(), message.getServerId());
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<byte[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(byte[] content) {
                        try {
                            parseFullBody(message, content);
                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                            setError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void parseFullBody(EasMessage message, byte[] content) throws MessagingException, IOException {
        String[] headerName = new String[]
                {"x-classification", "X-SecureAge-Classification", "message classification"};
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties()),
                new ByteArrayInputStream(content));
        //todo convert new instance of easmessage with mimemessage

        largeLog(mimeMessage);

        Timber.e("type: %s", mimeMessage.getContentType());

        Enumeration e = mimeMessage.getMatchingHeaderLines(headerName);
        String classification = "Unclassified";
        while (e.hasMoreElements()) {
            String nextElement = (String) e.nextElement();
            Timber.e("");
            String[] regex = nextElement.split(":");
            classification = regex[regex.length - 1];
        }
        classification = classification.replaceAll("\\s", "");
        message.setClassification(classification);
        message.setSync(true);
        updateEasMessage(message);
    }

    private void largeLog(Object o) throws IOException, MessagingException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (o instanceof MimeBodyPart || o instanceof MimeMessage) {
            ((MimePart) o).writeTo(bos);
        }
        String output = new String(bos.toByteArray());
        largeLog(output);
    }

    private void largeLog(String content) {
        if (content.length() > 2000) {
            Timber.e(content.substring(0, 2000));
            largeLog(content.substring(2000));
        } else {
            Timber.e(content);
        }
    }

    /**
     * Gets local room {@link com.frestoinc.maildemo.data.local.room.dao.EasMessageDao}.
     *
     * @param message the message
     */
    public void getEasMessageFromDb(EasMessage message) {
        syncResource.setValue(AuthResource.loading("Retrieving message..."));
        getDataManager().getEasMessage(message.getPriKey())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<EasMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(EasMessage message) {
                        syncResource.setValue(AuthResource.authenticated(null));
                        easMessage.setValue(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private MutableLiveData<MimeMessage> getMimeMessage() {
        return mimeMessage;
    }

    private void updateEasMessage(EasMessage message) {
        syncResource.setValue(AuthResource.loading("Updating local database..."));
        getDataManager().updateMessage(message)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        syncResource.setValue(AuthResource.authenticated(null));
                        easMessage.setValue(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Send mail.
     *
     * @param subject the subject
     * @param body    the body
     * @throws MessagingException the messaging exception
     */
    public void sendMail(String subject, String body) throws MessagingException {
        getEasMessage().getValue().setSubject(subject);
        MimeMessage message = createEmptyMimeMessage();
        message.setContent(generateBody(body), "text/html");
        mimeMessage.setValue(message);
        if (shouldEncrypt()) {
            shouldDoEncrypt.setValue(true);
            shouldReadSmartCard.setValue(true);
        } else {
            send();
        }
    }

    private boolean shouldEncrypt() {
        return getDataManager().getClassification() == ClassificationEnum.Secret
                || getDataManager().getEncryption() == CryptoEnum.Sign
                || getDataManager().getEncryption() == CryptoEnum.SignAndEncrypt;
    }

    private void send() {
        syncResource.setValue(AuthResource.loading("Connecting to server..."));
        Single.fromCallable(() -> {
            EasConnection conn = getSessionManager().getEasConnection();
            long policyKey = conn.getPolicyKey();
            postStatus("Connected...");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            getMimeMessage().getValue().writeTo(bos);
            postStatus("Sending...");
            return conn.sendNewMailCommand(policyKey, Utils.generateRandomDigits(),
                    new String(bos.toByteArray(), StandardCharsets.UTF_8));
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<ASMailStatusResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(ASMailStatusResponse response) {
                        boolean b = response.getStatus() == 1;
                        syncResource.setValue(AuthResource.authenticated(b));
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private MimeMessage createEmptyMimeMessage() throws MessagingException {
        final MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
        message.setFrom(new InternetAddress(getAccountEmail()));
        Address[] recpAdd = Utils.getAddresses(getToList().getValue());
        Address[] ccAdd = Utils.getAddresses(getCcList().getValue());
        if (recpAdd.length == 0) {
            throw new MessagingException("Unable to parse recipient list.");
        }
        message.setRecipients(Message.RecipientType.TO, recpAdd);
        if (ccAdd.length > 0) {
            message.setRecipients(Message.RecipientType.CC, ccAdd);
        }
        message.setSubject(getSubject());
        String classification = getDataManager().getClassification().toString();
        message.setHeader(Constants.X_CLASSIFICATION.toLowerCase(Locale.getDefault()), classification);
        message.setHeader(Constants.SECURAGE_CLASSIFICATION, classification);
        message.setHeader(Constants.MESSAGE_CLASSIFICATION, classification);
        return message;
    }

    private String getSubject() {
        if (getEasMessage().getValue() != null) {
            switch (getGlobalComposeEnum()) {
                case NEW:
                    return getEasMessage().getValue().getSubject();
                case REPLY:
                case REPLY_ALL:
                    return "RE:".concat(getEasMessage().getValue().getSubject());
                case FORWARD:
                    return "FW:".concat(getEasMessage().getValue().getSubject());
                default:
                    break;
            }
        }
        return "";
    }

    private String generateBody(String body) {
        return getGlobalComposeEnum() == ComposeEnum.NEW
                ? Utils.createBodyText(getDataManager().getClassification().toString(), body)
                : Utils.createReplyBodyText(getDataManager().getClassification().toString(),
                body, getEasMessage().getValue());
    }

    /******************************************************************************
     KEYSTORE OPERATIONS
     -SUPPLY CARD PROVIDE PARAM {@link Object}.
     -INSTANTIATE {@link CustomKeyStore} with {@link #readKeyStore(Object)}
     -DECIDED WHETHER TO ENCRYPT OR DECRYPT with {@link #getShouldDoEncrypt()}VALUE.
     *******************************************************************************/

    private CustomKeyStore readKeyStore(Object provider) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        postStatus("Reading Certificates...");
        return new CustomKeyStore(provider);
    }

    /**
     * Store aliases to a hashmap when reading the smart card.
     *
     * @param provider the provider
     */
    public void setKeyStore(Object provider) {
        syncResource.postValue(AuthResource.loading("SmartCard Detected..."));
        Single.fromCallable(
                () -> readKeyStore(provider))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<CustomKeyStore>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(CustomKeyStore keyStore) {
                        BcCrypto bcCrypto = new BcCrypto(keyStore, provider);
                        syncResource.setValue(AuthResource.loading("Initialising Cryptography..."));
                        if (getShouldDoEncrypt().getValue()) {
                            sign(bcCrypto);
                        } else {
                            decrypt(bcCrypto);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Gets attachment from ActiveSync Server by supplying the attachment id.
     */
    public void getAttachmentFromAS() {
        if (getEasMessage().getValue() == null) {
            setError(new Throwable("Unable to retrieve message"));
            return;
        }
        syncResource.setValue(AuthResource.loading("Retrieving attachment from server..."));
        EasMessage message = getEasMessage().getValue();
        Single.fromCallable(() -> {
            EasConnection conn = getSessionManager().getEasConnection();
            long policyKey = conn.getPolicyKey();
            return conn.getItemOperationsAttachmentCommand(policyKey, message.getAttachmentReference());
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<ASItemOperationsResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(ASItemOperationsResponse response) {
                        if (response == null
                                || response.getStatus() != 1
                                || response.getAttachmentData().length == 0) {
                            setError(
                                    new Throwable("Unable to retrieve attachment / attachment content is corrupted")
                            );
                            return;
                        }
                        if (getEasMessage().getValue().getAttachmentName().endsWith(".p7m")) {
                            byte[] data = Base64.getDecoder().decode(response.getAttachmentData());
                            setAttachmentData(data);
                            getP7mType();
                        } else {
                            setError(new Throwable("todo handle attachment type"));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void getP7mType() {
        BcCrypto crypto = new BcCrypto();
        Single.fromCallable(
                () -> crypto.parseContentType(getAttachmentData()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<CryptoEnum>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(CryptoEnum cryptoEnum) {
                        if (cryptoEnum == CryptoEnum.Sign) {
                            verify(crypto);
                        } else if (cryptoEnum == CryptoEnum.SignAndEncrypt) {
                            shouldReadSmartCard.setValue(true);
                            shouldDoEncrypt.setValue(false);
                        } else {
                            setNewEasMessageContent(crypto, getAttachmentData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void setNewEasMessageContent(BcCrypto crypto, byte[] data) {
        syncResource.setValue(AuthResource.authenticated(null));
        Timber.e("data: %s", new String(data));
        EasMessage newMessage = getEasMessage().getValue();
        newMessage.setMessage(data);
        newMessage.setDecrypted(true);
        attributes.setValue(crypto.getAttributes());
        easMessage.setValue(newMessage);
    }

    private void verify(BcCrypto crypto) {
        try {
            setNewEasMessageContent(crypto, crypto.parseSignedData(getAttachmentData()));
        } catch (Exception e) {
            e.printStackTrace();
            setNewEasMessageContent(crypto, e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    private void decrypt(BcCrypto bcCrypto) {
        Single.fromCallable(() -> {
            postStatus("Decrypting...");
            return bcCrypto.parseEnvelopedData(getAttachmentData());
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<byte[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(byte[] bytes) {
                        setNewEasMessageContent(bcCrypto, bytes);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }


    /**************************************************************************************
     ENCRYPTION OPERATIONS
     -INSTANTIATE {@link BcCrypto}.
     -DEFAULT TO {@link BcCrypto#cardSign(ByteArrayOutputStream, boolean)}
     -IF {@link CryptoEnum#SignAndEncrypt} proceed to {@link #queryLdapAndEncrypt(BcCrypto)}
     and {@link #encrypt(BcCrypto, List)}.
     **************************************************************************************/

    private void sign(BcCrypto bcCrypto) {
        Single.fromCallable(() -> {
            postStatus("Signing...");
            MimeMessage message = getMimeMessage().getValue();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            message.writeTo(bos);
            return bcCrypto.cardSign(bos, getDataManager().getIsEcc());
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<MimeMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(MimeMessage message) {
                        postStatus("Successfully Signed...");
                        mimeMessage.setValue(message);
                        if (getDataManager().getEncryption() == CryptoEnum.Sign) {
                            send();
                        } else {
                            queryLdapAndEncrypt(bcCrypto);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void queryLdapAndEncrypt(BcCrypto bcCrypto) {
        postStatus("Initialising ldap server...");
        Single.fromCallable(
                () -> getCertificates(bcCrypto))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<Certificate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<Certificate> list) {
                        encrypt(bcCrypto, list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private List<Certificate> getCertificates(BcCrypto bcCrypto) throws LDAPException, CertificateException {
        List<Certificate> certList = new ArrayList<>();
        try (LDAPConnection connection = new LDAPConnection("your ip", 389)) {
            postStatus("Connecting ldap server...");
            if (connection.isConnected()) {
                postStatus("Ldap server connected ...");
                connection.bind("your query");
                SearchRequest request = buildSearchRequest();
                SearchResult result = connection.search(request);
                postStatus("Searching for certificates...");
                if (result.getEntryCount() < 0) {
                    throw new LDAPException(ResultCode.NO_RESULTS_RETURNED, "Ldap query returns empty");
                } else if (result.getEntryCount() < toList.getValue().size() + ccList.getValue().size()) {
                    throw new LDAPException(ResultCode.NO_SUCH_ATTRIBUTE, "Not able to download all certificates");
                }
                postStatus("Encryption Certificate Obtained...");
                certList = parseSearchResult(bcCrypto, result);
            }
            return certList;
        }
    }

    private SearchRequest buildSearchRequest() throws LDAPException {
        List<String> queryList = Utils.getCompiledList(toList.getValue(), ccList.getValue());
        Filter filter = Filter.create(Utils.buildLdapQuery(queryList));
        Timber.e("Querying " + filter.toString() + "...");
        postStatus("Querying " + filter.toString() + "...");
        return new SearchRequest("your project", SearchScope.SUB, filter);
    }

    private List<Certificate> parseSearchResult(BcCrypto bcCrypto, SearchResult result) throws CertificateException {
        List<Certificate> certList = new ArrayList<>();
        postStatus("Parsing certificate attributes...");
        for (SearchResultEntry entry : result.getSearchEntries()) {
            if (entry.hasAttribute("userCertificate")) {
                Attribute attribute = entry.getAttribute("userCertificate");
                if (attribute != null) {
                    for (byte[] b : attribute.getValueByteArrays()) {
                        Certificate c = bcCrypto.createCertificate(b);
                        Timber.e("Certificate: %s", Base64.getEncoder().encodeToString(c.getEncoded()));
                        certList.add(c);
                    }
                } else {
                    throw new CertificateException("Invalid certificate attribute");
                }
            }
        }
        return certList;
    }

    private void encrypt(BcCrypto bcCrypto, List<Certificate> list) {
        postStatus("Began encryption...");
        Single.fromCallable(() -> {
            postStatus("Encrypting...");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MimeMessage message = getMimeMessage().getValue();
            message.writeTo(bos);
            return bcCrypto.cardEncrypt(bos, list, getDataManager().getIsEcc());
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<MimeMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(MimeMessage message) {
                        postStatus("Encrypted!");
                        mimeMessage.setValue(message);
                        send();
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /************************************************
     SYNCHRONISE UI UPDATE
     /************************************************

     /**
     * Update to list.
     *
     * @param source the source
     */
    public void updateToList(List<String> source) {
        source.remove(getAccountEmail());
        toList.setValue(source);
    }

    /**
     * Update cc list.
     *
     * @param source the source
     */
    public void updateCcList(List<String> source) {
        source.remove(getAccountEmail());
        ccList.setValue(source);
    }

    /**
     * Parse multiple recipient list.
     *
     * @param e the e
     */
    public void parseMultipleRecipientList(EasMessage e) {
        List<String> list = Utils.extractMailAddresses(e.getTo());
        list.add(Utils.extractMailAddresses(e.getFrom()).get(0));
        list.remove(getAccountEmail());
        toList.setValue(list.stream().distinct().collect(Collectors.toList()));

        if (e.getCc() != null && !e.getCc().isEmpty()) {
            ccList.setValue(Utils.extractMailAddresses(e.getCc()));
        }
    }

    /**
     * Parse single recipient list.
     *
     * @param e the e
     */
    public void parseSingleRecipientList(EasMessage e) {
        toList.setValue(Utils.extractMailAddresses(e.getFrom()));
    }

    /**
     * Parse forward recipient list.
     */
    public void parseForwardRecipientList() {
        updateToList(new ArrayList<>());
        updateCcList(new ArrayList<>());
    }

    /**
     * Build message attribute string.
     *
     * @return the string
     */
    public String buildMessageAttribute() {
        String s = "Classification Status: " + getDataManager().getClassification() + "\n"
                + "Encryption Status: " + getDataManager().getEncryption() + "\n";
        if (shouldEncrypt()) {
            s = s.concat("Algorithm: " + (getDataManager().getIsEcc() ? "ECC" : "RSA") + "\n");
        }
        return s;
    }

    private void postStatus(String msg) {
        syncResource.postValue(AuthResource.loading(msg));
    }
}
