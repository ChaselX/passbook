package com.chasel.passbook.controller;

import com.chasel.passbook.log.LogConstants;
import com.chasel.passbook.log.LogGenerator;
import com.chasel.passbook.service.IUserService;
import com.chasel.passbook.vo.Response;
import com.chasel.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>创建用户服务</h1>
 *
 * @author XieLongzhen
 * @date 2019/3/13 10:06
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {

    private final IUserService userService;

    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CreateUserController(IUserService userService,
                                HttpServletRequest httpServletRequest) {
        this.userService = userService;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * <h2>创建用户</h2>
     *
     * @param user {@link User}
     * @return {@link Response}
     */
    @ResponseBody
    @PostMapping("/createuser")
    Response createUser(@RequestBody User user) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                -1L,
                LogConstants.ActionName.CREATE_USER,
                user
        );
        return userService.createUser(user);

    }
}
