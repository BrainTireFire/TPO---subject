package com.example.tpo_5;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleChat {
    private static final String BROKER_URL = "tcp://DESKTOP-8MSO42K:61616";
    private static final String CHAT_TOPIC = "ChatTopic";

    public static void main(String[] args) {
        try {
            // Utwórz fabrykę połączeń do serwera ActiveMQ
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

            // Utwórz połączenie i rozpocznij sesję
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Utwórz temat dla czatu
            Destination chatTopic = session.createTopic(CHAT_TOPIC);

            // Utwórz producenta wiadomości
            MessageProducer producer = session.createProducer(chatTopic);

            // Utwórz konsumenta wiadomości
            MessageConsumer consumer = session.createConsumer(chatTopic);

            // Utwórz odbiornik asynchroniczny dla wiadomości od konsumenta
            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        System.out.println("Odebrano wiadomość: " + text);
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

            // Rozpocznij wątek, który będzie oczekiwał na wiadomości od konsumenta
            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

            // Wczytuj wiadomości od użytkownika i wysyłaj je jako producent
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input;
            while ((input = reader.readLine()) != null) {
                TextMessage message = session.createTextMessage(input);
                producer.send(message);
            }

            // Zamknij połączenie
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
