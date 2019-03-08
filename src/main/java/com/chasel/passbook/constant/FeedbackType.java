package com.chasel.passbook.constant;

/**
 * <h1>评论类型枚举</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/6 14:57
 */
public enum FeedbackType {

    PASS("pass", "针对优惠券的评论"),

    APP("app", "针对卡包 App 的评论");

    /**
     * 评论类型编码
     */
    private String code;

    /**
     * 评论类型描述
     */
    private String desc;

    FeedbackType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
