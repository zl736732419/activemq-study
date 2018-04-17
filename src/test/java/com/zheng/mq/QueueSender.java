package com.zheng.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.Optional;

/**
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
    
    @Test
    public void sendMapMessage() throws JMSException {
        MapMessage msg;
        for (int i = 0; i < 3; i++) {
            msg = session.createMapMessage();
            // 设置用户附加属性
            msg.setStringProperty("extra", "custom defined property" + i);
            // 添加消息内容
            msg.setString("message---" + i, "map message" + i);
            producer.send(msg);
        }
    }

    /**
     * 生产者发送消息给消费者，但是生产者需要等待从消费者发来确认信息
     * 以证明消息的确安全送到消费者手上，通过replyto destination来实现
     */
    @Test
    public void testReplyToMessage() throws JMSException {
        // 创建replyTo Destination，使用临时queue，因为只有当前会话才能访问该队列
        Destination replyToDestination = session.createTemporaryQueue();
        TextMessage msg = session.createTextMessage("hello consumer");
        msg.setJMSReplyTo(replyToDestination);
        //注册用于接收replyTo响应消息的消息监听器
        MessageConsumer consumer = session.createConsumer(replyToDestination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage replyMessage = (TextMessage) message;
                String reply = null;
                try {
                    reply = replyMessage.getText();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                System.out.println(reply);
            }
        });
        producer.send(msg);
        System.out.println("消息发送成功!");
        session.commit();
        // 这里需要保证程序不退出，需要等待消费者消息响应
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
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
