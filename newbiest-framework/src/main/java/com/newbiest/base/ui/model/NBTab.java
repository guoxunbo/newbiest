package com.newbiest.base.ui.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * TAB显示
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@Table(name="NB_TAB")
@Data
public class NBTab extends NBBase {

    private static final long serialVersionUID = 6867153457631553866L;

    /**
     * 以栏位的形式展现
     */
    public static final String TAB_TYPE_FIELD = "Field";

    /**
     * 以表格的形式展现
     */
    public static final String TAB_TYPE_TABLE = "Table";


    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="TABLE_RRN")
    private Long tableRrn;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="TAB_TYPE")
    private String tabType = TAB_TYPE_FIELD;

    /**
     * 当类型是table的时候需要绑定一下具体的动态表
     */
    @Column(name="REF_TABLE_NAME")
    private String refTableName;

    /**
     * 当类型是table的时候和主对象的关联关系
     * 比如 tableRrn = :objectRrn
     */
    @Column(name="WHERE_CLAUSE")
    private String whereClause;

    /**
     * 当类型是table的时候是否可以直接编辑表格
     */
    @Column(name="EDIT_FLAG")
    private String editFlag;


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

    @OneToMany(mappedBy = "tab", fetch= FetchType.LAZY,
            targetEntity = NBField.class)
    @OrderBy(value = "seqNo ASC")
    private List<NBField> fields;

    @ManyToOne
    @JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
    @JsonBackReference
    private NBTable table;

    public Boolean getEditFlag() {
        return StringUtils.YES.equalsIgnoreCase(editFlag);
    }

    public void setEditFlag(Boolean editFlag) {
        this.editFlag = editFlag ? StringUtils.YES : StringUtils.NO;
    }
}
