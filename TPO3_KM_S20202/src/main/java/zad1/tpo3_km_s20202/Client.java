package zad1.tpo3_km_s20202;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Client {
	private SocketChannel socketChannel;
	private final String serverHost = "localhost";
	private final int serverPort = 1234;
	private ByteBuffer buffer;

	public void start() throws IOException {
		socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
		socketChannel.configureBlocking(false);
		buffer = ByteBuffer.allocate(1024);

//		while (true) {
//			sendCommandToTheServer(socketChannel);
//
//			buffer.clear();
//			int bytesRead = socketChannel.read(buffer);
//			if (bytesRead > 0) {
//				buffer.flip();
//				byte[] bytes = new byte[buffer.remaining()];
//				buffer.get(bytes);
//				String message = new String(bytes);
//				System.out.println(message.trim());
//			}
//		}
	}

	public void disconnectServer() throws IOException {
		String sentMessage = "bye";
		buffer.clear();
		buffer.put(sentMessage.getBytes());
		buffer.flip();
		this.socketChannel.write(buffer);
	}

	public String updateNewsOfTopics() throws IOException {
		String sentMessage = "news";
		buffer.clear();
		buffer.put(sentMessage.getBytes());
		buffer.flip();
		this.socketChannel.write(buffer);
		String message = "";
		buffer.clear();
		int bytesRead = socketChannel.read(buffer);
		if (bytesRead > 0) {
			buffer.flip();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			message = new String(bytes);
		}
		return message.trim();
	}

	public String showTopics() throws IOException {
		String sentMessage = "show";
		buffer.clear();
		buffer.put(sentMessage.getBytes());
		buffer.flip();
		this.socketChannel.write(buffer);
		String message = "";
		buffer.clear();
		int bytesRead = socketChannel.read(buffer);
		if (bytesRead > 0) {
			buffer.flip();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			message = new String(bytes);
		}
		return message.trim();
	}

	public void subTopics(String message) throws IOException {
		String sentMessage = "sub:" + message;
		buffer.clear();
		buffer.put(sentMessage.getBytes());
		buffer.flip();
		this.socketChannel.write(buffer);
	}

	public void unsubTopics(String message) throws IOException {
		String sentMessage = "unsub:" + message;
		buffer.clear();
		buffer.put(sentMessage.getBytes());
		buffer.flip();
		this.socketChannel.write(buffer);
	}

	private void sendCommandToTheServer(SocketChannel channel) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter topics to subscribe to (comma-separated):");
		String message = scanner.nextLine();
		buffer.clear();
		buffer.put(message.getBytes());
		buffer.flip();
		channel.write(buffer);
	}
}
