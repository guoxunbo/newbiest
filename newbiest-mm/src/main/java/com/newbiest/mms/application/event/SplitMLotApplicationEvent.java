package com.newbiest.mms.application.event;

import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class SplitMLotApplicationEvent extends ApplicationEvent {

    @ApiModelProperty(value = "物料批次")
    private MaterialLot materialLot;

    public SplitMLotApplicationEvent(Object source, MaterialLot materialLot) {
        super(source);
        this.materialLot = materialLot;
    }
}
