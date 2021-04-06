package com.newbiest.liquibase.config;

import com.newbiest.security.model.NBAuthority;
import lombok.Data;

/**
 * 创建权限配置
 * {@link NBAuthority}
 * Created by guoxunbo on 2020-02-27 18:02
 */
@Data
public class AuthorityConfig extends VCimAbstractLiquibaseConfig {

    public static final String SERIALIZE_OBJECT_NAME = "authority";

    private String name;

    private String description;

    private String authorityCategory = NBAuthority.AUTHORITY_CATEGORY_FRAMEWORK;

    private String authorityType = NBAuthority.AUTHORITY_TYPE_MENU;

    private String tableName;

    private String secondTableName;

    private Long tableRrn;

    private String url;

    private Long parentRrn;

    private Long seqNo;

    private String image;

    private String labelRes;

    private String labelZh;

    private String labelEn;

    private String label;

    private String mobileFlag;

    private String parameter1;

    private String parameter2;

    private String parameter3;

    private String parameter4;

    private String parameter5;

    @Override
    public String getSerializedObjectName() {
        return SERIALIZE_OBJECT_NAME;
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }
}
