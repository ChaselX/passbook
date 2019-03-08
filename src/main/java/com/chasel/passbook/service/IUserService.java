package com.chasel.passbook.service;

import com.chasel.passbook.vo.Response;
import com.chasel.passbook.vo.User;

/**
 * <h1>用户服务: 创建 User 服务</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 9:14
 */
public interface IUserService {
    /**
     * <h2>创建用户</h2>
     *
     * @param user {@link User}
     * @return {@link Response}
     */
    Response createUser(User user) throws Exception;
}
