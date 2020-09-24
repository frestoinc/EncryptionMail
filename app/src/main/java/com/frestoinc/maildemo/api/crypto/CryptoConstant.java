package com.frestoinc.maildemo.api.crypto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by frestoinc on 14,January,2020 for MailDemo.
 */
public class CryptoConstant {

    protected static final String[] ECCAlgo = new String[]{
            "1.3.133.16.840.63.0.2", //ECDH_SHA1KDF
            "1.3.133.16.840.63.0.3", //ECCDH_SHA1KDF
            "1.3.133.16.840.63.0", //ECMQV_SHA1KDF
            "1.3.133.16.840.63.0.16", //ECMQV_SHA1KDF
            "1.3.132.1.11.1", //ECDH_SHA256KDF
            "1.3.132.1.11.2", //ECDH_SHA384KDF
            "1.3.132.1.11.3", //ECDH_SHA512KDF
            "1.3.132.1.14.0", //ECCDH_SHA224KDF
            "1.3.132.1.14.2", //ECCDH_SHA384KDF
            "1.3.132.1.14.3", //ECCDH_SHA512KDF
    };
    static final String CONTENT_TYPE = "Content-type";
    static final String CONTENT_DISPOSITION = "Content-Disposition";
    static final String CONTENT_DESCRIPTION = "Content-Description";
    static final String APPLICATION_PKCS7_MIME = "application/pkcs7-mime";
    static final String APPLICATION_PKCS7_SIGNATURE = "application/pkcs7-signature";
    static final String SMIME_P7M = "smime.p7m";
    static final String CONTENT_TYPE_SIGNED_DATA = "application/pkcs7-mime; name=\"smime.p7m\"; "
            + "smime-type=signed-data";
    static final String CONTENT_TYPE_ENVELOPED_DATA = "application/pkcs7-mime; name=\"smime.p7m\"; "
            + "smime-type=enveloped-data";
    static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"smime.p7m\"";
    static final String SIGNING_CERT_ALIAS = "SIGNING_CERT_ALIAS";
    static final String SIGNING_KEY_ALIAS = "SIGNING_KEY_ALIAS";
    static final String RSA_ENCRYPTION_CERT_ALIAS = "RSA_ENCRYPTION_CERT_ALIAS";
    static final String RSA_ENCRYPTION_KEY_ALIAS = "RSA_ENCRYPTION_KEY_ALIAS";
    static final String ECC_AGREEMENT_CERT_ALIAS = "ECC_AGREEMENT_CERT_ALIAS";
    static final String ECC_AGREEMENT_KEY_ALIAS = "ECC_AGREEMENT_KEY_ALIAS";
    static final String DEFAULT_RSA_SIGNING = "SHA256withRSA";
    static final String DEFAULT_ECC_SIGNING = "SHA384withECDSA";
    static final String DEFAULT_EC_CURVE = "secp384r1";
    static final String BC = "BC";

    public static Map<String, String> getAlgo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("1.3.133.16.840.63.0.2", "ECDH_SHA1KDF");
        map.put("1.3.133.16.840.63.0.3", "ECCDH_SHA1KDF");
        map.put("1.3.133.16.840.63.0", "ECMQV_SHA1KDF");
        map.put("11.3.133.16.840.63.0.16", "ECMQV_SHA1KDF");
        map.put("1.3.132.1.11.1", "ECDH_SHA256KDF");
        map.put("1.3.132.1.11.2", "ECDH_SHA384KDF");
        map.put("1.3.132.1.11.3", "ECDH_SHA512KDF");
        map.put("1.3.132.1.14.0", "ECCDH_SHA224KDF");
        map.put("1.3.132.1.14.2", "ECCDH_SHA384KDF");
        map.put("1.3.132.1.14.3", "ECCDH_SHA512KDF");
        return map;
    }
}
