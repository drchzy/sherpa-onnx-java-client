package com.k2fsa.websocket;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.URLUtil;
import com.k2fsa.common.Convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * wav语音文件通过WebSocket发送给sherpa-onnx并返回识别内容
 *
 * @author Eureka
 * @Date 2023/3/19
 */
public class WsDecodeFile {
    private static int SAMPLES_PER_MESSAGE = 4000;
    private static URL testFilePath = ResourceUtil.getResource("wavs\\4.wav");

    public String decodeFile(String filePath) throws IOException {
        WsClient client = new WsClient();
        try {
            client.init();
            File file = FileUtil.file(filePath);
            long fileSize = file.length();
            int oldDataSizeInt = Integer.parseInt(String.valueOf(fileSize));
            byte[] oldByte = new byte[oldDataSizeInt];
            int newDataSizeInt = Integer.parseInt(String.valueOf(fileSize - 44));
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(oldByte, 0, oldDataSizeInt);
            byte[] newByte = new byte[newDataSizeInt]; //定义byte数据长度
            // copy pcm data
            System.arraycopy(oldByte, 44, newByte, 0, newDataSizeInt);
            //byte[] to short[]
            short[] sData = new short[newByte.length / 2];
            ByteBuffer.wrap(newByte).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sData);
            //short[] to float[]
            float[] fData = new float[sData.length];
            for (int i = 0; i < sData.length; i++) {
                fData[i] = sData[i] / 32768.0f;
            }
            //float[] to byte[]
            byte[] bData = Convert.floatToByte(fData);
            int start = 0;
            while (start < bData.length) {
                int end = Math.min(start + SAMPLES_PER_MESSAGE, bData.length);
                byte[] d = Arrays.copyOfRange(bData, start, end);
                client.send(d);
                ThreadUtil.sleep(10, TimeUnit.MILLISECONDS);
                start += SAMPLES_PER_MESSAGE;
            }
            client.sendStr("Done");
            ThreadUtil.sleep(200, TimeUnit.MILLISECONDS);
            return WsClient.sessionTextMap.get(WsClient.getSession().getId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
            WsClient.sessionTextMap.remove(WsClient.getSession().getId());
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        WsDecodeFile wsDecodeFile = new WsDecodeFile();
        String res = wsDecodeFile.decodeFile(URLUtil.getDecodedPath(testFilePath));
        System.out.println("result：" + res);
    }

}
