package com.newbiest.security.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 角色
 * Created by guoxunbo on 2017/9/7.
 */
@Entity
@Table(name="NB_ROLE")
@Data
@NoArgsConstructor
public class NBRole extends NBUpdatable {

    @Column(name="ROLE_ID")
    private String roleId;

    @Column(name="DESCRIPTION")
    private String description;

    @ManyToMany(targetEntity = NBUser.class, fetch=FetchType.LAZY)
    @JoinTable(name = "NB_USER_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_RRN", referencedColumnName = "OBJECT_RRN"),
            inverseJoinColumns = @JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN"))
    private List<NBUser> users;

    @ManyToMany(targetEntity = NBAuthority.class, fetch=FetchType.LAZY)
    @JoinTable(name = "NB_ROLE_AUTHORITY",
            joinColumns = @JoinColumn(name = "ROLE_RRN", referencedColumnName = "OBJECT_RRN"),
            inverseJoinColumns = @JoinColumn(name = "AUTHORITY_RRN", referencedColumnName = "OBJECT_RRN"))
    private List<NBAuthority> authorities;

}
