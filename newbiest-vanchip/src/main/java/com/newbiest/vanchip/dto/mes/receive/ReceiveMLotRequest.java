package com.newbiest.vanchip.dto.mes.receive;

import com.newbiest.vanchip.dto.mes.MesRequest;
import lombok.Data;

@Data
public class ReceiveMLotRequest extends MesRequest {

    public  static final String MESSAGE_NAME = "ReceiveMLot" ;

    private ReceiveMLotRequestBody body;

}
