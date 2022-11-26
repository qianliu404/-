package com.spring.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.reggie.dto.DishDto;
import com.spring.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //  新增菜品，同时插入菜品对应的数据，需要操作两张表: dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    //保存更新菜单分类
    public void updateWithFlavor(DishDto dishDto);

    //根据传过来的id批量或者是单个的删除菜品
    void deleteByIds(List<Long> ids);

}
