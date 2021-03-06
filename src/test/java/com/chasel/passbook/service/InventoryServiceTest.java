package com.chasel.passbook.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chasel.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author XieLongzhen
 * @date 2019/3/19 9:39
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class InventoryServiceTest extends AbstractServiceTest {

    @Autowired
    private IInventoryService inventoryService;

    @Test
    public void testGetInventoryInfo() throws Exception {
        Response response = inventoryService.getInventoryInfo(userId);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect));
    }
}
