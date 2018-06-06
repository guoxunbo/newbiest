package com.newbiest.base.rest;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.TransHandlerFactory;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.msg.DefaultRequestParser;
import com.newbiest.msg.DefaultResponse;
import com.newbiest.msg.DefaultResponseParser;
import com.newbiest.msg.Request;
import com.newbiest.msg.trans.ITransHandler;
import com.newbiest.msg.trans.TransContext;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.security.service.SecurityService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 所有公共消息的入口
 * Created by guoxunbo on 2017/9/11.
 */
@RestController
@RequestMapping("/framework")
@Slf4j
public class FrameworkService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BaseDao nbManager;

    @ApiOperation("DoSomethingInTheFramework")
    @ApiImplicitParam(name = "request", value = "The String in json format!(The Json must extend Request Class)", required = true, dataType = "String")
    @ApiResponse(code = 400, message = "Parameter Error")
    @RequestMapping(value = "/execute/{messageName}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String execute(@RequestParam("request") String requestJson, @PathVariable("messageName") String messageName) throws Exception{
        return execute(requestJson);
    }

    @ApiOperation("DoSomethingInTheFramework")
    @ApiImplicitParam(name = "request", value = "The str is json format!(The Json must extend Request Class)", required = true, dataType = "String")
    @ApiResponse(code = 400, message = "Parameter Error")
    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String execute(@RequestParam("request") String requestJson) throws Exception{
        String transactionId = "";
        if (log.isDebugEnabled()) {
            log.debug(requestJson);
        }
        try {
            Request request = DefaultRequestParser.readerJson(requestJson);
            transactionId = request.getHeader().getTransactionId();
            if (!log.isDebugEnabled() && log.isInfoEnabled()) {
                log.info("Request message transactionID[" + transactionId + "]");
            }
            ITransHandler handler = TransHandlerFactory.getTransHandler(request.getHeader().getMessageName());
            if (handler != null) {
                TransContext context = new TransContext();
                context.setNbManager(nbManager);
                context.setUserRepository(userRepository);
                context.setRoleRepository(roleRepository);
                context.setSecurityService(securityService);

                context.setRequest(requestJson);
                String response = handler.execute(context);
                if (log.isDebugEnabled()) {
                    log.debug(response);
                } else if (log.isInfoEnabled()) {
                    log.info("Response message transactionID [" + transactionId + "]");
                }
                return response;
            } else {
                DefaultResponse response = new DefaultResponse();
                response.setTransactionId(transactionId);
                response = response.buildFailResponse(NewbiestException.COMMON_HANDLER_IS_NOT_FOUND);

                String responseStr = DefaultResponseParser.writerJson(response);
                if (log.isDebugEnabled()) {
                    log.debug(responseStr);
                } else if (log.isInfoEnabled()) {
                    log.info("Response message transactionID[" + transactionId + "], " +
                            "resultCode[" + response.getHeader().getResultCode() + "]" +
                            "resultMessage[" + response.getHeader().getResultEnglish() + "]");
                }
                return responseStr;
            }
        } catch (Exception e) {
            if(e instanceof ClientException) {
                ((ClientException) e).setTransactionId(transactionId);
            }
            throw e;
        }
    }

}
