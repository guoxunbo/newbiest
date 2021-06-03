package com.newbiest.vanchip.dto.mes.receive;

import com.newbiest.vanchip.dto.mes.MesRequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiveMLotRequestHeader extends MesRequestHeader {


    public ReceiveMLotRequestHeader() {
        super(ReceiveMLotRequest.MESSAGE_NAME);
    }



}
