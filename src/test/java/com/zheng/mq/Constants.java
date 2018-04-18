package com.zheng.mq;

/**
 * @Author zhenglian
 * @Date 2018/4/17 20:38
 */
public class Constants {
    public static final String BROKER_URL = "tcp://localhost:61616";
//    public static final String BROKER_URL = "vm://localhost";
//    public static final String BROKER_URL = "http://zl52:61621";
//    public static final String BROKER_URL = "ssl://zl52:61619";
//    public static final String BROKER_URL = "udp://zl52:61620";
//    public static final String BROKER_URL = "nio://zl52:61618";
//    public static final String BROKER_URL = "tcp://zl52:61616";
    public static final String QUEUE = "ssl_queue";
    public static final String TOPIC = "my_topic";

    public static String KEY_STORE = "client1.ks";
    public static String TRUST_STORE = "client1.ts";
    public static String KEY_STORE_PASSWORD = "123456";
    
}
