package com.zheng.mq.broker;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;

import java.io.File;

/**
 * 通过brokerService启动mq实例
 *
 * @Author zhenglian
 * @Date 2018/4/18 9:30
 */
public class BrokerServiceApp {
    
    public static void main(String[] args) throws Exception {
        // 设置kahadb相关参数
        // 设置kahadb log file目录
        File dataFileDir = new File("target/activemq/kahadb");
        KahaDBStore store = new KahaDBStore();
        store.setDirectory(dataFileDir);
        // 设置log data file最大100MB
        store.setJournalMaxFileLength(1024 * 1024 * 100);
        store.setEnableIndexWriteAsync(true);


        BrokerService brokerService = new BrokerService();
        // 使用kahadb存储数据
        brokerService.setPersistenceAdapter(store);

        brokerService.setUseJmx(true);
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.start();
    }
}
