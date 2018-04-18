package com.newbiest.base.rest;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.msg.Response;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guoxunbo on 2017/12/28.
 */
@RestController
@RequestMapping("/test")
public class TestExceptionService {

    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public void execute(@RequestParam("request") String requestJson) throws Exception{
        try {
            throw new ClientParameterException("aaaaaa%s", "1111");
        } catch (ClientException e) {
            throw ExceptionManager.handleException(e);
        }
    }

}
