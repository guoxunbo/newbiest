package com.newbiest.mms.model;

import com.newbiest.context.model.MergeRule;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 物料批次合批规则容器管理
 */
@Entity
@DiscriminatorValue(MaterialLotMergeRule.CLASS_MATERIAL_LOT)
public class MaterialLotMergeRule extends MergeRule {

    public static final String CLASS_MATERIAL_LOT = "MaterialLot";

}
