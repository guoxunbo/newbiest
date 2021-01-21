package com.newbiest.vanchip.dto.mes.returnlot;

import com.newbiest.vanchip.dto.mes.MesRequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnMLotRequestHeader extends MesRequestHeader {


    public ReturnMLotRequestHeader() {
        super(ReturnMLotRequest.MESSAGE_NAME);
    }



}
