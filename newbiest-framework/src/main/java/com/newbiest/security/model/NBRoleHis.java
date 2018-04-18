package com.newbiest.security.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色
 * Created by guoxunbo on 2017/9/7.
 */
@Entity
@Table(name="NB_ROLE_HIS")
@Data
@NoArgsConstructor
public class NBRoleHis extends NBHis {

    @Column(name="ROLE_ID")
    private String roleId;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="USERS")
    private String users;

    @Column(name="AUTHORITIES")
    private String authorities;

    @Override
    public void setNbBase(NBBase base, SessionContext sc) {
        super.setNbBase(base, sc);
        NBRole role = (NBRole) base;
        List<NBUser> users = role.getUsers();
        if (CollectionUtils.isNotEmpty(users)) {
            String userStr = "";
            List<String> userNames = users.stream().map(NBUser :: getUsername).collect(Collectors.toList());
            userStr = StringUtils.join(userNames, StringUtils.SPLIT_CODE);
            setUsers(userStr);
        }

        List<NBAuthority> authorities = role.getAuthorities();
        if (CollectionUtils.isNotEmpty(authorities)) {
            String authorityStr = "";
            List<String> orgNames = authorities.stream().map(NBAuthority::getName).collect(Collectors.toList());
            authorityStr = StringUtils.join(orgNames, StringUtils.SPLIT_CODE);
            setAuthorities(authorityStr);
        }
    }

}
