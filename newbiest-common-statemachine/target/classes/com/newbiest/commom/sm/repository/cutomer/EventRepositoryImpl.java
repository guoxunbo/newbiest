package com.newbiest.commom.sm.repository.cutomer;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.commom.sm.model.Event;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.commom.sm.repository.StatusModelRepository;
import com.newbiest.commom.sm.utils.StatusModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by guoxunbo on 2017/11/5.
 */
public class EventRepositoryImpl implements EventRepositoryCustom {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private StatusModelRepository statusModelRepository;

    public Event getEvent(Long statusModelRrn, String eventId, boolean deepFlag) throws ClientException {
        try {
            StatusModel statusModel = statusModelRepository.getByObjectRrn(statusModelRrn);
            if (statusModel == null) {
                throw new ClientException(StatusModelException.COM_SM_MODEL_IS_NOT_FOUND);
            }
            StringBuffer sql = new StringBuffer(" SELECT Event FROM Event Event ");
            sql.append(" WHERE ");
            sql.append(NBBase.BASE_CONDITION);
            sql.append(" AND category = :category ");
            if (!StringUtils.isNullOrEmpty(statusModel.getObjectType())) {
                sql.append(" AND objectType IS NULL ");
            } else {
                sql.append(" AND objectType = :objectType ");
            }
            sql.append(" AND eventId = :eventId ");

            Query query = em.createQuery(sql.toString());
            query.setParameter("orgRrn", statusModel.getOrgRrn());
            query.setParameter("category", statusModel.getCategory());
            if (deepFlag) {
                EntityGraph graph = em.createEntityGraph(StatusModel.class);
                graph.addSubgraph("eventStatus");
                query.setHint(NBBase.LAZY_LOAD_PROP, graph);
            }
            if (!StringUtils.isNullOrEmpty(statusModel.getObjectType())) {
                query.setParameter("objectType", statusModel.getObjectType());
            }
            query.setParameter("eventId", eventId);

            List<Event> events = query.getResultList();
            if (events != null && events.size() > 0 ) {
                return events.get(0);
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }


}
