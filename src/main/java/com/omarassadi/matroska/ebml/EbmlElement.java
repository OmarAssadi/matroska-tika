/*
 * Copyright 2017-2021 LavaPlayer contributors, Omar Assadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omarassadi.matroska.ebml;

public class EbmlElement {

    private final int level;
    private final long id;
    private final EbmlElementType type;
    private final long position;
    private final int headerSize;
    private final int dataSize;

    public EbmlElement(int level, long id, EbmlElementType type, long position, int headerSize, int dataSize) {
        this.level = level;
        this.id = id;
        this.type = type;
        this.position = position;
        this.headerSize = headerSize;
        this.dataSize = dataSize;
    }

    public int getLevel() {
        return level;
    }

    public long getId() {
        return id;
    }

    public EbmlElementType getType() {
        return type;
    }

    public long getPosition() {
        return position;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public long getRemaining(long currentPosition) {
        return (position + headerSize + dataSize) - currentPosition;
    }

    public long getDataPosition() {
        return position + headerSize;
    }
}
