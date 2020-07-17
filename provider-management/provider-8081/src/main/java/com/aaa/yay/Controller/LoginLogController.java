package com.aaa.yay.Controller;

import com.aaa.yay.base.BaseService;
import com.aaa.yay.base.CommonController;
import com.aaa.yay.model.LoginLog;
import com.aaa.yay.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yay
 * @Description TODO
 * @CreatTime 2020年 07月15日 星期三 21:42:44
 */
@RestController
public class LoginLogController extends CommonController<LoginLog> {

    @Autowired
    private LoginLogService loginLogService;

    @Override
    public BaseService<LoginLog> getBaseService() {
        return loginLogService;
    }

    /**
    * 保存日志
    * @author yay
    * @param loginLog
    * @updateTime 2020/07/15 21:47
    * @throws
    * @return java.lang.Integer
    */
    @PostMapping("/addLoginLog")
    public Integer addLoginLog(@RequestBody LoginLog loginLog){
        return loginLogService.add(loginLog);
    }
}
