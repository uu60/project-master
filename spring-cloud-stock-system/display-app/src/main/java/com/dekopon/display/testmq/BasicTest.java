package com.dekopon.display.testmq;

import com.dekopon.display.config.RabbitConfiguration;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author dekopon
 * @since 2023/6/13 16:33
 */
public class BasicTest {

}

class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 1. 创建一个链接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 2. 设置rabbitmq地址
        connectionFactory.setHost("localhost");
        // 3. 创建Connection对象
        Connection connection = connectionFactory.newConnection();
        // 4. 创建Channel
        Channel channel = connection.createChannel();
        // 5. 设置队列属性
        /*
         * 参数
         * 1 队列名称
         * 2 队列是否要持久化
         * 3 是否排他性（只能在同一个连接对象中操作）
         * 4 是否自动删除（如果没有消费者连接就自动删除）
         * 5 是否要设置一些额外参数
         */
//        channel.queueDeclare("01-hello", true, false, false, null);
        // 6. 发送消息
        /*
        参数
        1 交换机名称
        2 路由key
        3 消息属性
        4 消息的内容
         */
        channel.basicPublish("", RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME, null, "{\"code\": \"AAPL\"}".getBytes());
        // 7. 关闭资源
        connection.close();
    }
}

class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 1. 创建一个链接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 2. 设置rabbitmq地址
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        // 3. 创建Connection对象
        Connection connection = connectionFactory.newConnection();
        // 4. 创建Channel
        Channel channel = connection.createChannel();
        /*
        参数
        1 队列名称
        2 是否自动签收
         */
        channel.basicConsume(RabbitConfiguration.K_DATA_QUERY_QUEUE_NAME, true, new DeliverCallback() {
            /**
             * 当消息从mq取出来了会回调这个方法
             * 消费者消费消息就在这个handle中进行处理
             *
             * @param consumerTag the <i>consumer tag</i> associated with the consumer
             * @param message     the delivered message
             * @throws IOException
             */
            @Override
            public void handle(String consumerTag, Delivery message) throws IOException {
                System.out.println("receive: " + new String(message.getBody()));
            }
        }, new CancelCallback() {
            /**
             * 当消息取消了会回调这个方法
             * @param consumerTag the <i>consumer tag</i> associated with the consumer
             * @throws IOException
             */
            @Override
            public void handle(String consumerTag) throws IOException {
                System.out.println("msg cancelled");
            }
        });
        while (true) {}
    }
}