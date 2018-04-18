package com.newbiest;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

/**
 * Created by guoxunbo on 2018/2/9.
 */
public class ActiveMqProducerTest {

    public Session session;

    @Test
    public void sendMessage() {
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Test
    public void init() {
        ConnectionFactory connectionFactory;
        Connection connection;

        connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://127.0.0.1:61616");
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue("test-queue");;
            MessageProducer producer = session.createProducer(destination);

            for (int i = 0; i < 1000000; i++) {
                // 伪代码 因为此处是测试 故不写方法，在用方法send的时候注意其是否是空 空就创建 不空就不创建 保证每个Connection的每个queue都只有一个producer
                if (producer == null) {
                    producer = session.createProducer(destination);
                }
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                // 发送消息
                Message message = session.createTextMessage("aaa");
                producer.send(message);
            }
            producer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
