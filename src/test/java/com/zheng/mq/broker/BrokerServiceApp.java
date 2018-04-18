package com.zheng.mq.broker;

import org.apache.activemq.broker.BrokerService;

/**
 * 通过brokerService启动mq实例
 *
 * @Author zhenglian
 * @Date 2018/4/18 9:30
 */
public class BrokerServiceApp {
    
    public static void main(String[] args) throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setUseJmx(true);
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.start();
    }
}
