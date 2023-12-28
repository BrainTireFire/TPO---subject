package zad1.tpo3_km_s20202;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {
    private Client client;
    @FXML
    private Label topicsLabel;
    @FXML
    private Label newsUpdateLabel;
    @FXML
    private TextField subTF;
    @FXML
    private TextField unsubTF;

    @FXML
    protected void subTopic() throws IOException {
        String content = subTF.getText();
        client.subTopics(content);
    }

    @FXML
    protected void unsubTopic() throws IOException {
        String content = unsubTF.getText();
        client.unsubTopics(content);
    }

    @FXML
    protected void showTopic() throws IOException {
        topicsLabel.setText(client.showTopics());
    }

    @FXML
    protected void updateNewsOfTopic() throws IOException {
        newsUpdateLabel.setText(client.updateNewsOfTopics());
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        try {
            client.disconnectServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.exit();
    }

    @FXML
    public void initialize() {
        this.client = new Client();
        try {
            this.client.start();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

}