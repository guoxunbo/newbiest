package com.newbiest.base.service;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBOrg;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by guoxunbo on 2018/6/6.
 */
public interface BaseService {

    NBOrg findOrgByName(String name) throws ClientException;
    NBOrg findOrgByObjectRrn(Long objectRrn) throws ClientException;

    List<? extends NBBase> saveEntity(List<? extends NBBase> nbBaseList, SessionContext sc) throws ClientException;
    NBBase saveEntity(NBBase nbBase, SessionContext sc) throws ClientException;
    void delete(NBBase nbBase, SessionContext sc) throws ClientException;
    void delete(NBBase nbBase, boolean deleleRelationFlag, SessionContext sc) throws ClientException;
    NBBase findEntity(NBBase nbBase, boolean deepFlag) throws ClientException;

    List<? extends NBBase> findAll(String fullClassName, long orgRrn) throws ClientException;
    List<? extends NBBase> findAll(String fullClassName, String whereClause, String orderBy, long orgRrn) throws ClientException;
    List<? extends NBBase> findAll(String fullClassName, int firstResult, int maxResult, String whereClause, String orderBy, long orgRrn) throws ClientException;
    List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause, SessionContext sc) throws ClientException;
    List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;

}
