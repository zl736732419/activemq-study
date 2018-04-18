package com.zheng.mq.queue.ssl;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Optional;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:17
 */
public class SSLQueueSender {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String QUEUE = Constants.QUEUE;

    private String keyStore = Constants.KEY_STORE;
    private String trustStore = Constants.TRUST_STORE;
    private String keyStorePassword = Constants.KEY_STORE_PASSWORD;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    @Before
    public void init() {
        ActiveMQSslConnectionFactory factory = new ActiveMQSslConnectionFactory(BROKER_URL);
        factory.setBrokerURL(BROKER_URL);
        // 设置参数，加载ssl密匙和证书信息
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
            producer = session.createProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendTextMessage() throws JMSException {
        TextMessage msg = session.createTextMessage("message----ssl");
        producer.send(msg);
        System.out.println("消息发送成功!");
    }
    
    @After
    public void cleanup() {
        try {
            session.commit();
            if (Optional.ofNullable(producer).isPresent()) {
                producer.close();
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
