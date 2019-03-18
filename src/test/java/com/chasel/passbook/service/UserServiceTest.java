package com.chasel.passbook.service;

import com.chasel.passbook.constant.Constants;
import com.chasel.passbook.mapper.UserRowMapper;
import com.chasel.passbook.vo.Response;
import com.chasel.passbook.vo.User;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

/**
 * <h1>用户服务测试</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/15 16:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private UserRowMapper userRowMapper;

    private static byte[] FAMILY_B = Constants.UserTable.FAMILY_B.getBytes();
    private static byte[] NAME = Constants.UserTable.NAME.getBytes();
    private static byte[] AGE = Constants.UserTable.AGE.getBytes();
    private static byte[] SEX = Constants.UserTable.SEX.getBytes();

    private static byte[] FAMILY_O = Constants.UserTable.FAMILY_O.getBytes();
    private static byte[] PHONE = Constants.UserTable.PHONE.getBytes();
    private static byte[] ADDRESS = Constants.UserTable.ADDRESS.getBytes();

    @Test
    public void testHbaseUserTable() throws Exception {
        System.setProperty("hadoop.home.dir", "D:\\develop\\hadoop-2.7.7");
        User user = new User();
        user.setBaseInfo(new User.BaseInfo("Chasel", 10, "m"));
        user.setOtherInfo(new User.OtherInfo("12345678910", "广东省广州市"));

        Response response = userService.createUser(user);
        user = (User) response.getData();
        log.info("UserId: {}", user.getId());

        User hUser = getUser(user.getId());

        assert hUser != null;
        log.info(hUser.toString());

        deleteUser(hUser.getId());
    }

    private User getUser(Long userId) throws IOException {
        Result result = hbaseTemplate.getConnection()
                .getTable(TableName.valueOf(Constants.UserTable.TABLE_NAME))
                .get(new Get(Bytes.toBytes(userId)));
        User.BaseInfo baseInfo = new User.BaseInfo(
                Bytes.toString(result.getValue(FAMILY_B, NAME)),
                Bytes.toInt(result.getValue(FAMILY_B, AGE)),
                Bytes.toString(result.getValue(FAMILY_B, SEX))
        );

        User.OtherInfo otherInfo = new User.OtherInfo(
                Bytes.toString(result.getValue(FAMILY_O, PHONE)),
                Bytes.toString(result.getValue(FAMILY_O, ADDRESS))
        );
        return new User(Bytes.toLong(result.getRow()), baseInfo, otherInfo);

//        return hbaseTemplate.get(
//                Constants.UserTable.TABLE_NAME,
//                String.valueOf(Bytes.toBytes(userId)),
//                userRowMapper);
    }

    private void deleteUser(Long userId) {
        try {
            Delete delete = new Delete(Bytes.toBytes(userId));
            delete.addColumn(FAMILY_B, NAME);
            delete.addColumn(FAMILY_B, AGE);
            delete.addColumn(FAMILY_B, SEX);
            delete.addColumn(FAMILY_O, PHONE);
            delete.addColumn(FAMILY_O, ADDRESS);

            hbaseTemplate.getConnection()
                    .getTable(TableName.valueOf(Constants.UserTable.TABLE_NAME))
                    .delete(delete);
        } catch (Exception ex) {
            log.info(ex.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void getAllUsers() {
        Scan scan = new Scan();
        scan.setFilter(new SingleColumnValueFilter(
                Constants.UserTable.FAMILY_B.getBytes(),
                Constants.UserTable.NAME.getBytes(),
                CompareFilter.CompareOp.EQUAL,
                "Chasel".getBytes()));

        List<User> userList = hbaseTemplate.find(Constants.UserTable.TABLE_NAME, scan, new UserRowMapper());

        assert null != userList && userList.size() > 0;
        userList.forEach(u -> log.info(u.toString()));
    }
}
