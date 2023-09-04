package com.hm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.mapper.OrderRecordsMapper;
import com.hm.model.entity.OrderRecords;
import com.hm.service.OrderRecordsService;
import org.springframework.stereotype.Service;

/**
 * @author humeng
 * @description 针对表【order_records】的数据库操作Service实现
 * @createDate 2023-09-04 19:09:40
 */
@Service
public class OrderRecordsServiceImpl extends ServiceImpl<OrderRecordsMapper, OrderRecords>
        implements OrderRecordsService {

}




