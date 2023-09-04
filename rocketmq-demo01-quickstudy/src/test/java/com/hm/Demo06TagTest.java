package com.hm;

import com.hm.constants.MQConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Demo06TagTest {

    @Test
    public void testSendTagMessage() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("tag-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();

        Message message1 = new Message("tagTopic","vip","我是VIP的文章".getBytes());
        Message message2 = new Message("tagTopic","svip","我是SVIP的文章".getBytes());
        producer.send(message1);
        producer.send(message2);

        System.out.println("发送成功");

        producer.shutdown();
    }

    @Test
    public void testGetTagMessage1() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag-consumer-groupA");
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);

        consumer.subscribe("tagTopic","vip");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("我是普通VIP的消费者,我正在消费消息:"+new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

        System.in.read();
    }

    /**
     * SVIP 可以订阅VIP || SVIP的消息
     * @throws Exception
     */
    @Test
    public void testGetTagMessage2() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag-consumer-groupB");
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);

        consumer.subscribe("tagTopic","vip || svip");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("我是SVIP的消费者,我正在消费消息:"+new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

        System.in.read();
    }
}
