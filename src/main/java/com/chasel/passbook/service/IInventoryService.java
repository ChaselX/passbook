package com.chasel.passbook.service;

import com.chasel.passbook.vo.Response;

/**
 * <h1>获取库存信息: 只返回用户没有领取的, 即优惠券库存功能实现接口定义</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 13:57
 */
public interface IInventoryService {

    /**
     * <h2>获取库存信息</h2>
     *
     * @param userId 用户 id
     * @return {@link Response}
     */
    Response getInventoryInfo(Long userId) throws Exception;
}
