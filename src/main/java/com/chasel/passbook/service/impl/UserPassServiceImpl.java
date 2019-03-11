package com.chasel.passbook.service.impl;

import com.chasel.passbook.constant.Constants;
import com.chasel.passbook.dao.MerchantsDao;
import com.chasel.passbook.entity.Merchants;
import com.chasel.passbook.service.IUserPassService;
import com.chasel.passbook.vo.Pass;
import com.chasel.passbook.vo.PassTemplate;
import com.chasel.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XieLongzhen
 * @date 2019/3/8 17:09
 */
@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {

    private final HbaseTemplate hbaseTemplate;

    private final MerchantsDao merchantsDao;

    @Autowired
    public UserPassServiceImpl(HbaseTemplate hbaseTemplate, MerchantsDao merchantsDao) {
        this.hbaseTemplate = hbaseTemplate;
        this.merchantsDao = merchantsDao;
    }

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return null;
    }

    @Override
    public Response userUsePass(Pass pass) {
        return null;
    }

    /**
     * <h2>通过获取的 Passes 对象构造 Map</h2>
     *
     * @param passes {@link Pass}
     * @return Map {@link PassTemplate}
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes) throws Exception {

        String patterns = "yyyy-MM-dd";

        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);


//        List<String> templateIds = passes.stream().map(Pass::getTemplateId).collect(Collectors.toList());

        List<Get> templateGets = new ArrayList<>(passes.size());
        passes.forEach(t -> templateGets.add(new Get(Bytes.toBytes(t.getTemplateId()))));

        Result[] templateResults = hbaseTemplate.getConnection()
                .getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                .get(templateGets);

        // 构造 PassTemplateId -> PassTemplate Object 的 Map, 用于构造 PassInfo
        Map<String, PassTemplate> templateId2Object = new HashMap<>();
        for (Result item : templateResults) {
            PassTemplate passTemplate = new PassTemplate();

            passTemplate.setId(Bytes.toInt(item.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(item.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(item.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(item.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(item.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(item.getValue(FAMILY_B, BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(item.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(
                    Bytes.toString(item.getValue(FAMILY_C, START)), patterns));
            passTemplate.setEnd(DateUtils.parseDate(
                    Bytes.toString(item.getValue(FAMILY_C, END)), patterns
            ));

            templateId2Object.put(Bytes.toString(item.getRow()), passTemplate);
        }

        return templateId2Object;
    }

    private Map<Integer, Merchants> buildMerchantsMap(List<PassTemplate> passTemplates) {
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        List<Integer> merchantsIds = passTemplates.stream()
                .map(PassTemplate::getId)
                .collect(Collectors.toList());

        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);

        merchants.forEach(m -> merchantsMap.put(m.getId(), m));

        return merchantsMap;
    }

}
