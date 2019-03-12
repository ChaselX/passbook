package com.chasel.passbook.service.impl;

import com.chasel.passbook.constant.Constants;
import com.chasel.passbook.mapper.PassTemplateRowMapper;
import com.chasel.passbook.service.IGainPassTemplateService;
import com.chasel.passbook.utils.RowKeyGenUtil;
import com.chasel.passbook.vo.GainPassTemplateRequest;
import com.chasel.passbook.vo.PassTemplate;
import com.chasel.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author XieLongzhen
 * @date 2019/3/12 11:32
 */
@Slf4j
@Service
public class GainPassTemplateServiceImpl implements IGainPassTemplateService {

    private final HbaseTemplate hbaseTemplate;

    private final StringRedisTemplate redisTemplate;

    private final PassTemplateRowMapper passTemplateRowMapper;

    @Autowired
    public GainPassTemplateServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate, PassTemplateRowMapper passTemplateRowMapper) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
        this.passTemplateRowMapper = passTemplateRowMapper;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {

//        PassTemplate passTemplate;
//
//        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());
//
//        try {
//            passTemplate = hbaseTemplate.get(Constants.PassTemplateTable.TABLE_NAME,
//                    passTemplateId,
//                    passTemplateRowMapper);
//        }
        return null;
    }

    /**
     * <h2>给用户添加优惠券</h2>
     *
     * @param request        {@link GainPassTemplateRequest}
     * @param merchantsId    商户 id
     * @param passTemplateId 优惠券 id
     * @return true/false
     */
    private boolean addPassForUser(GainPassTemplateRequest request,
                                   Integer merchantsId, String passTemplateId) throws Exception {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genPassRowKey(request)));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        if (request.getPassTemplate().getHasToken()) {
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if (null == token) {
                log.error("Token not exist: {}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantsId, passTemplateId, token);
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
        } else {
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));
        }

        put.addColumn(FAMILY_I, ASSIGNED_DATE, Bytes.toBytes(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date())));
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes("-1"));

        datas.add(put);

        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);

        return true;
    }

    /**
     * <h2>将已使用的 token 记录到文件中</h2>
     *
     * @param merchantsId    商户 id
     * @param passTemplateId 优惠券 id
     * @param token          分配的优惠券 token
     */
    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token)
            throws Exception {
        Files.write(
                Paths.get(Constants.TOKEN_DIR, String.valueOf(merchantsId),
                        passTemplateId + Constants.USED_TOKEN_SUFFIX),
                (token + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
        );
    }
}
