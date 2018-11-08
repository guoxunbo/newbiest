package com.newbiest.security.service.impl;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.EncryptionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.JwtSigner;
import com.newbiest.main.MailService;
import com.newbiest.main.NewbiestConfiguration;
import com.newbiest.security.exception.SecurityException;
import com.newbiest.security.model.*;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.UserHistoryRepository;
import com.newbiest.security.repository.UserRepository;
import com.newbiest.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Created by guoxunbo on 2018/6/1.
 */
@Service
@Slf4j
@Transactional
public class SecurityServiceImpl implements SecurityService  {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MailService mailService;

    @Autowired
    NewbiestConfiguration newbiestConfiguration;

//    @Autowired
//    RedisService redisService;
//
    @Autowired
    JwtSigner jwtSigner;

    public NBUser login(String username, String password, SessionContext sc) throws ClientException {
        try {
            // 1. 先查找用户 用户如果没找到直接抛出异常
            NBUser nbUser = userRepository.findByUsername(username);
            if (nbUser == null) {
                throw new ClientParameterException(SecurityException.SECURITY_USER_IS_NOT_EXIST, username);
            }
            // 验证用户是否被锁住
            if (!nbUser.getInValidFlag()) {
                //TODO 是否要发送新密码邮件
                throw new ClientException(SecurityException.SECURITY_USER_IS_NOT_IN_VALIDATION);
            }
            // 验证密码错误次数大于定义的次数
            if (nbUser.getPwdWrongCount() != null && nbUser.getPwdWrongCount() > newbiestConfiguration.getPwdWrongCount()) {
                throw new ClientException(SecurityException.SECURITY_WRONG_PWD_MORE_THAN_COUNT);
            }
            // 验证密码是否过期
            if (nbUser.getPwdExpiry() != null && nbUser.getPwdExpiry().before(DateUtils.now())) {
                throw new ClientException(SecurityException.SECURITY_PASSWORD_IS_EXPIRY);
            }
            // admin用户具有所有区域的权限
            if (!NBUser.ADMIN_USER.equals(username)) {
                List<NBOrg> orgList = userRepository.getUserOrgs(nbUser.getObjectRrn());
                Optional optional = orgList.stream().filter(org -> org.getName().equals(sc.getOrgName()) || org.getObjectRrn().equals(sc.getOrgRrn())).findFirst();
                if (!optional.isPresent()) {
                    throw new ClientException(SecurityException.SECURITY_USER_IS_NOT_IN_ORG);
                }
            }

            // 验证密码
            password = EncryptionUtils.md5Hex(password);
            if (!password.equals(nbUser.getPassword())) {
                userRepository.loginFail(nbUser);
                throw new ClientException(SecurityException.SECURITY_USER_PASSWORD_IS_INCORRECT);
            }
            userRepository.loginSuccess(nbUser);

            // 获取菜单权限
            List<NBAuthority> authorities = getAuthorities(nbUser.getObjectRrn());
            nbUser.setAuthorities(authorities);
            // 生成jwtToken 后续存入redis
            String token = jwtSigner.sign(nbUser.getUsername());
            //            redisService.put(nbUser.getUsername(), signStr);

            nbUser.setToken(token);
            return nbUser;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取权限并转换成相应的DTO返回
     * @param userRrn
     * @return
     * @throws ClientException
     */
    public List<NBAuthority> getAuthorities(Long userRrn) throws ClientException{
        try {
            return userRepository.getTreeAuthorities(userRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public NBUser getUserByObjectRrn(Long userRrn) throws ClientException {
        try {
            return (NBUser) userRepository.findByObjectRrn(userRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public NBUser getUserByUsername(String username) throws ClientException {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 保存用户
     * @param nbUser
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBUser saveUser(NBUser nbUser, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            if (nbUser.getObjectRrn() != null) {
                NBUser oldUser = (NBUser) userRepository.findByObjectRrn(nbUser.getObjectRrn());
                //不允许修改用户密码
                String oldPassword = oldUser.getPassword();
                nbUser.setPassword(oldPassword);
                nbUser.setUpdatedBy(sc.getUsername());
                nbUser.setRoles(oldUser.getRoles());
                nbUser.setOrgs(oldUser.getOrgs());
                nbUser = userRepository.saveAndFlush(nbUser);

                NBUserHis nbUserHis = new NBUserHis(nbUser, sc);
                nbUserHis.setUpdatedBy(sc.getUsername());
                nbUserHis.setTransType(NBHis.TRANS_TYPE_UPDATE);
                userHistoryRepository.save(nbUserHis);
            } else {
                NBUser newUser = new NBUser();
                newUser.setUsername(nbUser.getUsername());
                // 如果没设置密码。则生成随机的6位数
                if (StringUtils.isNullOrEmpty(nbUser.getPassword())) {
                    newUser.setPassword(EncryptionUtils.md5Hex(getPassword()));
                }
                // 对密码进行加密
                newUser.setDescription(nbUser.getDescription());
                newUser.setDepartment(nbUser.getDepartment());
                newUser.setEmail(nbUser.getEmail());
                newUser.setPhone(nbUser.getPhone());
                newUser.setSex(nbUser.getSex());
                // 第一次登录是否需要修改密码
                if (newbiestConfiguration.getFirstLoginChangePwd()) {
                    newUser.setInValidFlag(false);
                } else {
                    newUser.setInValidFlag(true);
                }
                if (newbiestConfiguration.getPwdLife() != 0) {
                    newUser.setPwdLife(newbiestConfiguration.getPwdLife());
                    Date pwdExpiry = DateUtils.plus(new Date(), nbUser.getPwdLife().intValue(), ChronoUnit.DAYS);
                    newUser.setPwdExpiry(pwdExpiry);
                }
                newUser.setOrgRrn(sc.getOrgRrn());
                newUser.setCreatedBy(sc.getUsername());
                newUser.setUpdatedBy(sc.getUsername());
                nbUser = userRepository.saveAndFlush(newUser);

                NBUserHis nbUserHis = new NBUserHis(nbUser, sc);
                nbUserHis.setTransType(NBHis.TRANS_TYPE_CRAETE);
                userHistoryRepository.save(nbUserHis);

                if (!StringUtils.isNullOrEmpty(nbUser.getEmail())) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("user", nbUser);
                    mailService.sendTemplateMessage(Arrays.asList(nbUser.getEmail()), "CreateUser", MailService.CREATE_USER_TEMPLATE, map);
                }
            }
            return nbUser;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 修改密码
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param sc
     * @return
     * @throws ClientException
     */
    public NBUser changePassword(NBUser user, String oldPassword, String newPassword, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();

            if (StringUtils.isNullOrEmpty(newPassword)) {
                throw new ClientException(NewbiestException.COMMON_NEW_PASSWORD_IS_NULL);
            }
            NBUser oldUser = (NBUser) userRepository.findByObjectRrn(user.getObjectRrn());
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
            oldUser = userRepository.saveAndFlush(oldUser);

            NBUserHis his = new NBUserHis(oldUser, sc);
            his.setTransType(NBUserHis.TRANS_TYPE_CHANGE_PASSWORD);
            userHistoryRepository.saveAndFlush(his);

            return oldUser;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public NBUser resetPassword(NBUser nbUser, SessionContext sc) throws ClientException {
        try {
            sc.buildTransInfo();
            nbUser = (NBUser) userRepository.findByObjectRrn(nbUser.getObjectRrn());
            Date pwdChanged = new Date();
            nbUser.setPwdChanged(pwdChanged);
            //对Password进行加密
            nbUser.setPassword(EncryptionUtils.md5Hex(getPassword()));
            // 重置密码之后，对所有密码有效期及错误次数重新设置
            nbUser.setInValidFlag(true);
            nbUser.setPwdWrongCount(0);

            if (nbUser.getPwdLife() != null) {
                nbUser.setPwdExpiry(DateUtils.plus(pwdChanged, nbUser.getPwdLife().intValue(), ChronoUnit.DAYS));
            }
            nbUser = userRepository.saveAndFlush(nbUser);

            NBUserHis his = new NBUserHis(nbUser, sc);
            his.setTransType(NBUserHis.TRANS_TYPE_RESET_PASSWORD);
            userHistoryRepository.save(his);

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

    @Override
    public NBUser getDeepUser(long userRrn, boolean orgFlag) throws ClientException {
        try {
            return userRepository.getDeepUser(userRrn, orgFlag);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void deleteUser(NBUser user) throws ClientException {
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 生成密码 如果是随机生成就返回随机的6位数字，否则则返回111111
     * @return
     */
    private String getPassword() {
        if (NewbiestConfiguration.PASSWORD_POLICY_RANDOM.equals(newbiestConfiguration.getPwdPolicy())) {
            return String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        }
        return "111111";
    }

    /**
     * 保存role
     * @param nbRole
     * @return
     * @throws ClientException
     */
    public NBRole saveRole(NBRole nbRole) throws ClientException {
        try {
            return roleRepository.save(nbRole);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
        //TODO 处理历史
    }

    @Override
    public NBRole getRoleByObjectRrn(Long objectRrn) throws ClientException {
        try {
            return (NBRole) roleRepository.findByObjectRrn(objectRrn);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public NBRole getRoleByRoleId(String roleId) throws ClientException {
        try {
            return roleRepository.findByRoleId(roleId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public NBRole getDeepRole(Long roleRrn, boolean authorityFlag, SessionContext sc) throws ClientException {
        try {
            return roleRepository.getDeepRole(roleRrn, authorityFlag, sc);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void deleteRole(NBRole nbRole) throws ClientException {
        try {
            roleRepository.delete(nbRole);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
