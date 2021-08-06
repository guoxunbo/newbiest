package com.newbiest.mms.application.event;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Data
public class HoldMLotApplicationEvent extends ApplicationEvent {

    private MaterialLot materialLot ;

    private List<MaterialLotAction> materialLotActions;

    public HoldMLotApplicationEvent(Object source, MaterialLot materialLot, List<MaterialLotAction> materialLotActions) {
        super(source);
        this.materialLot = materialLot;
        this.materialLotActions = materialLotActions;
    }
}
