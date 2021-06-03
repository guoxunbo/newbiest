package com.newbiest.vanchip.dto.mes.receive;

import com.newbiest.vanchip.dto.mes.MesRequestBody;
import lombok.Data;

import java.util.List;

@Data
public class ReceiveMLotRequestBody extends MesRequestBody {

    private List<String> materialLotIds;

}
