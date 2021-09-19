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

import com.google.common.io.CountingInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.omarassadi.matroska.ebml.EbmlElementType.DOCTYPE;

@SuppressWarnings("UnstableApiUsage")
public class EbmlReader {

    private final CountingInputStream inputStream;

    public EbmlReader(final CountingInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getDocType() throws IOException {
        final EbmlElement ebml = readNextElement(null);
        if (ebml == null) {
            return null;
        }
        EbmlElement child;
        while ((child = readNextElement(ebml)) != null) {
            if (child.getType().equals(DOCTYPE)) {
                return asString(child);
            }
            skip(child);
        }
        return null;
    }

    public EbmlElement readNextElement(final EbmlElement parent) throws IOException {
        long position = inputStream.getCount();
        long remaining = parent != null ? parent.getRemaining(position) : inputStream.available() - position;
        if (remaining == 0) {
            return null;
        } else if (remaining < 0) {
            throw new IllegalStateException("Current position is beyond this element");
        }
        long id = readEbmlInteger();
        long dataSize = readEbmlInteger();
        long dataPosition = inputStream.getCount();
        return new EbmlElement(parent == null ? 0 : parent.getLevel() + 1, id, EbmlElementType.getById(id),
                position, (int) (dataPosition - position), (int) dataSize);
    }

    public String asString(final EbmlElement element) throws IOException {
        return new String(asBytes(element), StandardCharsets.US_ASCII);
    }

    public void skip(final EbmlElement element) throws IOException {
        long remaining = element.getRemaining(inputStream.getCount());
        if (remaining > 0) {
            if (inputStream.skip(remaining) != remaining) {
                throw new IllegalStateException("Skipped fewer bytes than expected!");
            }
        } else if (remaining < 0) {
            throw new IllegalStateException("Current position is beyond this element");
        }
    }

    public long readEbmlInteger() throws IOException {
        int firstByte = inputStream.read() & 0xFF;
        int codeLength = readCodeSize(firstByte);
        long code = applyFirstByte(firstByte, codeLength);
        for (int i = 2; i <= codeLength; i++) {
            code |= applyNextByte(codeLength, inputStream.read() & 0xFF, i);
        }
        return code;
    }

    public byte[] asBytes(final EbmlElement element) throws IOException {
        byte[] bytes = new byte[element.getDataSize()];
        if (inputStream.read(bytes) != bytes.length) {
            throw new IllegalStateException("Read fewer bytes than expected!");
        }
        return bytes;
    }

    private int readCodeSize(final int firstByte) {
        int codeLength = Integer.numberOfLeadingZeros(firstByte) - 23;
        if (codeLength > 8) {
            throw new IllegalStateException("More than 4 bytes for length, probably invalid data");
        }
        return codeLength;
    }

    private long applyFirstByte(final long firstByte, final int codeLength) {
        return (firstByte & (0xFFL >> codeLength)) << ((codeLength - 1) << 3);
    }

    private long applyNextByte(final int codeLength, final int value, final int index) {
        return (long) value << ((codeLength - index) << 3);
    }
}
