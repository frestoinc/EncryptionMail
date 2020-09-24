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

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Created by frestoinc on 04,November,2019 for FullMailDemo.
 */
public class SSLCredentials {

    private X509Certificate[] sslCertificate = null;
    private String alias;
    private PrivateKey privateKey;

    public X509Certificate[] getSslCertificate() {
        return sslCertificate;
    }

    public void setSslCertificate(X509Certificate[] sslCertificate) {
        this.sslCertificate = sslCertificate;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
