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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by frestoinc on 04,October,2019 for FullMailDemo.
 */
public class CustomKeyStore {

    private KeyStore keyStore;

    private Map<String, String> map;

    public CustomKeyStore(Object provider) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        map = new HashMap<>();
        keyStore = KeyStore.getInstance("SeKeystore", provider);
        keyStore.load(null); //todo pairing code

        Enumeration e = keyStore.aliases();
        while (e.hasMoreElements()) {
            String alias = (String) e.nextElement();
            if (alias.toLowerCase().contains("retired")) {
                continue;
            }

            if (keyStore.isCertificateEntry(alias)) {
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
                if (certificate == null) {
                    continue;
                }
                if (certificate.getKeyUsage()[0] && certificate.getKeyUsage()[1]) {
                    map.put(CryptoConstant.SIGNING_CERT_ALIAS, alias);
                } else if (certificate.getKeyUsage()[3]) {
                    map.put(CryptoConstant.RSA_ENCRYPTION_CERT_ALIAS, alias);
                } else if (certificate.getKeyUsage()[4]) {
                    map.put(CryptoConstant.ECC_AGREEMENT_CERT_ALIAS, alias);
                }
            }

            if (keyStore.isKeyEntry(alias)) {
                if (alias.contains("Digital Signature")) {
                    map.put(CryptoConstant.SIGNING_KEY_ALIAS, alias);
                } else if (alias.contains("PIV Authentication")) {
                    map.put(CryptoConstant.RSA_ENCRYPTION_KEY_ALIAS, alias);
                } else if (alias.contains("Key Management")) {
                    map.put(CryptoConstant.ECC_AGREEMENT_KEY_ALIAS, alias);
                }
            }
        }
    }

    private KeyStore getKeyStore() {
        return keyStore;
    }

    PrivateKey getSignPrivateKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) getKeyStore().getKey(getKeySignAlias(), null);
    }

    PrivateKey getEncryptPrivateKey(boolean isEcc) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) getKeyStore().getKey(
                isEcc ? getKeyAgreeAlias() : getKeySignAlias(), null);
    }

    X509Certificate getSignCert() throws KeyStoreException {
        return (X509Certificate) getKeyStore().getCertificate(getCertSignAlias());
    }

    X509Certificate getEncryptCert(boolean isEcc) throws KeyStoreException {
        return (X509Certificate) getKeyStore().getCertificate(
                isEcc ? getCertAgreeAlias() : getCertSignAlias());
    }

    private String getCertSignAlias() {
        return map.get(CryptoConstant.SIGNING_CERT_ALIAS);
    }

    private String getKeySignAlias() {
        return map.get(CryptoConstant.SIGNING_KEY_ALIAS);
    }

    private String getCertAgreeAlias() {
        Timber.e("getCertAgreeAlias: %s", map.get(CryptoConstant.ECC_AGREEMENT_CERT_ALIAS));
        return map.get(CryptoConstant.ECC_AGREEMENT_CERT_ALIAS);
    }

    private String getKeyAgreeAlias() {
        Timber.e("getKeyAgreeAlias: %s", map.get(CryptoConstant.ECC_AGREEMENT_KEY_ALIAS));
        return map.get(CryptoConstant.ECC_AGREEMENT_KEY_ALIAS);
    }
}
