package com.zheng.mq.selector;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QueueReceiver {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String QUEUE = Constants.QUEUE;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    
    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            consumer = session.createConsumer(destination, "age >= 10");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void receiveTextMessage() throws JMSException {
        String text = null;
        TextMessage textMessage;
        do {
            textMessage = (TextMessage) consumer.receive();
            try {
                text = textMessage.getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            System.out.println("Received: " + text);
            session.commit();
        } while(Optional.ofNullable(textMessage).isPresent());
    }

    @After
    public void cleanup() {
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
        System.out.println("消息处理完毕!");
    }
}
