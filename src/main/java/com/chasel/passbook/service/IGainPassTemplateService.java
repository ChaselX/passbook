package com.chasel.passbook.service;

import com.chasel.passbook.vo.GainPassTemplateRequest;
import com.chasel.passbook.vo.Response;

/**
 * <h1>用户领取优惠券功能实现</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 13:58
 */
public interface IGainPassTemplateService {

    /**
     * <h2>用户领取优惠券</h2>
     *
     * @param request {@link GainPassTemplateRequest}
     * @return {@link Response}
     */
    Response gainPassTemplate(GainPassTemplateRequest request) throws Exception;
}
