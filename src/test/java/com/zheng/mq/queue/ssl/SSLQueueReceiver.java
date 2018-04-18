package com.zheng.mq.queue.ssl;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Optional;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class SSLQueueReceiver {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String QUEUE = Constants.QUEUE;

    private String keyStore = Constants.KEY_STORE;
    private String trustStore = Constants.TRUST_STORE;
    private String keyStorePassword = Constants.KEY_STORE_PASSWORD;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @Before
    public void init() {
        ActiveMQSslConnectionFactory factory = new ActiveMQSslConnectionFactory();
        factory.setBrokerURL(BROKER_URL);
        try {
            factory.setKeyAndTrustManagers(SSLUtils.loadKeyManager(keyStore, keyStorePassword),
                    SSLUtils.loadTrustManager(trustStore), new SecureRandom());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 连接一定要在参数设置完成之后再创建和开启
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
    public void receiveTextMessage() throws JMSException {
        TextMessage textMessage = (TextMessage) consumer.receive();
        String text = null;
        try {
            text = textMessage.getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println("Received: " + text);
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
