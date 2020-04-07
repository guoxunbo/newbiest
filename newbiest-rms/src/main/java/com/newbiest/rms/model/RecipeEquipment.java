package com.newbiest.rms.model;

import com.google.common.collect.Lists;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="RMS_RECIPE_EQUIPMENT")
@Data
public class RecipeEquipment extends NBVersionControl {

    private static final long serialVersionUID = 6091706887024508280L;

    public static final String HOLD_STATE_ON = "On";
    public static final String HOLD_STATE_OFF = "Off";

    /**
     * Normal 正常Recipe，默认的RecipeMode。
     */
    public static final String PATTERN_NORMAL = "Normal";

    public static final String ACTIVE_TYPE_BY_LOT = "ByLot";
    public static final String ACTIVE_TYPE_BY_WAFER = "ByWafer";

    public static final String CONTEXT_RECIPE_EQUIPMENT= "RecipeEquipment";

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="EQUIPMENT_TYPE")
    private String equipmentType;

    @Column(name="HOLD_STATE")
    private String holdState = HOLD_STATE_OFF;

    /**
     * Recipe Body(二进制内容)
     */
    @Column(name="BODY")
    private String body;

    /**
     * Recipe时间戳
     */
    @Column(name="TIMESTAMP")
    private Date timestamp;

    @Column(name="CHECK_SUM")
    private String checkSum;

    @Column(name="LEVEL_NUMBER")
    private Integer levelNumber;

    @Column(name="GOLDEN_FLAG")
    private String goldenFlag;

    /**
     * 激活类型(ByWafer/ByLot)
     */
    @Column(name="ACTIVE_TYPE")
    private String activeType = ACTIVE_TYPE_BY_LOT;

    /**
     * 当前Recipe所处在的模式。不同模式下只能有一个Recipe是激活的
     */
    @Column(name="PATTERN")
    private String pattern = PATTERN_NORMAL;

    /**
     * 验证Body.此处的Body不是Parameter，而是一个二进制码
     */
    @Column(name="CHECK_BODY_FLAG")
    private String checkBodyFlag;

    @Column(name="CHECK_SUM_FLAG")
    private String checkSumFlag;

    /**
     * 验证Parameter
     */
    @Column(name="CHECK_PARAMETER_FLAG")
    private String checkParameterFlag;

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

    @Transient
    private List<RecipeEquipmentParameter> recipeEquipmentParameters = Lists.newArrayList();

    @Transient
    private List<RecipeEquipment> subRecipeEquipments;

    @Transient
    private int layers = 0;


    public Boolean getGoldenFlag() {
        return StringUtils.YES.equalsIgnoreCase(this.goldenFlag) ? true : false;
    }

    public void setGoldenFlag(Boolean goldenFlag) {
        this.goldenFlag = goldenFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getCheckBodyFlag() {
        return StringUtils.YES.equalsIgnoreCase(checkBodyFlag);
    }

    public void setCheckBodyFlag(Boolean checkBodyFlag) {
        this.checkBodyFlag = checkBodyFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getCheckSumFlag() {
        return StringUtils.YES.equalsIgnoreCase(checkSumFlag);
    }

    public void setCheckSumFlag(Boolean checkSumFlag) {
        this.checkSumFlag = checkSumFlag ? StringUtils.YES : StringUtils.NO;
    }

    public Boolean getCheckParameterFlag() {
        return StringUtils.YES.equalsIgnoreCase(checkParameterFlag);
    }

    public void setCheckParameterFlag(Boolean checkParameterFlag) {
        this.checkParameterFlag = checkParameterFlag ? StringUtils.YES : StringUtils.NO;
    }
}
