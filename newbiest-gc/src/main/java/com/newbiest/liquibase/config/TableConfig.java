package com.newbiest.liquibase.config;

import com.newbiest.base.ui.model.NBTable;
import lombok.Data;

/**
 * 创建权限配置
 * {@link NBTable}
 * Created by guozhangLuo on 2020-08-18
 */
@Data
public class TableConfig extends VCimAbstractLiquibaseConfig {

    public static final String SERIALIZE_OBJECT_NAME = "nbTable";

    /**
     * 是否垂直显示 默认左右显示
     */
    private static final int STYLE_VERTICAL = 1 << 1;

    private String name;

    private String description;

    private String category;

    private String tableName;

    private Integer style = STYLE_VERTICAL;

    private String modelName;

    private String modelClass;

    private String whereClause;

    private String orderBy;

    private String initWhereClause;

    private Long gridYBasic;

    private Long gridYQuery;

    private String label;

    private String labelZh;

    private String labelRes;

    @Override
    public String getSerializedObjectName() {
        return SERIALIZE_OBJECT_NAME;
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }
}
