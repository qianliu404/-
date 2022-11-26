package com.spring.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.reggie.common.CustomException;
import com.spring.reggie.dto.SetmealDto;
import com.spring.reggie.entity.Setmeal;
import com.spring.reggie.entity.SetmealDish;
import com.spring.reggie.mapper.SetmealMapper;
import com.spring.reggie.service.SetmealDishServcie;
import com.spring.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 千流
 * @version 1.0
 * @date 2022/11/1
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
     private SetmealDishServcie setmealDishServcie;

    //新增套餐，同时需要保存套餐和菜品的关联关系
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作

        setmealDishServcie.saveBatch(setmealDishes);

    }
    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * **/
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //查询套餐状态，确定是否可用删除（套餐的 status=1，表示套餐正在售卖，不能删除，如果非要删除需要停售套餐）
        //select count(*) from setmeal where id in (1,2,3) and status=1;
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //int count = this.count(queryWrapper);
        if(this.count(queryWrapper) >0){
            //如果不能删除，抛出一个业务异常
            throw  new CustomException("套餐正在售卖中，删除失败");
        }
        //如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        //删除关系表中的数据--setmeal_dish
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish
        setmealDishServcie.remove(lambdaQueryWrapper);
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @return
     */
    @Override
    public SetmealDto getDate(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        //在关联表中查询，setmealdish
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal != null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            List<SetmealDish> list = setmealDishServcie.list(queryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }
        return null;
    }
}
