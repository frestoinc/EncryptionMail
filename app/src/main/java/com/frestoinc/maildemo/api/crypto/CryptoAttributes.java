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

/**
 * Created by frestoinc on 04,October,2019 for FullMailDemo.
 */
public class CryptoAttributes {

    private boolean isVerified = false;
    private boolean isSign = false;
    private boolean isEncrypt = false;
    private boolean isCompress = false;
    private String signer = "";
    private String ealgo = "";
    private String salgo = "";

    public boolean isSign() {
        return isSign;
    }

    public void setSign(boolean sign) {
        isSign = sign;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public boolean isCompress() {
        return isCompress;
    }

    public void setCompress(boolean compress) {
        isCompress = compress;
    }

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String geteAlgorithm() {
        return ealgo;
    }

    public void seteAlgorithm(String ealgo) {
        this.ealgo = ealgo;
    }

    public String getsAlgorithm() {
        return salgo;
    }

    public void setsAlgorithm(String salgo) {
        this.salgo = salgo;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
