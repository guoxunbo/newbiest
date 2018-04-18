package com.newbiest.base.repository.custom.impl;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.repository.custom.TableRepositoryCustom;
import com.newbiest.base.ui.model.NBTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 * 用户操作相关类
 * Created by guoxunbo on 2017/9/27.
 */
@Transactional
@Slf4j
public class TableRepositoryImpl implements TableRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    /**
     *
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    @Override
    public NBTable getDeepTable(long tableRrn) throws ClientException {
        try {
            NBTable nbTable = em.find(NBTable.class, tableRrn);
            if (nbTable == null) {
                throw new ClientException(NewbiestException.BASE_TABLE_IS_NOT_FOUND);
            }
            if (nbTable.getTabs() != null) {
                nbTable.getTabs().size();
            }
            if (nbTable.getFields() != null) {
                nbTable.getFields().size();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ClientException(e);
        }
        return null;
    }

}
