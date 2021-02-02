package com.newbiest.vanchip.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 单据验证规则
 */
@Entity
@Data
@Table(name="VC_MLOT_DOC_RULE")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 32)
public class MLotDocRule extends NBBase {

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(fetch= FetchType.EAGER, cascade={CascadeType.REMOVE})
    @JoinColumn(name = "RULE_RRN", referencedColumnName = "OBJECT_RRN")
    private List<MLotDocRuleLine> lines;


}