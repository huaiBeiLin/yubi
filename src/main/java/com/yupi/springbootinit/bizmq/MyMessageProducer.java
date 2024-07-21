package com.yupi.springbootinit.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * packageName com.yupi.springbootinit.bizmq
 *
 * @author yuxin
 * @version JDK 8
 * @className MyMessageProducer (此处以class为例)
 * @date 2024/7/21
 * @description 生产者
 */
@Component
public class MyMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;
    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }


}
