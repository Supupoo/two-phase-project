package com.aaa.yay.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author yay
 * @Description 判断两个集合的值是否相等
 * @CreatTime 2020年 07月16日 星期四 22:50:01
 */
public class CompareListUtils {

    private CompareListUtils(){

    }

    /**
    * 判断两个集合的值是否相等
    * @author yay
    * @param list1 list2
    * @updateTime 2020/07/16 22:52
    * @throws
    * @return boolean
    */
    public static <T,V> boolean compareList(List<T> list1,List<V> list2){
        if (list1 == null || list2 == null){
            return false;
        }
        if (list1.size() != list2.size()){
            return false;
        }

        Set<Integer> hashCodeSet = new HashSet<>();
        for (T adInfoData : list1){
            hashCodeSet.add(adInfoData.hashCode());
        }
        for (V adInfoData : list2){
            if (!hashCodeSet.contains(adInfoData.hashCode())){
                return false;
            }
        }
        return true;
    }
}
