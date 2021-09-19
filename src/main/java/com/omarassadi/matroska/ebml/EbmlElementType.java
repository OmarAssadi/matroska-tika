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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum EbmlElementType {

    DOCTYPE(new byte[]{0x42, (byte) 0x82}),
    UNKNOWN;

    private static final Set<EbmlElementType> TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values())));
    private final long id;

    EbmlElementType() {
        this.id = -1;
    }

    @SuppressWarnings("UnstableApiUsage")
    EbmlElementType(final byte[] id) {
        try {
            this.id = new EbmlReader(new CountingInputStream(new ByteArrayInputStream(id))).readEbmlInteger();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static EbmlElementType getById(final long id) {
        return TYPES.stream().filter(t -> t.id == id).findFirst().orElse(UNKNOWN);
    }
}
