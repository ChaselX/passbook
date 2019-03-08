package com.chasel.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户领取优惠券的请求对象</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 13:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GainPassTemplateRequest {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * PassTemplate 对象
     */
    private PassTemplate passTemplate;
}
