package com.newbiest.ams.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.newbiest.base.core.ApplicationContextProvider;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.main.MailService;
import com.newbiest.security.model.NBRole;
import com.newbiest.security.model.NBUser;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by guoxunbo on 2019-11-19 14:25
 */
@Slf4j
public class AlarmEmailAction extends AlarmAction {

    @Override
    void triggerAction() throws ClientException {
        Set<String> emailAddress = Sets.newHashSet();

        MailService mailService = ApplicationContextProvider.getBean(MailService.class);
        if (mailService == null) {
            log.warn("MailService is not support in application. Please check");
            return;
        }

        if (!StringUtils.isNullOrEmpty(toUser)) {
            String[] toUsers = toUser.split(StringUtils.SEMICOLON_CODE);
            for (String user : toUsers) {
                NBUser nbUser = securityService.getUserByUsername(user);
                if (nbUser == null) {
                    log.warn("AlarmName [" + alarmData.getName() + "] send to [" + user + "] error. Because user is not exist.");
                    continue;
                }
                if (StringUtils.isNullOrEmpty(nbUser.getEmail())) {
                    log.warn("AlarmName [" + alarmData.getName() + "] send to [" + user + "] error. Because user email is empty.");
                    continue;
                }
                emailAddress.add(nbUser.getEmail());
            }
        }

        if (!StringUtils.isNullOrEmpty(toRole)) {
            String[] toRoles = toRole.split(StringUtils.SEMICOLON_CODE);
            for (String role : toRoles) {
                NBRole nbRole = securityService.getRoleByRoleId(role);
                if (nbRole == null) {
                    log.warn("AlarmName [" + alarmData.getName() + "] send to [" + role + "] error. Because role is not exist.");
                    continue;
                }
                nbRole = securityService.getDeepRole(nbRole.getObjectRrn());
                List<NBUser> userList = nbRole.getUsers();
                if (CollectionUtils.isNotEmpty(userList)) {
                    emailAddress.addAll(userList.stream().filter(user -> !StringUtils.isNullOrEmpty(user.getEmail())).map(NBUser::getEmail).collect(Collectors.toSet()));
                }
            }
        }
        mailService.sendSimpleMessage(Lists.newArrayList(emailAddress), alarmData.getTitle(), alarmData.getText());

    }
}
