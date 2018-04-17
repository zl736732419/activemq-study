package com.zheng.mq.topic;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import java.util.Enumeration;
import java.util.Optional;

/**
 * 非持久Topic消息接收者
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class PersistenceTopicReceiver {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String TOPIC = Constants.TOPIC;

    private Connection connection;
    private Session session;
    private TopicSubscriber consumer;

    private boolean printJMSProperty = false;

    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            // 设置消息订阅者标识ID
            connection.setClientID("cc1");
            printJMSPropertyName();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(TOPIC);
            consumer = session.createDurableSubscriber(destination, "ts1");
            // 一定要在设置完参数之后再启动
            connection.start();
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
