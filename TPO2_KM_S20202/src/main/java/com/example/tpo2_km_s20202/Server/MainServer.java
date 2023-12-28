package com.example.tpo2_km_s20202.Server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainServer implements Runnable  {
    private static final int PORT = 1234; // port serwera głównego
    private static Map<String, String> languageServers = new HashMap<>();
    private static List<ServerJezykowy> serverLanguageList = new ArrayList<>();

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("The master server is listening on the port " + PORT + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept(); // oczekiwanie na przychodzące połączenie od klienta
                System.out.println("Connected to the client " + clientSocket.getInetAddress().getHostName() + ":" + clientSocket.getPort());
                new ClientHandler(clientSocket).start(); // utworzenie nowego wątku obsługi klienta
            }
        } catch (IOException e) {
            System.err.println("Master server error: " + e.getMessage());
        }
    }

    public static void addDefaultLanguages(){
        addLanguageServer("EN", 2000);
        addLanguageServer("FR", 2001);
        addLanguageServer("ES", 2002);
    }

    public static List<ServerJezykowy> getServerLanguageList() {
        return serverLanguageList;
    }

    public static void addLanguageServer(String languageCode, int port) {
        ServerJezykowy newLanguageServer = new ServerJezykowy(languageCode, port);
        if (serverLanguageList.size() < 1){
            serverLanguageList.add(newLanguageServer);
        }
        boolean canAdd = true;
        for (ServerJezykowy serverJezykowy : serverLanguageList) {
            if (serverJezykowy.getLanguageCode().equals(languageCode) && serverJezykowy.getPort() == port){
                canAdd = false;
            }
        }

        if (canAdd){
            serverLanguageList.add(newLanguageServer);
        }
    }

    public static void startLanguageServer() {
        for (ServerJezykowy serverJezykowy: serverLanguageList) {
            Thread languageServerThread = new Thread(serverJezykowy);
            languageServerThread.start();
            languageServers.put(serverJezykowy.getLanguageCode(), "localhost," + serverJezykowy.getPort());
        }
    }

    // wewnętrzna klasa obsługi klienta
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            startLanguageServer();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = in.readLine();
                String[] query = inputLine.split(",");

                // walidacja zapytania - sprawdzenie czy został podany poprawny kod języka
                String languageCode = query[1];
                String wordToTranslate = query[0];
                String portOfClient = query[2];

                if (!isValidLanguageCode(languageCode)) {
                    String errorMessage = "Invalid language code: " + languageCode;
                    System.out.println(errorMessage);
                    sendResponseToClient(errorMessage, clientSocket);
                    return;
                }

                // wyszukanie adresu i portu serwera językowego dla danego kodu języka
                String serverLanguageAdressAndPort = languageServers.get(languageCode);
                String[] queryLanguageData = serverLanguageAdressAndPort.split(",");
                String serverAddress = queryLanguageData[0];
                int serverPort = Integer.parseInt(queryLanguageData[1]);

                // wysłanie zapytania do serwera językowego
                Socket serverSocket = new Socket(serverAddress, serverPort);
                String queryToServer = wordToTranslate + "," + clientSocket.getInetAddress().getHostAddress() + "," + portOfClient;
                PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
                out.println(queryToServer);

                // zamknięcie połączeń z klientem i serwerem językowym
                out.close();
                serverSocket.close();
                clientSocket.close();
                in.close();

            } catch (IOException e) {
                System.err.println("Customer service error: " + e.getMessage());
            }
        }

        private boolean isValidLanguageCode(String languageCode){
            if (!languageServers.containsKey(languageCode)){
                return false;
            }
            return true;
        }

        private void sendResponseToClient(String response, Socket clientSocket) throws IOException {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(response);
            out.close();
        }
    }
}
