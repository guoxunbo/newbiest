package com.newbiest.rms.model;

import com.google.common.collect.Lists;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBVersionControl;
import com.newbiest.base.threadlocal.ThreadLocalContext;
import com.newbiest.base.utils.StringUtils;
import lombok.Data;
import org.hibernate.annotations.Where;

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

    public static final int RECIPE_TYPE_BODY = 1 << 0;
    public static final int RECIPE_TYPE_TIMESTAMP = 1 << 1;
    public static final int RECIPE_TYPE_CHECKSUM = 1 << 2;
    public static final int RECIPE_TYPE_PARAMETER = 1 << 3;

    /**
     * Normal 正常Recipe，默认的RecipeMode。
     */
    public static final String PATTERN_NORMAL = "Normal";

    public static final String CONTEXT_RECIPE_EQUIPMENT= "RecipeEquipment";

    @Column(name="name")
    private String name;

    @Column(name="VERSION")
    private Long version;

    @Column(name="STATUS")
    private String status;

    @Column(name="RECIPE_TYPE")
    private Integer recipeType = RECIPE_TYPE_PARAMETER;

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
    private String activeType;

    /**
     * 当前Recipe所处在的模式。不同模式下只能有一个Recipe是激活的
     */
    @Column(name="PATTERN")
    private String pattern = PATTERN_NORMAL;

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

    // 只在删除的时候做级联
    @OneToMany(fetch= FetchType.LAZY, cascade={CascadeType.REMOVE})
    @OrderBy(value = "seqNo ASC")
    @JoinColumn(name = "RECIPE_EQUIPMENT_RRN", referencedColumnName = "OBJECT_RRN")
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

    public Boolean getBodyFlag(){
        return (recipeType & RECIPE_TYPE_BODY) == RECIPE_TYPE_BODY;
    }

    public void setBodyFlag(Boolean bodyFlag) {
        if (bodyFlag) {
            recipeType |= RECIPE_TYPE_BODY;
        } else {
            recipeType &= ~RECIPE_TYPE_BODY;
        }
    }

    public Boolean getTimestampFlag(){
        return (recipeType & RECIPE_TYPE_TIMESTAMP) == RECIPE_TYPE_TIMESTAMP;
    }

    public void setTimestampFlag(Boolean timestampFlag) {
        if (timestampFlag) {
            recipeType |= RECIPE_TYPE_TIMESTAMP;
        } else {
            recipeType &= ~RECIPE_TYPE_TIMESTAMP;
        }
    }

    public Boolean getCheckSumFlag(){
        return (recipeType & RECIPE_TYPE_CHECKSUM) == RECIPE_TYPE_CHECKSUM;
    }

    public void setCheckSumFlag(Boolean checkSumFlag) {
        if (checkSumFlag) {
            recipeType |= RECIPE_TYPE_CHECKSUM;
        } else {
            recipeType &= ~RECIPE_TYPE_CHECKSUM;
        }
    }

    public Boolean getParameterFlag(){
        return (recipeType & RECIPE_TYPE_PARAMETER) == RECIPE_TYPE_PARAMETER;
    }

    public void setParameterFlag(Boolean parameterFlag) {
        if (parameterFlag) {
            recipeType |= RECIPE_TYPE_PARAMETER;
        } else {
            recipeType &= ~RECIPE_TYPE_PARAMETER;
        }
    }

}
