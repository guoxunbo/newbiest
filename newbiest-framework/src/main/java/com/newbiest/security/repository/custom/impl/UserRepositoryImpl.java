package com.newbiest.security.repository.custom.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.annotation.MethodMonitor;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.main.MailService;
import com.newbiest.security.model.NBOrg;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.model.NBUserHis;
import com.newbiest.security.repository.custom.UserRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 用户操作相关类
 * Created by guoxunbo on 2017/9/27.
 */
@Slf4j
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MailService mailService;

    @Override
    public NBUser getDeepUser(Long userRrn, boolean orgFlag) throws ClientException {
        try {
            EntityGraph graph = em.createEntityGraph(NBUser.class);
            graph.addSubgraph("roles");
            Map<String, Object> props = Maps.newHashMap();
            props.put(NBBase.LAZY_FETCH_PROP, graph);

            NBUser user = em.find(NBUser.class, userRrn, props);
            if (user != null) {
                if (orgFlag) {
                    List<NBOrg> orgs = getUserOrgs(user.getObjectRrn());
                    user.setOrgs(orgs);
                }
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @MethodMonitor
    @Override
    public NBUser getDeepUser(String username, boolean orgFlag) throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT NBUser FROM NBUser NBUser ");
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(" username = :username");

            EntityGraph graph = em.createEntityGraph(NBUser.class);
            graph.addSubgraph("roles");

            Query query = em.createQuery(sqlBuffer.toString());
            query.setHint(NBBase.LAZY_LOAD_PROP, graph);
            query.setParameter("username", username);

            List<NBUser> users = query.getResultList();
            if (CollectionUtils.isNotEmpty(users)) {
                NBUser user = users.get(0);
                if (orgFlag) {
                    List<NBOrg> orgs = getUserOrgs(user.getObjectRrn());
                    user.setOrgs(orgs);
                }
                return user;
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public void loginSuccess(NBUser nbUser) throws ClientException {
        try {
            nbUser = em.find(NBUser.class, nbUser.getObjectRrn());
            // 清空密码错误次数
            nbUser.setPwdWrongCount(0);
            nbUser.setLastLogon(DateUtils.now());
            em.merge(nbUser);

            NBUserHis nbUserHis = new NBUserHis(nbUser, SessionContext.buildSessionContext(nbUser.getOrgRrn()));
            nbUserHis.setTransType(NBUserHis.TRANS_TYPE_LOGIN_SUCCESS);
            em.persist(nbUserHis);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 登录失败
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void loginFail(NBUser nbUser) throws ClientException {
        try {
            nbUser = em.find(NBUser.class, nbUser.getObjectRrn());

            int pwdWrongCount = nbUser.getPwdWrongCount() != null ? nbUser.getPwdWrongCount() + 1 : 1;
            nbUser.setPwdWrongCount(pwdWrongCount);
            em.merge(nbUser);

            NBUserHis nbUserHis = new NBUserHis(nbUser, SessionContext.buildSessionContext(nbUser.getOrgRrn()));
            nbUserHis.setTransType(NBUserHis.TRANS_TYPE_LOGIN_FAIL);
            em.persist(nbUserHis);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<NBOrg> getUserOrgs(long userRrn) throws ClientException {
        try {
            EntityGraph graph = em.createEntityGraph(NBUser.class);
            graph.addSubgraph("orgs");
            Map<String, Object> props = Maps.newHashMap();
            props.put(NBBase.LAZY_FETCH_PROP, graph);
            NBUser user = em.find(NBUser.class, userRrn, props);
            return user.getOrgs();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
