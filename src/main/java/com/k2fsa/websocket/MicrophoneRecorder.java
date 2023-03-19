package com.k2fsa.websocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneRecorder {
    private final AudioFormat audioFormat;
    private final TargetDataLine targetDataLine;
    public MicrophoneRecorder() throws LineUnavailableException {
        audioFormat = new AudioFormat(16000, 16, 1, true, false);
        targetDataLine = AudioSystem.getTargetDataLine(audioFormat);
        targetDataLine.open(audioFormat);
    }
    public void start() {
        targetDataLine.start();
    }
    public void stop() {
        targetDataLine.stop();
    }
    public byte[] getBytes(int numBytes) {
        byte[] data = new byte[numBytes];
        int numBytesRead = targetDataLine.read(data, 0, numBytes);
        if (numBytesRead == -1) {
            return null;
        }
        return data;
    }
}
