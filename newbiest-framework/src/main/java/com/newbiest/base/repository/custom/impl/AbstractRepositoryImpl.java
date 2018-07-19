package com.newbiest.base.repository.custom.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.factory.SqlBuilder;
import com.newbiest.base.factory.SqlBuilderFactory;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.repository.custom.IRepository;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.ApplicationContextProvider;
import com.newbiest.main.NewbiestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 所有Repository的父类
 * Created by guoxunbo on 2018/7/16.
 */
@Slf4j
public class AbstractRepositoryImpl<T extends NBBase, ID> extends SimpleJpaRepository<T, ID> implements IRepository<T, ID> {

    private final EntityManager em;

    private AtomicBoolean initFlag;

    private NewbiestConfiguration newbiestConfiguration;

    private SqlBuilderFactory sqlBuilderFactory;
    /**
     * 对应这个Repository具体的Class
     */
    private final Class<T> domainClass;

    public AbstractRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.domainClass = domainClass;
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
        return domainClass.getName().equals(fullClassName);
    }

    public List<NBBase> findAll(long orgRrn) throws ClientException {
        try {
            init();
            return findAll(orgRrn, newbiestConfiguration.getQueryMaxCount(), StringUtils.EMPTY, StringUtils.EMPTY);
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
    public List<NBBase> findAll(long orgRrn, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            init();
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
    public List<NBBase> findAll(long orgRrn, int firstResult, int maxResult, String whereClause, String orderBy) throws ClientException {
        try {
            init();
            SqlBuilder sqlBuilder = SqlBuilderFactory.getInstance().createSqlBuilder();
            StringBuffer sqlBuffer = sqlBuilder.selectWithBasedCondition(domainClass, orgRrn).build();

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
            if (maxResult < newbiestConfiguration.getQueryMaxCount()) {
                query.setMaxResults(maxResult);
            } else {
                query.setMaxResults(newbiestConfiguration.getQueryMaxCount());
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

}
