package com.zheng.mq.messagetransform;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;
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
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void receiveMapMessage() throws JMSException {
        MapMessage mapMessage = (MapMessage) consumer.receive();
        while(Optional.ofNullable(mapMessage).isPresent()) {
            System.out.println("Received: " + mapMessage.getString("key"));
            session.commit();
            mapMessage = (MapMessage) consumer.receive();
        }
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
