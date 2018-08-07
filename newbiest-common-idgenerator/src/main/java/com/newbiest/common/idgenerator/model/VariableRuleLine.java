package com.newbiest.common.idgenerator.model;

import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 参数类型的值
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@DiscriminatorValue(GeneratorRuleLine.DATA_TYPE_VARIABLE)
@Data
@Slf4j
public class VariableRuleLine extends GeneratorRuleLine {

    /**
     * 变量从Context的parameterMap中取值 如果没有对应的Parameter,也可以从Context的Object上取值
     */
    public static final String VARIABLE_TYPE_PARAMETER = "Parameter";

    /**
     * 从DB栏位上取值
     */
    public static final String VARIABLE_TYPE_DB_VALUE = "DBValue";

    /**
     * 从左到右截取
     */
    public static final String VARIABLE_DIRECTION_LEFT = "left";

    /**
     * 从右到左截取
     */
    public static final String VARIABLE_DIRECTION_RIGHT = "Right";

    @Column(name="VARIABLE_TYPE")
    private String variableType = VARIABLE_TYPE_PARAMETER;

    @Column(name="PARAMETER")
    private String parameter;

    @Column(name="TABLE_NAME")
    private String tableName;

    @Column(name="COLUMN_NAME")
    private String columnName;

    @Column(name="WHERE_CLAUSE")
    private String whereClause;

    @Column(name="VARIABLE_DIRECTION")
    private String variableDirection = VARIABLE_DIRECTION_LEFT;

    @Column(name="LENGTH")
    private Long length = 1L;

    /**
     * 如果超出长度，默认从第一位开始截取
     */
    @Column(name="START_POSITION")
    private Long startPosition = 1L;

    @Column(name="PLACEHOLDER")
    private String placeholder;

    @Override
    public String generator(GeneratorContext context) throws Exception {
        String variable = GENERATOR_ERROR_CODE;
        if (VARIABLE_TYPE_PARAMETER.equals(variableType)) {
            if (context.getParameterMap().containsKey(parameter)) {
                variable = String.valueOf(context.getParameter(parameter));
            } else if (context.getObject() != null) {
                //如果没有在Context的parameterMap中定义,则从Object中取值
                Object value;
                try {
                    value = PropertyUtils.getProperty(context.getObject(), parameter);
                    if (value != null) {
                        variable = String.valueOf(value);
                    }
                } catch (Exception e) {
                    // 此处异常不处理
                    log.warn(e.getMessage(), e);
                }
            }
        } else if (VARIABLE_TYPE_DB_VALUE.equals(variableType)) {
            // TODO 暂不支持从db上查值
        }

        if (!GENERATOR_ERROR_CODE.equals(variable)) {
            // 超过则截取相应的字符
            if (length != null && length > 0) {
                if (variable.length() > length) {
                    if (VARIABLE_DIRECTION_LEFT.equals(variableDirection)) {
                        variable = variable.substring((startPosition.intValue() - 1), (startPosition.intValue() + length.intValue() - 1));
                    } else if (VARIABLE_DIRECTION_RIGHT.equals(variableDirection)) {
                        variable = variable.substring(variable.length() - (startPosition.intValue() + length.intValue() - 1), variable.length() - startPosition.intValue() + 1);
                    }
                }
            } else if (variable.length() < length) {
                // 不够则进行补位
                if (VARIABLE_DIRECTION_LEFT.equals(variableDirection)) {
                    variable = StringUtils.padStart(variable, length.intValue(), placeholder.charAt(0));
                } else if (VARIABLE_DIRECTION_LEFT.equals(variableDirection)) {
                    variable = StringUtils.padEnd(variable, length.intValue(), placeholder.charAt(0));
                }
            }
        }

        if (!StringUtils.isNullOrEmpty(referenceName)) {
            variable = getReferenceValue(variable, context);
        }
        return variable;
    }

}
