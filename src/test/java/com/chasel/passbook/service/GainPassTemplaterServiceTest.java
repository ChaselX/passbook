package com.chasel.passbook.service;

import com.alibaba.fastjson.JSON;
import com.chasel.passbook.vo.GainPassTemplateRequest;
import com.chasel.passbook.vo.PassTemplate;
import com.chasel.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户领取优惠券功能测试</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/19 10:44
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GainPassTemplaterServiceTest extends AbstractServiceTest {

    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    @Test
    public void testGainPassTemplate() throws Exception {
        PassTemplate target = new PassTemplate();
        target.setId(9);
        target.setTitle("test-4890459186");
        target.setHasToken(true);
        Response response = gainPassTemplateService.gainPassTemplate(new GainPassTemplateRequest(userId, target));
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }

}
