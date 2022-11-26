package com.spring.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.reggie.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    void clean();
}
