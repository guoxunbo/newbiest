package com.newbiest.vanchip.dto.erp.backhaul.stocktransfer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class StockTransferRequestHeader extends ErpRequestHeader {

    //过账日期
    private String BUDAT;

    //备注
    private String BKTXT;

    private String FIELD4;

    private List<StockTransferRequestItem> ITEM;
}
