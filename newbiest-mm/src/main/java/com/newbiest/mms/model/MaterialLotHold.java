package com.newbiest.mms.model;

import com.newbiest.base.model.NBAction;
import com.newbiest.mms.dto.MaterialLotAction;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 物料批次Hold信息
 * @author guoxunbo
 * @date 2/2/21 5:28 PM
 */
@Data
@Entity
@Table(name="MMS_MATERIAL_LOT_HOLD")
public class MaterialLotHold extends NBAction {

    //IQC Hold code
    public static final String IQC_HOLD = "IQC_HOLD";

    //OQC Hold code
    public static final String OQC_HOLD = "OQC_HOLD";

    //装箱检验 Hold code
    public static final String PACK_CHECK_HOLD = "PACK_CHECK_HOLD";

    //物料过期hold code
    public static final String EXPIRE_HOLD = "EXPIRE_HOLD";

    @Column(name="MATERIAL_LOT_RRN")
    private String materialLotRrn;

    /**
     * 物料批次号
     */
    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private String materialRrn;

    /**
     * 物料名称
     */
    @Column(name="MATERIAL_NAME")
    private String materialName;

    /**
     * 物料版本
     */
    @Column(name="MATERIAL_VERSION")
    private Long materialVersion;

    /**
     * 物料描述
     */
    @Column(name="MATERIAL_DESC")
    private String materialDesc;

    /**
     * 物料类别
     */
    @Column(name="MATERIAL_CATEGORY")
    private String materialCategory;

    /**
     * 物料类型
     */
    @Column(name="MATERIAL_TYPE")
    private String materialType;

    @Column(name="UNIT_ID")
    private String unitId;

    @Column(name="UNIT_RRN")
    private String unitRrn;

    public MaterialLotHold setMaterialLot(MaterialLot materialLot) {
        this.setMaterialLotId(materialLot.getMaterialLotId());
        this.setMaterialLotRrn(materialLot.getObjectRrn());
        this.setMaterialRrn(materialLot.getMaterialRrn());
        this.setMaterialName(materialLot.getMaterialName());
        this.setMaterialDesc(materialLot.getMaterialDesc());
        this.setMaterialVersion(materialLot.getMaterialVersion());
        this.setMaterialCategory(materialLot.getMaterialCategory());
        this.setMaterialType(materialLot.getMaterialType());
        return this;
    }

    public MaterialLotHold setAction(MaterialLotAction materialLotAction) {
        this.setActionCode(materialLotAction.getActionCode());
        this.setActionReason(materialLotAction.getActionReason());
        this.setActionComment(materialLotAction.getActionComment());
        return this;
    }

}
