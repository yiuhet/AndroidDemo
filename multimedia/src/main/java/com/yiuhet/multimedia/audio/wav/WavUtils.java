package com.yiuhet.multimedia.audio.wav;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by yiuhet on 2019/8/20.
 */
public class WavUtils {
    private static final String TAG = "WavUtils";

    /**
     * 生成wav格式的Header
     * wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
     * FMT Chunk，Fact chunk（可选）,Data chunk
     *
     * @param sampleRate 采样率
     * @param channels   声道数
     * @param sampleBits 位宽
     */
    public static byte[] generateWavFileHeader(int sampleRate, int channels, int sampleBits) {
        WavFileHeader wavHeader = new WavFileHeader(sampleRate, channels, sampleBits);
        return wavHeader.getHeader();
    }

    /**
     * 将header写入到pcm文件中 不修改文件名
     *
     * @param file   写入的pcm文件
     * @param header wav头数据
     */
    public static void writeHeader(File file, byte[] header) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }

        RandomAccessFile wavRaf = null;
        try {
            wavRaf = new RandomAccessFile(file, "rw");
            wavRaf.seek(0);
            wavRaf.write(header);
            wavRaf.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (wavRaf != null) {
                    wavRaf.close();
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
