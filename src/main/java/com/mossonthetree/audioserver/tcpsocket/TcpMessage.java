package com.mossonthetree.audioserver.tcpsocket;

import com.mossonthetree.audioserver.memory.MemoryInStream;
import com.mossonthetree.audioserver.memory.MemoryOutStream;

public class TcpMessage {
    public int messageType;

    public int dataLength;

    public byte[] data;

    public TcpMessage(byte[] buffer) {
        MemoryInStream miStream = new MemoryInStream(buffer);
        messageType = (int)Integer.toUnsignedLong(miStream.readInt(true));
        dataLength = (int)Integer.toUnsignedLong(miStream.readInt(true));
        if(dataLength > 0) {
            data = new byte[(int) dataLength];
            miStream.readSome(data, dataLength);
        }
        miStream.close();
    }

    public void write(MemoryOutStream stream) {
        stream.write(messageType, true);
        stream.write(dataLength, true);
        stream.writeSome(data);
    }
}
