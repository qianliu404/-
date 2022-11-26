package com.spring.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})//只要类加上了这两个处理器的就会被拦截
public class GlobalExceptionHandler {
    /**
     * 进行异常处理SQLIntegrityConstraintViolationException
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //判断异常信息里面是否包含该关键字，作进一步处理
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");//以空格分隔
            String msg = split[2]+"已存在";
            return  R.error(msg);
        }
        return  R.error("未知错误！");
    }
    /**
     * 异常处理方法 自定义CustomException
     * @return
     */
    @ExceptionHandler(CustomException.class)//设置处理什么异常
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());//得到异常信息
        return R.error(ex.getMessage());
    }
}
