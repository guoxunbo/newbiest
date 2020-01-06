package com.newbiest.kms.service.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.kms.service.SeeyaService;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by guoxunbo on 2020-01-06 16:40
 */
@Service
@Transactional
@Slf4j
public class SeeyaServiceImpl implements SeeyaService {

    @Autowired
    BaseService baseService;

    @Autowired
    SecurityService securityService;

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    EntityManager em;

    public static final String QUERY_NAME_GET_MES_USER = "GetMesUser";

    public void asyncMesUser() {
        List<Map> mesUserMap = findEntityMapListByQueryName(QUERY_NAME_GET_MES_USER, Maps.newHashMap(), 0, StringUtils.EMPTY, StringUtils.EMPTY);
        if (CollectionUtils.isNotEmpty(mesUserMap)) {
            for (Map mesUser : mesUserMap) {
                String userId = (String) mesUser.get("USERID");
                String username = (String) mesUser.get("USERNAME");
                String password = (String) mesUser.get("PASSWORD");
                String shiftId = (String) mesUser.get("SHIFTID");
                String email = (String) mesUser.get("EMAIL");
                String phone = (String) mesUser.get("PHONE");
                NBUser nbUser = securityService.getUserByUsername(userId);
                if (nbUser == null) {
                    nbUser = new NBUser();
                    nbUser.setUsername(userId);
                    nbUser.setPassword(password);
                    nbUser.setDescription(username);
                    nbUser.setDepartment(shiftId);
                    nbUser.setPhone(phone);
                    nbUser.setEmail(email);
                    securityService.saveUser(nbUser);
                }
            }
        }


    }

    public List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, String whereClause, String orderByClause) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.findByName(queryName);
            if (nbQuery == null) {
                throw new ClientParameterException(NewbiestException.COMMON_QUERY_IS_NOT_EXIST, queryName);
            }
            return findEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, String whereClause, String orderByClause) throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT * FROM (");
            sqlBuffer.append(queryText);
            sqlBuffer.append(")");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sqlBuffer.append(" WHERE ");
                sqlBuffer.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderByClause)) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(orderByClause);
            }

            Query query = em.createNativeQuery(sqlBuffer.toString());
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
            }

            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    query.setParameter(key, paramMap.get(key));
                }
            }
            query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
