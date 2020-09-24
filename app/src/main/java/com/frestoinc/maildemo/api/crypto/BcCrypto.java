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

package com.frestoinc.maildemo.api.crypto;

import com.frestoinc.maildemo.data.enums.CryptoEnum;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSCompressedDataParser;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyAgreeEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyAgreeRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.ZlibCompressor;
import org.bouncycastle.cms.jcajce.ZlibExpanderProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.smime.SMIMECompressedGenerator;
import org.bouncycastle.mail.smime.smime.SMIMEException;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;

import timber.log.Timber;

/**
 * Created by frestoinc on 04,October,2019 for FullMailDemo.
 */
public class BcCrypto {

    private CustomKeyStore keyStore;

    private Object cardProvider;

    private CryptoAttributes attributes;

    /**
     * Instantiates a new Bc crypto with keystore and card provider.
     *
     * @param customKeyStore the custom key store
     * @param provider       the provider
     */
    public BcCrypto(CustomKeyStore customKeyStore, Object provider) {
        Timber.e("BcCrypto with params");
        this.keyStore = customKeyStore;
        this.cardProvider = provider;
        attributes = new CryptoAttributes();
        registerMailCapEntries();
    }

    /**
     * Instantiates a new Bc crypto.
     */
    public BcCrypto() {
        attributes = new CryptoAttributes();
        registerMailCapEntries();
    }

    private static ASN1ObjectIdentifier getContentType(byte[] data) throws IOException {
        try (ASN1InputStream inputStream = new ASN1InputStream(data)) {
            ASN1Sequence seq = (ASN1Sequence) inputStream.readObject();
            return seq.size() > 1 && seq.getObjectAt(0) instanceof ASN1ObjectIdentifier
                    ? (ASN1ObjectIdentifier) seq.getObjectAt(0) : null;
        }
    }

    private static byte[] getTextFromMimeMultipart(Object content)
            throws MessagingException, IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bodyPart = mp.getBodyPart(i);

                if (bodyPart.getContentType().contains("multipart")) {
                    mp = (Multipart) bodyPart.getContent();
                    i = 0;
                    continue;
                }

                if (!bodyPart.getContentType().contains("text/html")) {
                    continue;
                }

                Object o = bodyPart.getContent();
                if (o instanceof String) {
                    bos.write(((String) o).getBytes(StandardCharsets.UTF_8));
                }
            }
        } else {
            bos.write(((String) content).getBytes(StandardCharsets.UTF_8));
        }
        return bos.toByteArray();
    }

    private static byte[] streamToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int ch;

        while ((ch = in.read()) >= 0) {
            bos.write(ch);
        }

        return bos.toByteArray();
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public CryptoAttributes getAttributes() {
        return attributes;
    }

    private void registerMailCapEntries() {
        Security.removeProvider(CryptoConstant.BC);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("application/pkcs7-signature;;"
                + "x-java-content-handler=org.bouncycastle.mail.smime.smime.handlers.pkcs7_signature");
        mc.addMailcap("application/pkcs7-mime;;"
                + "x-java-content-handler=org.bouncycastle.mail.smime.smime.handlers.pkcs7_mime");
        mc.addMailcap("application/x-pkcs7-signature;;"
                + "x-java-content-handler=org.bouncycastle.mail.smime.smime.handlers.x_pkcs7_signature");
        mc.addMailcap("application/x-pkcs7-mime;;"
                + "x-java-content-handler=org.bouncycastle.mail.smime.smime.handlers.x_pkcs7_mime");
        mc.addMailcap("multipart/signed;;"
                + "x-java-content-handler=org.bouncycastle.mail.smime.smime.handlers.multipart_signed");
        CommandMap.setDefaultCommandMap(mc);
    }

    /**
     * Parse content type crypto enum.
     *
     * @param data the data
     * @return the crypto enum
     * @throws IOException the io exception
     */
    public CryptoEnum parseContentType(byte[] data) throws IOException {
        ASN1ObjectIdentifier id = getContentType(data);
        if (id == null) {
            return CryptoEnum.Normal;
        } else if (isEncrypted(id)) {
            return CryptoEnum.SignAndEncrypt;
        } else if (isSigned(id)) {
            return CryptoEnum.Sign;
        } else {
            return null;
        }
    }

    private boolean isEncrypted(ASN1ObjectIdentifier id) {
        return id.equals(CMSObjectIdentifiers.envelopedData);
    }

    private boolean isCompressed(ASN1ObjectIdentifier id) {
        return id.equals(CMSObjectIdentifiers.compressedData);
    }

    private boolean isSigned(ASN1ObjectIdentifier id) {
        return id.equals(CMSObjectIdentifiers.signedData);
    }

    private byte[] parseObjectData(Object partContent) throws IOException, MessagingException,
            CertificateException, CMSException, OperatorCreationException {

        if (partContent instanceof InputStream) {
            byte[] content = streamToByteArray((InputStream) partContent);
            ASN1ObjectIdentifier id = getContentType(content);
            if (id == null) {
                return content;
            } else if (isCompressed(id)) {
                return parseCompressedData(content);
            } else if (isSigned(id)) {
                return parseSignedData(content);
            }
        }
        return getTextFromMimeMultipart(partContent);
    }

    /****************************************************************
     *SIGN FUNCTIONS.**************************
     * @param bos the bos
     * @param isEcc the is ecc
     * @return the mime message
     * @throws MessagingException the messaging exception
     * @throws IOException the io exception
     * @throws CertificateEncodingException the certificate encoding exception
     * @throws OperatorCreationException the operator creation exception
     * @throws CMSException the cms exception
     * @throws UnrecoverableKeyException the unrecoverable key exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws KeyStoreException the key store exception
     */
    public MimeMessage cardSign(ByteArrayOutputStream bos, boolean isEcc) throws MessagingException,
            IOException, CertificateEncodingException, OperatorCreationException, CMSException,
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {

        final Session session = Session.getDefaultInstance(System.getProperties(), null);

        MimeMessage oriMsg = new MimeMessage(session, new ByteArrayInputStream(bos.toByteArray()));
        CMSSignedData signedData =
                cardSign(bos, keyStore.getSignPrivateKey(), keyStore.getSignCert(), isEcc);
        MimeMessage signedMessage = new MimeMessage(session);

        copyHeaderLines(oriMsg, signedMessage);
        setSignedDataHandler(signedData, signedMessage);
        setSignedDataHeader(signedMessage);

        largeLog(signedMessage);

        return signedMessage;
    }

    private CMSSignedData cardSign(ByteArrayOutputStream bos, PrivateKey key, X509Certificate cert,
                                   boolean isEcc)
            throws OperatorCreationException, CertificateEncodingException, CMSException {

        CMSSignedDataGenerator siGen = getCardSignGenerator(key, cert, isEcc);

        return siGen.generate(new CMSProcessableByteArray(bos.toByteArray()), true);
    }

    private void setSignedDataHeader(MimeMessage message) throws MessagingException {
        message.setHeader(CryptoConstant.CONTENT_TYPE, CryptoConstant.CONTENT_TYPE_SIGNED_DATA);
        message.setHeader(CryptoConstant.CONTENT_DISPOSITION, CryptoConstant.CONTENT_DISPOSITION_VALUE);
        message.setHeader(CryptoConstant.CONTENT_DESCRIPTION, CryptoConstant.SMIME_P7M);
    }

    private void setSignedDataHandler(CMSSignedData signedData, MimeMessage message)
            throws IOException, MessagingException {

        ByteArrayDataSource dataSource = new ByteArrayDataSource(signedData.getEncoded(),
                CryptoConstant.APPLICATION_PKCS7_SIGNATURE);
        DataHandler dataHandler = new DataHandler(dataSource);
        message.setDataHandler(dataHandler);
    }

    private CMSSignedDataGenerator getCardSignGenerator(PrivateKey key, X509Certificate cert,
                                                        boolean isEcc)
            throws CertificateEncodingException, OperatorCreationException, CMSException {

        CMSSignedDataGenerator siGen = new CMSSignedDataGenerator();
        String algorithm = isEcc
                ? CryptoConstant.DEFAULT_ECC_SIGNING
                : CryptoConstant.DEFAULT_RSA_SIGNING;
        ContentSigner sha1Signer =
                new JcaContentSignerBuilder(algorithm).setProvider(cardProvider).build(key);
        JcaDigestCalculatorProviderBuilder builder = new JcaDigestCalculatorProviderBuilder();
        builder.setProvider(CryptoConstant.BC);
        DigestCalculatorProvider digestProvider = builder.build();
        JcaSignerInfoGeneratorBuilder signBuilder = new JcaSignerInfoGeneratorBuilder(digestProvider);
        siGen.addSignerInfoGenerator(signBuilder.build(sha1Signer, cert));
        siGen.addCertificates(generateCertificates(cert));
        return siGen;
    }

    /****************************************************************
     *********************COMPRESS FUNCTIONS.************************
     ****************************************************************/

    private MimeBodyPart compress(MimeBodyPart m) throws SMIMEException {
        SMIMECompressedGenerator generator = new SMIMECompressedGenerator();
        return generator.generate(m, new ZlibCompressor());
    }

    /****************************************************************
     *ENCRYPT FUNCTIONS.************************
     * @param bos the bos
     * @param certs the certs
     * @param isEcc the is ecc
     * @return the mime message
     * @throws CertificateException the certificate exception
     * @throws KeyException the key exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     * @throws IOException the io exception
     * @throws NoSuchProviderException the no such provider exception
     * @throws CMSException the cms exception
     * @throws InvalidAlgorithmParameterException the invalid algorithm parameter exception
     * @throws MessagingException the messaging exception
     * @throws KeyStoreException the key store exception
     */
    public MimeMessage cardEncrypt(ByteArrayOutputStream bos, List<Certificate> certs,
                                   boolean isEcc)
            throws CertificateException, KeyException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException, CMSException, InvalidAlgorithmParameterException,
            MessagingException, KeyStoreException {

        certs.add(keyStore.getEncryptCert(isEcc));
        return isEcc ? cardEccEncrypt(bos, certs) : cardRsaEncrypt(bos, certs);
    }

    private MimeMessage cardRsaEncrypt(ByteArrayOutputStream bos, List<Certificate> certs)
            throws MessagingException, CertificateException, CMSException, IOException {

        final Session session = Session.getDefaultInstance(System.getProperties(), null);
        final CMSEnvelopedDataGenerator enGen = new CMSEnvelopedDataGenerator();

        for (Certificate c : certs) {
            enGen.addRecipientInfoGenerator(
                    new JceKeyTransRecipientInfoGenerator((X509Certificate) c));
        }
        CMSEnvelopedData envelopedData = getEnvelopedData(enGen, bos.toByteArray());

        MimeMessage oriMsg = new MimeMessage(session, new ByteArrayInputStream(bos.toByteArray()));
        MimeMessage encryptMessage = new MimeMessage(session);
        copyHeaderLines(oriMsg, encryptMessage);
        setEnvelopedDataHandler(envelopedData, encryptMessage);
        setEnvelopedDataHeader(encryptMessage);

        largeLog(encryptMessage);

        return encryptMessage;
    }

    private MimeMessage cardEccEncrypt(ByteArrayOutputStream bos, List<Certificate> certs)
            throws MessagingException, CMSException, IOException, NoSuchProviderException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException, CertificateException,
            KeyException {

        final Session session = Session.getDefaultInstance(System.getProperties(), null);

        final KeyPair keyPair = generateEcKeyPair();
        final CMSEnvelopedDataGenerator enGen = new CMSEnvelopedDataGenerator();

        if (keyPair.getPublic() == null || keyPair.getPrivate() == null) {
            throw new KeyException("Keypair generation failed!");
        }
        JceKeyAgreeRecipientInfoGenerator gen = getKeyAgreementGenerator(
                keyPair.getPrivate(), keyPair.getPublic());

        for (Certificate c : certs) {
            gen.addRecipient((X509Certificate) c);
        }

        enGen.addRecipientInfoGenerator(gen);

        CMSEnvelopedData envelopedData = getEnvelopedData(enGen, bos.toByteArray());

        MimeMessage oriMsg = new MimeMessage(session, new ByteArrayInputStream(bos.toByteArray()));
        MimeMessage encryptMessage = new MimeMessage(session);
        copyHeaderLines(oriMsg, encryptMessage);
        setEnvelopedDataHandler(envelopedData, encryptMessage);
        setEnvelopedDataHeader(encryptMessage);

        largeLog(encryptMessage);
        return encryptMessage;
    }

    private KeyPair generateEcKeyPair() throws InvalidAlgorithmParameterException,
            NoSuchProviderException, NoSuchAlgorithmException {

        KeyPairGenerator kpgen = KeyPairGenerator.getInstance("EC", CryptoConstant.BC);
        ECGenParameterSpec spec = new ECGenParameterSpec(CryptoConstant.DEFAULT_EC_CURVE);
        kpgen.initialize(spec, new SecureRandom());
        return kpgen.generateKeyPair();
    }

    private CMSEnvelopedData getEnvelopedData(CMSEnvelopedDataGenerator generator, byte[] data)
            throws CMSException {

        CMSProcessableByteArray byteArray = new CMSProcessableByteArray(data);
        return generator.generate(byteArray, getOutputEncryptor());
    }

    private OutputEncryptor getOutputEncryptor() throws CMSException {
        return new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                .setProvider(CryptoConstant.BC).build();
    }

    private void setEnvelopedDataHeader(MimeMessage message) throws MessagingException {
        message.setHeader(CryptoConstant.CONTENT_TYPE, CryptoConstant.CONTENT_TYPE_ENVELOPED_DATA);
        message.setHeader(CryptoConstant.CONTENT_DISPOSITION, CryptoConstant.CONTENT_DISPOSITION_VALUE);
        message.setHeader(CryptoConstant.CONTENT_DESCRIPTION, CryptoConstant.SMIME_P7M);
    }

    private void setEnvelopedDataHandler(CMSEnvelopedData envelopedData, MimeMessage message)
            throws IOException, MessagingException {

        ByteArrayDataSource dataSource =
                new ByteArrayDataSource(envelopedData.getEncoded(), CryptoConstant.APPLICATION_PKCS7_MIME);
        DataHandler dataHandler = new DataHandler(dataSource);
        message.setDataHandler(dataHandler);
    }

    private JceKeyAgreeRecipientInfoGenerator getKeyAgreementGenerator(PrivateKey privateKey,
                                                                       PublicKey publicKey) {
        return new JceKeyAgreeRecipientInfoGenerator(CMSAlgorithm.ECDH_SHA384KDF, privateKey,
                publicKey, CMSAlgorithm.AES256_WRAP);
    }

    private void copyHeaderLines(MimeMessage fromMessage, MimeMessage toMessage)
            throws MessagingException {

        Enumeration headerLines = fromMessage.getAllHeaderLines();
        while (headerLines.hasMoreElements()) {
            String nextElement = (String) headerLines.nextElement();
            toMessage.addHeaderLine(nextElement);
        }
    }

    private JcaCertStore generateCertificates(Certificate cert)
            throws CertificateEncodingException {
        List<Certificate> certList = new ArrayList<>();
        certList.add(cert);
        return new JcaCertStore(certList);
    }

    /**
     * Parse enveloped data byte [ ].
     *
     * @param data the data
     * @return the byte [ ]
     * @throws CMSException              the cms exception
     * @throws IOException               the io exception
     * @throws MessagingException        the messaging exception
     * @throws CertificateException      the certificate exception
     * @throws UnrecoverableKeyException the unrecoverable key exception
     * @throws NoSuchAlgorithmException  the no such algorithm exception
     * @throws KeyStoreException         the key store exception
     * @throws OperatorCreationException the operator creation exception
     */
    public byte[] parseEnvelopedData(byte[] data)
            throws CMSException, IOException, MessagingException, CertificateException,
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            OperatorCreationException {

        Timber.e("parseEnvelopedData");
        CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(data);
        parseEncryptionAttributes(parser);

        RecipientInformationStore recipients = parser.getRecipientInfos();
        Collection collections = recipients.getRecipients();
        Iterator it = collections.iterator();
        if (!it.hasNext()) {
            return "No Recipient".getBytes(StandardCharsets.UTF_8);
        }
        RecipientInformation information = (RecipientInformation) it.next();

        if (Arrays.asList(CryptoConstant.ECCAlgo).contains(information.getKeyEncryptionAlgOID())) {
            setEncryptionEcAttributes(information);
            return parseEnvelopedEcc(recipients);
        } else {
            return parseEnvelopedRsa(recipients);
        }
    }

    private byte[] parseEnvelopedRsa(RecipientInformationStore informationStore)
            throws CMSException, IOException, MessagingException, KeyStoreException,
            UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException,
            OperatorCreationException {

        RecipientId recipientId = new JceKeyTransRecipientId(keyStore.getSignCert());

        RecipientInformation info = informationStore.get(recipientId);
        if (info == null) {
            return "User not in Recipient List".getBytes(StandardCharsets.UTF_8);
        }

        Recipient recipient = new JceKeyTransEnvelopedRecipient(
                keyStore.getEncryptPrivateKey(false))
                .setProvider(cardProvider)
                .setContentProvider(CryptoConstant.BC);

        CMSTypedStream stream = info.getContentStream(recipient);
        MimeMessage mimeMessage = createMimeMessageInstance(stream);
        stream.drain();

        return parseObjectData(mimeMessage.getContent());
    }

    private byte[] parseEnvelopedEcc(RecipientInformationStore informationStore)
            throws CMSException, IOException, MessagingException, KeyStoreException,
            UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException,
            OperatorCreationException {

        RecipientId recipientId = new JceKeyAgreeRecipientId(keyStore.getEncryptCert(true));

        RecipientInformation info = informationStore.get(recipientId);
        if (info == null) {
            return "User not in Recipient List".getBytes(StandardCharsets.UTF_8);
        }

        Recipient recipient =
                new JceKeyAgreeEnvelopedRecipient(keyStore.getEncryptPrivateKey(true))
                        .setProvider(cardProvider);

        CMSTypedStream stream = info.getContentStream(recipient);
        MimeMessage mimeMessage = createMimeMessageInstance(stream);
        stream.drain();

        Timber.e("parseEnvelopedEcc");
        largeLog(mimeMessage);

        return parseObjectData(mimeMessage.getContent());
    }

    /**
     * Parse compressed data byte [ ].
     *
     * @param b the b
     * @return the byte [ ]
     * @throws CMSException              the cms exception
     * @throws MessagingException        the messaging exception
     * @throws IOException               the io exception
     * @throws CertificateException      the certificate exception
     * @throws OperatorCreationException the operator creation exception
     */
    private byte[] parseCompressedData(byte[] b) throws CMSException, MessagingException, IOException,
            CertificateException, OperatorCreationException {

        setCompressAttributes();

        CMSCompressedDataParser parser = new CMSCompressedDataParser(b);
        CMSTypedStream stream = parser.getContent(new ZlibExpanderProvider());

        MimeMessage mimeMessage = createMimeMessageInstance(stream);
        stream.drain();

        return parseObjectData(mimeMessage.getContent());
    }

    /**
     * Parse signed data byte [ ].
     *
     * @param b the b
     * @return the byte [ ]
     * @throws IOException               the io exception
     * @throws MessagingException        the messaging exception
     * @throws CMSException              the cms exception
     * @throws CertificateException      the certificate exception
     * @throws OperatorCreationException the operator creation exception
     */
    public byte[] parseSignedData(byte[] b) throws IOException, MessagingException, CMSException,
            CertificateException, OperatorCreationException {

        CMSSignedDataParser parser = new CMSSignedDataParser(new BcDigestCalculatorProvider(), b);
        CMSTypedStream stream = parser.getSignedContent();
        MimeMessage mimeMessage = createMimeMessageInstance(stream);
        setSignedAttributes(parser);
        stream.drain();

        return parseObjectData(mimeMessage.getContent());
    }

    private void parseEncryptionAttributes(CMSEnvelopedDataParser parser) {
        attributes.setEncrypt(true);
        ASN1ObjectIdentifier identifier = new ASN1ObjectIdentifier(parser.getEncryptionAlgOID());
        String algorithm = new DefaultAlgorithmNameFinder().getAlgorithmName(identifier);
        attributes.seteAlgorithm(algorithm);
    }

    private void setCompressAttributes() {
        attributes.setCompress(true);
    }

    private void setEncryptionEcAttributes(RecipientInformation information) {
        String eccAlgorithm = information.getKeyEncryptionAlgorithm().getAlgorithm().toString();
        ASN1ObjectIdentifier identifier = new ASN1ObjectIdentifier(eccAlgorithm);
        String algorithm = new DefaultAlgorithmNameFinder().getAlgorithmName(identifier);
        String namedAlgorithm = CryptoConstant.getAlgo().get(algorithm) != null
                ? CryptoConstant.getAlgo().get(algorithm)
                : algorithm;
        attributes.seteAlgorithm(attributes.geteAlgorithm() + "/" + namedAlgorithm);
    }

    private MimeMessage createMimeMessageInstance(CMSTypedStream stream) throws MessagingException {
        return new MimeMessage(
                Session.getDefaultInstance(System.getProperties()), stream.getContentStream());
    }

    //todo review
    private void setSignedAttributes(CMSSignedDataParser parser)
            throws CMSException, CertificateException, OperatorCreationException {
        Store store = parser.getCertificates();
        SignerInformationStore signers = parser.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();

        while (it.hasNext()) {
            attributes.setSign(true);
            SignerInformation signer = (SignerInformation) it.next();
            attributes.setsAlgorithm(new DefaultAlgorithmNameFinder().getAlgorithmName(
                    new ASN1ObjectIdentifier(signer.getEncryptionAlgOID())));

            Collection certCollection = store.getMatches(signer.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
            attributes.setVerified(signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider(CryptoConstant.BC).build(certHolder)));
            X500Name x500name = certHolder.getSubject();
            RDN cn = x500name.getRDNs(BCStyle.E)[0];
            attributes.setSigner(IETFUtils.valueToString(cn.getFirst().getValue()));
        }
    }

    /**
     * Create certificate certificate.
     *
     * @param b the b
     * @return the certificate
     * @throws CertificateException the certificate exception
     */
    public Certificate createCertificate(byte[] b) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream is = new ByteArrayInputStream(b);
        return cf.generateCertificate(is);
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
}