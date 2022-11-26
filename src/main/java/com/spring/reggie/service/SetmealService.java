package com.spring.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spring.reggie.dto.SetmealDto;
import com.spring.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * **/

    public void removeWithDish(List<Long> ids);

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    SetmealDto getDate(Long id);
}
