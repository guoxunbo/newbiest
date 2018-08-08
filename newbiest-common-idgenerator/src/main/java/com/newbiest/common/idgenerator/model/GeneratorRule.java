package com.newbiest.common.idgenerator.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@Table(name = "COM_GENERATOR_RULE")
@Data
public class GeneratorRule extends NBUpdatable {

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "RULE_TYPE")
    private String ruleType;

    @OneToMany(fetch= FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RULE_RRN", referencedColumnName = "OBJECT_RRN")
    @OrderBy(value="seqNo ASC")
    private List<GeneratorRuleLine> ruleLines;

}
