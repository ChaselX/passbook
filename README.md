系统后续待完善内容：

* 查询卡券库存无分页，卡券数据量大的情况下会有问题
* 全局异常捕获待完善
* Response处理需要修改
* 商户token需要动态分配
* redis集群不支持pipelined操作，需要别的手段来解决redis批量写入的问题，参考https://www.cnblogs.com/drwong/p/4825752.html