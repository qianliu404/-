package com.spring.reggie.dto;

import com.spring.reggie.entity.Setmeal;
import com.spring.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
