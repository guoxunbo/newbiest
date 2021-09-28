package com.newbiest.vanchip.dto.erp.backhaul.issue;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IssueOrReturnRequestItem implements Serializable {

    //发料类型
    public static final String ISSUE_BWART = "201";

    //退料类型
    public static final String RETURN_BWART = "202";

    //项目
    private String ZEILE;

    //物料号
    private String MATNR;

    //仓库代码
    private String LGORT;

    //数量
    private String ERFMG;

    //基本单位
    private String MEINS;

    //备注
    private String SGTXT;

    //移动类型
    private String BWART;

    //成本中心
    private String KOSTL;

    private String Z_BATCH_REEL;
    private String Z_BATCH_TBATCH;
    private String Z_BATCH_BOXNO;
    private String Z_BATCH_WMSBATCH;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    public IssueOrReturnRequestItem copyMaterialLotToIssueOrReturnRequestItem(IssueOrReturnRequestItem requestItem, MaterialLot materialLot){
        requestItem.setMATNR(materialLot.getMaterialName());
        requestItem.setERFMG(materialLot.getCurrentQty() + StringUtils.EMPTY);
        requestItem.setLGORT(materialLot.getLastWarehouseId());
        requestItem.setMEINS(materialLot.getStoreUom());
        requestItem.setZ_BATCH_WMSBATCH(materialLot.getMaterialLotId());

        return requestItem;
    }

}
