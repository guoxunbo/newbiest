package com.newbiest.vanchip.dto.erp.backhaul.scrap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class ScrapRequestItem implements Serializable {

    //项目
    private String ZITEM;

    //报废数量
    private BigDecimal ZMENGE;

    //基本单位
    private String MEINS;

    private String FIELD1;
    private String FIELD2;
    private String FIELD3;
    private String FIELD4;
}
