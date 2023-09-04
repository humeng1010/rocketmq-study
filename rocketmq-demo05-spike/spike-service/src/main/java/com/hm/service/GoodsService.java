package com.hm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.model.entity.Goods;

/**
 * @author humeng
 * @description 针对表【goods】的数据库操作Service
 * @createDate 2023-09-04 19:05:42
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 处理秒杀的业务
     *
     * @param goodsId
     * @param userId
     */
    void seckillGoods(String goodsId, String userId);
}
