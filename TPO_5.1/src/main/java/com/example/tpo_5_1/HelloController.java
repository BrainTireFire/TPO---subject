package com.example.tpo_5_1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

public class HelloController {
    private static final String SERVER_URL = "tcp://DESKTOP-8MSO42K:61616";
    private static final String SUBJECT = "Simple Chat";
    private MessageProducer messageProducer;
    private Session session;

    @FXML
    private TextArea ta_Chat_Messages;
    @FXML
    private TextField tf_MessageToSend;

    public void initialize() {
        this.connectToServer();
    }

    private void connectToServer(){
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(SERVER_URL);

            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination chat = session.createTopic(SUBJECT);

            messageProducer = session.createProducer(chat);

            MessageConsumer messageConsumer = session.createConsumer(chat);

            messageConsumer.setMessageListener(mess -> {
                if (mess instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) mess;
                    String message;
                    try {
                        message = textMessage.getText();
                        addTextToChatTextField(message);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.runChat(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runChat(Connection connection) throws Exception{
        Platform.runLater(() -> {
            Stage stage = (Stage) ta_Chat_Messages.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void addTextToChatTextField(String message) {
        Platform.runLater(() -> {
            ta_Chat_Messages.appendText(message + "\n");
        });
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        String message = tf_MessageToSend.getText();
        if (!message.isEmpty()) {
            try {
                TextMessage textMessage = session.createTextMessage(message);
                messageProducer.send(textMessage);
                tf_MessageToSend.clear();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void clearChatMessages(ActionEvent event) {
        ta_Chat_Messages.clear();
    }
}