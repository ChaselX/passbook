package com.chasel.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.chasel.passbook.component.DistributedLockComponent;
import com.chasel.passbook.component.IDistributedLock;
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

    private final DistributedLockComponent distributedLockComponent;

    @Autowired
    public GainPassTemplateServiceImpl(HbaseTemplate hbaseTemplate, StringRedisTemplate redisTemplate, PassTemplateRowMapper passTemplateRowMapper, DistributedLockComponent distributedLockComponent) {
        this.hbaseTemplate = hbaseTemplate;
        this.redisTemplate = redisTemplate;
        this.passTemplateRowMapper = passTemplateRowMapper;
        this.distributedLockComponent = distributedLockComponent;
    }

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {

        PassTemplate passTemplate;

        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());

        IDistributedLock lock = distributedLockComponent.getRedisLock(passTemplateId);
        if (!lock.acquire()) {
            return Response.failure("Gain PassTemplate Failed, Please Try again later!");
        }

        try {
            passTemplate = hbaseTemplate.get(Constants.PassTemplateTable.TABLE_NAME,
                    passTemplateId,
                    passTemplateRowMapper);

            if (passTemplate.getLimit() <= 1 && passTemplate.getLimit() != -1) {
                log.error("PassTemplate Limit Max: {}",
                        JSON.toJSONString(request.getPassTemplate()));
                return Response.failure("PassTemplate Limit Max!");
            }

            Date cur = new Date();
            if (!(cur.getTime() >= passTemplate.getStart().getTime()
                    && cur.getTime() < passTemplate.getEnd().getTime())) {
                log.error("PassTemplate ValidTime Error: {}",
                        JSON.toJSONString(request.getPassTemplate()));
                return Response.failure("PassTemplate ValidTime Error!");
            }

            // 减去优惠券的 limit
            if (passTemplate.getLimit() != -1) {
                List<Mutation> datas = new ArrayList<>();
                byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
                byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();
                Put put = new Put(Bytes.toBytes(passTemplateId));
                put.addColumn(FAMILY_C, LIMIT,
                        Bytes.toBytes(passTemplate.getLimit() - 1));
                datas.add(put);

                hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, datas);
            }
        } catch (Exception ex) {
            log.error("Gain PassTemplate Error: {}",
                    JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Gain PassTemplate Error!");
        } finally {
            lock.release();
        }

        // 将优惠券保存到用户优惠券表
        if (!addPassForUser((request), passTemplate.getId(), passTemplateId)) {
            // TODO 用户保存优惠券失败的情况下，当前已获取优惠券会被丢弃，若不能丢弃需要设计回滚的方法
            return Response.failure("GainPassTemplate Failure!");
        }

        return Response.success();
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
