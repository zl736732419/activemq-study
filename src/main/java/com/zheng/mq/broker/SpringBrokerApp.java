package com.zheng.mq.broker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Author zhenglian
 * @Date 2018/4/18 10:09
 */
public class SpringBrokerApp {
    
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }
}
