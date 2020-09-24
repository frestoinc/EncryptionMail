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

public class FolderInfo {

    public String id;
    public String parentId;
    public String name;
    public int type;
    public Action action;
    // The current sync key for this folder
    public String syncKey = "0";
    // Should items deleted from this folder
    // be permanently deleted?
    public boolean areDeletesPermanent = false;
    // Should changes be ignored?
    public boolean areChangesIgnored = false;
    // The max number of changes that should be
    // returned in a sync.
    public int windowSize = 0;
    // Conversation mode
    public boolean useConversationMode = false;
    public FolderSyncOptions options;

    // This enumeration covers the available
    // sync filter types specified in MS-ASCMD
    // section 2.2.3.64.2
    public enum SyncFilterType {
        NoFilter, OneDayBack, ThreeDaysBack, OneWeekBack, TwoWeeksBack, OneMonthBack, ThreeMonthsBack, SixMonthsBack, IncompleteTasks
    }

    // This enumeration covers the possible
    // values for the Conflict element specified
    // in MS-ASCMD 2.2.3.34
    public enum MimeTruncationType {
        TruncateAll, Truncate4k, Truncate5k, Truncate7k, Truncate10k, Truncate20k, Truncate50k, Truncate100k, NoTruncate
    }

    // This enumeration covers the possible
    // values for the MIMESupport element
    // specified in MS-ASCMD section 2.2.3.100.3
    public enum MimeSupport {
        NeverSendMime, SendMimeForSMime, SendMimeForAll
    }

    // This enumeration covers the allowable
    // body types specified in MS-ASAIRS section
    // 2.2.2.22
    public enum BodyType {
        NoType, PlainText, HTML, RTF, MIME
    }

    // This enumeration covers the possible
    // values for the Conflict element specified
    // in MS-ASCMD 2.2.3.34
    public enum ConflictResolution {
        KeepClientVersion, KeepServerVersion, LetServerDecide
    }

    public enum Action {
        None, Added, Deleted, Updated
    }

    // This class represents body or body part
    // preferences included in a <BodyPreference> or
    // <BodyPartPreference> element.
    public class BodyPreferences {
        public BodyType Type = BodyType.NoType;
        public long TruncationSize = 0;
        public boolean AllOrNone = false;
        public int Preview = -1;
    }

    // This class represents the sync options
    // that are included under the <Options> element
    // in a Sync command request.
    public class FolderSyncOptions {
        public SyncFilterType FilterType = SyncFilterType.NoFilter;
        public ConflictResolution ConflictHandling = ConflictResolution.LetServerDecide;
        public MimeTruncationType MimeTruncation = MimeTruncationType.NoTruncate;
        public String Class = null;
        public int MaxItems = -1;
        public BodyPreferences[] BodyPreference = null;
        public BodyPreferences BodyPartPreference = null;
        public boolean IsRightsManagementSupported = false;
        public MimeSupport MimeSupportLevel = MimeSupport.NeverSendMime;
    }
}
