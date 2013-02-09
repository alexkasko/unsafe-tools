package com.alexkasko.util.unsafe;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
* User: alexkasko
* Date: 1/14/13
*/
public class LongPackUtilsTest {
    @Test
    public void test34() {
        String bigBodyStr = "01111111" + "10111111" + "11011111" + "11101111";
        String bigTailStr = "01";
        String littleBodyStr = "11111100" + "11110011" + "11001111";
        String littleTailStr = "001111";
        long big = Long.parseLong(bigBodyStr + bigTailStr, 2);
        int little = Integer.parseInt(littleBodyStr + littleTailStr, 2);
        long res = Long.parseLong(bigBodyStr + littleBodyStr + littleTailStr + bigTailStr, 2);
        long pack = LongPackUtils.pack(big, little, 34);
        long bigLoaded = LongPackUtils.big(pack, 34);
        int littleLoaded = LongPackUtils.little(pack, 34);
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
        long pack = LongPackUtils.pack(big, little, 59);
        long bigLoaded = LongPackUtils.big(pack, 59);
        int littleLoaded = LongPackUtils.little(pack, 59);
        assertEquals("Pack fail", Long.toBinaryString(pack), Long.toBinaryString(res));
//        System.out.println(Long.toBinaryString(pack));
//        System.out.println(Long.toBinaryString(big));
//        System.out.println(Long.toBinaryString(bigLoaded));
        assertEquals("Big load fail", Long.toBinaryString(big), Long.toBinaryString(bigLoaded));
        assertEquals("Little load fail", Integer.toBinaryString(little), Integer.toBinaryString(littleLoaded));
    }
}
