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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class Demo04DelayTest {

    @Test
    public void testSendDelayMessage() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("ms-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();
        Message message = new Message("orderMSTopic", "订单号:co-spa-meawr-as-tq".getBytes());
        // 给消息设置一个延时等级
        // https://rocketmq.apache.org/zh/docs/4.x/producer/04message3#%E5%BB%B6%E6%97%B6%E6%B6%88%E6%81%AF%E7%BA%A6%E6%9D%9F
        //* 发送时间:   2023-09-02 20:04:57
        //* 收到消息时间:2023-09-02 20:05:07
        message.setDelayTimeLevel(3);
        // 发送延时时间
        producer.send(message);
        System.out.println("发送时间:"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        producer.shutdown();

    }

    /**
     * 发送时间:   2023-09-02 20:04:57
     * 收到消息时间:2023-09-02 20:05:07
     * @throws Exception
     */
    @Test
    public void testMsConsumerMessage() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ms-consumer-group");
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        consumer.subscribe("orderMSTopic","*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("收到消息时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                System.out.println(msgs.get(0).toString());
                System.out.println("消息内容:"+new String(msgs.get(0).getBody()));
                System.out.println("消息消费上下文:"+context);
                return null;
            }
        });
        consumer.start();
        // 挂起jvm
        System.in.read();
    }
}
