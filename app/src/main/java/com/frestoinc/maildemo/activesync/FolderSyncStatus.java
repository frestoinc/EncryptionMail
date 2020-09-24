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

public class FolderSyncStatus {
    // This enumeration covers the possible Status
    // values for FolderSync responses.
    public final static int Success = 1;
    public final static int ServerError = 6;
    public final static int InvalidSyncKey = 9;
    public final static int InvalidFormat = 10;
    public final static int UnknownError = 11;
    public final static int UnknownCod = 12;
}
