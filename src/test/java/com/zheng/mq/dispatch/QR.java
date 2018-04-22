package com.zheng.mq.dispatch;

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
 * 两个消费者异步消费消息
 *
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QR {
    private static final String BROKER_URL = Constants.FAIL_OVER_URL;
    private static final String TOPIC = Constants.TOPIC;

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        for (int i = 0; i < 2; i++) {
            new Thread(new MyReceiverRunnable(factory)).start();
        }
    }

    private static class MyReceiverRunnable implements Runnable {
        private ActiveMQConnectionFactory factory;

        public MyReceiverRunnable(ActiveMQConnectionFactory factory) {
            this.factory = factory;
        }

        @Override
        public void run() {
            try {
                Connection connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(TOPIC);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        try {
                            TextMessage textMessage = (TextMessage) message;
                            System.out.println(consumer + " received: " + textMessage.getText());
                            session.commit();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        } 
                    }
                });
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
