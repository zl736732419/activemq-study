package com.zheng.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * 消息消费者
 *
 * @Author zhenglian
 * @Date 2018/4/17 9:08
 */
public class QueueReceiver {

    private static final String BROKER_URL = "tcp://zl52:61616";
    private static final String QUEUE = "test_queue";

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            consumer = session.createConsumer(destination);
            Message msg;
            for (int i = 0; i < 3; i++) {
                msg = consumer.receive();
                handleMsg(msg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Optional.ofNullable(consumer).isPresent()) {
                    consumer.close();
                }
                if (Optional.ofNullable(session).isPresent()) {
                    session.close();
                }
                if (Optional.ofNullable(connection).isPresent()) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleMsg(Message msg) {
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            handleTextMessage(textMessage);
        }
    }

    private static void handleTextMessage(TextMessage textMessage) {
        String text = null;
        try {
            text = textMessage.getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("Received: " + text);
    }
}
