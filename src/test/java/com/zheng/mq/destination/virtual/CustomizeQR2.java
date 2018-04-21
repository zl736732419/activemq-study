package com.zheng.mq.destination.virtual;

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
 * 通过队列方式消费虚拟主题，需要先于生产者运行，进行注册
 * activemq.xml通过virtualDestinations的virtualTopic来修改自定义消费虚拟主题地址前缀
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class CustomizeQR2 {
    private static final String BROKER_URL = Constants.BROKER_URL;

    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        Connection connection;
        Session session;
        MessageConsumer consumer;
        try {
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            // 通过队列的方式来消费虚拟主题消息
            String topicName = "VirtualTopic.topic3";
            String queueName = "VirtualTopicConsumer.B." + topicName;
            Destination destination = session.createQueue(queueName);
            // 使用activemq提供的客户端负载均衡方式(通过同一个session创建多个相同broker上的consumer)
            for (int i = 0; i < 1; i++) {
                consumer = session.createConsumer(destination);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        TextMessage msg = (TextMessage) message;
                        try {
                            System.out.println("QR2 received: " + msg.getText());
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
