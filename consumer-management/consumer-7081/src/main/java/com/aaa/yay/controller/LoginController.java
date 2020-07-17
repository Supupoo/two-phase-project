package com.aaa.yay.controller;

import com.aaa.yay.annotation.LoginAnnotation;
import com.aaa.yay.base.BaseController;
import com.aaa.yay.base.ResultData;
import com.aaa.yay.model.User;
import com.aaa.yay.service.SystemApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yay
 * @Description 登录
 * @CreatTime 2020年 07月15日 星期三 21:05:52
 */
@RestController
public class LoginController extends BaseController {

    @Autowired
    private SystemApi systemApi;

    /**
    * 执行登录操作
    * @author yay
    * @param user
    * @updateTime 2020/07/15 21:10
    * @throws
    * @return com.aaa.yay.base.ResultData
    */
    @PostMapping("/doLogin")
    @LoginAnnotation(operationType = "登录操作",operationName = "管理员登录")
    public ResultData doLogin(User user){
        return systemApi.doLogin(user);
    }
}
