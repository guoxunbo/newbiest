package com.newbiest.vanchip.dto.erp.backhaul.scrap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class ScrapRequestHeader extends ErpRequestHeader {

    //过账日期
    private String BUDAT;

    //报废申请单
    private String ZSCRAP;

    //成本中心
    private String KOSTL;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

}
