package com.newbiest.base.dao;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.SessionContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 基本框架提供的查询，保存等方法
 * Created by guoxunbo on 2017/10/11.
 */
public interface BaseDao {

    NBBase saveEntity(NBBase nbBase, SessionContext sc) throws ClientException;
    NBBase getEntity(NBBase nbBase, boolean loadLazyObject) throws ClientException;
    void deleteEntity(NBBase nbBase, SessionContext sc) throws ClientException;
    void deleteEntity(NBBase nbBase, boolean throwExistException, SessionContext sc) throws ClientException;

    List<? extends NBBase> getEntityList(long orgRrn, Class clazz) throws ClientException;
    List<NBBase> getEntityList(long orgRrn, Class entityClass, int maxResult, String whereClause, String orderBy) throws ClientException;
    List<NBBase> getEntityList(long orgRrn, String entityName, int firstResult, int maxResult, String whereClause, String orderBy) throws ClientException;
    List<NBBase> getEntityListForFiled(long orgRrn, String entityName, int firstResult, int maxResult, String whereClause, String orderBy, List<String> fields) throws ClientException;

    List<Map> getEntityMapListByQueryName(String queryName,  Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;
    List<Map> getEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException;

}
