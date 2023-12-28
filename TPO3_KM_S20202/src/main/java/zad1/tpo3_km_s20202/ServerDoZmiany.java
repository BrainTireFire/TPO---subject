package zad1.tpo3_km_s20202;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerDoZmiany {

    public static void main(String[] args) {
        ServerDoZmiany serverDoZmiany = new ServerDoZmiany();
        try {
            serverDoZmiany.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private SocketChannel adminAccount = null;
    private final String host = "localhost";
    private final int port = 1234;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private Map<String, String> newsOfTopic = new HashMap<>();
    private List<String> topics = new ArrayList<>();
   // private Map<SocketChannel, String> clientsTest = new HashMap<>();
    private Map<String, Set<SocketChannel>> clientsTest = new HashMap<>();

    public void start() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server is waiting for connection...");

        while(true){
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()){
                    SocketChannel clientSocket = serverSocketChannel.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ);
                    System.out.println("Client connected: " + clientSocket.getRemoteAddress());
                } else if(key.isReadable()){
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    serviceRequested(clientSocket);
                }
//                else if (key.isWritable()) {
//                    // obsłuż dane do wysłania na kanale
//                    SocketChannel channel = (SocketChannel) key.channel();
//                    ByteBuffer buffer = (ByteBuffer) key.attachment();
//                    channel.write(buffer);
//                    if (!buffer.hasRemaining()) {
//                        // wszystkie dane zostały wysłane, zmień zainteresowanie kanału na "czytanie"
//                        key.interestOps(SelectionKey.OP_READ);
//                    }
//                }

            }

        }

    }

//    public void addTopic(SocketChannel client, String message) {
//        String[] topicsArray = message.substring(4).split(",");
//        Set<SocketChannel> clients = new HashSet<>();
//
//        for (String topic : topicsArray){
//            Set<SocketChannel> topicClients = this.topics.get(topic);
//            if (topicClients == null) {
//                topicClients = new HashSet<>();
//                topics.put(topic, topicClients);
//            }
//            System.out.println(topic);
//            topicClients.add(client);
//            clients.addAll(topicClients);
//        }
//        for (String topic : topics.keySet()){
//            System.out.println("Topics: " + topic);
//        }
//        //sendTopicListToClients(clients);
//        sendTopicListToClient(client);
//    }

    public void addTopic(String message) {
        String[] topicsArray = message.substring(4).split(",");

        for (String topic : topicsArray){
            clientsTest.put(topic, null);
            //topics.add(topic);
        }
        for (String topic : clientsTest.keySet()){
            System.out.println("Topics: " + topic);
        }
    }

    public void removeTopic(String message) {
        String[] topicsArray = message.substring(7).split(",");

        for (String topic : topicsArray){
            if (clientsTest.containsKey(topic)){
                clientsTest.remove(topic);
            }
        }

        for (String topic : clientsTest.keySet()){
            System.out.println("Topics: " + topic);
        }
    }

    public void setNewsOfTopic(String message){
        String[] query = message.substring(5).split("::");

        if (clientsTest.containsKey(query[0])){
            newsOfTopic.put(query[0], query[1]);
        }

        for (String topic : newsOfTopic.keySet()){
            System.out.println("Topics: " + topic + " Value " + newsOfTopic.get(topic));
        }
    }

    private void sendTopicListToClients(Set<SocketChannel> clients){
        StringBuffer stringBuffer = new StringBuffer();
        for (String topic : topics) {
            stringBuffer.append(topic).append(",");
        }
        String topicList = stringBuffer.toString();
        ByteBuffer byteBuffer = ByteBuffer.wrap(topicList.getBytes());
        for (SocketChannel client : clients){
            try {
                client.write(byteBuffer);
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unsubTopic(SocketChannel socketChannel, String message) {
        String[] topicsArray = message.substring(6).split(",");

        for (String topic : topicsArray){
            if (clientsTest.containsKey(topic))
            {
                Set<SocketChannel> client = clientsTest.get(topic);
                if (client != null)
                {
                    client.remove(socketChannel);
                    clientsTest.put(topic, client);
                }
            }
        }
    }

    public void subTopic(SocketChannel socketChannel, String message) {
        String[] topicsArray = message.substring(4).split(",");

        for (String topic : topicsArray){
            Set<SocketChannel> client = new HashSet<>();
            if (clientsTest.containsKey(topic))
            {
                client = clientsTest.get(topic);
                if (client == null)
                {
                    client = new HashSet<>();
                    client.add(socketChannel);
                    clientsTest.put(topic, client);
                }
                else
                {
                    client.add(socketChannel);
                    clientsTest.put(topic, client);
                }
            }
        }
    }

    public void sentNewsTopics(SocketChannel socketChannel) {
        for (Map.Entry<String, Set<SocketChannel>> entry : clientsTest.entrySet()) {
            String key = entry.getKey();
            Set<SocketChannel> channelSet = entry.getValue();
            if (channelSet != null) {
                for (SocketChannel channel : channelSet) {
                    if (channel.equals(socketChannel)) {
                        String message = newsOfTopic.get(key);
                        if (!message.equals(null)){
                            try {
                                socketChannel.write(StandardCharsets.UTF_8.encode(message + '\n'));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }

    public void showMyTopics(SocketChannel socketChannel) {
        String response = "";
        for (Map.Entry<String, Set<SocketChannel>> entry : clientsTest.entrySet()) {
            String key = entry.getKey();
            Set<SocketChannel> channelSet = entry.getValue();
            System.out.println("Key " + key);
            if (channelSet != null) {
                for (SocketChannel channel : channelSet) {
                    if (channel.equals(socketChannel)) {
                        response += key + ", ";
                    }
                }
            }
        }

        if (!response.equals("")){
            try {
                socketChannel.write(StandardCharsets.UTF_8.encode(response + '\n'));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


//    private void broadcast(String response) throws IOException {
//        for (Map.Entry<SocketChannel, String> entry : this.clientsTest.entrySet()) {
//            SocketChannel clientSocket = entry.getKey();
//
//            clientSocket.write(StandardCharsets.UTF_8.encode(response + '\n'));
//        }
//    }

    private void sendTopicListToClient(SocketChannel client){
        StringBuilder stringBuilder = new StringBuilder();
        for (String topic : topics) {
            System.out.println("sendTopicListToClient " + topic);
            stringBuilder.append(topic).append(",");
        }
        String topicList = stringBuilder.toString();
        System.out.println(topicList);
//        byte[] bytes = topicList.getBytes();
//        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        try {
            CharBuffer charBuffer = CharBuffer.wrap(topicList);
            while (charBuffer.hasRemaining()) {
                client.write(Charset.forName("ISO-8859-2").encode(charBuffer));
            }
            charBuffer.clear();
           //client.write(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }

//    private void sendTopicListToClient(SocketChannel client){
//        StringBuffer stringBuffer = new StringBuffer();
//        for (String topic : topics.keySet()) {
//            System.out.println("sendTopicListToClient " + topic);
//            stringBuffer.append(topic).append(",");
//        }
//        String topicList = stringBuffer.toString();
//        System.out.println(topicList);
//        ByteBuffer byteBuffer = ByteBuffer.wrap(topicList.getBytes());
//        try {
//              client.write(byteBuffer);
//              byteBuffer.clear();
////            client.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    public void addTopicOLDDD(SocketChannel client, String message) {
//        System.out.println(message);
//
//        String[] topicsArray = message.substring(4).split(",");
//
//
//        for (String topic : topicsArray){
//            if (!topics.containsKey(topic)) {
//                topics.put(topic, new HashSet<>());
//            }
//        }
//
//        sendTopicListToClient(client);
//    }

    private void serviceRequested(SocketChannel client){
        //if (!client.isOpen()) return;
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.clear();
            int readBytes = client.read(byteBuffer);
            if (readBytes == -1){
                client.close();
                return;
            }

            byteBuffer.flip();
            byte[] data = new byte[readBytes];
            byteBuffer.get(data);
            String message = new String(data).trim();
            System.out.println("Client message: " + message);

            if (message.startsWith("login:")){
                if (message.substring(6).equals("69")){
                    adminAccount = client;
                }
                //clientsTEST.put(client, message.substring(6));
            }
            else if (message.startsWith("add:"))
            {
                if (adminAccount != null){
                    if (adminAccount.equals(client))
                    {
                        addTopic(message);
                    }
                }
                else
                {
                    System.out.println("No access");
                }
                //sendTopicListToClient(client);
                //client.close();
            }
            else if(message.startsWith("remove:"))
            {
                if (adminAccount != null){
                    if (adminAccount.equals(client))
                    {
                        removeTopic(message);
                    }
                }
                else
                {
                    System.out.println("No access");
                }
                //sendTopicListToClient(client);
                //client.close();

            }
            else if (message.startsWith("news:"))
            {
                if (adminAccount != null){
                    if (adminAccount.equals(client))
                    {
                        setNewsOfTopic(message);
                    }
                }
                else
                {
                    System.out.println("No access");
                }
               // client.close();

            }
            else if (message.startsWith("sub:"))
            {
                subTopic(client, message);
               // client.close();

            }
            else if (message.startsWith("unsub:"))
            {
                unsubTopic(client, message);
               // client.close();
            }
            else if (message.startsWith("show"))
            {
                showMyTopics(client);
               // client.close();
            }
            else if (message.startsWith("news"))
            {
                sentNewsTopics(client);
               // client.close();
            }else if (message.startsWith("bye"))
            {
                client.close();
            }

//            String[] query = message.split(",");
//            if (query.length == 2) {
//                String topic = query[0].trim();
//                String contentOfTopic = query[1].trim();
//                Set<SocketChannel> subscribers = topics.get(topic);
//                if (subscribers != null) {
//                    ByteBuffer response = ByteBuffer.wrap(("[" + topic + "] " + contentOfTopic).getBytes());
//                    for (SocketChannel subscriber : subscribers) {
//                        subscriber.write(response);
//                    }
//                }
//            }


        }catch (IOException e) {
            throw new RuntimeException(e);
        }

//        catch (SocketException e) {
//            System.err.println("Błąd odczytu danych z klienta: " + e.getMessage());
//        }
    }
}
