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

public class PolicyAcknowledgement {
    // This enumeration covers the acceptable values
    // of the Status element in a Provision request
    // when acknowledging a policy, as specified
    // in MS-ASPROV section 3.1.5.1.2.1.
    public static final int Success = 1;
    public static final int PartialSuccess = 2;
    public static final int PolicyIgnored = 3;
    public static final int ExternalManagement = 4;

}
