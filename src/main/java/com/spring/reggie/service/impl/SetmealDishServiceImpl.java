package com.spring.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.reggie.common.CustomException;
import com.spring.reggie.dto.SetmealDto;
import com.spring.reggie.entity.Setmeal;
import com.spring.reggie.entity.SetmealDish;
import com.spring.reggie.mapper.SetmealDishMapper;
import com.spring.reggie.service.SetmealDishServcie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishServcie {

}
