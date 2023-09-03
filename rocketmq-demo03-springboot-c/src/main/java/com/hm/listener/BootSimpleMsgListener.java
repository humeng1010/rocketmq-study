package com.hm.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(
        topic = "bootTopic",
        consumerGroup = "boot-test-consumer-group"
        // selectorExpression = "vip" // 选择标签 vip || svip
)
public class BootSimpleMsgListener implements RocketMQListener<MessageExt> {

    /**
     * 消费者方法
     * 只要是正常的消费没有保存就是签收成功,如果报错就会拒收然后重试
     *
     * @param message 消息的全部内容,如果写String/User等具体的对象,则会直接获取消息体并且进行转换
     */
    @Override
    public void onMessage(MessageExt message) {
        log.info("消息内容:{}", new String(message.getBody()));
    }
}
