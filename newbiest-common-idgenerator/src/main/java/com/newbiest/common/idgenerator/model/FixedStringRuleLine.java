package com.newbiest.common.idgenerator.model;

import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 固定字符串的类型
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@DiscriminatorValue(GeneratorRuleLine.DATA_TYPE_FIXED_STRING)
@Data
public class FixedStringRuleLine extends GeneratorRuleLine{

    @Column(name="FIXED_STRING")
    private String fixedString;

    @Override
    public String generator(GeneratorContext context) throws Exception {
        if (StringUtils.isNullOrEmpty(fixedString)) {
            return GENERATOR_ERROR_CODE;
        }
        return fixedString;
    }

}
