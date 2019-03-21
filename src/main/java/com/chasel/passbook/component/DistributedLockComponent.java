package com.chasel.passbook.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 分布式锁工具类
 *
 * @author XieLongzhen
 * @date 2019/3/21 10:36
 */
@Component
public class DistributedLockComponent {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 锁等待, 防止线程饥饿
     */
    private final int DEFAULT_TIMEOUT_MSECS = 15 * 1000;
    /**
     * 锁超时, 防止线程得到锁之后, 不去释放锁
     */
    private final int DEFAULT_EXPIRE_MSECS = 15 * 1000;

    /**
     * 获取分布式锁
     * 默认获取锁15s超时, 锁过期时间15s
     */
    public IDistributedLock getRedisLock(String key) {
        return getRedisLock(key, DEFAULT_TIMEOUT_MSECS, DEFAULT_EXPIRE_MSECS);
    }

    /**
     * 获取分布式锁
     */
    public IDistributedLock getRedisLock(String key, int timeoutMsecs) {
        return getRedisLock(key, timeoutMsecs, DEFAULT_EXPIRE_MSECS);
    }

    /**
     * 获取分布式锁
     */
    public IDistributedLock getRedisLock(String key, int timeoutMsecs, int expireMsecs) {
        return new RedisDistributedLock(assembleKey(key), timeoutMsecs, expireMsecs, redisTemplate);
    }

    /**
     * 对 lockKey 进行拼接装配
     *
     * @param key 系统内保证该lockKey唯一即可
     */
    private static String assembleKey(String key) {
        return String.format("passbook:distributed-lock:%s", key);
    }
}
