package com.zheng.mq.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * spring集成消息生产者
 * @Author zhenglian
 * @Date 2018/4/18 13:59
 */
@Component
public class SpringQueueSender {
    
    @Autowired
    private JmsTemplate template;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SpringQueueSender producer = ctx.getBean("springQueueSender", SpringQueueSender.class);
        producer.template.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("hello spring activemq");
                return textMessage;
            }
        });
    }
    
}
