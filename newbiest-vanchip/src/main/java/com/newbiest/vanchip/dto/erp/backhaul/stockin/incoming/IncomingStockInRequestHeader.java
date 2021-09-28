package com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingStockInRequestHeader extends ErpRequestHeader {

    /**
     * 移动类型 入库
     */
    private static final String BWART_STOCK_IN_MLOT = "511";

    /**
     * 移动类型 退库
     */
    private static final String BWART_RETURN_MLOT = "512";


    /**
     * 入库日期
     */
    private String BUDAT;

    /**
     * 凭证文本
     */
    private String BKTXT;

    /**
     * 移动类型
     */
    private String BWART;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private List<IncomingStockInRequestItem> Item;

}
