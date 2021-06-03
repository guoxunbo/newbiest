package com.newbiest.vanchip.dto.mes.sync.material;

import com.newbiest.vanchip.dto.mes.MesRequestBody;
import lombok.Data;

@Data
public class SyncMaterialRequestBody extends MesRequestBody {

    private String materialName;

}
