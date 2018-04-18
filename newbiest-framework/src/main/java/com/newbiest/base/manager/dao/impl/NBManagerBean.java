package com.newbiest.base.manager.dao.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.manager.dao.NBManager;
import com.newbiest.base.model.*;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.repository.RelationRepository;
import com.newbiest.base.utils.*;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装的查询保存等管理对象
 * Created by guoxunbo on 2017/10/11.
 */
@Component
@Transactional
@Slf4j
public class NBManagerBean implements NBManager {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private QueryRepository queryRepository;

    /**
     * 获取实体列表
     * @param orgRrn 区域号
     * @param clazz 实体类
     * @param maxResult 最大条数
     * @param whereClause 条件
     * @param orderBy 排序
     * @return
     * @throws ClientException
     */
    @Override
    public List<NBBase> getEntityList(long orgRrn, Class clazz, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            return getEntityList(orgRrn, clazz.getSimpleName(), 0, maxResult, whereClause, orderBy);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取实体列表
     * @param orgRrn 区域号
     * @param entityName 实体类名
     * @param firstResult 起始条数
     * @param maxResult 最大条数
     * @param whereClause 条件
     * @param orderBy 排序
     * @return
     * @throws ClientException
     */
    @Override
    public List<NBBase> getEntityList(long orgRrn, String entityName, int firstResult, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            StringBuffer sql = new StringBuffer(" SELECT ");
            sql.append(entityName);
            sql.append(" FROM ");
            sql.append(entityName);
            sql.append(" ");
            sql.append(entityName);
            sql.append(" WHERE ");
            // 为0默认为查全部
            if (orgRrn != 0) {
                sql.append(NBBase.BASE_CONDITION);
            } else {
                sql.append(" 1=1 ");
            }
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sql.append(" AND ");
                sql.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderBy)) {
                sql.append(" ORDER BY ");
                sql.append(orderBy);
            }

            Query query = em.createQuery(sql.toString());
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
            }
            if (maxResult < NewbiestConfiguration.getQueryMaxCount()) {
                query.setMaxResults(maxResult);
            } else {
                query.setMaxResults(NewbiestConfiguration.getQueryMaxCount());
            }
            if (orgRrn != 0) {
                query.setParameter("orgRrn", orgRrn);
            }
            return query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取实体列表
     * @param orgRrn 区域号
     * @param entityName 实体类名
     * @param firstResult 起始条数
     * @param maxResult 最大条数
     * @param whereClause 条件
     * @param orderBy 排序
     * @param fields 具体的查询栏位
     * @return
     * @throws ClientException
     */
    @Override
    public List<NBBase> getEntityListForFiled(long orgRrn, String entityName, int firstResult, int maxResult, String whereClause, String orderBy, List<String> fields) throws ClientException {
        try {
            Class entityClass = Class.forName(entityName);
            List<NBBase> entityList = Lists.newArrayList();
            if (fields == null || fields.isEmpty()) {
                entityList = getEntityList(orgRrn, entityClass.getSimpleName(), 0, maxResult, whereClause, orderBy);
            } else {
                StringBuffer sql = new StringBuffer(" SELECT ");
                sql.append(" NEW MAP");
                sql.append(" ( ");
                sql.append(StringUtils.join(fields, ", "));
                sql.append(" ) ");
                sql.append(" FROM ");
                sql.append(entityClass.getSimpleName());
                sql.append(" ");
                sql.append(entityClass.getSimpleName());
                sql.append(" WHERE ");
                if (orgRrn != 0) {
                    sql.append(NBBase.BASE_CONDITION);
                } else {
                    sql.append(" 1=1 ");
                }
                if (!StringUtils.isNullOrEmpty(whereClause)){
                    sql.append(" AND ");
                    sql.append(whereClause);
                }
                if (!StringUtils.isNullOrEmpty(orderBy)) {
                    sql.append(" ORDER BY ");
                    sql.append(orderBy);
                }

                Query query = em.createQuery(sql.toString());
                if (firstResult > 0) {
                    query.setFirstResult(firstResult);
                }
                if (maxResult < NewbiestConfiguration.getQueryMaxCount()) {
                    query.setMaxResults(maxResult);
                } else {
                    query.setMaxResults(NewbiestConfiguration.getQueryMaxCount());
                }
                if (orgRrn != 0) {
                    query.setParameter("orgRrn", orgRrn);
                }

                List<Map> objectList = query.getResultList();
                for (Map object : objectList) {
                    NBBase nbBase = (NBBase)entityClass.newInstance();
                    for (int i = 0; i < fields.size(); i++) {
                        // SQL上没使用AS *** 所以MAP的KEY为相应的下标
                        PropertyUtils.setProperty(nbBase, fields.get(i), object.get(String.valueOf(i)));
                    }
                    entityList.add(nbBase);
                }
            }
            return entityList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据NBquery中定义的SQL语句进行以返回Map的形式查询返回
     * @param queryName NBQuery名称
     * @param paramMap WhereClause的参数值
     * @param firstResult 起始
     * @param maxResult 最大返回数据
     * @param whereClause 查询条件
     * @param orderByClause 排序条件
     * @return
     * @throws ClientException
     */
    @Override
    public List<Map> getEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.getByName(queryName);
            if (nbQuery == null) {
                throw new ClientException(NewbiestException.COMMON_QUERY_IS_NOT_FOUND);
            }
            return getEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, maxResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据自定义定义的SQL语句进行以返回Map的形式查询返回
     * @param queryText 自定义的SQL语句
     * @param paramMap WhereClause的参数值
     * @param firstResult 起始
     * @param maxResult 最大返回数据
     * @param whereClause 查询条件
     * @param orderByClause 排序条件
     * @return
     * @throws ClientException
     */
    @Override
    public List<Map> getEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
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
            if (maxResult < NewbiestConfiguration.getQueryMaxCount()) {
                query.setMaxResults(maxResult);
            } else {
                query.setMaxResults(NewbiestConfiguration.getQueryMaxCount());
            }
            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    query.setParameter(key, paramMap.get(key));
                }
            }

            query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public void deleteEntity(NBBase nbBase, SessionContext sc) throws ClientException {
        deleteEntity(nbBase, false, sc);
    }

    /**
     * 删除实体
     * @param nbBase 具体实体 需要包含objectRrn
     * @param throwExistException 存在时是否抛出异常
     * @param sc
     * @throws ClientException
     */
    public void deleteEntity(NBBase nbBase, boolean throwExistException, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            nbBase = em.find(nbBase.getClass(), nbBase.getObjectRrn());
            if (nbBase == null) {
                throw new ClientException(NewbiestException.COMMON_ENTITY_IS_NOT_EXIST_OR_DELETE);
            }
            deleteRelationObject(nbBase, throwExistException);

            NBHis nbHis = NBHis.getHistoryBean(nbBase);
            if (nbHis != null) {
                nbHis.setTransType(NBHis.TRANS_TYPE_DELETE);
                nbHis.setNbBase(nbBase, sc);
                em.persist(nbHis);
            }

            em.remove(nbBase);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 删除关联对象
     * @param nbBase
     * @throws ClientException
     */
    public void deleteRelationObject(NBBase nbBase, boolean throwExistException) throws ClientException{
        try {
            List<NBRelation> relations = relationRepository.getBySource(nbBase.getClass().getName());
            if (CollectionUtils.isNotEmpty(relations)) {
                Query query;
                for (NBRelation relation : relations) {
                    StringBuffer sqlBuffer = new StringBuffer("DELETE FROM");
                    String whereClause = relation.getWhereClause(nbBase);
                    List relationObjects = Lists.newArrayList();
                    if (NBRelation.RELATION_TYPE_CLASS.equals(relation.getRelationType())) {
                        Class clazz = Class.forName(relation.getTarget());
                        if (!throwExistException) {
                            sqlBuffer.append(clazz.getSimpleName());
                            sqlBuffer.append(" WHERE ");
                            sqlBuffer.append(whereClause);

                            query = em.createQuery(sqlBuffer.toString());
                            query.executeUpdate();
                        } else {
                            relationObjects = getEntityList(NBOrg.GLOBAL_ORG_RRN, clazz, NewbiestConfiguration.getQueryMaxCount(), whereClause, null);
                        }
                    } else if (NBRelation.RELATION_TYPE_SQL.equals(relation.getRelationType())) {
                        if (!throwExistException) {
                            sqlBuffer.append(relation.getTarget());
                            sqlBuffer.append(" WHERE ");
                            sqlBuffer.append(whereClause);
                            query = em.createNativeQuery(sqlBuffer.toString());
                            query.executeUpdate();
                        } else {
                            StringBuffer queryBuffer = new StringBuffer();
                            queryBuffer.append(" SELECT * FROM ");
                            queryBuffer.append(relation.getTarget());
                            queryBuffer.append(" WHERE ");
                            queryBuffer.append(whereClause);
                            query = em.createNativeQuery(sqlBuffer.toString());
                            relationObjects = query.getResultList();
                        }
                    }
                    if (CollectionUtils.isNotEmpty(relationObjects)) {
                        throw new ClientParameterException(NewbiestException.COMMON_RELATION_OBJECT_IS_EXIST, relation.getTarget());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 保存对象
     * @param nbBase
     * @param sc
     * @throws ClientException
     */
    public NBBase saveEntity(NBBase nbBase, SessionContext sc) throws ClientException{
        try {
            sc.buildTransInfo();
            NBHis nbHis = NBHis.getHistoryBean(nbBase);

            if (nbBase.getObjectRrn() != null) {
                if (nbBase instanceof NBUpdatable) {
                    ((NBUpdatable) nbBase).setUpdatedBy(sc.getUsername());
                }
                nbBase = em.merge(nbBase);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                    nbHis.setNbBase(nbBase, sc);
                    em.persist(nbHis);
                }
            } else {
                nbBase.setOrgRrn(sc.getOrgRrn());
                if (nbBase instanceof NBUpdatable) {
                    ((NBUpdatable) nbBase).setCreatedBy(sc.getUsername());
                    ((NBUpdatable) nbBase).setUpdatedBy(sc.getUsername());
                }
                em.persist(nbBase);

                if (nbHis != null) {
                    nbHis.setTransType(NBHis.TRANS_TYPE_CRAETE);
                    nbHis.setNbBase(nbBase, sc);
                    em.persist(nbHis);
                }
            }
            return nbBase;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据ObjectRrn取得相应的实体
     * @param nbBase 实体必带objectRrn
     * @param loadLazyObject 是否加载懒加载对象
     * @return
     * @throws ClientException
     */
    public NBBase getEntity(NBBase nbBase, boolean loadLazyObject) throws ClientException{
        try {
            nbBase = em.find(nbBase.getClass(), nbBase.getObjectRrn());
            if (nbBase == null) {
                throw new ClientException(NewbiestException.COMMON_ENTITY_IS_NOT_EXIST_OR_DELETE);
            }
            if (loadLazyObject) {
                PropertyDescriptor[] descriptors = org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(nbBase);
                if (descriptors != null && descriptors.length > 0) {
                    for (PropertyDescriptor descriptor : descriptors) {
                        Class clazz = descriptor.getPropertyType();
                        if (clazz.isInstance(ArrayList.class.newInstance())) {
                            List list = (List)PropertyUtils.getProperty(nbBase, descriptor.getName());
                            if (list != null) {
                                list.size();
                            }
                        }
                    }
                }
            }
            return nbBase;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }
}
