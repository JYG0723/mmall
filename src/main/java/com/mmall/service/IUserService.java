package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:28 2017/11/6.
 * @功能描述:
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse register(User user);

    ServerResponse checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse checkAnswer(String username, String question, String answer);

    ServerResponse forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);

}
