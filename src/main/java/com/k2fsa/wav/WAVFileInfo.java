package com.k2fsa.wav;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class WAVFileInfo {

    private static URL filePath = ResourceUtil.getResource("wavs\\4.wav");

    public static void main(String[] args) {
        try {
            File file = FileUtil.file(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioInputStream.getFormat();
            int sampleSizeInBits = format.getSampleSizeInBits(); // 每个样本的位数
            float sampleRate = format.getSampleRate(); // 采样率
            int numChannels = format.getChannels(); // 声道数
            long numFrames = audioInputStream.getFrameLength(); // 总帧数
            long numSamples = numFrames * numChannels; // 样本数
            System.out.println("该WAV文件有" + numSamples + "个样本，每个样本是" + sampleSizeInBits + "个bit。");
            System.out.println("采样率为：" + sampleRate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
