package com.spring.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spring.reggie.common.R;
import com.spring.reggie.entity.Employee;
import com.spring.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
    *员工登录
     **/
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //将界面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        System.out.println(employee.getUsername());
        //根据页面提交的用户名的username查询数据库，注意添加泛型
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//调用service的getOne方法获取数据库唯一的数据，就是在索引中有Unique字段的数据
        //如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("用户名不存在");
        }

        //密码对比，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("用户名或密码错误");
        }

        //查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
        }
        /**
         * 登录退出功能
         * LocalStorage 清理Session中的用户id
         * 返回结果
         */
        @PostMapping("/logout")
        public R<String> logout(HttpServletRequest request){
            request.getSession().removeAttribute("employee");
            return R.success("退出成功");
        }

        /**
        *新增员工
         * @param employee
         * @return
         **/
        @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
            log.info("新增员工，前台发过来的信息",employee.toString());
            //新增员工时添加初始密码，注意要md5加密
            employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
            //设置创建时间
            employee.setCreateTime(LocalDateTime.now());
            //设置更新时间
            employee.setUpdateTime(LocalDateTime.now());

            //设置创建人。拿到当前登录用户的session从而得到id
            employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
            //设置更新,拿到当前登录用户的session
            employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
            employeeService.save(employee);//mp简洁方法
            return R.success("新增员工成功");
        }

    /**
     * 分页查询员工信息
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);//使用MP的分页查询，如果有name的话就构造条件构造器
        //分页构造器
        Page pageInfo = new Page(page,pageSize);//查第page页,查pageSize条件
        //条件构造器,添加过滤条件 name不为null，才会 比较 getUsername方法和前端传入的name是否匹配 的过滤条件
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();//注意指定泛型
        lambdaQueryWrapper.like(!StringUtils.isBlank(name),Employee::getUsername,name);//where name = ,注意当name为空的时候不搞这个sql
        //保证查询数据的一致性，设置排序规则 根据用户的时间升序分页展示
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询(分页构造器，条件构造器)
        Page data = employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(data);
    }
    /**
     * 根据id修改员工信息，修改信息需要使用put
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("收到前台给的员工信息:{}",employee.toString());
        //收到信息后开始Update
        Long empId = (Long) request.getSession().getAttribute("employee");//获取浏览器info字段的id
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser(empId);
        employeeService.updateById(employee);
        return  R.success("员工信息修改成功");
    }

    /*
    根据id查询员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        log.info("根据id查询员工信息{}",employee.toString());
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查到对应得员工信息");
    }
}
