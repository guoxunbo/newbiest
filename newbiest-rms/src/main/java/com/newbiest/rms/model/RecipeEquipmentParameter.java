package com.newbiest.rms.model;

import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.rms.exception.RmsException;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="RMS_RECIPE_EQUIPMENT_PARAMETER")
@Data
@NoArgsConstructor
public class RecipeEquipmentParameter extends NBBase {

    public static final String VALIDATE_TYPE_NONE = "None";
    public static final String VALIDATE_TYPE_EXACT = "Exact";
    public static final String VALIDATE_TYPE_RANGE = "Range";

    public static final String DATE_TYPE_STRING = "String";
    public static final String DATE_TYPE_INTEGER = "Integer";
    public static final String DATE_TYPE_DOUBLE = "Double";

    public static final String GROUP_DEFAULT = "Default";

    @Column(name="RECIPE_EQUIPMENT_RRN")
    private Long recipeEquipmentRrn;

    @Column(name="PARAMETER_NAME")
    private String parameterName;

    @Column(name="PARAMETER_DESC")
    private String parameterDesc;

    @Column(name="PARAMETER_GROUP")
    private String parameterGroup = GROUP_DEFAULT;

    @Column(name="SEQ_NO")
    private Long seqNo;

    @Column(name="DATA_TYPE")
    private String dataType = DATE_TYPE_STRING;

    @Column(name="COMPARE_FLAG")
    private String compareFlag = StringUtils.NO;

    @Column(name="VALIDATE_TYPE")
    private String validateType = VALIDATE_TYPE_EXACT;

    @Column(name = "MIN_VALUE")
    private String minValue;

    @Column(name = "MAX_VALUE")
    private String maxValue;

    @Column(name = "CURRENT_VALUE")
    private String currentValue;

    /**
     * 是否允许onLine修改
     */
    @Column(name = "SPECIAL_PARAMETER_FLAG")
    private String specialParameterFlag = StringUtils.NO;

    @Column(name = "RESERVED1")
    private String reserved1;

    @Column(name = "RESERVED2")
    private String reserved2;

    @Column(name = "RESERVED3")
    private String reserved3;

    @Column(name = "RESERVED4")
    private String reserved4;

    @Column(name = "RESERVED5")
    private String reserved5;

    public Boolean getCompareFlag() {
        return compareFlag.equals(StringUtils.YES);
    }

    public void setCompareFlag(Boolean compareFlag) {
        this.compareFlag = compareFlag ? StringUtils.YES : StringUtils.NO;
    }


    public Boolean getSpecialParameterFlag() {
        return StringUtils.YES.equalsIgnoreCase(specialParameterFlag);
    }

    public void setSpecialParameterFlag(Boolean specialParameterFlag) {
        this.specialParameterFlag = specialParameterFlag ? StringUtils.YES : StringUtils.NO;
    }

    public String getFullName() {
        return this.parameterGroup + StringUtils.UNDERLINE_CODE + this.parameterName;
    }

    public void compare(RecipeEquipmentParameter other) throws Exception {
        if (!StringUtils.isNullOrEmpty(currentValue) && !StringUtils.isNullOrEmpty(other.getCurrentValue())) {
            if (VALIDATE_TYPE_EXACT.equals(getValidateType())) {
                exactValidate(other);
            } else if (VALIDATE_TYPE_RANGE.equals(getValidateType())) {
                rangeValidate(other);
            }
        } else {
            throw new ClientParameterException(RmsException.RECIPE_PARAMETER_VALUE_IS_NOT_EXIST, getRecipeEquipmentRrn(), getFullName(), currentValue, other.getCurrentValue());
        }
    }

    public void exactValidate(RecipeEquipmentParameter other) throws Exception {
        if (RecipeEquipmentParameter.DATE_TYPE_STRING.equalsIgnoreCase(getValidateType())) {
            validateString(other);
        } else if (RecipeEquipmentParameter.DATE_TYPE_DOUBLE.equalsIgnoreCase(getValidateType())) {
            validateDouble(other);
        } else if (RecipeEquipmentParameter.DATE_TYPE_INTEGER.equalsIgnoreCase(getValidateType())) {
            validateInt(other);
        }
    }

    public void rangeValidate(RecipeEquipmentParameter other) throws Exception {
        if (RecipeEquipmentParameter.DATE_TYPE_STRING.equalsIgnoreCase(getValidateType())) {
            validateString(other);
        } else if (RecipeEquipmentParameter.DATE_TYPE_DOUBLE.equalsIgnoreCase(getValidateType()) || RecipeEquipmentParameter.DATE_TYPE_INTEGER.equalsIgnoreCase(getValidateType())) {
            validateRange(other);
        }
    }

    /**
     * 比较string类型的参数 不支持range
     * @param other
     * @return
     */
    public void validateString(RecipeEquipmentParameter other) throws Exception {
        //比较字符串
        if (!currentValue.equals(other.getCurrentValue())) {
           throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_SAME, getRecipeEquipmentRrn(), getFullName(), currentValue, other.getCurrentValue());
        }
    }

    public void validateDouble(RecipeEquipmentParameter other) throws Exception {
        try {
            double value = Double.parseDouble(getCurrentValue());
            double checkValue = Double.parseDouble(other.getCurrentValue());
            if (value != checkValue) {
                throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_SAME, getRecipeEquipmentRrn(), getFullName(), getCurrentValue(), other.getCurrentValue());
            }
        } catch (NumberFormatException e) {
            throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_NUMBER_FORMAT, getRecipeEquipmentRrn(), getFullName(), getCurrentValue(), other.getCurrentValue());
        }
    }

    public void validateInt(RecipeEquipmentParameter other) throws Exception {
        try {
            // 出现类似20.0 转 int报错
            double doubleValue = Double.parseDouble(getCurrentValue());
            int value = (int)doubleValue;

            double doubleCheckValue = Double.parseDouble(other.getCurrentValue());
            int checkValue = (int)doubleCheckValue;
            if (value != checkValue) {
                throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_SAME, getRecipeEquipmentRrn(), getFullName(), getCurrentValue(), other.getCurrentValue());
            }
        } catch (NumberFormatException e) {
            throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_NUMBER_FORMAT, getRecipeEquipmentRrn(), getFullName(), getCurrentValue(), other.getCurrentValue());
        }
    }

    public void validateRange(RecipeEquipmentParameter other) throws Exception {
        try {
            double checkValue = Double.parseDouble(other.getCurrentValue());
            Double maxValue =  null;
            Double minValue = null;
            if (!StringUtils.isNullOrEmpty(getMaxValue()) || !StringUtils.isNullOrEmpty(getMinValue())) {
                if (!StringUtils.isNullOrEmpty(getMaxValue()) && !StringUtils.isNullOrEmpty(getMinValue())) {
                    maxValue = Double.parseDouble(getMaxValue());
                    minValue = Double.parseDouble(getMinValue());
                    if (checkValue > maxValue || checkValue < minValue) {
                        throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_IN_RANGE, getRecipeEquipmentRrn(), getFullName(), getMaxValue(), getMinValue(), other.getCurrentValue());
                    }
                } else if (!StringUtils.isNullOrEmpty(getMaxValue())) {
                    maxValue = Double.parseDouble(getMaxValue());
                    if (checkValue > maxValue) {
                        throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_IN_RANGE, getRecipeEquipmentRrn(), getFullName(), getMaxValue(), other.getCurrentValue());
                    }
                } else if (!StringUtils.isNullOrEmpty(getMinValue())) {
                    minValue = Double.parseDouble(getMinValue());
                    if (checkValue < minValue) {
                        throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_IN_RANGE, getRecipeEquipmentRrn(), getFullName(), getMinValue(), other.getCurrentValue());
                    }
                }
            } else {
                // 没有设置上限限 则都表示正常Do Nothing
            }
        } catch (NumberFormatException e) {
            throw new ClientParameterException(RmsException.RECIPE_PARAMETER_NOT_NUMBER_FORMAT, getRecipeEquipmentRrn(), getFullName(), getMaxValue(), getMinValue(), other.getCurrentValue());
        }
    }
}
