package com.hm.listener;

import com.hm.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(topic = "seckillTopic",
        consumerGroup = "seckill-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 40
)
@Component
@Slf4j
public class SeckillListener implements RocketMQListener<MessageExt> {

    @Resource
    private GoodsService goodsService;

    @Override
    public void onMessage(MessageExt message) {
        log.info("接收到消息:{}", message.toString());
        String msg = new String(message.getBody());
        String[] uk = msg.split("-");
        String goodsId = uk[0];
        String userId = uk[1];
        goodsService.seckillGoods(goodsId, userId);

    }
}
