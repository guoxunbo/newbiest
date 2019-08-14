package com.newbiest.context.repository.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.factory.SqlBuilder;
import com.newbiest.base.factory.SqlBuilderFactory;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.context.model.Context;
import com.newbiest.context.model.ContextValue;
import com.newbiest.context.repository.custom.ContextValueRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by guoxunbo on 2018/7/6.
 */
@Slf4j
public class ContextValueRepositoryImpl implements ContextValueRepositoryCustom {

    @PersistenceContext
    EntityManager em;

    /**
     * 获得与ContextValue的ContextField值相同的所有激活的ContextValue
     * @param context 容器
     * @param contextValue 具有相同组合的ContextValue 比如contextFieldValue1值相同
     * @return
     * @throws ClientException
     */
    public List<ContextValue> getContextValue(Context context, ContextValue contextValue) throws ClientException {
        try {
            SqlBuilder sqlBuilder = SqlBuilderFactory.createSqlBuilder();
            StringBuffer sqlBuffer = sqlBuilder.selectWithBasedCondition(ContextValue.class, ThreadLocalContext.getOrgRrn())
                        .mapFieldValue(ImmutableMap.of("contextRrn", context.getObjectRrn(), "status", NBVersionControl.STATUS_ACTIVE))
                        .build();
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue1())) {
                sqlBuffer.append(" AND contextFieldValue1 = '" + contextValue.getFieldValue1() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue1 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue2())) {
                sqlBuffer.append(" AND contextFieldValue2 = '" + contextValue.getFieldValue2() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue2 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue3())) {
                sqlBuffer.append(" AND contextFieldValue3 = '" + contextValue.getFieldValue3() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue3 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue4())) {
                sqlBuffer.append(" AND contextFieldValue4 = '" + contextValue.getFieldValue4() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue4 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue5())) {
                sqlBuffer.append(" AND contextFieldValue5 = '" + contextValue.getFieldValue5() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue5 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue6())) {
                sqlBuffer.append(" AND contextFieldValue6 = '" + contextValue.getFieldValue6() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue6 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue7())) {
                sqlBuffer.append(" AND contextFieldValue7 = '" + contextValue.getFieldValue7() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue7 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue8())) {
                sqlBuffer.append(" AND contextFieldValue8 = '" + contextValue.getFieldValue8() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue8 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue9())) {
                sqlBuffer.append(" AND contextFieldValue9 = '" + contextValue.getFieldValue9() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue9 IS NULL ");
            }
            if (!StringUtils.isNullOrEmpty(contextValue.getFieldValue10())) {
                sqlBuffer.append(" AND contextFieldValue10 = '" + contextValue.getFieldValue10() + "'");
            } else {
                sqlBuffer.append(" AND contextFieldValue10 IS NULL ");
            }
            Query query = em.createQuery(sqlBuffer.toString());
            List<ContextValue> contextValues = query.getResultList();
            if (!CollectionUtils.isNotEmpty(contextValues)) {
                return contextValues;
            }
            return Lists.newArrayList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
