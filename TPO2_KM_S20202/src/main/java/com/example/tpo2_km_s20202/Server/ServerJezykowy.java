package com.example.tpo2_km_s20202.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerJezykowy implements Runnable{
    private Map<String, String> dictionary;
    private String languageCode;
    private int port;
    private ServerSocket serverSocket;

    public ServerJezykowy(String languageCode, int port) {
        this.languageCode = languageCode;
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.err.println("Error while starting the server: " + e.getMessage());
        }

        dictionary = new HashMap<>();
        loadDictionaryFromFile();
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getPort() {
        return port;
    }

    private void loadDictionaryFromFile() {
        String fileName = this.languageCode + ".txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(",");
                dictionary.put(words[0].trim(), words[1].trim());
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    @Override
    public void run() {
            System.out.println("Server " + this.languageCode + " launched on the port " + port);

            while (true) {
                try{
                    Socket clientSocket = this.serverSocket.accept();
                    System.out.println("New connection with " + clientSocket.getPort());

                    // Odbieranie zapytania od klienta
                    InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String msgFromClient = bufferedReader.readLine();
                    System.out.println(msgFromClient);

                    //Przydzielanie danych do zmiennych
                    String[] query = msgFromClient.split(",");
                    String wordToTranslate = query[0];
                    String clientAddress = query[1];
                    int clientPort =  Integer.parseInt(query[2]);

                    // Tłumaczenie słowa
                    String translatedWord = dictionary.getOrDefault(wordToTranslate, "No translation");

                    // Wysyłanie wyniku do klienta
                    try {
                        Socket resultSocket = new Socket(clientAddress, clientPort);

                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(resultSocket.getOutputStream());
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

                        bufferedWriter.write(translatedWord);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
                        bufferedWriter = new BufferedWriter(outputStreamWriter);

                        bufferedWriter.close();
                        resultSocket.close();
                        clientSocket.close();
                        bufferedReader.close();
                        inputStreamReader.close();
                        outputStreamWriter.close();


                        //serverSocket.close();

                        System.out.println("Result: " + translatedWord + " (sent to the customer " + clientAddress + ":" + clientPort + ")");
                    } catch (IOException e) {
                        System.err.println("Error sending result to client: " + e.getMessage());
                    }
                } catch (IOException e) {
                    System.err.println("Error while handling the call: " + e.getMessage());
                }
            }
    }
}
