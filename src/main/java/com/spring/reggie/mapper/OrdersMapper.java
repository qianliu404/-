package com.spring.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
