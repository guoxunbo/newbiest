package com.newbiest.security.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.DateUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户历史
 * Created by guoxunbo on 2017/10/5.
 */
@Entity
@Table(name="NB_USER_HIS")
@Data
@NoArgsConstructor
public class NBUserHis extends NBHis {

    public static final String TRANS_TYPE_RESET_PASSWORD = "ResetPassword";
    public static final String TRANS_TYPE_CHANGE_PASSWORD = "ChangePassword";

    public static final String TRANS_TYPE_LOGIN_SUCCESS = "LoginSuccess";
    public static final String TRANS_TYPE_LOGIN_FAIL = "LoginFail";

    /**
     * 默认密码有效期是300天
     */
    private static final Long PWD_LIFE = 300L;

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

    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date pwdChanged;

    @Column(name="PWD_LIFE")
    private Long pwdLife = PWD_LIFE;

    @Column(name="PWD_EXPIRY")
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date pwdExpiry;

    @Column(name="PWD_WRONG_COUNT")
    private Integer pwdWrongCount;

    @Column(name="LAST_LOGON")
    @JsonFormat(timezone = GMT_PE,pattern = DateUtils.DEFAULT_DATETIME_PATTERN)
    private Date lastLogon;

    /**
     * 是否处于密码有效期之内
     */
    @Column(name="IN_VALID_FLAG")
    private String inValidFlag;

    @Column(name="ROLE_LIST")
    private String roleList;

    @Column(name="ORG_LIST")
    private String orgList;

    public NBUserHis(NBUser user, SessionContext sc) {
        super(user, sc);
    }

    @Override
    public void setNbBase(NBBase base, SessionContext sc) {
        super.setNbBase(base, sc);
        NBUser user = (NBUser) base;
        List<NBRole> roles = user.getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            String roleStr = "";
            List<String> roleNames = user.getRoles().stream().map(NBRole :: getRoleId).collect(Collectors.toList());
            roleStr = StringUtils.join(roleNames, StringUtils.SPLIT_CODE);
            setRoleList(roleStr);
        }

        List<NBOrg> orgs = user.getOrgs();
        if (CollectionUtils.isNotEmpty(orgs)) {
            String orgStr = "";
            List<String> orgNames = user.getOrgs().stream().map(NBOrg::getName).collect(Collectors.toList());
            orgStr = StringUtils.join(orgNames, StringUtils.SPLIT_CODE);
            setOrgList(orgStr);
        }
    }

    public Boolean getInValidFlag() {
        return "Y".equalsIgnoreCase(inValidFlag);
    }

    public void setInValidFlag(Boolean inValidFlag) {
        this.inValidFlag = inValidFlag ? "Y" : "N";
    }
}
