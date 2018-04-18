package com.zheng.mq.spring;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @Author zhenglian
 * @Date 2018/4/18 14:21
 */
public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("received: " + textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
