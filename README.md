系统后续待完善内容：

* 查询卡券库存无分页，卡券数据量大的情况下会有问题
* 全局异常捕获待完善
* Response处理需要修改
* 商户token需要动态分配
* redis集群不支持pipelined操作，需要别的手段来解决redis批量写入的问题，参考https://www.cnblogs.com/drwong/p/4825752.html
* 用户获取优惠券时做优惠券扣减的时候不满足数据操作原子性，put进去的值只是在之前获取值的基础上减一，并发下必然存在问题。
* 商户投放的优惠券token放入redis中，redis要做持久化处理防止崩溃后redis无token导致领券失败
* 现阶段优惠券token重度依赖redis，缓存若崩溃，领优惠券会出错，且恢复需要做本地持久化

1. 上传优惠券 token
    GET: 127.0.0.1:9528/upload
    merchantsId - 53
    PassTemplateId: c71db6f94486866345c12fcbcc86ab26

2. 创建用户 -- 用户 3531053
    POST: 127.0.0.1:9528/passbook/createuser
    {
        "baseInfo": {
            "name": "Chasel1",
            "age": 10,
            "sex": "m"
        },
        "otherInfo": {
            "phone": "12345678910",
            "address": "广东省广州市"
        }
    }

3. 库存信息
    GET: 127.0.0.1:9528/passbook/inventoryinfo?userId=3531053

4. 获取优惠券 -- 获取的是带有 token 的优惠券
    POST: 127.0.0.1:9528/passbook/gainpasstemplate
    {
        "userId": 3531053,
        "passTemplate": {
            "id": 9,
            "title": "test-4890459186",
            "hasToken": true
        }
    }

5. userpassinfo
    GET: 127.0.0.1:9528/passbook/userpassinfo?userId=3531053

6. userusedpassinfo
    GET: 127.0.0.1:9528/passbook/userusedpassinfo?userId=3531053

7. userusepass
    POST: 127.0.0.1:9528/passbook/userusepass
    {
        "userId": 3531053,
        "templateId": "5d29d9921f96f724244065eff06b7d87"
    }

8. 创建评论信息
    POST: 127.0.0.1:9528/passbook/createfeedback
    {
        "userId": 3531053,
        "type": "app",
        "templateId": -1,
        "comment": "测试应用评论"
    }
    {
        "userId": 3531053,
        "type": "pass",
        "templateId": "5d29d9921f96f724244065eff06b7d87",
        "comment": "测试优惠券评论"
    }

9. 查看评论信息
    GET: 127.0.0.1:9528/passbook/getfeedback?userId=3531053