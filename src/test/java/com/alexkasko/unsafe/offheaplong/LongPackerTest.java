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

package com.alexkasko.unsafe.offheaplong;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
* User: alexkasko
* Date: 1/14/13
*/
public class LongPackerTest {
    @Test
    public void test34() {
        String bigBodyStr = "01111111" + "10111111" + "11011111" + "11101111";
        String bigTailStr = "01";
        String littleBodyStr = "11111100" + "11110011" + "11001111";
        String littleTailStr = "001111";
        long big = Long.parseLong(bigBodyStr + bigTailStr, 2);
        int little = Integer.parseInt(littleBodyStr + littleTailStr, 2);
        long res = Long.parseLong(bigBodyStr + littleBodyStr + littleTailStr + bigTailStr, 2);

        long pack = LongPacker.pack(big, little, 34);
        long bigLoaded = LongPacker.big(pack, 34);
        int littleLoaded = LongPacker.little(pack, 34);
        assertEquals("Pack fail", Long.toBinaryString(pack), Long.toBinaryString(res));
        assertEquals("Big load fail", Long.toBinaryString(big), Long.toBinaryString(bigLoaded));
        assertEquals("Little load fail", Integer.toBinaryString(little), Integer.toBinaryString(littleLoaded));
    }

    @Test
    public void test59() {
        String bigBodyStr = "01111111" + "10111111" + "11011111" + "11101111" + "11110111" + "11111011" + "11111101";
        String bigTailStr = "001";
        String littleTailStr = "01010";
        long big = Long.parseLong(bigBodyStr + bigTailStr, 2);
        int little = Integer.parseInt(littleTailStr, 2);
        long res = Long.parseLong(bigBodyStr + littleTailStr + bigTailStr, 2);

        long pack = LongPacker.pack(big, little, 59);
        long bigLoaded = LongPacker.big(pack, 59);
        int littleLoaded = LongPacker.little(pack, 59);
        assertEquals("Pack fail", Long.toBinaryString(pack), Long.toBinaryString(res));
        assertEquals("Big load fail", Long.toBinaryString(big), Long.toBinaryString(bigLoaded));
        assertEquals("Little load fail", Integer.toBinaryString(little), Integer.toBinaryString(littleLoaded));
    }
}
