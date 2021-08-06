package com.newbiest.mms.application.event;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Data
public class StockInApplicationEvent extends ApplicationEvent {

    @ApiModelProperty(value = "物料批次")
    private List<MaterialLot> materialLots;

    @ApiModelProperty(value = "物料批次动作")
    private List<MaterialLotAction> materialLotActionList;

    public StockInApplicationEvent(Object source, List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActionList) {
        super(source);
        this.materialLots = materialLots;
        this.materialLotActionList = materialLotActionList;
    }
}
