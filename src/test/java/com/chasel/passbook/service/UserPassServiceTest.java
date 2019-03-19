package com.chasel.passbook.service;

import com.alibaba.fastjson.JSON;
import com.chasel.passbook.vo.Pass;
import com.chasel.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户优惠券服务测试</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/19 13:39
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest {

    @Autowired
    private IUserPassService userPassService;

    //    {
//        "data": [
//        {
//            "merchants": {
//            "address": "GZ市",
//                    "businessLicenseUrl": "www.chasel.com",
//                    "id": 9,
//                    "isAudit": true,
//                    "logoUrl": "www.chasel.com",
//                    "name": "Chasel",
//                    "phone": "12345678910"
//        },
//            "pass": {
//            "assignedDate": 1547827200000,
//                    "rowKey": "330787292233704838858807475d29d9921f96f724244065eff06b7d87",
//                    "templateId": "5d29d9921f96f724244065eff06b7d87",
//                    "token": "token-4",
//                    "userId": 2787033
//        },
//            "passTemplate": {
//            "background": 2,
//                    "desc": "desc test",
//                    "end": 1553788800000,
//                    "hasToken": true,
//                    "id": 9,
//                    "limit": 9999,
//                    "start": 1552060800000,
//                    "summary": "summary test",
//                    "title": "test-4890459186"
//        }
//        }
//    ],
//        "errorCode": 0,
//            "errorMsg": ""
//    }
    @Test
    public void testGetUserPassInfo() throws Exception {
        Response response = userPassService.getUserPassInfo(userId);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testGetUserUsedPassInfo() throws Exception {
        Response response = userPassService.getUserUsedPassInfo(userId);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testGetUserAllPassInfo() throws Exception {
        Response response = userPassService.getUserAllPassInfo(userId);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testUserUsePass() {
        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("5d29d9921f96f724244065eff06b7d87");

        Response response = userPassService.userUsePass(pass);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }
}
