package com.spring.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spring.reggie.common.R;
import com.spring.reggie.dto.DishDto;
import com.spring.reggie.entity.Category;
import com.spring.reggie.entity.Dish;
import com.spring.reggie.entity.DishFlavor;
import com.spring.reggie.service.CategoryService;
import com.spring.reggie.service.DishFlavorService;
import com.spring.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);
        //清理某个分类下面的菜品缓存数据
        Object key = redisTemplate.opsForValue().get("dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus());
        redisTemplate.delete(key);
        return R.success("新增菜品操作成功！");
    }

    /**
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //要注意的是,由于渲染数据还需要的菜品分类,但是我们Dish中只有菜品分类的id,而前端是无法获知我们菜品分类的id的
        //因此我们需要在拿到Dish之后,拿着这个菜品分类的id,去菜品分类表中去查你这个菜品分类的名称,拿到之后,再包装成DTO发送到前端即可
        //这里可以提一嘴,就是前端渲染数据的时候,拿到我们后端给它的json,会挨个地查询有没有一个属性名字叫做categoryName的,如果没有的话它就渲染不上
        //同样的,我们后端接收数据的话也就这样,它会去扫描前端给的json数据,挨个地查询有没有我们参数表里面的字段名,假如说有字段名的话,那么就会把数据填进去
        /**先拿到Dish**/
        log.info("page={},pageSize={}", page, pageSize);
        //使用MP的分页插件，如果有name的话就构造条件构造器
        //分页构造器
        Page<Dish> pageInfo = new Page(page, pageSize);//查第page页，查pageSize条件
        Page<DishDto> pageDtoInfo = new Page<>();
        //条件构造器,添加过滤条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();//注意指定泛型
        lambdaQueryWrapper.like(!StringUtils.isBlank(name), Dish::getName, name);//where name =,注意当name为空时不搞这个sql
        //保证查询数据的一致性，设置排序规则
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询(分页构造器，条件构造器）
        dishService.page(pageInfo, lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");//records是正常的列表数据,不需要修改,我们的目的是修改categoryName
        List<Dish> records = pageInfo.getRecords();//然后咱根据records里面的数据id去获取名字
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);//设置普通属性值(Dish的值全部赋值进去)
            dishDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());//设置categoryName
            return dishDto;//完成后将该对象返回
        }).collect(Collectors.toList());
        pageDtoInfo.setRecords(list);
        return R.success(pageDtoInfo);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     **/
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        //清理所有菜品缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //清理某个分类下面的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("修改菜品操作成功！");
    }

    // 根据条件(分类id)查询对应的菜品数据
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //条件条件，查询状态是1 （Status=0代表禁售，Status=1代表正常）
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);   //根据getSort升序排,根据getUpdateTime降序排
        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);

    }*/


    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_12346564616163166_1

        //先从redis中获取缓存数据

        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get("key");
        //如果存在，直接返回，无需查询数据
        if (dishDtoList != null) {
            //如果存在，就直接返回，无需查询数据库
            return R.success(dishDtoList);
        }

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);  //口味的集合
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis
        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    /*
     *对菜品批量或是单个进行停售或是起售
     */
    @PostMapping("/status/{status}")
    //这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status, @RequestParam List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(ids != null, Dish::getId, ids);
        //根据数据进行批量查询
        List<Dish> list = dishService.list(queryWrapper);
        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("售卖状态修改成功");
    }

    /*
    *套餐的批量删除和单个删除
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        //删除菜品 这里删除是逻辑删除
        dishService.deleteByIds(ids);
        //删除菜品对应的口味 也是逻辑删除
        LambdaQueryWrapper<DishFlavor>queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        return R.success("菜单删除成功");

    }
}

