package com.newbiest.vanchip.dto.erp.backhaul.check;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class CheckRequestHeader extends ErpRequestHeader {

    /**
     * 库存盘点凭证 盘点单
     */
    private String IBLNR;

    /**
     * 建单日期
     */
    private String BLDAT;

    /**
     * 仓库代码
     */
    private String LGORT;


    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIRLD4;

    private List<CheckRequestItem> Item;
}
