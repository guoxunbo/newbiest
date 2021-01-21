package com.newbiest.vanchip.dto.mes.returnlot;

import com.newbiest.vanchip.dto.mes.MesRequestBody;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReturnMLotRequestBody extends MesRequestBody {

    private List<String> materialLotIds;

}
