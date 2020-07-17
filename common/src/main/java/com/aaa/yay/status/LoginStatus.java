package com.aaa.yay.status;

/**
 * @author yay
 * @description 登录状态信息
 * @creatTime 2020年 07月08日 星期三 18:50:47
 */
public enum  LoginStatus {
    //定义登录状态码和返回消息
    LOGIN_SUCCESS("200","登录成功"),
    LOGIN_FAILED("400","登录失败，系统异常"),
    USER_EXIST("201","用户已经存在"),
    USER_NOT_EXIST("401","用户不存在"),
    PASSWORD_WRONG("402","密码错误"),
    LOGOUT_WRONG("405","用户退出异常"),
    SYSTEM_EXCEPTION("406","系统异常");

    LoginStatus(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
