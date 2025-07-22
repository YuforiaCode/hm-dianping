package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录校验拦截器
 */
public class LoginInterceptor implements HandlerInterceptor{

    /**
     * 前置拦截
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.判断是否需要拦截(ThreadLocal中是否有用户)
        if (UserHolder.getUser() == null) {
            //没有，需要拦截，设置状态码
            response.setStatus(401);
            return false;
        }
        //有用户，则放行
        return true;
    }
}
