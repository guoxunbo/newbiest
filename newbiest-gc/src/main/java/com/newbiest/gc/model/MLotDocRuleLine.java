package com.newbiest.gc.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 物料单据比对详细信息
 */
@Entity
@Table(name="GC_MLOT_DOC_RULE_LINE")
@Data
public class MLotDocRuleLine extends NBBase  {

    public static final String COMPARISON_OPERATORS_EQUALS = "Equals";

    public static final String COMPARISON_OPERATORS_NULL_OR_EQUALS = "NullOrEquals";

    @Column(name="RULE_RRN")
    private Long ruleRrn;

    /**
     * 源栏位名称
     */
    @Column(name="SOURCE_FILED_NAME")
    private String sourceFiledName;

    /**
     * 目标栏位名称
     */
    @Column(name="TARGET_FILED_NAME")
    private String targetFiledName;

    /**
     * 表达式
     */
    @Column(name="COMPARISON_OPERATORS")
    private String comparisonOperators = COMPARISON_OPERATORS_EQUALS;

}

