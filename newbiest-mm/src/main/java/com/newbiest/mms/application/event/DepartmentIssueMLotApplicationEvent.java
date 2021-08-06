package com.newbiest.mms.application.event;

import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.Document;
import com.newbiest.mms.model.MaterialLot;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 部门发料
 */
@Data
public class DepartmentIssueMLotApplicationEvent extends ApplicationEvent {

    @ApiModelProperty(value = "单据")
    private Document document;

    @ApiModelProperty(value = "物料批次")
    private List<MaterialLot> materialLots;

    @ApiModelProperty(value = "物料批次动作")
    private List<MaterialLotAction> materialLotActions;

    public DepartmentIssueMLotApplicationEvent(Object source, Document document, List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions) {
        super(source);
        this.document = document;
        this.materialLots = materialLots;
        this.materialLotActions = materialLotActions;
    }

}
