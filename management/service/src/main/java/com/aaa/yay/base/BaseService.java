package com.aaa.yay.base;

import com.aaa.yay.utils.Map2BeanUtils;
import com.aaa.yay.utils.SpringContextUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.aaa.yay.staticproperties.OrderWordStatic.*;

/**
 * @author yay
 * @description 通用service
 * 这个service中封装了很多的通用方法:
 * insert
 * update
 * delete
 * select
 * ...
 * @creatTime 2020年 07月10日 星期五 15:20:14
 */
public abstract class BaseService<T> {

    /**
     * 全局变量，缓存子类的泛型类型
     */
    private Class<T> cache = null;

    @Autowired
    private Mapper<T> mapper;

    protected Mapper<T> getMapper() {
        return mapper;
    }

    /**
     * 新增数据
     *
     * @param t
     * @return java.lang.Integer
     * @throws
     * @author yay
     * @updateTime 2020/07/10 15:35
     */
    public Integer add(T t) {
        return mapper.insert(t);
    }


    /**
     * 删除数据
     *
     * @param t
     * @return java.lang.Integer
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:07
     */
    public Integer delete(T t) {
        return mapper.deleteByPrimaryKey(t);
    }

    /**
     * 根据主键进行批量删除
     *
     * @param ids
     * @return java.lang.Integer
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:22
     */
    public Integer deleteByIds(List<Integer> ids) {
        /**
         * delete * from user where 1 = 1 and id in (1,2,3,4,5,6,7,8)
         * andIn("id")--->id就是数据库中的主键名称
         */
        Example example = Example.builder(getTypeArgument()).where(Sqls.custom().andIn("id", ids)).build();
        return mapper.deleteByPrimaryKey(example);
    }

    /**
     * 根据主键更新属性不为null的值
     *
     * @param t
     * @return java.lang.Integer
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:23
     */
    public Integer update(T t) {
        return mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 批量更新属性不为null的数据
     * update username = ?  from user where id in (1,2,3,4,5,6,7)
     *
     * @param t ids
     * @return java.lang.Integer
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:27
     */
    public Integer batchUpdate(T t, Integer[] ids) {
        Example example = Example.builder(getTypeArgument()).where(Sqls.custom().andIn("id", Arrays.asList(ids))).build();
        return mapper.updateByExampleSelective(t, example);
    }

    /**
     * 查询一条数据
     * 形参中的t所传递的数据--->主键，唯一键(username, phone number....)
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param t
     * @return T
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:40
     */
    public T selectOne(T t) {
        return mapper.selectOne(t);
    }

    /**
     * 查询一条数据,可以排序
     *
     * @param where orderByField fileds(不只是代表唯一键,password,age,address)
     *              select * from user where password = xxxx and age = xx and address = xxx
     * @return T
     * @throws
     * @author yay
     * @updateTime 2020/07/10 19:09
     */
    public T selectOneByFiled(Sqls where, String orderByField, String... fileds) {
        return selectByFileds(null, null, where, orderByField, null, fileds).get(0);
    }

    /**
     * 条件查询带分页
     * @param pageNo pageSize where orderByFiled fileds
     * @return com.github.pagehelper.PageInfo<T>
     * @throws
     * @author yay
     * @updateTime 2020/07/10 19:17
     */
    public PageInfo<T> selectListByPageAndField(Integer pageNo, Integer pageSize, Sqls where, String orderByFiled, String... fileds) {
        return new PageInfo<T>(selectByFileds(pageNo, pageSize, where, orderByFiled, null, fileds));
    }

    /**
     * 根据实体中的属性值进行查询，返回List
     *
     * @param t
     * @return java.util.List<T>
     * @throws
     * @author yay
     * @updateTime 2020/07/10 19:26
     */
    public List<T> selectList(T t) {
        return mapper.select(t);
    }

    /**
     * 带条件分页查询
     * @param t pageNo pageSize
     * @return com.github.pagehelper.PageInfo<T>
     * @throws
     * @author yay
     * @updateTime 2020/07/10 19:39
     */
    public PageInfo<T> selectListByPage(T t, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<T> select = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<T>(select);
        return pageInfo;
    }

    /**
     * Map转换实体类型
     *
     * @param map
     * @return T
     * @throws
     * @author yay
     * @updateTime 2020/07/10 21:39
     */
    public T newInstance(Map map) {
        return (T) Map2BeanUtils.map2Bean(map, getTypeArgument());
    }


    /**
     * 实现查询通用
     * 不但可以作用于分页，还可以作用于排序，还能作用于多条件查询
     *
     * @param pageNo pageSize where orderByFiled(所要排序的字段) orderWord(排序关键字:ASC、DESC) fileds(数据库中的字段)
     * @return java.util.List<T>
     * @throws
     * @author yay
     * @updateTime 2020/07/10 17:00
     */
    public List<T> selectByFileds(Integer pageNo, Integer pageSize, Sqls where, String orderByFiled, String orderWord, String... fileds) {
        Example.Builder builder = null;
        if (null == fileds || fileds.length == 0) {
            //查询所有数据
            builder = Example.builder(getTypeArgument());
        } else {
            //说明需要进行条件查询
            builder = Example.builder(getTypeArgument()).select(fileds);
        }
        if (null != where) {
            //说明有用户自定义的where语句条件
            builder = builder.where(where);
        }
        if (null != orderByFiled) {
            //说明需要对某个字段进行排序
            if (DESC.equals(orderWord.toUpperCase())) {
                //说明需要倒序排列
                builder = builder.orderByDesc(orderByFiled);
            } else if (ASC.equals(orderWord.toUpperCase())) {
                //说明需要正序排列
                builder = builder.orderByAsc(orderByFiled);
            }
        }
        Example example = builder.build();
        //实现分页
        if (null != pageNo && null != pageSize) {
            PageHelper.startPage(pageNo, pageSize);
        }
        return getMapper().selectByExample(example);
    }

    /**
     * 获取子类泛型类型
     *
     * @param
     * @return java.lang.Class<T>
     * @throws
     * @author yay
     * @updateTime 2020/07/10 16:13
     */
    public Class<T> getTypeArgument() {
        if (null == cache) {
            cache = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return cache;
    }

    /**
     * 获取spring容器/获取spring的上下文
     * 在项目开始运行的时候，会去加载spring配置，
     * 如果你们项目需要在项目启动的时候也加载自己的配置文件
     * 在spring的源码中有一个必须要看的方法(init())
     * init()--->就是在项目启动的时候去加载spring的配置
     * 如果你的项目中也需要把某一些配置一开始就托管给spring
     * 需要获取到spring的上下文(ApplicationContext)
     *
     * @param
     * @return org.springframework.context.ApplicationContext
     * @throws
     * @author yay
     * @updateTime 2020/07/11 10:47
     */
    public ApplicationContext getApplicationContext() {
        return SpringContextUtils.getApplicationContext();
    }
}
