package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @作者: Ji YongGuang.
 * @修改时间: 19:26 2017/11/6.
 * @功能描述:
 */
@Controller
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     *
     * @param username    用户名
     * @param password    密码
     * @param httpSession session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession httpSession) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        // Controller 层需要判断一下 Service 返回对象的状态即success或error
        if (serverResponse.isSuccess()) {
            // 有管理员和用户两种，类型较少且明确根本不用描述 偷个懒常量类
            httpSession.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 当输入完用户名，点击下一个input框的时候要实时的调用一个校验接口(校验用户名或者email是否存在)。在前台给予一个"实时"的反馈
     * 虽然service的register方法已经判断过了，但是那已经是表单提交之后了，没有实时性。用户体验不好
     *
     * @param str  输入的文本
     * @param type email/username
     * @return 参数的实时校验
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取登录简要用户的信息
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession httpSession) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser != null) {
            return ServerResponse.createBySuccess(currentUser);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    /**
     * 忘记密码获取用户密码问题
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 用户问题答案的验证
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */// 返回是String因为要把token放到String中。
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_resst_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }


    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession httpSession, String passwordOld, String passwordNew) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        return iUserService.resetPassword(passwordOld, passwordNew, currentUser);
    }

    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpSession httpSession, User user) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 传上来的user对象没有ID属性 以及User对象的username属性不能更改
        user.setId(currentUser.getId());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            // user对象的用户名不能更开，同时在修改后的user对象重新存储到session之前，
            // 重新将username的值set到该对象之中因为username不能更改的约束，所以用户名并没有传递到后台
            response.getData().setUsername(currentUser.getUsername());
            httpSession.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 获取当前登录用户的详细信息，并强制登录
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession httpSession) {
        User currentUser = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
