package com.zheng.mq.dispatch;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;

/**
 * 两个生产者生产消息
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QS {
    private static final String BROKER_URL = Constants.FAIL_OVER_URL;
    private static final String TOPIC = Constants.TOPIC;

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            for (int i = 0; i < 2; i++) { // 创建两个生产者
                Destination destination = session.createTopic(TOPIC);
                producer = session.createProducer(destination);
                for(int j = 0; j < 3; j++) {
                    TextMessage message = session.createTextMessage("message--" + j);
                    producer.send(message);
                }
            }
            session.commit();
        } catch(Exception e) {
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
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }
    }
    
}
