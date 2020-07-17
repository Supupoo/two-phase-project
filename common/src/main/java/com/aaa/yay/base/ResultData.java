package com.aaa.yay.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yay
 * @description 统一接口返回值
 *              也就是说把后端的controller的返回值统一了
 *              T: 所谓的泛型说白了就相当于一个占位符
 * @creatTime 2020年 07月08日 星期三 21:36:43
 */
@Data
@Accessors(chain = true)
public class ResultData<T> implements Serializable {
    private String code;
    private String msg;
    private String detail;
    private T data;
}
