/*
 * Copyright 2013 Alex Kasko (alexkasko.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexkasko.unsafe.offheappayload;

import com.alexkasko.unsafe.offheap.OffHeapAddressable;

/**
 * Interface for off-heap header-payload collection.
 *
 * @author alexkasko
 * Date: 3/4/13
 */
@Deprecated // use offheapstruct package
public interface OffHeapPayloadAddressable extends OffHeapAddressable {

    /**
     * Returns length of the each payload block in bytes
     *
     * @return length of the each payload block in bytes
     */
    int payloadLength();

    /**
     * Loads payload into provided buffer
     *
     * @param index collection index to load payload from
     * @param buffer buffer to load payload into
     */
    void getPayload(long index, byte[] buffer);

    /**
     * Sets header value and copies payload data on provided index
     *
     * @param index collection index to set header and payload
     * @param header header value
     * @param payload payload value
     */
    void set(long index, long header, byte[] payload);
}
