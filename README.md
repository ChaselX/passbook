系统后续待完善内容：

* 查询卡券库存无分页，卡券数据量大的情况下会有问题
* 全局异常捕获待完善
* Response处理需要修改
* 商户token需要动态分配
* redis集群不支持pipelined操作，需要别的手段来解决redis批量写入的问题，参考https://www.cnblogs.com/drwong/p/4825752.html
* 用户获取优惠券时做优惠券扣减的时候不满足数据操作原子性，put进去的值只是在之前获取值的基础上减一，并发下必然存在问题。
* 商户投放的优惠券token放入redis中，redis要做持久化处理防止崩溃后redis无token导致领券失败