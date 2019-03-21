package com.chasel.passbook.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 锁超时时间约等于expireMsecs的分布式锁解决方案
 *
 * @author XieLongzhen
 * @date 2019/3/21 10:31
 */
@Slf4j
public class RedisDistributedLock implements IDistributedLock {

    private StringRedisTemplate redisTemplate;

    /**
     * 锁的键值
     */
    private String lockKey;
    /**
     * 锁超时, 防止线程得到锁之后, 不去释放锁
     */
    private int expireMsecs;
    /**
     * 锁等待, 防止线程饥饿
     */
    private int timeoutMsecs;
    /**
     * 是否已经获取锁
     */
    private boolean locked = false;

    public RedisDistributedLock(String lockKey, int timeoutMsecs, int expireMsecs, StringRedisTemplate redisTemplate) {
        this.lockKey = lockKey;
        this.timeoutMsecs = timeoutMsecs;
        this.expireMsecs = expireMsecs;
        this.redisTemplate = redisTemplate;
    }

    public String getLockKey() {
        return this.lockKey;
    }

    /**
     * 方法去掉了synchronized关键字
     */
    @Override
    public boolean acquire() {

        int timeout = timeoutMsecs;

//        改为构造时传入StringRedisTemplate
//        if (redisTemplate == null) {
//            redisTemplate = SpringContextComponent.getBean(StringRedisTemplate.class);
//        }

        try {

            while (timeout >= 0) {

                long expires = System.currentTimeMillis() + expireMsecs + 1;
                String expiresStr = String.valueOf(expires); // 锁到期时间

                if (redisTemplate.opsForValue().setIfAbsent(lockKey, expiresStr)) {
                    locked = true;
                    log.info("[1] 成功获取分布式锁!");
                    return true;
                }
                String currentValueStr = redisTemplate.opsForValue().get(lockKey); // redis里的时间

                // 判断是否为空, redis旧锁是否已经过期, 如果被其他线程设置了值, 则第二个条件判断是过不去的
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {

                    String oldValueStr = redisTemplate.opsForValue().getAndSet(lockKey, expiresStr);

                    // 获取上一个锁到期时间, 并设置现在的锁到期时间
                    // 如果这个时候, 多个线程恰好都到了这里
                    // 只有一个线程拿到的过期时间是小于当前时间的，后续的线程set进去过期时间但拿到的过期时间会大于当前时间
                    // 只有一个线程的设置值和当前值相同, 那么它才有权利获取锁，其余线程继续等待
                    if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                        locked = true;
                        log.info("[2] 成功获取分布式锁!");
                        return true;
                    }
                }

                timeout -= 100;
                Thread.sleep(100);
            }
        } catch (Exception e) {
            log.error("获取锁出现异常, 必须释放: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 方法去掉了synchronized关键字
     */
    @Override
    public void release() {

//        改为构造时传入StringRedisTemplate
//        if (redisTemplate == null) {
//            redisTemplate = SpringContextComponent.getBean(StringRedisTemplate.class);
//        }

        try {
            if (locked) {

                String currentValueStr = redisTemplate.opsForValue().get(lockKey); // redis里的时间

                // 校验是否超过有效期, 如果不在有效期内, 那说明当前锁已经失效, 不能进行删除锁操作
                if (currentValueStr != null && Long.parseLong(currentValueStr) > System.currentTimeMillis()) {
                    redisTemplate.delete(lockKey);
                    locked = false;
                    log.info("[3] 成功释放分布式锁!");
                }
            }
        } catch (Exception e) {
            log.error("释放锁出现异常, 必须释放: {}", e.getMessage());
        }
    }
}
