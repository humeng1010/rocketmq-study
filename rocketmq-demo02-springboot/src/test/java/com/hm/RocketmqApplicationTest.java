package com.hm;

import com.alibaba.fastjson.JSON;
import com.hm.model.entity.MsgModel;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class RocketmqApplicationTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void testSendMessageByBoot() {
        /*// 同步
        rocketMQTemplate.syncSend("bootTopic", "我是 spring boot 02 project 发送的普通消息");
        // 异步
        rocketMQTemplate.asyncSend("bootAsyncTopic", "我是 spring boot 02 project 发送异步的消息", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送失败" + throwable.getMessage());

            }
        });
        //    单向
        rocketMQTemplate.sendOneWay("bootOnewayTopic", "我是 spring boot 02 project 发送单向的消息");
        //    延迟消息
        Message<String> message = MessageBuilder.withPayload("我是一个延迟消息").build();
        rocketMQTemplate.syncSend("bootMsTopic", message, 3000, 3);
*/
        // 带key的消息
        // Message<String> message = MessageBuilder.withPayload("我是一个可以带key的消息,可以避免消息的重复消费").setHeader(RocketMQHeaders.KEYS, UUID.randomUUID().toString()).build();
        // rocketMQTemplate.syncSend("bootKeyTopic", message);
        
        //    顺序消息 发送者需要将一组消息都发送到同一组队列中去 消费者需要单线程消费
        List<MsgModel> msgModels = Arrays.asList(
                new MsgModel("qwer", 1L, "下单"),
                new MsgModel("qwer", 1L, "支付"),
                new MsgModel("qwer", 1L, "发货"),

                new MsgModel("zxcv", 2L, "下单"),
                new MsgModel("zxcv", 2L, "支付"),
                new MsgModel("zxcv", 2L, "发货")
        );
        msgModels.forEach(msgModel -> {
            // 一般都是以JSON的方式进行处理
            rocketMQTemplate.syncSendOrderly(
                    "bootOrderlyTopic",
                    JSON.toJSONString(msgModel),
                    msgModel.getOrderId());
        });

    }
}
