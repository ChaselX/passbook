package com.chasel.passbook.vo;

import com.chasel.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>用户领取的优惠券信息</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 13:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassInfo {

    /**
     * 优惠券
     */
    private Pass pass;

    /**
     * 优惠券模板
     */
    private PassTemplate passTemplate;

    /**
     * 优惠券对应的商户
     */
    private Merchants merchants;
}

