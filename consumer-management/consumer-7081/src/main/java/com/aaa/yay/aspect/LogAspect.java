package com.aaa.yay.aspect;

import com.aaa.yay.annotation.LoginAnnotation;
import com.aaa.yay.model.LoginLog;
import com.aaa.yay.model.User;
import com.aaa.yay.utils.AddressUtils;
import com.aaa.yay.utils.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.util.DateUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aaa.yay.service.SystemApi;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.aaa.yay.staticproperties.TimeFormatProperties.TIME_FORMAT;

/**
 * @Author yay
 * @Description 日志切面类
 *      此@Slf4j:simple log for java
 * @CreatTime 2020年 07月15日 星期三 19:55:20
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private SystemApi systemApi;

    @Pointcut("@annotation(com.aaa.yay.annotation.LoginAnnotation)")
    public void pointcut(){
    }

    /**
    * 定义环形切面(就是具体来实现业务逻辑的方法)
    * @author yay
    * @param proceedingJoinPoint:封装了目标路径中的所用到的所有参数
    * @updateTime 2020/07/15 21:09
    * @throws
    * @return java.lang.Object
    */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws ClassNotFoundException {
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // 获取Request对象
        HttpServletRequest request = ((ServletRequestAttributes) (Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
        // 1.获取ip地址(最简单的)
        String ipAddr = IPUtils.getIpAddr(request);
        // 2.获取地理位置(最简单的)
        Map<String, Object> addresses = AddressUtils.getAddresses(ipAddr, "UTF-8");

        LoginLog loginLog = new LoginLog();
        assert addresses != null;
        loginLog.setIp(ipAddr)
                .setLocation(addresses.get("province")+"|"+addresses.get("city"))
                .setLoginTime(DateUtil.formatDate(new Date(),TIME_FORMAT));

        // 3.获取Username--->想要获取到username，必须要获取到目标方法的参数值
        Object[] args = proceedingJoinPoint.getArgs();
        User user = (User) args[0];
        loginLog.setUsername(user.getUsername());

        // 4.获取操作的类型以及具体操作的内容(反射)
        // 4.1.获取目标类名(全限定名)
        String tarClassName = proceedingJoinPoint.getTarget().getClass().getName();
        // 4.2.获取目标方法名
        String tarMethodName = proceedingJoinPoint.getSignature().getName();
        // 4.3.获取类对象
        Class<?> tarClass = Class.forName(tarClassName);
        // 4.4.获取目标类中的所有方法
        Method[] methods = tarClass.getMethods();
        String operationType = "";
        String operationName = "";
        for (Method method : methods){
            String methodName = method.getName();
            if (tarMethodName.equals(methodName)){
                // 这个时候虽然已经确定了目标方法没有问题，但是有可能会出现方法的重载
                // 还需要进一步判断
                // 4.5.获取目标方法的参数
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == args.length){
                    operationType = method.getAnnotation(LoginAnnotation.class).operationType();
                    operationName = method.getAnnotation(LoginAnnotation.class).operationName();
                }
            }
        }
        loginLog.setOperationName(operationName).setOperationType(operationType);
        systemApi.addLoginLog(loginLog);
        return result;
    }
}
