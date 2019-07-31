package com.newbiest.context.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 合批规则详情
 */
@Entity
@Table(name="COM_MERGE_RULE_LINE")
@Data
public class MergeRuleLine extends NBBase  {

    public static final String COMPARISON_OPERATORS_EQUALS = "Equals";

    @Column(name="RULE_RRN")
    private Long ruleRrn;

    /**
     * 栏位名称
     */
    @Column(name="FILED_NAME")
    private String filedName;

    /**
     * 表达式
     */
    @Column(name="COMPARISON_OPERATORS")
    private String comparisonOperators = COMPARISON_OPERATORS_EQUALS;

}

