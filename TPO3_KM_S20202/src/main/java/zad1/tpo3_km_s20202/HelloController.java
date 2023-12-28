package zad1.tpo3_km_s20202;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class HelloController {
    private Admin admin;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField addTF;
    @FXML
    private TextField removeTF;
    @FXML
    private TextField newsTF;

    @FXML
    protected void addNewTopic() throws IOException {
        String content = addTF.getText();
        admin.addTopics(content);
    }

    @FXML
    protected void removeNewTopic() throws IOException {
        String content = removeTF.getText();
        admin.removeTopics(content);
    }

    @FXML
    protected void addNewNewsAboutTopic() throws IOException  {
        String content = newsTF.getText();
        admin.addNewsOfTopic(content);
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        try {
            admin.disconnectServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.exit();
    }

    @FXML
    public void initialize() {
        this.admin = new Admin();
        try {
            this.admin.start();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

}