package com.mossonthetree.audioserver;

import com.mossonthetree.audioserver.file.DirectoryInfo;
import com.mossonthetree.audioserver.file.FileInStream;
import com.mossonthetree.audioserver.memory.MemoryInStream;
import com.mossonthetree.audioserver.memory.MemoryOutStream;
import com.mossonthetree.audioserver.tcpsocket.OnReceiveSig;
import com.mossonthetree.audioserver.tcpsocket.TcpClient;
import com.mossonthetree.audioserver.tcpsocket.TcpMessage;
import com.mossonthetree.audioserver.tcpsocket.TcpServer;
import com.mossonthetree.audioserver.wav.WavFile;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.ByteBuffer;
import java.util.List;

@SpringBootApplication
public class AudioserverApplication {
	public static void main(String[] args) throws Exception {
		//SpringApplication.run(AudioserverApplication.class, args);

		OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive = (client, message) -> {
			MemoryOutStream moStream = new MemoryOutStream();
			String songsFolder = "/home/moss/Repos/audio_streamer/server/songs";

			switch (message.messageType) {
				case 2001 -> {
					DirectoryInfo dirInfo = new DirectoryInfo(songsFolder);
					List<String> files = dirInfo.getFiles();
					MemoryOutStream fileMoStream = new MemoryOutStream();
					fileMoStream.write(files.size(), true);
					for(String file : files) {
						fileMoStream.write(file.length(), true);
						fileMoStream.writeSome(file.getBytes());
					}
					message.dataLength = fileMoStream.size();
					message.data = fileMoStream.getBuffer();
					message.write(moStream);
					fileMoStream.close();
				}
				case 2002 -> {
					String fileName = new String(message.data);
					FileInStream fiStream = new FileInStream(String.format("%s/%s", songsFolder, fileName));
					byte[] fileBuffer = new byte[fiStream.getSize()];
					fiStream.readSome(fileBuffer);
					WavFile wav = new WavFile(fileBuffer);
					MemoryOutStream wavoStream = new MemoryOutStream();
					wavoStream.write(wav.getAudioDataSize(), true);
					message.dataLength = wavoStream.size();
					message.data = wavoStream.getBuffer();
					message.write(moStream);
					fiStream.close();
					wavoStream.close();
				}
				case 2003 -> {
					MemoryInStream miStream = new MemoryInStream(message.data);
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
					message.dataLength = computedLength;
					ByteBuffer byteBuffer = ByteBuffer.allocate(computedLength);
					byteBuffer.put(wav.getAudioData(), offset, computedLength);
					message.data = byteBuffer.array();
					message.write(moStream);
				}
			}
			client.send(moStream.getBuffer());
			moStream.close();
			return true;
		};

		TcpServer server = new TcpServer("127.0.0.1", 8081, onReceive);

		server.start();

		byte[] buffer = new byte[32];
		int read = System.in.read(buffer);
		System.out.println(new String(buffer, 0, read));

		server.stop();
		server.close();
	}

}

