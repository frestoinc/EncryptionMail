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

public final class GlobalTokens {
    // This enumeration is straight from the WBXML spec's
    // list of global tokens.
    public final static byte SWITCH_PAGE = (byte) 0x00;
    public final static byte END = (byte) 0x01;
    public final static byte ENTITY = (byte) 0x02;
    public final static byte STR_I = (byte) 0x03;
    public final static byte LITERAL = (byte) 0x04;
    public final static byte EXT_I_0 = (byte) 0x40;
    public final static byte EXT_I_1 = (byte) 0x41;
    public final static byte EXT_I_2 = (byte) 0x42;
    public final static byte PI = (byte) 0x43;
    public final static byte LITERAL_C = (byte) 0x44;
    public final static byte EXT_T_0 = (byte) 0x80;
    public final static byte EXT_T_1 = (byte) 0x81;
    public final static byte EXT_T_2 = (byte) 0x82;
    public final static byte STR_T = (byte) 0x83;
    public final static byte LITERAL_A = (byte) 0x84;
    public final static byte EXT_0 = (byte) 0xC0;
    public final static byte EXT_1 = (byte) 0xC1;
    public final static byte EXT_2 = (byte) 0xC2;
    public final static byte OPAQUE = (byte) 0xC3;
    public final static byte LITERAL_AC = (byte) 0xC4;
}
