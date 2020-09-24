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

public class ProvisionStatus {
    // This enumeration covers the Provision-
    // specific status values that can come from
    // the server.
    public static final int Success = 1;
    public static final int SyntaxError = 2;
    public static final int ServerError = 3;
    public static final int DeviceNotFullyProvisionable = 139;
    public static final int LegacyDeviceOnStrictPolicy = 141;
    public static final int ExternallyManagedDevicesNotAllowed = 145;
    public static final int DeviceInformationRequired = 165;
}
