package com.k2fsa.common;

import cn.hutool.core.util.ByteUtil;

/**
 * 转换工具类
 */
public class Convert {

    /**
     * float[]转byte[]
     * @param floats
     * @return byte[]
     */
    public static byte[] floatToByte(float[] floats) {
        byte[] result = new byte[floats.length * 4];
        for (int i = 0; i < floats.length; i++) {
            byte[] signalBytes = ByteUtil.intToBytes(Float.floatToIntBits(floats[i]));
            result[4 * i] = signalBytes[3];
            result[4 * i + 1] = signalBytes[2];
            result[4 * i + 2] = signalBytes[1];
            result[4 * i + 3] = signalBytes[0];
        }
        return result;
    }

}
