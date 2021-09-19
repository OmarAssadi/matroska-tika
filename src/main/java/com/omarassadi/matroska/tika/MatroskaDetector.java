/*
 * Copyright 2021 Omar Assadi
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

package com.omarassadi.matroska.tika;

import com.google.common.io.CountingInputStream;
import com.omarassadi.matroska.ebml.EbmlReader;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import java.io.IOException;
import java.io.InputStream;

public class MatroskaDetector implements Detector {

    public static final MediaType MKV_MEDIA_TYPE = MediaType.video("x-matroska");
    public static final MediaType WEBM_MEDIA_TYPE = MediaType.video("webm");

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        if (input == null) {
            return MediaType.OCTET_STREAM;
        }

        if (!input.markSupported()) {
            return MediaType.OCTET_STREAM;
        }

        input.mark(4);
        try {
            if (input.read() != 0x1A || input.read() != 0x45 || input.read() != 0xDF || input.read() != 0xA3) {
                return MediaType.OCTET_STREAM;
            }
        } finally {
            input.reset();
        }
        final String docType = new EbmlReader(new CountingInputStream(input)).getDocType();
        switch (docType) {
            case "matroska":
                return MKV_MEDIA_TYPE;
            case "webm":
                return WEBM_MEDIA_TYPE;
            default:
                return MediaType.OCTET_STREAM;
        }
    }
}
