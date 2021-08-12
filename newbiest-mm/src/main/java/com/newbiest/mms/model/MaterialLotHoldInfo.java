package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import com.newbiest.mms.dto.MaterialLotAction;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 物料批次Hold信息
 * 支持多重Hold
 * @author guoxunbo
 * @date 2021/8/6 4:47 下午
 */
@Data
@Table(name="MMS_MATERIAL_LOT_HOLD_INFO")
@Entity
public class MaterialLotHoldInfo extends NBUpdatable {

    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

    /**
     * 物料主键
     */
    @Column(name="MATERIAL_RRN")
    private Long materialRrn;

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

    /**
     * 库存单位
     */
    @Column(name="STORE_UOM")
    private String storeUom;

    @Column(name="ACTION_CODE")
    private String actionCode;

    @Column(name="ACTION_REASON")
    private String actionReason;

    @Column(name="ACTION_COMMENT")
    private String actionComment;

    public void setMaterialLot(MaterialLot materialLot) {
        this.materialLotId = materialLot.getMaterialLotId();
        this.setMaterialRrn(materialLot.getMaterialRrn());
        this.setMaterialName(materialLot.getMaterialName());
        this.setMaterialDesc(materialLot.getMaterialDesc());
        this.setMaterialVersion(materialLot.getMaterialVersion());
        this.setMaterialCategory(materialLot.getMaterialCategory());
        this.setMaterialType(materialLot.getMaterialType());
        this.setStoreUom(materialLot.getStoreUom());
    }

    public void setMaterialLotAction(MaterialLotAction materialLotAction) {
        this.actionCode = materialLotAction.getActionCode();
        this.actionReason = materialLotAction.getActionReason();
        this.actionComment = materialLotAction.getActionComment();
    }
}
