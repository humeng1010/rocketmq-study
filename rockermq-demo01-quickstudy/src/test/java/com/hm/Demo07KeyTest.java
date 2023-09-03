package com.hm;

import com.hm.constants.MQConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class Demo07KeyTest {
    @Test
    public void testSendKeyMessage() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("key-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();
        String key = UUID.randomUUID().toString();
        System.out.println(key);
        Message msg = new Message("keyTopic", "order", key, "订单消息".getBytes());

        SendResult send = producer.send(msg);

        System.out.println("发送状态: " + send.getSendStatus());

        producer.shutdown();
    }

    @Test
    public void testGetKeyMessage() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("key-consumer-group");
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        consumer.subscribe("keyTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                System.out.println("订单唯一标识: " + messageExt.getKeys());
                // 确保去重表中的key是唯一字段, 创建订单,如果成功则是没有重复消费,如果失败了则代表出现了重复的消息,但是key是一样的就没有插入到数据库
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

        System.in.read();

    }
}
