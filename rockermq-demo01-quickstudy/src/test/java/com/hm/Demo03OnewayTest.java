package com.hm;

import com.hm.constants.MQConstant;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Demo03OnewayTest {

    @Test
    public void testSendOnewayMessage() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("oneway-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();
        // 单向消息,具有更大的吞吐量
        producer.sendOneway(new Message("onewayTopic","日志xxx".getBytes()));
        System.out.println("发送完毕");
        producer.shutdown();

    }
}
