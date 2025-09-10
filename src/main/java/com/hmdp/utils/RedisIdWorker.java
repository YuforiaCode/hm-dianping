package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 基于Redis的唯一id生成器
 */
@Component
public class RedisIdWorker {

    /**
     * 开始时间戳(2022年1月1日0时0秒0分的秒数)
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    /**
     * 序列号位数
     */
    private static final long COUNT_BITS = 32;

    /**
     * 生成序列号利用了Redis的increment方法(自增长)，所以要注入StringRedisTemplate
     */
    private StringRedisTemplate stringRedisTemplate;
    //利用构造函数注入，也可以通过@Resource注解注入
    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long nextId(String keyPrefix) {  //返回值是64bit位的数字，对应Java的long类型。参数keyPrefix是业务前缀
        //1.生成时间戳
        LocalDateTime now = LocalDateTime.now();  //当前的时间
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);  //当前的秒数
        long timestamp = nowSecond - BEGIN_TIMESTAMP;  //计算时间戳

        //2.生成序列号
        //2.1.获取当前日期，精确到天(两个好处：避免超过32位的上限，方便做统计)
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //2.2.自增长
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);

        //3.拼接并返回
        //先将时间戳左移32位，把序列号的位置让出来，然后通过与运算将序列号拼接上去
        return timestamp << COUNT_BITS | count;
    }
}
