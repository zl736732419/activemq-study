package com.zheng.mq.destination.composite;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:17
 */
public class CompositeDestinationSender {
    private static final String BROKER_URL = Constants.BROKER_URL;

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
            // 客户端配置composite destination方式
//            String queueName = ""test.queue1,test.queue2"";
            // broker端配置composite com.zheng.mq.destination,<destinationInterceptors>
            String queueName = "MY.QUEUE";
            Destination destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendTextMessage() throws JMSException {
        TextMessage msg;
        for (int i = 0; i < 3; i++) {
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
