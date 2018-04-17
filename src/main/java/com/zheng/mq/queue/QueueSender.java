package com.zheng.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * 消息生产者
 *
 * @Author zhenglian
 * @Date 2018/4/17 9:08
 */
public class QueueSender {

    private static final String BROKER_URL = "tcp://zl52:61616";
    private static final String QUEUE = "test_queue";

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            producer = session.createProducer(destination);

            TextMessage msg;
            for (int i = 0; i < 3; i++) {
                msg = session.createTextMessage("message----" + (i + 1));
                producer.send(msg);
            }
            session.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
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

}
