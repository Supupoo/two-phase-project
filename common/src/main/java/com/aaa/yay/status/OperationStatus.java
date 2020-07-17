package com.aaa.yay.status;

/**
 * @Author yay
 * @Description 操作状态码
 * @CreatTime 2020年 07月12日 星期日 19:21:16
 */
public enum OperationStatus {

    SUCCESS("1","操作成功"),
    FAILED("2", "操作失败"),
    DELETE_OPERATION("3", "删除操作"),
    UPDATE_OPERATION("4", "修改操作"),
    INSERT_OPERATION("5", "新增操作"),
    ZUUL_FILTER_SUCCESS("6", "路由过滤成功"),
    ZUUL_FILTER_FAILED("7", "路由过滤失败"),
    ZUUL_FILTER_TOKEN_SUCCESS("8", "token值存在"),
    ZUUL_FILTER_TOKEN_FAILED("9", "token值不存在"),
    REQUEST_IS_NULL("10", "request对象为null");
    
    /**
    * 
    * @author yay
    * @param code msg
    * @updateTime 2020/07/12 20:01 
    * @throws 
    * @return 
    */
    OperationStatus(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    private String code;
    private String msg;

    public String getCode(){
        return code;
    }
    public String getMsg(){
        return msg;
    }
}
