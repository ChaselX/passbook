package com.chasel.passbook.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author XieLongzhen
 * @date 2019/3/13 11:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        // redis flushall
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushAll();
            return null;
        });

        assert redisTemplate.opsForValue().get("name") == null;

        redisTemplate.opsForValue().set("name", "imooc");
        assert redisTemplate.opsForValue().get("name").equals("imooc");
    }

}
