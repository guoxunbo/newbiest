package com.newbiest.commom.sm.repository.cutomer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.model.Status;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.repository.StatusModelRepository;
import com.newbiest.commom.sm.utils.StatusModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public class StatusRepositoryImpl implements StatusRepositoryCustom {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StatusModelRepository statusModelRepository;

    public Status getStatus(Long statusModelRrn, String state) throws ClientException {
        try {
            StatusModel statusModel = statusModelRepository.getByObjectRrn(statusModelRrn);
            if (statusModel == null) {
                throw new ClientException(StatusModelException.COM_SM_MODEL_IS_NOT_FOUND);
            }
            StringBuffer sql = new StringBuffer(" SELECT Status FROM Status Status ");
            sql.append(" WHERE ");
            sql.append(NBBase.BASE_CONDITION);
            sql.append(" AND category = :category ");
            if (!StringUtils.isNullOrEmpty(statusModel.getObjectType())) {
                sql.append(" AND objectType IS NULL ");
            } else {
                sql.append(" AND objectType = :objectType ");
            }
            sql.append(" AND state = :state ");

            Query query = em.createQuery(sql.toString());
            query.setParameter("orgRrn", statusModel.getOrgRrn());
            query.setParameter("category", statusModel.getCategory());
            if (!StringUtils.isNullOrEmpty(statusModel.getObjectType())) {
                query.setParameter("objectType", statusModel.getObjectType());
            }
            query.setParameter("state", state);

            List<Status> status = query.getResultList();
            if (status != null && status.size() > 0 ) {
                return status.get(0);
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


}
