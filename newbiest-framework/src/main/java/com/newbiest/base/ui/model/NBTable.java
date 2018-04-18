package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * UI基本模板数据表格来源
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_TABLE")
@Data
public class NBTable extends NBBase {

    private static final long serialVersionUID = -8294980293795481105L;

    /**
     * 是否是视图 如果是视图则用SQL查询 不是则用model查询
     */
    private static final int STYLE_VIEW = 1 << 0;

    /**
     * 是否垂直显示 默认左右显示
     */
    private static final int STYLE_VERTICAL = 1 << 1;

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
    private Integer style = STYLE_VERTICAL;

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

    @Column(name="ORDER_BY_CLAUSE")
    private String orderByClause;

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
    private String label_zh;

    /**
     * 其它语言标签
     */
    @Column(name="LABEL_RES")
    private String label_res;

    @OneToMany(mappedBy = "table", fetch=FetchType.LAZY,
            targetEntity = NBTab.class)
    @OrderBy(value = "seqNo ASC")
    private List<NBTab> tabs;

    @OneToMany(mappedBy = "table", fetch=FetchType.LAZY,
            targetEntity = NBField.class)
    @OrderBy(value = "seqNo ASC")
    private List<NBField> fields;

    public Boolean getView() {
        return (style & STYLE_VIEW) == STYLE_VIEW;
    }

    public void setView(Boolean view) {
        if (view) {
            style |= STYLE_VIEW;
        } else {
            style &= ~STYLE_VIEW;
        }
    }

    public Boolean getVertical() {
        return (style & STYLE_VERTICAL) == STYLE_VERTICAL;
    }

    public void setVertical(Boolean isVertical) {
        if (isVertical) {
            style |= STYLE_VERTICAL;
        } else {
            style &= ~STYLE_VERTICAL;
        }
    }

}
