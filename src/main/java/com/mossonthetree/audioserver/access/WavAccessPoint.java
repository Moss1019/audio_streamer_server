package com.mossonthetree.audioserver.access;

import com.mossonthetree.audioserver.file.DirectoryInfo;
import com.mossonthetree.audioserver.file.FileInStream;
import com.mossonthetree.audioserver.memory.MemoryInStream;
import com.mossonthetree.audioserver.memory.MemoryOutStream;
import com.mossonthetree.audioserver.wav.WavFile;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.List;

@Service
public class WavAccessPoint {
    private final String songsFolder = "content";
    public byte[] getSongList() {
        DirectoryInfo dirInfo = new DirectoryInfo(songsFolder);
        List<String> files = dirInfo.getFiles();
        try(MemoryOutStream fileMoStream = new MemoryOutStream()) {
            fileMoStream.write(files.size(), true);
            for (String file : files) {
                fileMoStream.write(file.length(), true);
                fileMoStream.writeSome(file.getBytes());
            }
            return fileMoStream.getBuffer();
        }
    }

    public byte[] getSongLength(byte[] fileNameBuffer) {
        String fileName = new String(fileNameBuffer);
        FileInStream fiStream = new FileInStream(String.format("%s/%s", songsFolder, fileName));
        byte[] fileBuffer = new byte[fiStream.getSize()];
        fiStream.readSome(fileBuffer);
        fiStream.close();
        WavFile wav = new WavFile(fileBuffer);
        try(MemoryOutStream wavoStream = new MemoryOutStream()) {
            wavoStream.write(wav.getAudioDataSize(), true);
            return wavoStream.getBuffer();
        }
    }

    public byte[] getSongChunk(byte[] requestInfoBuffer) {
        MemoryInStream miStream = new MemoryInStream(requestInfoBuffer);
        int offset = (int)Integer.toUnsignedLong(miStream.readInt(true));
        int length = (int)Integer.toUnsignedLong(miStream.readInt(true));
        int fileNameLength = (int)Integer.toUnsignedLong(miStream.readInt(true));
        byte[] fileNameBuffer = new byte[fileNameLength];
        miStream.readSome(fileNameBuffer, fileNameLength);
        String fileName = new String(fileNameBuffer);
        FileInStream fiStream = new FileInStream(String.format("%s/%s", songsFolder, fileName));
        byte[] wavFileBuffer = new byte[fiStream.getSize()];
        fiStream.readSome(wavFileBuffer);
        WavFile wav = new WavFile(wavFileBuffer);
        fiStream.close();
        int computedLength = length;
        if(offset + computedLength > wav.getAudioDataSize()) {
            computedLength = wav.getAudioDataSize() - offset;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(computedLength);
        byteBuffer.put(wav.getAudioData(), offset, computedLength);
        miStream.close();
        return byteBuffer.array();
    }
}
