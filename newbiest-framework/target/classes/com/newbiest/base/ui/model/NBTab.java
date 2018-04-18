package com.newbiest.base.ui.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.newbiest.base.model.NBBase;
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
     * 在Tab中显示(默认风格)
     */
    public static final String TAB_TYPE_TAB  = "Tab";

    /**
     * 上下衔接风格
     */
    public static final String TAB_TYPE_SECTION = "Section";


    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="TABLE_RRN")
    private Long tableRrn;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="TAB_TYPE")
    private String tabType = TAB_TYPE_TAB;

    @Column(name="HEIGHT_HINT")
    private Long heightHint;

    /**
     * 定义Tab页面的列数
     */
    @Column(name="GRID_Y")
    private Long gridY;

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

    @OneToMany(mappedBy = "tab", fetch= FetchType.LAZY,
            targetEntity = NBField.class)
    @OrderBy(value = "seqNo ASC")
    private List<NBField> fields;

    @ManyToOne
    @JoinColumn(name = "TABLE_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
    @JsonBackReference
    private NBTable table;

}
