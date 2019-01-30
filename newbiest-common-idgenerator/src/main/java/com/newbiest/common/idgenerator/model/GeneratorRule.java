package com.newbiest.common.idgenerator.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.base.utils.CollectionUtils;
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

    /**
     * 根据ruleLine生成一个临时代表的字符串
     * 比如#AyyMMdd001 parameter类型用#代替
     */
    @Transient
    private String generatorTempStr;

    @OneToMany(fetch= FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "RULE_RRN", referencedColumnName = "OBJECT_RRN")
    @OrderBy(value="seqNo ASC")
    private List<GeneratorRuleLine> ruleLines;

    public void generatorTempStr() {
        if (CollectionUtils.isNotEmpty(ruleLines)) {
            //TODO 生成临时字符串
            generatorTempStr = "";
        }
    }
}
