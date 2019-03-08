package com.chasel.passbook.utils;

import com.chasel.passbook.vo.Feedback;
import com.chasel.passbook.vo.GainPassTemplateRequest;
import com.chasel.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * <h1>RowKey 生成器工具类</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/7 15:28
 */
@Slf4j
public class RowKeyGenUtil {

    /**
     * <h2>根据提供的 PassTemplate 对象生成 RowKey</h2>
     *
     * @param passTemplate {@link PassTemplate}
     * @return String RowKey
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate) {

        String passInfo = String.valueOf(passTemplate.getId()) + "_" + passTemplate.getTitle();
        // 使用md5加密做rowKey的原因，HBase集群的数据是基于rowKey做存储，相近的值会存储在一起
        // 因此需要保证rowKey尽可能的分散使数据不会集中于一个节点上，提高查询效率
        String rowKey = DigestUtils.md5Hex(passInfo);
        log.info("GenPassTemplateRowKey: {}, {}", passInfo, rowKey);

        return rowKey;
    }

    /**
     * <h2>根据提供的领取优惠券请求生成 RowKey, 只可以在领取优惠券的时候使用</h2>
     * Pass RowKey = reversed(userId) + inverse(timestamp) + PassTemplate RowKey
     *
     * @param request {@link GainPassTemplateRequest}
     * @return String RowKey
     */
    public static String genPassRowKey(GainPassTemplateRequest request) {

        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis())
                + genPassTemplateRowKey(request.getPassTemplate());
    }

    /**
     * <h2>根据 Feedback 构造 RowKey</h2>
     *
     * @param feedback {@link Feedback}
     * @return String RowKey
     */
    public static String genFeedbackRowKey(Feedback feedback) {
        return new StringBuilder(String.valueOf(feedback.getUserId()))
                .reverse()
                .append(Long.MAX_VALUE - System.currentTimeMillis())
                .toString();
    }

}
