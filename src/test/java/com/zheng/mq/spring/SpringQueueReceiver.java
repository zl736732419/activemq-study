package com.zheng.mq.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * spring集成的消息消费者
 * 对于topic生产的消息，默认是非持久化的
 * @Author zhenglian
 * @Date 2018/4/18 14:10
 */
@Component
public class SpringQueueReceiver {
    
    @Autowired
    private JmsTemplate template;
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        SpringQueueReceiver receiver = ctx.getBean("springQueueReceiver", SpringQueueReceiver.class);
        String message = (String) receiver.template.receiveAndConvert();
        System.out.println("received: " + message);
    }
}
