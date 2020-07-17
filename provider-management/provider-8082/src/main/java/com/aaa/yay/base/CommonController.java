package com.aaa.yay.base;

import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author yay
 * @Description 通用Controller
 * @CreatTime 2020年 07月12日 星期日 21:37:44
 */
public abstract class CommonController<T> extends BaseController {

    /**
     * 钩子函数
     * 在新增之前去执行某些操作
     * 下单操作需求:
     * 在购物车中当点击立即下单按钮的时候--->跳转下单页面(选择地址，选择优惠券...)
     * 把购物车中的这个商品删除
     * deleteCart(List<Integer> id);--->是优先于insertOrder前置执行
     * insertOrder(Order oder);
     *
     * @param map
     * @return void
     * @throws
     * @author yay
     * @updateTime 2020/07/12 21:39
     */
    protected void beforeAdd(Map map) {
        // TODO AddMethod Before to do something
    }

    /**
     * 钩子函数
     * 在新增之后去执行
     * int result = insertOrder(Order order)
     * if(result > 0) {
     * insertOrderDetail(OrderDetail orderDetail);
     * }
     *
     * @param map
     * @return void
     * @throws
     * @author yay
     * @updateTime 2020/07/13 9:25
     */
    protected void afterAdd(Map map) {
        // TODO AddMethod Before to do something
    }

    public abstract BaseService<T> getBaseService();

    /**
     * 通用的新增方法
     * 因为咱们目前市面上所有的公司实现的全是异步了,也就是说前端向后端所传递的数据都是json格式
     * 之前在controller的方法中接收固定的实体类,是因为你知道前端给你传递的类型就是这个实体类
     * 但是既然要做通用,前端所传递的类型就不会固定了,所以使用Map类型来统一接收
     *
     * @param map
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/13 10:12
     */
    public ResultData add(@RequestBody Map map) {
        // 因为根据咱们的封装规则，在service中是需要传递泛型的，就意味着service需要接收固定的实体类
        // 但是controller是一个Map类型
        beforeAdd(map);
        // 1.Map转实体类
        T instance = getBaseService().newInstance(map);
        // 2.通用service
        Integer addResult = getBaseService().add(instance);
        if (addResult > 0) {
            afterAdd(map);
            return super.operationSuccess();
        }
        return super.operationFailed();
    }

    /**
     * 通用删除
     *
     * @param map
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/13 10:27
     */
    public ResultData delete(@RequestBody Map map) {
        T instance = getBaseService().newInstance(map);
        Integer deleteResult = getBaseService().delete(instance);
        if (deleteResult > 0) {
            return super.operationSuccess();
        }
        return super.operationFailed();
    }

    /**
     * 通用批量删除
     *
     * @param ids
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/13 10:32
     */
    public ResultData batchDelete(@RequestParam("ids") Integer[] ids) {
        Integer deleteResults = getBaseService().deleteByIds(Arrays.asList(ids));
        if (deleteResults > 0) {
            return super.operationSuccess();
        }
        return super.operationFailed();
    }

    /**
     * 通用新增
     *
     * @param map
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/14 9:44
     */
    public ResultData update(@RequestBody Map map) {
        T t = getBaseService().newInstance(map);
        Integer updateResult = getBaseService().update(t);
        if (updateResult > 0) {
            return operationSuccess();
        }
        return operationFailed();
    }

    /**
     * 查询一条数据
     *
     * @param map
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/14 9:49
     */
    public ResultData getOne(@RequestBody Map map) {
        T t = getBaseService().newInstance(map);
        t = getBaseService().selectOne(t);
        if (null != t) {
            return operationSuccess(t);
        }
        return operationFailed("未查询到结果");
    }

    /**
     * 查询多条结果
     *
     * @param map
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/14 10:00
     */
    public ResultData getList(@RequestBody Map map) {
        T t = getBaseService().newInstance(map);
        List<T> tList = getBaseService().selectList(t);
        if (tList.size() > 0) {
            return operationSuccess(tList);
        }
        return operationFailed("未查询到结果");
    }

    /**
     * 带条件分页查询
     *
     * @param map pageNo pageSize
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/14 10:11
     */
    public ResultData getListByPage(@RequestBody Map map, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        T t = getBaseService().newInstance(map);
        PageInfo<T> pageInfo = getBaseService().selectListByPage(t, pageNo, pageSize);
        List<T> tList = pageInfo.getList();
        if (tList.size() > 0) {
            return operationSuccess(pageInfo);
        }
        return operationFailed("未查询到结果");
    }

    /**
     * 不带条件的分页查询
     *
     * @param pageNo pageSize
     * @return com.aaa.yay.base.ResultData
     * @throws
     * @author yay
     * @updateTime 2020/07/14 10:17
     */
    public ResultData getListByPage(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        PageInfo<T> pageInfo = getBaseService().selectListByPage(null, pageNo, pageSize);
        List<T> tList = pageInfo.getList();
        if (tList.size() > 0) {
            return operationSuccess(pageInfo);
        }
        return operationFailed("未查询到结果");
    }

    /**
     * 从本地当前线程中获取request对象
     *
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @throws
     * @author yay
     * @updateTime 2020/07/14 10:32
     */
    public HttpServletRequest getServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes;
        if (requestAttributes instanceof ServletRequestAttributes) {
            servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    /**
     * 获取当前客户端session对象(如果没有则直接返回null)
     * @param
     * @return javax.servlet.http.HttpSession
     * @throws
     * @author yay
     * @updateTime 2020/07/14 10:39
     */
    public HttpSession getExistSession() {
        return getServletRequest().getSession(false);
    }
}
