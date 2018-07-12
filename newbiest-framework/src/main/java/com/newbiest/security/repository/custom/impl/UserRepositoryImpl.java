package com.newbiest.security.repository.custom.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.MethodMonitor;
import com.newbiest.base.dao.BaseDao;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.*;
import com.newbiest.main.MailService;
import com.newbiest.security.model.*;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.custom.UserRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户操作相关类
 * Created by guoxunbo on 2017/9/27.
 */
@Slf4j
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BaseDao baseDao;

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

    /**
     * 根据用户获取权限并按照树形结构返回 结构如下
     *  基础设置
     *      安全管理
     *          用户管理
     *          用户组管理
     * @param userRrn 用户主键
     * @return
     */
    public List<NBAuthority> getTreeAuthorities(long userRrn) throws ClientException {
        try {
            NBUser nbUser = getDeepUser(userRrn, false);
            List<NBAuthority> authorities = Lists.newArrayList();
            // 如果是admin用户的话代表拥有所有权限
            if (NBUser.ADMIN_USER.equals(nbUser.getUsername())) {
                authorities.addAll((Collection<? extends NBAuthority>) baseDao.getEntityList(nbUser.getOrgRrn(), NBAuthority.class));
            } else {
                List<NBRole> roles = nbUser.getRoles();
                if (CollectionUtils.isNotEmpty(roles)) {
                    for (NBRole role : roles) {
                        List<NBAuthority> roleAuthorities = roleRepository.getRoleAuthorities(role.getObjectRrn());
                        if (CollectionUtils.isNotEmpty(roleAuthorities)) {
                            authorities.addAll(roleAuthorities);
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(authorities)) {
                // 取出父级菜单
                List<NBAuthority> firstLevelAuthorities = authorities.stream().filter(nbAuthority -> nbAuthority.getParentRrn() == null).collect(Collectors.toList());
                authorities.removeAll(firstLevelAuthorities);
                // 组织树形结构
                List<NBAuthority> nbAuthorities = firstLevelAuthorities.stream().map(authority -> {
                     return authority.recursionAuthority(authority, authorities);
                }).collect(Collectors.toList());
                return nbAuthorities;
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

    private List<NBOrg> getUserOrgs(long userRrn) throws ClientException {
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

    public EntityManager getEntityManager() {
        return em;
    }

}
