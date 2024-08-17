package com.yupi.springbootinit.mq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * packageName com.yupi.springbootinit.mq
 *
 * @author yuxin
 * @version JDK 8
 * @className SingleProducer (此处以class为例)
 * @date 2024/7/22
 * @description 单生产者*/
public class SingleProducer {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
             channel.queueDeclare(QUEUE_NAME, false, false, false, null);
             Scanner scanner = new Scanner(System.in);
             while (scanner.hasNext()) {
                 String message = scanner.nextLine();
                 channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                 System.out.println(" [x] Sent '" + message + "'");
             }
        }
    }
}
