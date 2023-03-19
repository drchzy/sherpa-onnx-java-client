package com.k2fsa.websocket;

import com.k2fsa.common.Convert;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

/**
 * 实时采集麦克风设备语音通过WebSocket发送给sherpa-onnx并返回识别内容
 *
 * @author Eureka
 * @Date 2023/3/19
 */

public class WsMicroPhone {

    private static Logger log = Logger.getLogger("WsClient");

    public static void main(String[] args) throws IOException, LineUnavailableException {
        WsClient client = new WsClient();
        MicrophoneRecorder recorder = new MicrophoneRecorder();
        try {
            client.init();
            // 开启麦克风
            recorder.start();
            log.info("麦克风已打开");
            long startTime = System.currentTimeMillis();
            while (false || (System.currentTimeMillis() - startTime) < 10000) {
                // PCM数据直接发送
                byte[] pcmData = recorder.getBytes(1024);
                short[] sData = new short[pcmData.length / 2];
                ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sData);
                float[] fData = new float[sData.length];
                for (int i = 0; i < sData.length; i++) {
                    fData[i] = sData[i] / 32768.0f;
                }
                byte[] newByte = Convert.floatToByte(fData);
                client.send(newByte);
            }
        } finally {
            recorder.stop();
            client.close();
            log.info("麦克风已关闭，ws已关闭");
        }

    }
}
