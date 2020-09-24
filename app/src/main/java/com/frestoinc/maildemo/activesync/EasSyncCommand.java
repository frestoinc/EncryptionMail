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

import com.frestoinc.maildemo.data.model.EasMessage;

import java.util.ArrayList;
import java.util.List;

public class EasSyncCommand {

    private List<Command> added = new ArrayList<>();
    private List<Command> updated = new ArrayList<>();
    private List<Command> deleted = new ArrayList<>();
    private String syncKey;

    public Command add(String serverId, Type type) throws Exception {
        Command cmd = new Command();
        cmd.setId(serverId);
        cmd.setType(type);
        switch (type) {
            case Add:
                added.add(cmd);
                break;
            case Change:
                updated.add(cmd);
                break;
            case Delete:
            case SoftDelete:
                deleted.add(cmd);
                break;
            default:
                throw new Exception("Invalid sync command type: " + type.toString());
        }
        return cmd;
    }

    public List<Command> getAdded() {
        return added;
    }

    public List<Command> getUpdated() {
        return updated;
    }

    public List<Command> getDeleted() {
        return deleted;
    }

    public Command getLastAdded() {
        return !getAdded().isEmpty() ? getAdded().get(getAdded().size() - 1) : null;
    }

    public String getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(String syncKey) {
        this.syncKey = syncKey;
    }

    public int allSize() {
        return added.size() + updated.size() + deleted.size();
    }

    public enum Type {
        Invalid,
        Add,
        Change,
        Delete,
        SoftDelete
    }

    public class Command {
        private String id;
        private Type type;
        private EasMessage message;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public EasMessage getMessage() {
            return message;
        }

        public void setMessage(EasMessage message) {
            this.message = message;
        }
    }
}