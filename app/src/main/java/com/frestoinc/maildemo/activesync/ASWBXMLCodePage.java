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

//
// Translated by CS2J (http://www.cs2j.com): 06/11/2015 15:37:33
//

package com.frestoinc.maildemo.activesync;

import java.util.HashMap;
import java.util.Map;

// This class represents a WBXML code page
// and associates a namespace (and corresponding xmlns value)
// to that code page.
public class ASWBXMLCodePage {
    private String codePageNamespace = "";
    private String codePageXmlns = "";
    private Map<Byte, String> tokenLookup = new HashMap<>();
    private Map<String, Byte> tagLookup = new HashMap<>();

    public String getNamespace() {
        return codePageNamespace;
    }

    public void setNamespace(String value) {
        codePageNamespace = value;
    }

    public String getXmlns() {
        return codePageXmlns;
    }

    public void setXmlns(String value) {
        codePageXmlns = value;
    }

    // This function adds a token/tag pair to the
    // code page.
    public void addToken(byte token, String tag) {
        tokenLookup.put(token, tag);
        tagLookup.put(tag, token);
    }

    // This function returns the token for a given
    // tag.
    public byte getToken(String tag) {
        if (tagLookup.containsKey(tag)) {
            return tagLookup.get(tag);
        }
        return (byte) 0xFF;
    }

    // This function returns the tag for a given
    // token.
    public String getTag(byte token) {
        if (tokenLookup.containsKey(token)) {
            return tokenLookup.get(token);
        }

        return null;
    }

}
