package com.mossonthetree.audioserver.wav;

import com.mossonthetree.audioserver.memory.MemoryInStream;

public class WavFile {
    private String fileType;

    private long fileSize;

    private String fileHeader;

    private String formatMarker;

    private long formatLength;

    private int formatType;

    private int channels;

    private long sampleRate;

    private long clockRate;

    private int bitsPerFrame;

    private int bitsPerSample;

    private String dataMarker;

    private int audioDataSize;

    private byte[] audioData;

    public WavFile(byte[] fileData) {
        final long mask = 0x0ffffffffL;
        MemoryInStream miStream = new MemoryInStream(fileData);
        byte[] buffer = new byte[1024];
        miStream.readSome(buffer, 4);
        fileType = new String(buffer, 0, 4);
        fileSize = Integer.toUnsignedLong(miStream.readInt(true));
        miStream.readSome(buffer, 4);
        fileHeader = new String(buffer, 0, 4);
        miStream.readSome(buffer, 4);
        formatMarker = new String(buffer, 0, 4);
        formatLength = Integer.toUnsignedLong(miStream.readInt(true));
        formatType = Short.toUnsignedInt(miStream.readShort(true));
        channels = Short.toUnsignedInt(miStream.readShort(true));
        sampleRate = Integer.toUnsignedLong(miStream.readInt(true));
        clockRate = Integer.toUnsignedLong(miStream.readInt(true));
        bitsPerFrame = Short.toUnsignedInt(miStream.readShort(true));
        bitsPerSample = Short.toUnsignedInt(miStream.readShort(true));
        miStream.readSome(buffer, 4);
        dataMarker = new String(buffer, 0, 4);
        audioDataSize = (int)Integer.toUnsignedLong(miStream.readInt(true));
        audioData = new byte[(int)audioDataSize];
        miStream.readSome(audioData, (int)audioDataSize);
        miStream.close();
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileHeader() {
        return fileHeader;
    }

    public String getFormatMarker() {
        return formatMarker;
    }

    public long getFormatLength() {
        return formatLength;
    }

    public int getFormatType() {
        return formatType;
    }

    public int getChannels() {
        return channels;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public long getClockRate() {
        return clockRate;
    }

    public int getBitsPerFrame() {
        return bitsPerFrame;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public String getDataMarker() {
        return dataMarker;
    }

    public int getAudioDataSize() {
        return audioDataSize;
    }

    public byte[] getAudioData() {
        return audioData;
    }
}
