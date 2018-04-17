package com.zheng.mq.topic;

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
import java.util.Enumeration;
import java.util.Optional;

/**
 * 非持久Topic消息接收者
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class NoPersistenceTopicReceiver {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String TOPIC = Constants.TOPIC;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;
    
    private boolean printJMSProperty = false;

    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            connection.start();

            printJMSPropertyName();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(TOPIC);
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void printJMSPropertyName() throws JMSException {
        if (!printJMSProperty) {
            return;
        }
        Enumeration<String> propertyNames = connection.getMetaData().getJMSXPropertyNames();
        String propertyName;
        while (propertyNames.hasMoreElements()) {
            propertyName = propertyNames.nextElement();
            System.out.println(propertyName);
        }
    }

    @Test
    public void receiveTextMessage() throws JMSException {
        TextMessage textMessage;
        String text = null;
        for (int i = 0; i < 3; i++) {
            textMessage = (TextMessage) consumer.receive();
            try {
                text = textMessage.getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            System.out.println("Received: " + text);
        }
    }

    @After
    public void cleanup() {
        try {
            session.commit();
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
