package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 包装规则定义
 * Created by guoxunbo on 2019/4/2.
 */
@Table(name="MMS_PACKAGE_TYPE")
@Entity
@Data
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 32)
public abstract class PackageType extends NBUpdatable {

    public static final String CLASS_MATERIAL_LOT = "MaterialLot";

    /**
     * 包装之后就是数量就是1
     */
    public static final String COUNT_TYPE_ONE = "One";

    /**
     * 以选择的物料批次个数来产生数量
     */
    public static final String COUNT_TYPE_BY_LOT = "ByLot";

    /**
     * 以选择的物料批次上数量总和来作为数量
     */
    public static final String COUNT_TYPE_BY_LOT_QTY = "ByLotQty";

    @Column(name="NAME")
    protected String name;

    @Column(name="DESCRIPTION")
    protected String description;

    /**
     * 包装选用的ID生成规则
     */
    @Column(name="PACK_ID_RULE")
    protected String packIdRule;

    /**
     * 源物料类型 对选择的物料批次的包装类型
     *
     */
    @Column(name="SOURCE_MATERIAL_TYPE")
    protected String sourceMaterialType;

    /**
     * 目标物料类型 包装之后产生的包装批次的物料类型
     */
    @Column(name="TARGET_MATERIAL_TYPE")
    protected String targetMaterialType;

    /**
     * 包装后批次的数量计数类型
     */
    @Column(name="PACKED_COUNT_TYPE")
    protected String packedCountType = COUNT_TYPE_BY_LOT;

    /**
     * 包装前的物料批次数量计数类型
     */
    @Column(name="BEFORE_PACK_COUNT_TYPE")
    protected String beforePackCountType = COUNT_TYPE_BY_LOT;

    /**
     * 允许包装的最大数量
     */
    @Column(name="MAX_QTY")
    protected BigDecimal maxQty;


    /**
     * 验证是否包装
     * @param packageChildren 待验证的物料批次、批次
     */
    public abstract void validationPacking(List<? extends NBUpdatable> packageChildren);

    /**
     * 获取包装之后的物料批次的数量
     * @param packageChildren 待包装的物料批次、批次
     * @return
     */
    public abstract BigDecimal getPackedQty(List<? extends NBUpdatable> packageChildren);

}
