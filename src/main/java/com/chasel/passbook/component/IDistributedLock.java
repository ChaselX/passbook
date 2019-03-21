package com.chasel.passbook.component;

import org.springframework.stereotype.Component;

/**
 * @author XieLongzhen
 * @date 2019/3/21 9:51
 */
public interface IDistributedLock {
    /**
     * <h2>获取锁</h2>
     * */
    boolean acquire();
    /**
     * <h2>释放锁</h2>
     * */
    void release();
}
