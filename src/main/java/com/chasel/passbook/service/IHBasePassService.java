package com.chasel.passbook.service;

import com.chasel.passbook.vo.PassTemplate;

/**
 * <h1>Pass Hbase 服务</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/7 16:27
 */
public interface IHBasePassService {

    /**
     * <h2>将 PassTemplate 写入 HBase</h2>
     * @param passTemplate {@link PassTemplate}
     * @return true/false
     * */
    boolean dropPassTemplateToHBase(PassTemplate passTemplate);
}
