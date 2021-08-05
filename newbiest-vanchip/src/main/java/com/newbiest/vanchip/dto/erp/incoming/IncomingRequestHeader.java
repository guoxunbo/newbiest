package com.newbiest.vanchip.dto.erp.incoming;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.newbiest.vanchip.dto.erp.ErpRequestHeader;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY,getterVisibility= JsonAutoDetect.Visibility.NONE)
public class IncomingRequestHeader extends ErpRequestHeader {

    @ApiModelProperty("20210712")
    private String BGDAT;

    @ApiModelProperty("20210713")
    private String ENDAT;
}
