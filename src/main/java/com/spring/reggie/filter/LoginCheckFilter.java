package com.spring.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.spring.reggie.common.BaseContext;
import com.spring.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;



/*
使用拦截器检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFileter",urlPatterns = "/*")//拦截所有内容
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配，支持通配符

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
        //1.获取本次请求的URI(请求路径)
        //URL是同一资源定位符,是网络上资源的地址,可以定义为引用地址的字符串,用于指示资源的位置以及用于访问它的协议
        //包括访问资源的协议 服务器的位置(IP或者是域名) 服务器上的端口号 在服务器目录结构中的位置 片段标识符
        //URI是表示逻辑或者物理资源的字符序列,与URL类似,也是一串字符,通过使用位置,名称或两者来表示
        //URL主要用来连接网页,网页组件或网页上的程序,借助访问方法
        //URI用于定义项目的标识
        HttpServletRequest request= (HttpServletRequest) servletRequest;//强制转换
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截请求:{}",((HttpServletRequest)servletRequest).getRequestURI());

        //获得本次请求的URI
        String requestURI = request.getRequestURI();
        //设置一个字符串级，表示不需要处理的路径
        String[] urls =new String[]{
        "/employee/login",//如果请求路径是用来登录的直接放行
        "/employee/logout",//退出则直接放行
        "/backend/**",//用户未登录时，屏蔽请求数据的接口
        "/front/**",
         "/common/**",
          "/user/sendMsg", //移动端发送短信地址
          "/user/login"    //移动端登录
        };
        //判断本次请求是否需要处理，不需要则直接放行
        boolean check = isMatch(urls,requestURI);
        if(check){//不需要处理，直接放行
            log.info("不需要处理,直接放行",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态(session当中是否含有employee的登录信息)，如果已经登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null) {
            //之前登录过的，直接放行
            log.info("用户已经登录,用户id为:{}",request.getSession().getAttribute("employee"));
            BaseContext.setCurrentUserId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        //未登录则返回未登录结果，由于前端拦截的是我们的response对象，所以往response对象里面写result对象即可
        //res.data.code === 0 && res.data.msg === 'NOTLOGIN',这是前端的逻辑
        log.info("用户未登录");

       response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
}
       //路径匹配，检查本次请求是否需要放行
       public  boolean isMatch(String[] urls,String requestURI){
       for (String url : urls){
           boolean match = PATH_MATCHER.match(url,requestURI);
           if(match){
               return  true;
           }
       }
       return  false;
    }
}

