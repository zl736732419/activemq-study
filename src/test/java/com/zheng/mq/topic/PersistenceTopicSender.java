package com.zheng.mq.topic;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * 持久Topic消息生产者
 * @Author zhenglian
 * @Date 2018/4/17 15:17
 */
public class PersistenceTopicSender {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String TOPIC = Constants.TOPIC;

    private Connection connection;
    private Session session;
    private MessageProducer producer;


    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(TOPIC);
            producer = session.createProducer(destination);
            // 设置消息生产者传递模式为持久化模式
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            // 一定要在完成持久化传递模式之后才启动连接
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendTextMessage() throws JMSException {
        TextMessage msg;
        for (int i = 0; i < 10; i++) {
            msg = session.createTextMessage("message----" + (i + 1));
            producer.send(msg);
            session.commit();
        }
    }

    @After
    public void cleanup() {
        System.out.println("消息发送成功!");
        try {
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
