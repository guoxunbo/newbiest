package com.newbiest.vanchip.dto.erp.backhaul.stocktransfer;

import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
public class StockTransferRequestHeader extends ErpRequestHeader {

    //过账日期
    private String BUDAT;

    //备注
    private String BKTXT;

    private String FIELD4;

    private List<StockTransferRequestItem> Item;
}
