package com.newbiest.context.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 合批规则定义
 */
@Entity
@Data
@Table(name="COM_MERGE_RULE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 32)
public class MergeRule extends NBBase {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @OneToMany(fetch= FetchType.EAGER, cascade={CascadeType.REMOVE})
    @JoinColumn(name = "RULE_RRN", referencedColumnName = "OBJECT_RRN")
    private List<MergeRuleLine> lines;

}
