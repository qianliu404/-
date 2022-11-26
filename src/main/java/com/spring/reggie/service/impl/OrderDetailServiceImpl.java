package com.spring.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.reggie.entity.OrderDetail;
import com.spring.reggie.mapper.OrderDetailMapper;
import com.spring.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl  extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
