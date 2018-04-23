package com.zheng.mq.advisorymessage;

import com.zheng.mq.Constants;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.ProducerInfo;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * 持久Topic消息接收者
 *
 * @Author zhenglian
 * @Date 2018/4/17 15:22
 */
public class QR {
    private static final String BROKER_URL = Constants.BROKER_URL;
    private static final String TOPIC = Constants.TOPIC;

    private static Connection connection;
    private static Session session;
    private static MessageConsumer consumer;

    public static void main(String[] args) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
        try {
            connection = factory.createConnection();
            // 一定要在设置完参数之后再启动
            connection.start();
            
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            // 直接使用普通创建topic的方式
//            Topic destination = session.createTopic("ActiveMQ.Advisory.Producer.Topic.my_topic");
            
            // 通过AdvisorySupport方式
            Destination dest = session.createTopic(TOPIC);
            ActiveMQTopic destination = AdvisorySupport.getProducerAdvisoryTopic(dest);

            consumer = session.createConsumer(destination);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof ActiveMQMessage) {
                            ActiveMQMessage aMsg = (ActiveMQMessage) message;
                            DataStructure dataStructure = aMsg.getDataStructure();
                            if (dataStructure instanceof ProducerInfo) {
                                ProducerInfo prod = (ProducerInfo) dataStructure;
                                System.out.println(prod);
                            } else {
                                System.out.println(dataStructure);
                            }
                            
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
