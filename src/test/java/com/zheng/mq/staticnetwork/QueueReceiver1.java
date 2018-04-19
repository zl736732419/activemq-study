package com.zheng.mq.staticnetwork;

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
import java.util.Optional;

/**
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QueueReceiver1 {
    private static final String BROKER_URL = "tcp://zl202:61616";
    private static final String QUEUE = Constants.QUEUE;

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        for (int i = 0; i < 30; i++) {
            new Thread(new MyReceiverRunnable(factory)).start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                Destination destination = session.createQueue(QUEUE);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        try {
                            TextMessage textMessage = (TextMessage) message;
                            System.out.println("receiver1111: " + textMessage.getText());
                            session.commit();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (Optional.ofNullable(consumer).isPresent()) {
                                    consumer.close();
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
                });
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
