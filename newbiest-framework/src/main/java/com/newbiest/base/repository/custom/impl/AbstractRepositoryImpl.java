package com.newbiest.base.repository.custom.impl;

import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.factory.SqlBuilder;
import com.newbiest.base.factory.SqlBuilderFactory;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.EntityReflectUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.ApplicationContextProvider;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.model.NBOrg;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.reflections.ReflectionUtils;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 所有Repository的父类
 * Created by guoxunbo on 2018/7/16.
 */
@Slf4j
public class AbstractRepositoryImpl<T extends NBBase, ID> extends SimpleJpaRepository<T, ID> implements IRepository<T, ID> {

    private final EntityManager em;

    private AtomicBoolean initFlag = new AtomicBoolean(false);

    private NewbiestConfiguration newbiestConfiguration;

    private SqlBuilderFactory sqlBuilderFactory;
    /**
     * 对应这个Repository具体的Class
     */
    private final Class<T> entityClass;

    public AbstractRepositoryImpl(Class<T> entityClass, EntityManager entityManager) {
        super(entityClass, entityManager);
        this.entityClass = entityClass;
        this.em = entityManager;
    }

    private void init() {
        if (!initFlag.get()) {
            newbiestConfiguration = ApplicationContextProvider.getBean(NewbiestConfiguration.class);
            sqlBuilderFactory = ApplicationContextProvider.getBean(SqlBuilderFactory.class);
            initFlag.compareAndSet(false, true);
        }
    }

    /**
     * 判断Repository是否支持这个class的查询
     * @param fullClassName className的全称 比如com.newbiest.base.ui.model.NBTable
     * @return
     * @throws ClientException
     */
    @Override
    public boolean support(String fullClassName) throws ClientException {
        return entityClass.getName().equals(fullClassName);
    }

    @Override
    public EntityManager getEntityManager() throws ClientException {
        return em;
    }

    @Override
    public NBBase findByObjectRrn(long objectRrn) throws ClientException {
        try {
            List<? extends NBBase> nbBases = findAll(NBOrg.GLOBAL_ORG_RRN, 1, "objectRrn = " + objectRrn, null);
            if (CollectionUtils.isNotEmpty(nbBases)) {
                return nbBases.get(0);
            } else {
                throw new ClientParameterException(NewbiestException.COMMON_ENTITY_IS_NOT_EXIST, entityClass.getName(), objectRrn);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public List<? extends NBBase> findByNameAndOrgRrn(String name, long orgRrn) throws ClientException {
        try {
            // 检查entityClass中是否有name持久化栏位
            checkFiled("name");
            List<? extends NBBase> nbBases = findAll(orgRrn, "name = '" + name + "'", null);
            if (CollectionUtils.isNotEmpty(nbBases)) {
                return nbBases;
            } else {
                return Lists.newArrayList();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据条件删除
     * @param whereClause 如name = 'a'
     * @throws ClientException
     */
    @Override
    public void delete(String whereClause) throws ClientException {
        try {
            if (StringUtils.isNullOrEmpty(whereClause)) {
                throw new ClientException(NewbiestException.COMMON_NONSUPPORT_DELETE_ALL_TABLE_DATA);
            }
            StringBuffer sqlBuffer = sqlBuilderFactory.createSqlBuilder().delete(entityClass).and().build();
            sqlBuffer.append(whereClause);
            em.createQuery(sqlBuffer.toString()).executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 检查栏位是否存在 以及是否具有domainClass的@Column栏位
     * @param fieldName
     */
    private void checkFiled(String fieldName) throws ClientException {

        List<Field> fields = Lists.newArrayList(ReflectionUtils.getAllFields(entityClass));
        Optional optional = fields.stream()
                .filter(field -> EntityReflectUtils.checkFieldPersist(field) && field.getName().equals(fieldName)).findFirst();
        if (!optional.isPresent()) {
            throw new ClientParameterException(NewbiestException.COMMON_ENTITY_FIELD_IS_NOT_PERSIST, entityClass.getName(), fieldName);
        }
    }

    public List<? extends NBBase> findAll(long orgRrn) throws ClientException {
        try {
            init();
            return findAll(orgRrn, newbiestConfiguration.getQueryMaxCount(), StringUtils.EMPTY, StringUtils.EMPTY);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<? extends NBBase> findAll(long orgRrn, String whereClause, String orderBy) throws ClientException {
        try {
            init();
            return findAll(orgRrn, newbiestConfiguration.getQueryMaxCount(), whereClause, orderBy);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取实体列表
     * @param orgRrn 区域号
     * @param maxResult 最大条数
     * @param whereClause 条件ued
     * @param orderBy 排序
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> findAll(long orgRrn, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            return findAll(orgRrn, 0, maxResult, whereClause, orderBy);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取实体列表
     * @param orgRrn 区域号
     * @param firstResult 起始条数
     * @param maxResult 最大条数
     * @param whereClause 条件
     * @param orderBy 排序
     * @return
     * @throws ClientException
     */
    public List<? extends NBBase> findAll(long orgRrn, int firstResult, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            init();
            SqlBuilder sqlBuilder = sqlBuilderFactory.createSqlBuilder();
            StringBuffer sqlBuffer = sqlBuilder.selectWithBasedCondition(entityClass, orgRrn).build();

            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sqlBuffer.append(" AND ");
                sqlBuffer.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderBy)) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(orderBy);
            }

            Query query = em.createQuery(sqlBuffer.toString());
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
            }
            if (maxResult != 0 && maxResult < newbiestConfiguration.getQueryMaxCount()) {
                query.setMaxResults(maxResult);
            } else {
                query.setMaxResults(newbiestConfiguration.getQueryMaxCount());
            }
            if (orgRrn != NBOrg.GLOBAL_ORG_RRN) {
                query.setParameter("orgRrn", orgRrn);
            }
            return query.getResultList();
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
    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
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
            if (maxResult < newbiestConfiguration.getQueryMaxCount()) {
                query.setMaxResults(maxResult);
            } else {
                query.setMaxResults(newbiestConfiguration.getQueryMaxCount());
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
