package com.hm.controller;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class SeckillController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/seckill")
    public String doSeckill(Integer id, Integer userId) {

        // 扣减缓存库存
        String key2 = "seckill:stock:" + id;
        ValueOperations<String, String> value = stringRedisTemplate.opsForValue();
        // 扣减库存
        Long stock = value.decrement(key2);

        if (stock == null || stock < 0) {
            return "该商品已售罄";
        }

        
        SetOperations<String, String> set = stringRedisTemplate.opsForSet();
        String time = DateTimeFormatter.ofPattern("yyyy:MM:dd:").format(LocalDateTime.now());
        String key = "seckill:" + time + id;
        if (Boolean.TRUE.equals(set.isMember(key, userId.toString()))) {
            //    该用户已经抢过该商品了
            return "不能重复下单";
        }
        // 缓存用户到商品中,一人一单
        set.add(key, userId.toString());


        //    发送MQ异步处理
        rocketMQTemplate.asyncSend("seckillTopic", id + "-" + userId, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("消息发送失败:" + throwable.getMessage());
                String log = String.format("用户id:%s,商品id:%s", userId, id);
                System.out.println(log);
            }
        });

        return "正在拼命抢购中,请稍后再订单中查看";

    }
}
