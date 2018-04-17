package com.zheng.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Enumeration;
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
    
    private boolean printJMSProperty = false;

    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            connection.start();

            printJMSPropertyName();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
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
    public void receiveMapMessage() throws JMSException {
        MapMessage mapMessage;
        String msg;
        String property;
        for (int i = 0; i < 3; i++) {
            mapMessage = (MapMessage) consumer.receive();
            msg = mapMessage.getString("message---" + i);
            System.out.println("received msg: " + msg);
            property = mapMessage.getStringProperty("extra");
            System.out.println("received Property: " + property);
        }
    }
    
    @Test
    public void testReplyToMessage() throws JMSException {
        TextMessage msg = (TextMessage) consumer.receive();
        System.out.println("received: " + msg.getText());
        // 根据replyTo Destination向生产者发送响应消息
        Destination replyToDestination = msg.getJMSReplyTo();
        MessageProducer producer = session.createProducer(replyToDestination);
        TextMessage response = session.createTextMessage("ok");
        producer.send(response);
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
