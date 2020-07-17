package com.aaa.yay.utils;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yay
 * @description Map转实体工具类
 * @creatTime 2020年 07月10日 星期五 19:53:38
 */
public class Map2BeanUtils {

    private Map2BeanUtils() {

    }

    /**
     * 高性能java实例化工具类
     */
    private final static Objenesis OBJENESIS = new ObjenesisStd(true);
    private final static StringBuilder STRING_BUILDER = new StringBuilder();
    /**
     * 高性能反射工具类中，高性能反射字节集
     * ConcurrentHashMap:在线程中运转，这个Map会在当前线程中出现，而且线程和线程具有隔离性，这里的Map就不会被其他的线程所干扰
     */
    private final static ConcurrentHashMap<Class, MethodAccess> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>(16);


    public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) {
        //1.获取实例对象信息
        T instance = OBJENESIS.newInstance(clazz);
        //2.从ConcurrentHashMap中通过key(instance),获取MethodAccess对象
        MethodAccess methodAccess = CONCURRENT_HASH_MAP.get(clazz);
        //3.判断
        if (null == methodAccess) {
            //4.通过类获取MethodAccess对象
            methodAccess = MethodAccess.get(clazz);
            //5.存入CONCURRENT_HASH_MAP中
            CONCURRENT_HASH_MAP.putIfAbsent(clazz, methodAccess);
        }
        //6.循环Map对象
        for (Map.Entry entry : map.entrySet()) {
            /**
             * Map中的数据需要通过对象的set方法来进行存放
             * User.java{
             *      private String username;
             *    }
             */
            String setMethodName = getSetMethodName((String) entry.getKey());
            int index = methodAccess.getIndex(setMethodName, entry.getValue().getClass());
            methodAccess.invoke(instance, index, entry.getValue());
        }
        return instance;
    }

    /**
     * 通过字段拼接方法名
     *
     * @param filedName
     * @return java.lang.String
     * @throws
     * @author yay
     * @updateTime 2020/07/10 20:31
     */
    private static String getSetMethodName(String filedName) {
        STRING_BUILDER.setLength(0);
        //eg:setUsername
        return STRING_BUILDER.append("set").append(first2UpperCase(filedName)).toString();
    }

    /**
     * 把属性的首字母转换为大写
     *
     * @param str
     * @return java.lang.String
     * @throws
     * @author yay
     * @updateTime 2020/07/10 21:04
     */
    private static String first2UpperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
