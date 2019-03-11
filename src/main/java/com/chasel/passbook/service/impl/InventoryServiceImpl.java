package com.chasel.passbook.service.impl;

import com.chasel.passbook.constant.Constants;
import com.chasel.passbook.dao.MerchantsDao;
import com.chasel.passbook.mapper.PassTemplateRowMapper;
import com.chasel.passbook.service.IInventoryService;
import com.chasel.passbook.service.IUserPassService;
import com.chasel.passbook.utils.RowKeyGenUtil;
import com.chasel.passbook.vo.PassTemplate;
import com.chasel.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>获取库存信息, 只返回用户没有领取的</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/11 15:40
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {
    private final HbaseTemplate hbaseTemplate;

    private final MerchantsDao merchantsDao;

    private final IUserPassService userPassService;

    @Autowired
    public InventoryServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao, IUserPassService userPassService) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
        this.userPassService = userPassService;
    }

    @Override
    public Response getInventoryInfo(Long userId) throws Exception {
        return null;
    }

    /**
     * <h2>获取系统中可用的优惠券</h2>
     * @param excludeIds 需要排除的优惠券 ids
     * @return {@link PassTemplate}
     * */
    private List<PassTemplate> getAvaliablePassTemplate(List<String> excludeIds) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);

        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.GREATER,
                        new LongComparator(0L)
                )
        );
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.EQUAL,
                         Bytes.toBytes("-1")
                )
        );

        Scan scan = new Scan();
        scan.setFilter(filterList);

        List<PassTemplate> validTemplates = hbaseTemplate.find(
                Constants.PassTemplateTable.TABLE_NAME, scan, new PassTemplateRowMapper());
        List<PassTemplate> avaliablePassTemplates = new ArrayList<>();

        Date cur = new Date();

        for (PassTemplate validTemplate : validTemplates) {
            if (excludeIds.contains(RowKeyGenUtil.genPassTemplateRowKey(validTemplate))) {
                continue;
            }
            if (cur.getTime() >= validTemplate.getStart().getTime() && cur.getTime() <= validTemplate.getEnd().getTime()) {
                avaliablePassTemplates.add(validTemplate);
            }
        }

        return avaliablePassTemplates;
    }
}
