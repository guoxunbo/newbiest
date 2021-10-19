package com.newbiest.vanchip.dto.erp.split;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.Material;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class SplitRequestHeader extends ErpRequestHeader {

    /**
     * 过账日期
     */
    private String BUDAT;

    /**
     * 凭证抬头文本
     */
    private String BKTXT;

    /**
     * 物料编号
     */
    private String MATNR;

    /**
     * 存储地点
     */
    private String LGORT;

    /**
     *基本计量单位
     */
    private String ERFME;

    /**
     *行项目文本
     */
    private String SGTXT;

    /**
     * 母批
     */
    private String Z_BATCH_WMSBATCH;
    /**
     *子批
     */
    private String Z_BATCH_WMSBATCH2;

    private String Z_BATCH_BOXNO;
    private String Z_BATCH_BOXNO2;

    private String Z_BATCH_REEL;
    private String Z_BATCH_REEL2;
    private String Z_BATCH_TBATCH;
    private String Z_BATCH_TBATCH2;

    /**
     * 子批数量
     */
    private BigDecimal ERFMG;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    public SplitRequestHeader copyMaterialLotToSplitRequestHeader(MaterialLot materiallot, SplitRequestHeader requestHeader){
        requestHeader.setMATNR(materiallot.getMaterialName());
        requestHeader.setLGORT(materiallot.getLastWarehouseId());
        requestHeader.setERFME(materiallot.getStoreUom());
        if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materiallot.getMaterialCategory())) {
            requestHeader.setZ_BATCH_BOXNO(materiallot.getParentMaterialLotId());
            requestHeader.setZ_BATCH_BOXNO2(materiallot.getMaterialLotId());
        }
        if (!StringUtils.isNullOrEmpty(materiallot.getRmaFlag())) {
            requestHeader.setZ_BATCH_REEL(materiallot.getParentMaterialLotId());
            requestHeader.setZ_BATCH_REEL2(materiallot.getMaterialLotId());
        }
        requestHeader.setZ_BATCH_WMSBATCH(materiallot.getParentMaterialLotId());
        requestHeader.setZ_BATCH_WMSBATCH2(materiallot.getMaterialLotId());
        requestHeader.setERFMG(materiallot.getCurrentQty());
        return requestHeader;
    }
}
