package com.example.tpo2_km_s20202;

import com.example.tpo2_km_s20202.Server.MainServer;
import com.example.tpo2_km_s20202.Server.ServerJezykowy;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField textField;
    @FXML
    private TextField addNewLanguangeTF;
    @FXML
    private TextField textFliedTranslated;
    @FXML
    private ListView<String> listOfLanguange;

    @FXML
    protected void translateTheWord() {
        String word = textField.getText();
        String langName = listOfLanguange.getSelectionModel().getSelectedItem();
        if (word == null || word.length() < 1 || langName == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error has occurred");
            alert.setContentText("Please write correct values. Text can not be empty! Languange need to be selected");
            alert.showAndWait();
            return;
        }
        String content = word + "," + langName;
        Client client = new Client(1234);
        client.connectToServer(content);
        setTextField(client.getTranslatedWord());
    }

    @FXML
    protected void addNewLanguange() {
        String content = addNewLanguangeTF.getText();

        if (content == null || content.length() < 1 || content.length() > 3){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error has occurred");
            alert.setContentText("Please add correct SHORT language name. Max 3 character, Can not be null. Like EN, IT, etc..");
            alert.showAndWait();
            return;
        }
        Random random = new Random();
        int portNumber = random.nextInt(9000) + 2000;
        MainServer.addLanguageServer(content, portNumber);
        reloadList();
    }

    @FXML
    public void initialize() {
        startServerMain();
    }

    public void startServerMain() {
        MainServer.addDefaultLanguages();
        reloadList();
        Thread mainServerThread = new Thread(new MainServer());
        mainServerThread.start();
    }

    private void reloadList() {
        List<ServerJezykowy> serverLanguageList = MainServer.getServerLanguageList();
        for (ServerJezykowy serverJezykowy : serverLanguageList) {
            if (!listOfLanguange.getItems().contains(serverJezykowy.getLanguageCode())){
                listOfLanguange.getItems().add(serverJezykowy.getLanguageCode());
            }
        }
    }

    private void setTextField(String word){
        textFliedTranslated.setText(word);
    }

}