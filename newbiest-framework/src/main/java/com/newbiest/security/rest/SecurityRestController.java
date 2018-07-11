package com.newbiest.security.rest;

import com.newbiest.base.rest.AbstractRestController;
import com.newbiest.msg.security.user.UserRequest;
import com.newbiest.msg.security.user.UserResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * Created by guoxunbo on 2018/7/11.
 */
@RestController
@RequestMapping("/security")
@Slf4j
@Api(value="/security", tags="SecurityService")
public class SecurityRestController extends AbstractRestController {

    @ApiOperation(value = "对用户做操作", notes = "aaa")
    @ApiImplicitParam(name="userRequest", value="userRequest", required = true, dataType = "UserRequest")
    @RequestMapping(value = "/user_request", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public UserResponse executeUser(@RequestBody UserRequest userRequest) throws Exception {
        log(log, userRequest);

        return null;
    }


}
