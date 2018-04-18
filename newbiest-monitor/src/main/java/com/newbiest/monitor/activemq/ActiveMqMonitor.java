package com.newbiest.monitor.activemq;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;

/**
 * MQ监控
 * Created by guoxunbo on 2018/2/2.
 */
@Slf4j
public class ActiveMqMonitor implements Serializable  {

    protected BrokerService brokerService;

    protected String bindAdd = "";

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    void init() {
//        brokerService = create
    }

    BrokerService createBroker() throws Exception {
        BrokerService service = new BrokerService();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

        return service;
    }

    @PreDestroy
    void destroy() {

    }
}
