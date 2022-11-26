package com.spring.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.reggie.entity.Employee;
import com.spring.reggie.mapper.EmployeeMapper;
import com.spring.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
//这个写法是mp所特有的，第一个泛型参数代表实现的mapper接口类型，第二个是所接收的实体类型
//最终再实现service接口即可