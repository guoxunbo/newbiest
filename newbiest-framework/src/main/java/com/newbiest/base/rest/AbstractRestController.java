package com.newbiest.base.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.Request;
import com.newbiest.msg.trans.TransContext;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by guoxunbo on 2018/7/11.
 */
@Slf4j
public class AbstractRestController {

    @Autowired
    BaseService baseService;

    private final String requestToJson(Request request) throws Exception{
        ObjectMapper objectMapper = DefaultParser.getObjectMapper();
        ObjectWriter jsonWriter = objectMapper.writerWithView(request.getClass());
        return jsonWriter.writeValueAsString(request);
    }

    public final void log(Logger logger, Request request) throws Exception{
        if (logger.isDebugEnabled()) {
            String requestJson = requestToJson(request);
            logger.debug(requestJson);
        }
        if (!logger.isDebugEnabled() && logger.isInfoEnabled()) {
            logger.info(request.getHeader().getTransactionId());
        }
    }

    public SessionContext getSessionContext(Request request) throws ClientException {
        SessionContext sc = new SessionContext();
        NBOrg nbOrg = null;
        Long orgRrn = request.getHeader().getOrgRrn();
        String orgName = request.getHeader().getOrgName();
        if (orgRrn != null) {
            nbOrg = baseService.getOrgByObjectRrn(request.getHeader().getOrgRrn());
        } else if (!StringUtils.isNullOrEmpty(orgName)) {
            nbOrg = baseService.getOrgByName(orgName);
        }
        if (nbOrg == null) {
            throw new ClientParameterException(NewbiestException.COMMON_ORG_IS_NOT_EXIST, orgRrn != null ? orgRrn : orgName);
        }
        sc.setOrgRrn(nbOrg.getObjectRrn());
        sc.setOrgName(nbOrg.getName());
        sc.setUsername(request.getHeader().getUsername());
        return sc;
    }

}
