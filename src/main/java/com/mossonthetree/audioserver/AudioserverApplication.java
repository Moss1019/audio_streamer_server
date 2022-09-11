package com.mossonthetree.audioserver;

import com.mossonthetree.audioserver.access.WavAccessPoint;
import com.mossonthetree.audioserver.memory.MemoryOutStream;
import com.mossonthetree.audioserver.tcpsocket.OnReceiveSig;
import com.mossonthetree.audioserver.tcpsocket.TcpClient;
import com.mossonthetree.audioserver.tcpsocket.TcpMessage;
import com.mossonthetree.audioserver.tcpsocket.TcpServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AudioserverApplication {
	public static void main(String[] args) throws Exception {
		SpringApplicationBuilder app = new SpringApplicationBuilder(AudioserverApplication.class);
		app.build();
		ConfigurableApplicationContext appContext = app.run();

		OnReceiveSig<TcpClient, TcpMessage, Boolean> onReceive = (client, message) -> {
			MemoryOutStream moStream = new MemoryOutStream();
			WavAccessPoint wavAccessPoint = new WavAccessPoint();
			switch (message.messageType) {
				case 2001 -> {
					byte[] fileList = wavAccessPoint.getSongList();
					message.data = fileList;
					message.dataLength = fileList.length;
					message.write(moStream);
				}
				case 2002 -> {
					byte[] fileLength = wavAccessPoint.getSongLength(message.data);
					message.data = fileLength;
					message.dataLength = fileLength.length;
					message.write(moStream);
				}
				case 2003 -> {
					byte[] chunk = wavAccessPoint.getSongChunk(message.data);
					message.data = chunk;
					message.dataLength = chunk.length;
					message.write(moStream);
				}
			}
			client.send(moStream.getBuffer());
			moStream.close();
			return true;
		};
		TcpServer server = new TcpServer("127.0.0.1", 8081, onReceive);
		server.start();

		boolean running = true;
		byte[] buffer = new byte[32];
		while(running) {
			int read = System.in.read(buffer);
			String input = new String(buffer, 0, read).strip();
			System.out.println(input);
			if(input.equals("-q")) {
				running = false;
			}
		}

		server.stop();
		server.close();
		appContext.close();
	}
}

