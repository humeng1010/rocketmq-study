package com.hm.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hm.model.entity.Goods;
import com.hm.service.GoodsService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Component
public class GoodsStockSync {

    @Resource
    private GoodsService goodsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void initData() {
        LambdaQueryWrapper<Goods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Goods::getId, Goods::getStocks).gt(Goods::getStocks, 0);
        List<Goods> goods = goodsService.list(queryWrapper);
        if (goods.isEmpty()) {
            return;
        }
        goods.forEach(g -> {
            stringRedisTemplate.opsForValue().set("seckill:stock:" + g.getId(), g.getStocks().toString());
        });
    }

    // 每天早上十点执行
    // @Scheduled(cron = "0 0 10 * * ? ")
    // public void initData() {
    //
    // }
}
