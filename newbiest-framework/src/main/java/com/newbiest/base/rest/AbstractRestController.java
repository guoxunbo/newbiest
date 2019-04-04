package com.newbiest.base.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.JwtSigner;
import com.newbiest.msg.DefaultParser;
import com.newbiest.msg.Request;
import com.newbiest.msg.RequestHeader;
import com.newbiest.security.model.NBOrg;
import com.newbiest.security.service.SecurityService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2018/7/11.
 */
@Component
public class AbstractRestController implements Serializable{

    private static final long serialVersionUID = 5792313127796577694L;

    protected static final String AUTHORITY_HEAD_NAME = "Authorization";

    @Autowired
    protected BaseService baseService;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected VersionControlService versionControlService;

    @Autowired
    private JwtSigner jwtSigner;

    private final String requestToJson(Request request) throws Exception {
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

    /**
     * TODO 验证是否登录
     */
    public void validationLogin(Request request) {
        RequestHeader requestHeader = request.getHeader();

    }

    protected SessionContext getSessionContext(Request request) throws ClientException {
        SessionContext sc = new SessionContext();
        NBOrg nbOrg = null;
        Long orgRrn = request.getHeader().getOrgRrn();
        String orgName = request.getHeader().getOrgName();
        if (orgRrn != null) {
            nbOrg = baseService.findOrgByObjectRrn(request.getHeader().getOrgRrn());
        } else if (!StringUtils.isNullOrEmpty(orgName)) {
            nbOrg = baseService.findOrgByName(orgName);
        }
        if (nbOrg == null) {
            throw new ClientParameterException(NewbiestException.COMMON_ORG_IS_NOT_EXIST, orgRrn != null ? orgRrn : orgName);
        }
        sc.setOrgRrn(nbOrg.getObjectRrn());
        sc.setOrgName(nbOrg.getName());
        sc.setUsername(request.getHeader().getUsername());
        return sc;
    }

    protected NBBase saveEntity(NBBase nbBase, SessionContext sc) throws ClientException {
        if (nbBase instanceof NBVersionControl) {
            return versionControlService.save((NBVersionControl) nbBase, sc);
        }
        return baseService.saveEntity(nbBase, sc);
    }

    protected void deleteEntity(NBBase nbBase, boolean deleteRelationFlag, SessionContext sc) throws ClientException {
        baseService.delete(nbBase, deleteRelationFlag, sc);
    }

    protected void deleteEntity(NBBase nbBase, SessionContext sc) throws ClientException {
        baseService.delete(nbBase, sc);
    }

    /**
     * 查找Entity 默认带出所有的懒加载对象
     * @param nbBase
     * @return
     */
    protected NBBase findEntity(NBBase nbBase) {
        return baseService.findEntity(nbBase, true);
    }

    /**
     * 验证当前对象是不是最新的对象
     * @param nbUpdatable
     * @throws ClientException
     */
    protected void validateEntity(NBUpdatable nbUpdatable) throws ClientException {
        NBUpdatable oldBase = (NBUpdatable) baseService.findEntity(nbUpdatable, false);
        if (!oldBase.getLockVersion().equals(nbUpdatable.getLockVersion())) {
            throw new ClientParameterException(NewbiestException.COMMON_ENTITY_IS_NOT_NEWEST, oldBase.getClass().getName(), oldBase.getLockVersion());
        }
    }

    /**
     * 更新实体。但不会更新实体上对应的关联数据。
     * @param nbBase
     * @param sc
     * @return
     * @throws ClientException
     */
    protected NBBase updateEntity(NBBase nbBase, SessionContext sc) throws ClientException {
        if (nbBase instanceof NBUpdatable) {
            validateEntity((NBUpdatable) nbBase);
        }
        // 有关联关系的时候，不update相应的关联关系
        List<String> relationFiledNameList = Lists.newArrayList();
        List<Field> fields = Lists.newArrayList();
        fields.addAll(Arrays.asList(nbBase.getClass().getDeclaredFields()));
        // 只获取一层父类上的多属性
        if (nbBase.getClass().getSuperclass() != null) {
            fields.addAll(Arrays.asList(nbBase.getClass().getSuperclass().getDeclaredFields()));
        }
        if (CollectionUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                // 只有是集合类型并且没有Transient注解才把已有数据的值放到更新对象中。保证关联关系不会被更新
                if (List.class.isAssignableFrom(field.getType()) && field.getAnnotation(Transient.class) == null) {
                    relationFiledNameList.add(field.getName());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(relationFiledNameList)) {
            // 有关联关系的时候，不update相应的关联关系
            NBBase oldBase = baseService.findEntity(nbBase, true);
            for (String fieldName : relationFiledNameList) {
                PropertyUtils.setProperty(nbBase, fieldName, PropertyUtils.getProperty(oldBase, fieldName));
            }
        }
        return saveEntity(nbBase, sc);
    }
}
