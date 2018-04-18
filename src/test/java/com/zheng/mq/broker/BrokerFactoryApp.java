package com.zheng.mq.broker;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import java.net.URI;

/**
 * @Author zhenglian
 * @Date 2018/4/18 9:36
 */
public class BrokerFactoryApp {
    
    public static void main(String[] args) throws Exception {
        String uri = "properties:broker.properties";
        BrokerService brokerService = BrokerFactory.createBroker(new URI(uri));
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.start();
    }
    
}
