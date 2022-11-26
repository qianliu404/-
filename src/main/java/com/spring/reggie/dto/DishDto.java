package com.spring.reggie.dto;

import com.spring.reggie.entity.Dish;
import com.spring.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();//注意名称要一致,否则传过来的json数据无法解析

    private String categoryName;

    private Integer copies;
}
