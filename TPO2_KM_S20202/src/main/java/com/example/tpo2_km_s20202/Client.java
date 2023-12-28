package com.example.tpo2_km_s20202;

import javafx.scene.control.Alert;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Client {
    private int port;
    private String translatedWord;
    private int portWaiting;

    public Client(int port) {
        this.port = port;
        Random random = new Random();
        this.portWaiting = random.nextInt(9000) + 2000;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void connectToServer(String text){
        try {
            Socket socket = new Socket("localhost", this.port);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(text + "," + this.portWaiting);
            bufferedWriter.newLine();
            bufferedWriter.flush();


            ServerSocket serverSocket = new ServerSocket(this.portWaiting);
            Socket server = serverSocket.accept();

            InputStreamReader inputStreamReader = new InputStreamReader(server.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String translatedWord = bufferedReader.readLine();
            this.translatedWord = translatedWord;

            serverSocket.close();
            inputStreamReader.close();
            bufferedReader.close();
            bufferedWriter.close();
            outputStreamWriter.close();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error has occurred");
            alert.setContentText("Server is not open");
            alert.showAndWait();
            System.out.println("Server is not open");
        }
    }
}
