package com.aaa.yay.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yay
 * @description 自定义Spring上下文
 * @creatTime 2020年 07月11日 星期六 10:27:39
 */
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext APPLICATION_CONTEXT = null;
    private static final ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

    private SpringContextUtils() {
    }

    /**
     * 自定义Spring上下文
     *
     * @param applicationContext
     * @return void
     * @throws
     * @author yay
     * @updateTime 2020/07/11 10:45
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        /**
         * 虽然已经把自己写的spring上下文代替了spring自带的
         * 当spring开始运行加载的时候，仍然会去把spring配置文件覆盖自定义编写的
         * 所以需要加锁
         */
        SpringContextUtils.APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 加锁,解锁
     *
     * @param
     * @return org.springframework.context.ApplicationContext
     * @throws
     * @author yay
     * @updateTime 2020/07/11 10:45
     */
    public static ApplicationContext getApplicationContext() {
        Lock lock = READ_WRITE_LOCK.readLock();
        lock.lock();
        try {
            if (null != APPLICATION_CONTEXT) {
                return APPLICATION_CONTEXT;
            } else {
                return null;
            }
        } finally {
            lock.unlock();
        }
    }
}
