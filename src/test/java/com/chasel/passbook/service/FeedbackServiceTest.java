package com.chasel.passbook.service;

import com.alibaba.fastjson.JSON;
import com.chasel.passbook.constant.FeedbackType;
import com.chasel.passbook.vo.Feedback;
import com.chasel.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1>用户反馈服务测试</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/19 18:53
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest {

    @Autowired
    private IFeedbackService feedbackService;

    @Test
    public void testCreateFeedback() {

        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.getCode());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("测试应用评论");

        Response response = feedbackService.createFeedback(appFeedback);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));


        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("5d29d9921f96f724244065eff06b7d87");
        passFeedback.setComment("优惠券评论");

        response = feedbackService.createFeedback(passFeedback);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testGetFeedback() {
        Response response = feedbackService.getFeedback(userId);
        assert response.getErrorCode() == 0;
        log.info(JSON.toJSONString(response));
    }
}
