package com.aaa.yay.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * @Author yay
 * @Description json转换工具类
 * @CreatTime 2020年 07月13日 星期一 14:47:08
 */
public class JsonUtils {

    // 1.定义私有静态常量ObjectMapper(命名规则：所有字母全部大写，单词与单词之间使用_连接)
    /**
     * ObjectMapper:就是fastJson包中进行类型转换的工具类
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
    * 把对象转换为json字符串
    * @author yay
    * @param object
    * @updateTime 2020/07/13 14:59
    * @throws
    * @return java.lang.String
    */
    public static String toJsonString(Object object){
        try {
            String jsonString = OBJECT_MAPPER.writeValueAsString(object);
            return jsonString;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
    * 把json转换为指定的对象
     * <T>:定义了一个类型
     * T：返回值的类型
    * @author yay
    * @param jsonData：传入的json对象
     *       beanType：所需要转换对象的目标类型 User.class,Book.class...
    * @updateTime 2020/07/13 15:06
    * @throws
    * @return T
    */
    public static <T> T toObject(String jsonData,Class<T> beanType){
        try {
            T t = OBJECT_MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    * 把json转换为List集合
    * @author yay
    * @param jsonData beanType
    * @updateTime 2020/07/13 15:24
    * @throws
    * @return java.util.List<T>
    */
    public static <T> List<T> toList(String jsonData,Class<T> beanType){
        //1.为List集合添加一个指定的泛型
        /**
         * List User.class ---> 通过constructParametricType方法把List和User合并，也就是说为List指定一个User对象的泛型(List<User>)
         */
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = OBJECT_MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
