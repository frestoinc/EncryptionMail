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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

// This class extends the .NET Queue<byte> class
// to add some WBXML-specific functionality.
public class ASWBXMLByteQueue {
    private byte[] bytes;
    private int index;

    public ASWBXMLByteQueue(byte[] bytes) {
        this.bytes = bytes;
    }

    // This function will pop a multi-byte integer
    // (as specified in WBXML) from the WBXML stream.
    public int dequeueMultibyteInt() {
        int returnValue = 0;
        byte singleByte;
        do {
            returnValue <<= 7;
            singleByte = this.dequeue();
            returnValue |= singleByte & 0x7F;
        } while (checkContinuationBit(singleByte));
        return returnValue;
    }

    public byte dequeue() {
        return bytes[index++];
    }

    // This function checks a byte to see if the continuation
    // bit is set. This is used in deciphering multi-byte integers
    // in a WBXML stream.
    private boolean checkContinuationBit(byte byteValue) {
        byte continuationBitmask = (byte) 0x80;
        return (continuationBitmask & byteValue) != 0;
    }

    // This function pops a string from the WBXML stream.
    // It will read from the stream until a null byte is found.
    public String dequeueString() {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte currentByte = 0x00;
        do {
            // that characters could be more than one byte long. This will fail
            // if we have
            // characters outside of the US-ASCII range
            currentByte = this.dequeue();
            if (currentByte != 0x00) {
                byteArray.write(currentByte);
            }

        } while (currentByte != 0x00);
        return new String(byteArray.toByteArray(), StandardCharsets.UTF_8);
    }

    // This function pops a string of the specified length from
    // the WBXML stream.
    public String dequeueString(int length) {
        byte[] buffer = dequeueBinary(length);
        return new String(buffer, StandardCharsets.UTF_8);
    }

    // This function dequeues a byte array of the specified length
    // from the WBXML stream.
    public byte[] dequeueBinary(int length) {
        byte[] returnBytes = new byte[length];
        for (int i = 0; i < length; i++) {
            returnBytes[i] = dequeue();
        }
        return returnBytes;
    }

    public int getCount() {
        return bytes != null ? bytes.length - index : 0;
    }

}
