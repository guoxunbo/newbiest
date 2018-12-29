package com.newbiest.security.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.CollectionUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限表
 * Created by guoxunbo on 2017/9/7.
 */
@Entity
@Table(name="NB_AUTHORITY")
@Data
@NoArgsConstructor
public class NBAuthority extends NBBase{

    /**
     * 菜单
     */
    public static String AUTHORITY_TYPE_MENU = "M";

    /**
     * 按钮
     */
    public static String AUTHORITY_TYPE_BUTTON = "B";

    /**
     * 系统公共功能
     */
    public static String AUTHORITY_CATEGORY_FRAMEWORK = "Framework";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="AUTHORITY_CATEGORY")
    private String authorityCategory = AUTHORITY_CATEGORY_FRAMEWORK;

    @Column(name="AUTHORITY_TYPE")
    private String authorityType = AUTHORITY_TYPE_MENU;

    @Column(name="URL")
    private String url;

    @Column(name="TABLE_RRN")
    private Long tableRrn;

    @Column(name="PARENT_RRN")
    private Long parentRrn;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="LABEL_EN")
    private String labelEn;

    @Column(name="LABEL_ZH")
    private String labelZh;

    @Column(name="LABEL_RES")
    private String labelRes;

    @Column(name="IMAGE")
    private String image;

    @Transient
    private Long level;

    @Transient
    private String attribute1;

    @Transient
    private String attribute2;

    @Transient
    private String attribute3;

    @Transient
    private List<NBAuthority> subAuthorities;

    public NBAuthority recursionAuthority(NBAuthority parentAuthority, List<NBAuthority> authorities) {
        List<NBAuthority> subAuthorities = authorities.stream().filter(authority -> parentAuthority.getObjectRrn().equals(authority.getParentRrn())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(subAuthorities)) {
            subAuthorities.stream().forEach(subAuthority -> recursionAuthority(subAuthority, authorities));
        }
        parentAuthority.setSubAuthorities(subAuthorities);
        return parentAuthority;
    }

}
