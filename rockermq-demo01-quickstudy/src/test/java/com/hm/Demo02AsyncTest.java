package com.hm;

import com.hm.constants.MQConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class Demo02AsyncTest {
    @Test
    void testAsyncSendMessage() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("async-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();
        Message message = new Message("asyncTopic", "我是一个异步的消息".getBytes());
        // 异步发送
        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
                log.info("SendResult:{};\tstatus:{}",sendResult.toString(),sendResult.getSendStatus());
            }

            @Override
            public void onException(Throwable e) {
                log.error("发送失败",e);
            }
        });
        System.out.println("由于是异步发送,所以会继续执行这行代码:1");
        System.in.read();

    }

    /**
     * 消费者获取消息
     */
    @Test
    public void consumerGetMessage() throws Exception {
        // 创建一个消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consumer-group");
        // 设置name srv地址
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        // 订阅一个主题 testTopic ; * 表示订阅这个主题中的所有消息
        consumer.subscribe("asyncTopic","*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 消费的方法
                System.out.println("我是消费者");
                System.out.println(list.get(0).toString());
                System.out.println("消息内容:"+new String(list.get(0).getBody()));
                System.out.println("消息消费上下文:"+consumeConcurrentlyContext);
                // 返回值 CONSUME_SUCCESS:成功,表示消息会从mq中出队列.
                // 如果是RECONSUME_LATER(报错或者null值) 失败 消息会重新回到队列 过一会重新投递出来
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动
        consumer.start();

        //	主线程执行完毕不可以直接停止,要阻塞主线程,从而保证消费者可以一直监听队列
        System.in.read();

    }
}
