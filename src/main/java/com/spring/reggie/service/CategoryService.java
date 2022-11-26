package com.spring.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
