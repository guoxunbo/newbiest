package com.newbiest.mms.application.event;

import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 仓库退料到供应商/ERP
 */
@Data
public class ReturnMLotApplicationEvent extends ApplicationEvent {

    @ApiModelProperty(value = "单据号")
    private String documentId;

    @ApiModelProperty(value = "物料批次")
    private List<MaterialLot> materialLots;

    public ReturnMLotApplicationEvent(Object source, String documentId, List<MaterialLot> materialLots) {
        super(source);
        this.documentId = documentId;
        this.materialLots = materialLots;
    }
}
