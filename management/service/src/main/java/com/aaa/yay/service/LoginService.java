package com.aaa.yay.service;

import com.aaa.yay.base.BaseService;
import com.aaa.yay.model.User;
import com.aaa.yay.vo.TokenVo;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author yay
 * @Description 登录
 * @CreatTime 2020年 07月15日 星期三 21:15:32
 */
@Service
public class LoginService extends BaseService<User> {

    /**
    * 执行登录操作
     *      pojo：实体类
     *      povo:封装类型(当在实际开发中，会有很多种情况导致多表联查的时候无法装载数据--->我就根据返回前端的数据自己封装一个对象出来---->view object)
    * @author yay
    * @param user
    * @updateTime 2020/07/15 21:32
    * @throws
    * @return com.aaa.yay.vo.TokenVo
    */
    public TokenVo doLogin(User user){
        TokenVo tokenVo = new TokenVo();
        User user1 = new User();
        // 1.判断User是否为null
        if (null != user){
            user1.setUsername(user.getUsername());
            // 执行查询 查的是用户名
            User user2 = super.selectOne(user1);
            // 2.判断user2是否为null
            if (null != user2){
                // 说明用户名不存在
                tokenVo.setIfSuccess(false).setType(1);
                return tokenVo;
            }else {
                // 用户名OK，查询密码
                user1.setPassword(user.getPassword());
                User user3 = super.selectOne(user1);
                // 3.判断user3是否为null
                if (null == user3){
                    // 说明密码错误
                    tokenVo.setIfSuccess(false).setType(2);
                    return tokenVo;
                }else {
                    /**
                     * 说明登录成功
                     * !!!!!!mybatis是无法检测连接符的，他会把连接符进行转译(\\-)需要把连接符替换掉
                     */
                    String token = UUID.randomUUID().toString().replaceAll("-", "");
                    user3.setToken(token);
                    Integer updateResult = super.update(user3);
                    if (updateResult > 0){
                        tokenVo.setIfSuccess(true).setToken(token);
                    }else {
                        // 说明系统异常
                        tokenVo.setIfSuccess(false).setType(4);
                        return tokenVo;
                    }
                }
            }
        }else {
            // 说明系统异常
            tokenVo.setIfSuccess(false).setType(4);
            return tokenVo;
        }
        return tokenVo;
    }
}
