package com.spring.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spring.reggie.common.R;
import com.spring.reggie.entity.Category;
import com.spring.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;



//新增菜品
@PostMapping
public R<String> save(@RequestBody Category category){
    log.info("category:{}",category);
    categoryService.save(category);
    return R.success("新增分类成功");
}
//分页信息分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
    //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
    //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件 根据sort进行排序
        queryWrapper.orderByDesc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
   //删除菜单分类
    @DeleteMapping
    public R<String> deleteById(Long ids){
    log.info("删除分类");
        //在分类管理列表页面，可以对某个分类进行删除，需要注意的是当分类关联了菜品或者套餐时，此分类不允许删除
        //一般不使用外键,外键影响性能
        categoryService.remove(ids);
        return R.success("删除成功！");
    }
    //根据id修改菜品信息
    @PutMapping
    public R<String> update(@RequestBody Category category){
    log.info("修改分类信息");
    //修改数据
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }
    // 根据条件查询分类数据
    @GetMapping("/list")
    public R<List<Category>> categoryList(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //  条件只有当 category.getType()不为空
        queryWrapper.eq(category.getType() != null, Category::getType,category.getType());

        //排序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);
    }


}