package zad1.tpo3_km_s20202;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Admin {
    private int id  = 69;
    private SocketChannel socketChannel;
    private final String serverHost = "localhost";
    private final int serverPort = 1234;
    private ByteBuffer buffer;

    public void start() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
        socketChannel.configureBlocking(false);
        buffer = ByteBuffer.allocate(1024);
        login(socketChannel);

//        while (true) {
//            sendCommandToTheServer(socketChannel);
//
//            buffer.clear();
//            int bytesRead = socketChannel.read(buffer);
//            if (bytesRead > 0) {
//                buffer.flip();
//                byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);
//                String message = new String(bytes);
//                System.out.println(message.trim());
//            }
//        }
    }

    private void login(SocketChannel channel) throws IOException {
        String message = "login:" + this.id;
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    public void disconnectServer() throws IOException {
        String sentMessage = "bye";
        buffer.clear();
        buffer.put(sentMessage.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer);
    }

    public void addTopics(String message) throws IOException {
        String sentMessage = "add:" + message;
        buffer.clear();
        buffer.put(sentMessage.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer);
    }

    public void removeTopics(String message) throws IOException {
        String sentMessage = "remove:" + message;
        buffer.clear();
        buffer.put(sentMessage.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer);
    }

    public void addNewsOfTopic(String message) throws IOException {
        String sentMessage = "news:" + message;
        buffer.clear();
        buffer.put(sentMessage.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer);
    }

    private void sendCommandToTheServer(String message) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter topics to subscribe to (comma-separated):");
//        String message = scanner.nextLine();
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer);
    }
}
