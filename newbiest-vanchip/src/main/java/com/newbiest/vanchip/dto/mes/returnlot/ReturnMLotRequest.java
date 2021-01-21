package com.newbiest.vanchip.dto.mes.returnlot;

import com.newbiest.vanchip.dto.mes.MesRequest;
import lombok.Data;

@Data
public class ReturnMLotRequest extends MesRequest {

    public  static final String MESSAGE_NAME = "ReturnMLot" ;

    private ReturnMLotRequestBody body;

}
