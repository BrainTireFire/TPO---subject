package zad1.tpo3_km_s20202;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ClientDoZmian {
    private int id  = 1;
    private SocketChannel socketChannel;
    private final String serverHost = "localhost";
    private final int serverPort = 1234;
    private ByteBuffer buffer;

    public static void main(String[] args) {
        ClientDoZmian clientDoZmian = new ClientDoZmian();
        try {
            clientDoZmian.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
        socketChannel.configureBlocking(false);
        buffer = ByteBuffer.allocate(1024);
        login(socketChannel);

        while (true) {
            registerTopics(socketChannel);

            buffer.clear();
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
//                byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);
//                String message = new String(bytes);
                CharBuffer cbuf = Charset.forName("ISO-8859-2").decode(buffer);
                String message = cbuf.toString();
                System.out.println(message);
            }
        }

    }

    private void login(SocketChannel channel) throws IOException {
        String message = "login:" + this.id;
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        channel.write(buffer);
    }

    private void registerTopics(SocketChannel channel) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter topics to subscribe to (comma-separated):");
        String message = scanner.nextLine();
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        channel.write(buffer);
    }

}
