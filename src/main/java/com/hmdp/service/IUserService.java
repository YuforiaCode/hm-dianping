package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    /**
     * 发送短信验证码
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 短信验证码登录
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 签到
     */
    Result sign();

    /**
     * 统计连续签到
     */
    Result signCount();
}
