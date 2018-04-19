package com.zheng.mq.consumercluster;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QR1 {
    private static final String BROKER_URL = "tcp://zl202:61616";
    private static final String QUEUE = Constants.QUEUE;

    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection;
        Session session;
        MessageConsumer consumer;
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(QUEUE);
            // 使用activemq提供的客户端负载均衡方式(通过同一个session创建多个相同broker上的consumer)
            for (int i = 0; i < 1; i++) {
                consumer = session.createConsumer(destination);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        TextMessage msg = (TextMessage) message;
                        try {
                            System.out.println("QR1 received: " + msg.getText());
                            session.commit();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
