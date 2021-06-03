package com.newbiest.vanchip.dto.mes.sync.material;

import com.newbiest.vanchip.dto.mes.MesRequestHeader;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncMaterialRequestHeader extends MesRequestHeader {

    public SyncMaterialRequestHeader() {
        super(SyncMaterialRequest.MESSAGE_NAME);
    }

}
