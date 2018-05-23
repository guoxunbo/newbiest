package com.newbiest.security.repository.custom.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.annotation.MethodMonitor;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户操作相关类
 * Created by guoxunbo on 2017/9/27.
 */
@Transactional
@Slf4j
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleRepository roleRepository;

    @MethodMonitor
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
            List<NBRole> roles = nbUser.getRoles();
            if (CollectionUtils.isNotEmpty(roles)) {
                List<NBAuthority> authorities = Lists.newArrayList();
                for (NBRole role : roles) {
                    List<NBAuthority> roleAuthorities = roleRepository.getRoleAuthorities(role.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(roleAuthorities)) {
                        authorities.addAll(roleAuthorities);
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
            }
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<NBUser> testGetDeepUser() throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT NBUser FROM NBUser NBUser ");

            EntityGraph graph = em.createEntityGraph(NBUser.class);
            graph.addSubgraph("roles");

            Query query = em.createQuery(sqlBuffer.toString());
            query.setHint(NBBase.LAZY_LOAD_PROP, graph);
            query.setFirstResult(1);
            query.setMaxResults(2);
            List<NBUser> users = query.getResultList();
            return users;
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

    /**
     * 保存用户 当前不支持用户赋给那几个Role,支持Role下赋用户 Org同理
     * @param nbUser
     * @param sc
     * @throws ClientException
     */
    @Override
    public void save(NBUser nbUser, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (nbUser.getObjectRrn() != null) {
                NBUser oldUser = em.find(NBUser.class, nbUser.getObjectRrn());
                //不允许修改用户密码
                String oldPassword = oldUser.getPassword();
                nbUser.setPassword(oldPassword);

                nbUser.setUpdatedBy(sc.getUsername());
                nbUser.setRoles(oldUser.getRoles());
                nbUser.setOrgs(oldUser.getOrgs());
                nbUser = em.merge(nbUser);

                NBUserHis nbUserHis = new NBUserHis(nbUser, sc);
                nbUserHis.setUpdatedBy(sc.getUsername());
                nbUserHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                em.persist(nbUserHis);
            } else {
                nbUser.setOrgRrn(sc.getOrgRrn());
                nbUser.setCreatedBy(sc.getUsername());
                nbUser.setUpdatedBy(sc.getUsername());
                em.persist(nbUser);

                NBUserHis nbUserHis = new NBUserHis(nbUser, sc);
                nbUserHis.setTransType(NBHis.TRANS_TYPE_CRAETE);
                em.persist(nbUserHis);

                if (!StringUtils.isNullOrEmpty(nbUser.getEmail())) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("user", nbUser);
                    mailService.sendTemplateMessage(Arrays.asList(nbUser.getEmail()), "CreateUser", MailService.CREATE_USER_TEMPLATE, map);
                }
            }
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
    public void loginFail(NBUser nbUser) throws ClientException {
        try {
            nbUser = em.find(NBUser.class, nbUser.getObjectRrn());
            // 清空密码错误次数
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

    @Override
    public NBUser resetPassword(NBUser nbUser, String newPassword, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            nbUser = em.find(NBUser.class, nbUser.getObjectRrn());
            if (StringUtils.isNullOrEmpty(newPassword)) {
                throw new ClientException(NewbiestException.COMMON_NEW_PASSWORD_IS_NULL);
            }
            Date pwdChanged = new Date();
            nbUser.setPwdChanged(pwdChanged);
            //对Password进行加密
//            nbUser.setPassword(EncryptionUtils.encode(newPassword));
            // 重置密码之后，对所有密码有效期及错误次数重新设置
            nbUser.setInValidFlag(true);
            nbUser.setPwdWrongCount(0);

            if (nbUser.getPwdLife() != null) {
                nbUser.setPwdExpiry(DateUtils.plus(pwdChanged, nbUser.getPwdLife().intValue(), ChronoUnit.DAYS));
            }
            nbUser = em.merge(nbUser);
            em.createNativeQuery("");
            NBUserHis his = new NBUserHis(nbUser, sc);
            his.setTransType(NBUserHis.TRANS_TYPE_RESET_PASSWORD);
            em.persist(his);

            // 发邮件
            if (!StringUtils.isNullOrEmpty(nbUser.getEmail())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("user", nbUser);
                mailService.sendTemplateMessage(Arrays.asList(nbUser.getEmail()), "ResetPassword", MailService.RESET_PASSWORD_TEMPLATE, map);
            }
            return nbUser;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 修改用户密码
     */
    @Override
    public NBUser changePassword(NBUser user, String oldPassword, String newPassword, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            if (StringUtils.isNullOrEmpty(newPassword)) {
                throw new ClientException(NewbiestException.COMMON_NEW_PASSWORD_IS_NULL);
            }

            NBUser oldUser = em.find(NBUser.class, user.getObjectRrn());
            String oldPwd = oldUser.getPassword();

            if (!oldPwd.equals(oldPassword)) {
                throw new ClientException(NewbiestException.COMMON_OLD_PASSWORD_IS_INCORRECT);
            }
            if (newPassword.equals(oldPassword)) {
                throw new ClientException(NewbiestException.COMMON_NEW_PASSWORD_EQUALS_OLD_PASSWORD);
            }

            Date pwdChanged = new Date();
            oldUser.setPwdChanged(new Date());
            oldUser.setPassword(newPassword);
            // 修改密码之后，对所有密码有效期及错误次数重新设置
            oldUser.setInValidFlag(true);
            oldUser.setPwdWrongCount(0);
            oldUser.setUpdatedBy(sc.getUsername());
            if (oldUser.getPwdLife() != null) {
                oldUser.setPwdExpiry(DateUtils.plus(pwdChanged, oldUser.getPwdLife().intValue(), ChronoUnit.DAYS));
            }
            oldUser = em.merge(oldUser);

            NBUserHis his = new NBUserHis(oldUser, sc);
            his.setTransType(NBUserHis.TRANS_TYPE_CHANGE_PASSWORD);
            em.persist(his);

            return oldUser;
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
