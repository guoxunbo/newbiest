package com.newbiest.base.ui.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统栏位参考表 数据来源于另外表
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_REFERENCE_TABLE")
@Data
@EqualsAndHashCode(callSuper = true)
public class NBReferenceTable extends NBBase {

    private static final long serialVersionUID = -2393311151290133616L;

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    /**
     * 参考表所对应NBTable的ObjectRrn
     */
    @Column(name="TABLE_RRN")
    private Long tableRrn;

    /**
     * 参考表所对应Key栏位,保存到数据库中的值
     */
    @Column(name="KEY_FIELD")
    private String keyField;

    /**
     * 参考表所对应Text栏位,显示在页面上的值
     */
    @Column(name="TEXT_FIELD")
    private String textField;

    @Column(name="WHERE_CLAUSE")
    private String whereClause;

    @Column(name="ORDER_BY")
    private String orderBy;

}
