package com.newbiest.vanchip.dto.erp.backhaul.check;

import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
public class CheckRequestHeader extends ErpRequestHeader {

    /**
     * 库存盘点凭证 盘点单
     */
    private String IBLNR;

    /**
     * 建单日期
     */
    private String BLDAT;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIRLD4;

    private List<CheckRequestItem> Item;
}
