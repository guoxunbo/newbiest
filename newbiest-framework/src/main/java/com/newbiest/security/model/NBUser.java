package com.newbiest.security.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.DateUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户
 * Created by guoxunbo on 2017/9/7.
 */
@Entity
@Table(name="NB_USER")
@Data
public class NBUser extends NBUpdatable {

    private static final long serialVersionUID = 1L;
    public static final String ADMIN_USER = "admin";

    @Column(name="USERNAME")
    private String username;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="SEX")
    private String sex;

    @Column(name="EMAIL")
    private String email;

    @Column(name="PHONE")
    private String phone;

    @Column(name="DEPARTMENT")
    private String department;

    @Column(name="PWD_CHANGED")
    @JsonFormat(timezone = "GMT+8",pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date pwdChanged;

    @Column(name="PWD_LIFE")
    private Long pwdLife;

    @Column(name="PWD_EXPIRY")
    @JsonFormat(timezone = "GMT+8",pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date pwdExpiry;

    @Column(name="PWD_WRONG_COUNT")
    private Integer pwdWrongCount;

    @Column(name="LAST_LOGON")
    @JsonFormat(timezone = "GMT+8",pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date lastLogon;

    /**
     * 是否处于密码有效期之内
     */
    @Column(name="IN_VALID_FLAG")
    private String inValidFlag;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "NB_USER_ROLE",
            inverseJoinColumns = @JoinColumn(name = "ROLE_RRN", referencedColumnName = "OBJECT_RRN"),
            joinColumns = @JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN"))
    @JsonBackReference("roles")
    private List<NBRole> roles;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name = "NB_USER_ORG",
            inverseJoinColumns = @JoinColumn(name = "ORG_RRN", referencedColumnName = "OBJECT_RRN"),
            joinColumns = @JoinColumn(name = "USER_RRN", referencedColumnName = "OBJECT_RRN"))
    @JsonBackReference("orgs")
    private List<NBOrg> orgs;

    @Transient
    private String newPassword;

    @Transient
    private List<NBAuthority> authorities;

    public Boolean getInValidFlag() {
        return "Y".equalsIgnoreCase(inValidFlag);
    }

    public void setInValidFlag(Boolean inValidFlag) {
        this.inValidFlag = inValidFlag ? "Y" : "N";
    }

}
