package com.newbiest.vanchip.dto.mes.sync.material;

import com.newbiest.vanchip.dto.mes.MesRequest;
import lombok.Data;

@Data
public class SyncMaterialRequest extends MesRequest {

    public  static final String MESSAGE_NAME = "SyncMaterial" ;

    private SyncMaterialRequestBody body;

}
