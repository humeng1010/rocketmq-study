package com.hm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.mapper.GoodsMapper;
import com.hm.model.entity.Goods;
import com.hm.model.entity.OrderRecords;
import com.hm.service.GoodsService;
import com.hm.service.OrderRecordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author humeng
 * @description 针对表【goods】的数据库操作Service实现
 * @createDate 2023-09-04 19:05:42
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {

    @Resource
    private OrderRecordsService orderRecordsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void seckillGoods(String goodsId, String userId) {
        int goods_id = Integer.parseInt(goodsId);
        int user_id = Integer.parseInt(userId);
        //    1.查询订单中是否有该数据
        OrderRecords orderRecords = orderRecordsService.query().eq("user_id", user_id).eq("goods_id", goods_id).one();
        //    2.如果有,则不允许重复下单
        if (orderRecords != null) {
            log.info("不允许重复下单");
            return;
        }

        //    分布式锁
        try {
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("order:lock:" + user_id + ":" + goods_id, "", 5, TimeUnit.MINUTES);

            if (Boolean.FALSE.equals(lock)) {
                // 如果锁被占用,则直接返回
                return;
            }

            //    3.查询商品库存是否足够
            Goods goods = this.query().select("stocks").eq("id", goods_id).one();
            Integer stocks = goods.getStocks();
            //    4.如果库存不足,清除缓存数据
            if (stocks <= 0) {
                log.info("库存不足,清除缓存数据");
                stringRedisTemplate.delete("seckill:stock:" + goods_id);
                return;
            }
            log.info("库存充足");
            //    5.如果库存充足,扣减库存使用乐观锁CAS法防止超卖
            boolean update = this.update().setSql("stocks = stocks - 1").eq("id", goods_id).gt("stocks", 0).update();
            if (!update) {
                return;
            }

            log.info("创建订单");
            //    6.创建订单号
            OrderRecords records = new OrderRecords();
            records.setOrderSn(UUID.randomUUID().toString());
            records.setGoodsId(goods_id);
            records.setUserId(user_id);
            records.setCreateTime(new Date());

            orderRecordsService.save(records);

        } finally {
            //    释放锁
            stringRedisTemplate.delete("order:lock");

        }


    }
}




