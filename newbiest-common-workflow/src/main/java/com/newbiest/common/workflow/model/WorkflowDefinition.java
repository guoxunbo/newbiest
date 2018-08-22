package com.newbiest.common.workflow.model;

import com.newbiest.base.model.NBVersionControl;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * Created by guoxunbo on 2018/8/14.
 */
@Entity
@Table(name="WF_DEFINITION")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CATEGORY", discriminatorType = DiscriminatorType.STRING, length = 16)
@Data
public class WorkflowDefinition extends NBVersionControl {

    public static final String CATEGORY_TYPE_PROCESS = "Process";
    public static final String CATEGORY_TYPE_ROUTE = "Route";
    public static final String CATEGORY_TYPE_STEP = "Step";

    @Column(name="OWNER")
    private String owner;

    @Column(name="COMMENTS")
    private String comments;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="RESERVED1")
    private String reserved1;

    @Column(name="RESERVED2")
    private String reserved2;

    @Column(name="RESERVED3")
    private String reserved3;

    @Column(name="RESERVED4")
    private String reserved4;

    @Column(name="RESERVED5")
    private String reserved5;

    @Column(name="RESERVED6")
    private String reserved6;

    @Column(name="RESERVED7")
    private String reserved7;

    @Column(name="RESERVED8")
    private String reserved8;

    @Column(name="RESERVED9")
    private String reserved9;

    @Column(name="RESERVED10")
    private String reserved10;
}
