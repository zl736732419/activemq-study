package com.zheng.mq.messagetransform;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.MessageTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * 通过message transform可以将一种消息类型转化为另外一种消息类型
 * 可以在producer和consumer端添加自己的消息转化逻辑
 * @Author zhenglian
 * @Date 2018/4/17 15:17
 */
public class QueueSender {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String QUEUE = Constants.QUEUE;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    @Before
    public void init() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            producer = session.createProducer(destination);
            ((ActiveMQMessageProducer)producer).setTransformer(new MessageTransformer() {
                @Override
                public Message producerTransform(Session session, MessageProducer producer, Message message) throws JMSException {
                    TextMessage textMessage = (TextMessage) message; 
                    // 将text消息转化为map消息
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("key", textMessage.getText());
                    return mapMessage;
                }

                @Override
                public Message consumerTransform(Session session, MessageConsumer consumer, Message message) throws JMSException {
                    return message;
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendTextMessage() throws JMSException {
        TextMessage msg;
        for (int i = 0; i < 1; i++) {
            msg = session.createTextMessage("message----" + (i + 1));
            producer.send(msg);
        }
    }
    
    @After
    public void cleanup() {
        System.out.println("消息发送成功!");
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
