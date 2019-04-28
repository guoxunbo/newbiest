package com.newbiest.rms.repository.custom.impl;

import com.google.common.collect.ImmutableMap;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.factory.SqlBuilderFactory;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.rms.model.RecipeEquipmentParameterTemp;
import com.newbiest.rms.repository.custom.RecipeEquipmentParameterTempRepositoryCustom;
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
public class RecipeEquipmentParameterTempRepositoryImpl implements RecipeEquipmentParameterTempRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    public List<RecipeEquipmentParameterTemp> getByEcnId(String ecnId, String status, SessionContext sc) throws ClientException {
        try {
            if(StringUtils.isNullOrEmpty(status)) {
                status = NBVersionControl.STATUS_ACTIVE;
            }
            StringBuffer sqlBuffer = SqlBuilderFactory.createSqlBuilder().selectWithBasedCondition(RecipeEquipmentParameterTemp.class, sc.getOrgRrn())
                                                .mapFieldValue(ImmutableMap.of("ecnId", ecnId, "status", status))
                                                .build();

            Query query = em.createQuery(sqlBuffer.toString());
            query.setParameter("orgRrn", sc.getOrgRrn());
            List<RecipeEquipmentParameterTemp> temps = query.getResultList();
            if (temps != null && temps.size() > 0) {
                return temps;
            }
            return null;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
