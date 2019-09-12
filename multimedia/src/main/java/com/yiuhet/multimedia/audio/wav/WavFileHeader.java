package com.yiuhet.multimedia.audio.wav;

import com.yiuhet.multimedia.audio.ByteUtils;

/**
 * Created by yiuhet on 2019/8/20.
 * <p>
 * wav文件头
 */
public class WavFileHeader {

    public static final int WAV_FILE_HEADER_SIZE = 44;
    public static final int WAV_CHUNKSIZE_EXCLUDE_DATA = 36;

    public static final int WAV_CHUNKSIZE_OFFSET = 4;
    public static final int WAV_SUB_CHUNKSIZE1_OFFSET = 16;
    public static final int WAV_SUB_CHUNKSIZE2_OFFSET = 40;
    /**
     * RIFF数据块
     */
    public String mChunkID = "RIFF";
    public int mChunkSize = 0; //整个 wav 文件的字节数 录制完成后计算填入  音频数据的长度 + 36
    public String mFormat = "WAVE";

    /**
     * FORMAT 数据块
     */
    public String mSubChunk1ID = "fmt ";
    public int mSubChunk1Size = 16; //表示fmt Chunk的数据块大小为16字节
    public short mAudioFormat = 1;//1：表示是PCM 编码
    public short mNumChannel = 1;//声道数，单声道为1，双声道为2
    public int mSampleRate = 8000;//	采样率
    public int mByteRate = 0;//码率 ：采样率 * 采样位数 * 声道个数，bytePerSecond = sampleRate * (bitsPerSample / 8) * channels
    public short mBlockAlign = 0;//每次采样的大小：位宽 * 声道数 / 8
    public short mBitsPerSample = 8; //位宽

    /**
     * FORMAT 数据块
     */
    public String mSubChunk2ID = "data";
    public int mSubChunk2Size = 0;//音频数据的长度     录制完成后计算填入

    public WavFileHeader() {
    }

    public WavFileHeader(int sampleRateInHz, int bitsPerSample, int channels) {
        mSampleRate = sampleRateInHz;
        mBitsPerSample = (short) bitsPerSample;
        mNumChannel = (short) channels;
        mByteRate = mSampleRate * mNumChannel * mBitsPerSample / 8;
        mBlockAlign = (short) (mNumChannel * mBitsPerSample / 8);
    }

    public void setChunkSize(int mChunkSize) {
        this.mChunkSize = mChunkSize;
    }

    public byte[] getHeader() {
        byte[] result;
        result = ByteUtils.merger(ByteUtils.toBytes(mChunkID), ByteUtils.toBytes(mChunkSize));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mFormat));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mSubChunk1ID));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mSubChunk1Size));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mAudioFormat));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mNumChannel));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mSampleRate));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mByteRate));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mBlockAlign));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mBitsPerSample));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mSubChunk2ID));
        result = ByteUtils.merger(result, ByteUtils.toBytes(mSubChunk2Size));
        return result;
    }
}
