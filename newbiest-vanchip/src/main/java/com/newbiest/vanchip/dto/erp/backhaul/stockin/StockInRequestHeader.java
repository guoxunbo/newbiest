package com.newbiest.vanchip.dto.erp.backhaul.stockin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class StockInRequestHeader extends ErpRequestHeader {

    /**
     * 物料号
     */
    private String MATNR;

    /**
     * 库存地点
     */
    private String LGORT;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private List<StockInRequestItem> ITEM;
}
