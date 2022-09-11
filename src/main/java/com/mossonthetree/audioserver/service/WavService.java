package com.mossonthetree.audioserver.service;

import com.mossonthetree.audioserver.access.WavAccessPoint;
import com.mossonthetree.audioserver.memory.MemoryInStream;
import com.mossonthetree.audioserver.memory.MemoryOutStream;
import com.mossonthetree.audioserver.view.WavFileView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WavService {
    @Autowired
    private WavAccessPoint wavAccessPoint;

    public WavFileView getSongLength(String artist, String album, String songName) {
        String fileName = String.format("%s/%s/%s", artist, album, songName);
        byte[] fileLengthBuffer = wavAccessPoint.getSongLength(fileName.getBytes());
        MemoryInStream miStream = new MemoryInStream(fileLengthBuffer);
        WavFileView wavFileView = new WavFileView();
        wavFileView.audioFileSize = (int)Integer.toUnsignedLong(miStream.readInt(true));
        wavFileView.artist = artist;
        wavFileView.album = album;
        wavFileView.songName = songName;
        miStream.close();
        return wavFileView;
    }

    public byte[] getSongChunk(int offset, int length, String artist, String album, String songName) {
        String filePath = String.format("%s/%s/%s", artist, album, songName);
        MemoryOutStream moStream = new MemoryOutStream();
        moStream.write(offset, true);
        moStream.write(length, true);
        moStream.write(filePath.length(), true);
        moStream.writeSome(filePath.getBytes());
        byte[] chunkBuffer = wavAccessPoint.getSongChunk(moStream.getBuffer());
        moStream.close();
        return chunkBuffer;
    }
}
