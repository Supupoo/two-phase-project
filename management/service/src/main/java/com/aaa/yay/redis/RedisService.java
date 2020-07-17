package com.aaa.yay.redis;

import com.aaa.yay.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.aaa.yay.staticproperties.RedisProperties.*;

/**
 * @Author yay
 * @Description redis的业务实现类
 *       set:存数据-->返回值如果成功就是"OK"
 *       get:取数据
 *       del:删除数据
 *       expire:为数据设置失效时间
 *       如果需要存入redis数据库，首先应该把需要缓存的数据从mysql中查询出来，然后通过java代码存入到redis中
 *       --->从mysql中所查询出来的可能就是对象类型，可能是List...
 * @CreatTime 2020年 07月13日 星期一 17:12:43
 */
@Service
public class RedisService<T> {

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * spring提供的key的序列化器，作用把key进行序列化
     */
    private RedisSerializer keySerializer = null;

    /**
    *
    * @author yay
    * @param key value
     *            nxxx:"nx":如果redis中没有这个key，才会去存，有这个key不再存数据;"xx":redis中有这个key才能存，没有key则不能存
     *            expx:ex:失效时间的单位为秒;px:失效时间的单位为毫秒
     *            time:具体的失效时间
    * @updateTime 2020/07/13 18:20
    * @throws
    * @return java.lang.String
    */
    public String set(String key,T value,String nxxx,String expx,Integer time){
        // 判断是否需要设置失效时间
        boolean b = null != time && 0 < time && (NX.equals(nxxx) || XX.equals(nxxx)) && (EX.equals(expx) || PX.equals(expx));
        if (b){
            //需要设置失效时间
            return jedisCluster.set(key, JsonUtils.toJsonString(value),nxxx,expx,time);
        }else
            // 说明不需要设置失效时间
            // 需要再次判断--->是否nx或者xx
            if (NX.equals(nxxx)){
                return String.valueOf(jedisCluster.setnx(key, JsonUtils.toJsonString(value)));
            }else
                // 不能直接使用else，因为防止客户端传递过来的数据不是xx
                if (XX.equals(nxxx)){
                    return jedisCluster.set(key,JsonUtils.toJsonString(value));
                }
          return NO;
        }

    /**
    * 查询一条数据（String除外）
    * @author yay
    * @param key
    * @updateTime 2020/07/13 18:39
    * @throws
    * @return T
    */
    public T getObject(String key){
        String redisValue = jedisCluster.get(key);
        return (T) JsonUtils.toObject(redisValue,Object.class);
    }

    /**
    * 查询一条数据（Value是String）
    * @author yay
    * @param key
    * @updateTime 2020/07/13 18:46
    * @throws
    * @return java.lang.String
    */
    public String getString(String key){
        return jedisCluster.get(key);
    }

    /**
    * 查询List集合数据
    * @author yay
    * @param key
    * @updateTime 2020/07/13 19:33
    * @throws
    * @return java.util.List<T>
    */
    public List<T> getList(String key){
        return (List<T>)JsonUtils.toList(jedisCluster.get(key),Object.class);
    }

    /**
    * 删除一条redis的数据
    * @author yay
    * @param key
    * @updateTime 2020/07/13 20:38
    * @throws
    * @return java.lang.Long
    */
    public Long delOne(Object key){
        /**
         * 思路:
         *  目前来说架构遇到的问题:
         *      封装redis的时候发现无法实现通用，因为JedisCluster只能接收String类型key值
         *      并不符合架构标准，最终可以把Object对象转换为字节数组来进行处理这个问题
         */
        return jedisCluster.del(object2ByteArray(key));
    }

    /**
    * 批量删除redis的数据
    * @author yay
    * @param keys
    * @updateTime 2020/07/13 20:38
    * @throws
    * @return java.lang.Long
    */
    public  Long delMany(Collection<T> keys){
        // 1.严谨判断集合的长度
        if (CollectionUtils.isEmpty(keys)){
            return 0L;
        }else {
            // 因为JedisCluster中提供了可变长度的参数，所以咱们就可以使用这种模式来进行批量删除
            // 为了实现通用，还得必须转换成字节--->因为可变的，所以最好的方案就是使用二维数组来进行批量删除
            byte[][] keyBytes = this.collection2ByteArray(keys);
            return jedisCluster.del(keyBytes);
        }
    }

    /**
    * 设置通用失效时间
    * @author yay
    * @param key expx time
    * @updateTime 2020/07/13 20:41
    * @throws
    * @return java.lang.Long
    */
    public Long expire(String key,String expx,Integer time){
        if (null == time || 0 == time){
            return 0L;
        }
        if (EX.equals(expx)){
            return jedisCluster.expire(key,time);
        }else if (PX.equals(expx)){
            return jedisCluster.pexpire(key,time);
        }
        return 0L;
    }


    /**
    * 把Object对象类型转换为字节数组
    * @author yay
    * @param key
    * @updateTime 2020/07/13 20:06
    * @throws
    * @return byte[]
    */
    public byte[] object2ByteArray(Object key){

        // spring工具类--->如果为null直接抛异常，如果不为null直接往下走
        Assert.notNull(key,"this key is required, you can't send null!");
        // 因为要转换字节数组，需要进行把对象序列化(让实体类必须要实现序列化接口的原因)
        return this.keySerializer == null && key instanceof byte[] ? (byte[]) key : this.keySerializer.serialize(key);
    }

    /**
    * 把集合转换为二维字节数组
    * @author yay
    * @param keys
    * @updateTime 2020/07/13 20:26
    * @throws
    * @return byte[][]
    */
    public byte[][] collection2ByteArray(Collection<T> keys){
        // 定义一个长度为集合长度的二维数组
        byte[][] bytes = new byte[keys.size()][];
        // 初始化二维数组的下标，来进行存储数据用
        int i = 0;
        // 因为keys是一个集合，泛型对象是Object--->所以需要循环这个集合，把集合中的所有元素都要序列化
        Object key;
        // 使用迭代器去循环keys的集合(因为Collection不一定是List，有可能是Map)
        for (Iterator var4 = keys.iterator(); var4.hasNext(); bytes[i++] = object2ByteArray(key)){
            key = var4.next();
        }
        return bytes;
    }

    /**
    * PostConstruct注解用于需要依赖注入完成后执行初始化的方法，必须在类投入使用之前调用此方法。
    * @author yay
    * @param
    * @updateTime 2020/07/13 20:28
    * @throws
    * @return void
    */
    @PostConstruct
    public void initRedisSerializer(){
        if (this.keySerializer == null){
            this.keySerializer = new JdkSerializationRedisSerializer(this.getClass().getClassLoader());
        }
    }
}
