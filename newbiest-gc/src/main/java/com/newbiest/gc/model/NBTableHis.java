package com.newbiest.gc.model;

import com.newbiest.base.model.NBHis;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

/**
 * UI基本模板数据表格来源历史
 * Created by guozhangLuo on 2020/10/26.
 */
@Entity
@Table(name="NB_TABLE_HIS")
@Data
@EqualsAndHashCode(callSuper = false)
public class NBTableHis extends NBHis {

    private static final long serialVersionUID = -8294980293795481105L;

    /**
     * 名称
     */
    @Column(name="NAME")
    private String name;

    /**
     * 描述
     */
    @Column(name="DESCRIPTION")
    private String description;

    /**
     * 模块
     */
    @Column(name="CATEGORY")
    private String category;

    /**
     * 对应的数据库表
     */
    @Column(name="TABLE_NAME")
    private String tableName;

    @Column(name="STYLE")
    private Integer style;

    /**
     * 对应的Java Model的名称
     */
    @Column(name="MODEL_NAME")
    private String modelName;

    /**
     * 对应的Java Model的全称,包括package
     */
    @Column(name="MODEL_CLASS")
    private String modelClass;

    /**
     * 查询条件，每次查询都会带上
     */
    @Column(name="WHERE_CLAUSE")
    private String whereClause;

    @Column(name="ORDER_BY")
    private String orderBy;

    /**
     * 初始的查询条件,只在刚生产动态页面时有效
     */
    @Column(name="INIT_WHERE_CLAUSE")
    private String initWhereClause;

    /**
     * 定义动态页面中基本信息块的列数
     */
    @Column(name="GRID_Y_BASIC")
    private Long gridYBasic;

    /**
     * 定义查询页面中查询条件块的列数
     */
    @Column(name="GRID_Y_QUERY")
    private Long gridYQuery;

    /**
     * 英文标签
     */
    @Column(name="LABEL")
    private String label;

    /**
     * 中文标签
     */
    @Column(name="LABEL_ZH")
    private String labelZh;

    /**
     * 其它语言标签
     */
    @Column(name="LABEL_RES")
    private String labelRes;

}
