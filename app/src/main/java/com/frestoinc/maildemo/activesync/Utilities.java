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

public class Utilities {
    // This function generates a string representation
    // of hexadecimal bytes.
    public static String printHex(byte[] bytes) {
        StringBuilder returnString = new StringBuilder();
        for (byte b : bytes) {
            returnString.append(String.format("%02X ", b));
        }
        return returnString.toString();
    }

    // This function converts a string representation
    // of hexadecimal bytes into a byte array
    public static byte[] convertHexToBytes(String hexString) {
        hexString = appendString(hexString);
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    public static String appendString(String s) {
        if (s.length() % 2 == 1) {
            return s + " ";
        }
        return s;
    }
}
