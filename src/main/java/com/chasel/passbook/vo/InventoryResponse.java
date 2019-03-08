package com.chasel.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <h1>库存请求响应</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/8 11:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 优惠券模板信息
     */
    private List<PassTemplateInfo> passTemplateInfos;
}
