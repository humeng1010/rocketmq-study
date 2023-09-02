package com.hm;

import com.hm.constants.MQConstant;
import com.hm.model.entity.MsgModel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class Demo05OrderlyTest {

    @Test
    public void testSendOrderlyMessage() throws Exception {
        List<MsgModel> msgModels = Arrays.asList(
                new MsgModel("qwer",1L,"下单"),
                new MsgModel("qwer",1L,"支付"),
                new MsgModel("qwer",1L,"发货"),

                new MsgModel("zxcv",2L,"下单"),
                new MsgModel("zxcv",2L,"支付"),
                new MsgModel("zxcv",2L,"发货")
        );
        DefaultMQProducer producer = new DefaultMQProducer("order-producer-group");
        producer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        producer.start();
        msgModels.forEach(msgModel -> {
            Message message = new Message("orderlyTopic", msgModel.toString().getBytes());
            try {
                // 参数一: 消息
                // 参数二: 消息队列选择器
                // 参数三: 可以区分消息唯一标识,根据这个进行Hash取余保证同一种消息在同一个队列中
                SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                    /**
                     *
                     * @param mqs 消息队列
                     * @param msg 消息
                     * @param arg 就是外面传递过来的消息唯一标识(参数三)
                     * @return 选择的消息队列
                     */
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        // Hash 取模法 保证相同的订单号存放到同一个队列中
                        int hashCode = arg.toString().hashCode();
                        int i = hashCode % mqs.size();
                        return mqs.get(i);
                    }
                }, msgModel.getOrderId());
                System.out.println("status: "+ sendResult.getSendStatus());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("发送完毕(顺序性)");
    }

    @Test
    public void testConsumerOrderlyMessage() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order-consumer-group");
        consumer.setNamesrvAddr(MQConstant.NAME_SRV_ADDR);
        consumer.subscribe("orderlyTopic","*");
        // 顺序取出消息必须是单线程模型,注意并不是全局只有一个线程,而是每一个队列只对应一个线程
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                System.out.println("线程id: "+Thread.currentThread().getId());
                System.out.println("消息内容:"+new String(msgs.get(0).getBody()));
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();

        System.in.read();

    }
}
