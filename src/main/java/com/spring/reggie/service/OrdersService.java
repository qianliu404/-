package com.spring.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
