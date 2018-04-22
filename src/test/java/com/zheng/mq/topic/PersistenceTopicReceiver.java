package com.zheng.mq.topic;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

/**
 * 持久Topic消息接收者
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class PersistenceTopicReceiver {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String TOPIC = Constants.TOPIC;

    private static Connection connection;
    private static Session session;
    private static TopicSubscriber consumer;

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            // 设置消息订阅者标识ID
            connection.setClientID("cc1");
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            Topic destination = session.createTopic(TOPIC);
            consumer = session.createDurableSubscriber(destination, "ts1");

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println(textMessage.getText());
                        session.commit();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            // 一定要在设置完参数之后再启动
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
