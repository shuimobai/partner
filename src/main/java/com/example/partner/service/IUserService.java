package com.example.partner.service;

import com.example.partner.common.Result;
import com.example.partner.controller.domain.UserRequest;
import com.example.partner.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 程序员人走茶凉
 * @since 2022-12-27
 */
public interface IUserService extends IService<User> {

    User login(User user);

    User register(UserRequest user);

    void sendEmail(String email, String type);

    String passwordReset(UserRequest userRequest);
}
