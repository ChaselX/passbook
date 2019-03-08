package com.chasel.passbook.service;

import com.chasel.passbook.vo.Feedback;
import com.chasel.passbook.vo.Response;

/**
 * <h1>评论功能: 即用户评论相关功能实现</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 9:45
 */
public interface IFeedbackService {

    /**
     * <h2>创建评论</h2>
     *
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    Response createFeedback(Feedback feedback);

    /**
     * <h2>获取用户评论</h2>
     *
     * @param userId 用户 id
     * @return {@link Response}
     */
    Response getFeedbacks(Long userId);
}
