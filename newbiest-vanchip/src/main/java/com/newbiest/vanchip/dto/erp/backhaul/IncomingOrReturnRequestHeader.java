package com.newbiest.vanchip.dto.erp.backhaul;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingOrReturnRequestHeader extends ErpRequestHeader {

    /**
     *入库日期 20210715
     */
    private String BUDAT;

    /**
     *凭证抬头文本
     */
    private String BKTXT;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;

    private List<IncomingOrReturnRequestItem> Item;
}
