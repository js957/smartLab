package com.ynusmartgrid.face_.util;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitMQ {
    @Autowired
    RabbitTemplate rabbitTemplate;

    //将消息放入消息队列的方法
    public void sendFanoutMessage(Map<String,String> messageData) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("fanout.A",false,false,false,null);
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(messageData);
        channel.basicPublish("","fanout.A",null,message.getBytes(StandardCharsets.UTF_8));
        System.out.println( "消息已经传入消息队列");
    }

    public void sendFanoutMessagT(Map<String,String> messageData){
        rabbitTemplate.convertAndSend("fanout.A", null,messageData);
        System.out.println( "消息已经传入消息队列");
    }

}
