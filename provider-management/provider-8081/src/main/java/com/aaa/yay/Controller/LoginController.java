package com.aaa.yay.Controller;

import com.aaa.yay.base.BaseService;
import com.aaa.yay.base.CommonController;
import com.aaa.yay.base.ResultData;
import com.aaa.yay.model.User;
import com.aaa.yay.service.LoginService;
import com.aaa.yay.vo.TokenVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.aaa.yay.status.LoginStatus.*;

/**
 * @Author yay
 * @Description 登录日志
 * @CreatTime 2020年 07月15日 星期三 21:28:59
 */
@RestController
public class LoginController extends CommonController<User> {

    @Autowired
    private LoginService loginService;

    @Override
    public BaseService<User> getBaseService() {
        return loginService;
    }

    /**
    * 执行登录操作
    * @author yay
    * @param user
    * @updateTime 2020/07/15 21:33
    * @throws
    * @return com.aaa.yay.base.ResultData
    */
    @PostMapping("/doLogin")
    public ResultData doLogin(@RequestBody User user){
        TokenVo tokenVo = loginService.doLogin(user);
        if (tokenVo.getIfSuccess()){
            return super.loginSuccess(tokenVo.getToken());
        }else if (tokenVo.getType() == 1){
            return super.loginFailed(USER_NOT_EXIST.getMsg());
        }else if (tokenVo.getType() == 2){
            return super.loginFailed(PASSWORD_WRONG.getMsg());
        }else {
            return super.loginFailed(SYSTEM_EXCEPTION.getMsg());
        }
    }
}
