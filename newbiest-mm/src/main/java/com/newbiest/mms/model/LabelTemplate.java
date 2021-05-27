package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author guoxunbo
 * @date 4/6/21 2:22 PM
 */
@Entity
@Table(name="MMS_LBL_TEMPLATE")
@Data
public class LabelTemplate extends NBBase {

    public static final String BARTENDER_REPLACE_URL_PARAMETER = "${remote_address}";

    public static final String TYPE_BARTENDER = "Bartender";
    public static final String TYPE_EXCEL = "Excel";

    //标签打印模板
    public static final String PRINT_OBLIQUE_BOX_LABEL = "PrintObliqueBoxLabel";//斜标签打印
    public static final String PRINT_WLT_CP_BOX_LABEL = "PrintWltOrCpBoxLabel";//Wlt/CP箱标签打印
    public static final String PRINT_RW_LOT_CST_LABEL = "PrintRwLotCstLabel";//RW Lot标签打印
    public static final String PRINT_COB_BOX_LABEL = "PrintCOBBoxLabel";//COB装箱标签打印
    public static final String PRINT_WLT_BOX_LABEL = "PrintWltBoxLabel";//WLT装箱标签打印
    public static final String PRINT_COM_BOX_LABEL = "PrintComBoxLabel";//COM装箱标签打印
    public static final String PRINT_CUSTOMER_NAME_LABEL = "PrintCusNameLabel";//COM装箱客户标签打印
    public static final String PRINT_RW_CST_BOX_LABEL = "PrintRwCstBoxLabel";//RW的CST标签打印

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="TYPE")
    private String type;

    /**
     * 当type是Bartender的时候
     *  需要指定其IB的地址。如http://${remote_address}:10099/******
     * 当type是excel的时候，则为excel的Template的模板名称
     */
    @Column(name="DESTINATION")
    private String destination;

    @Column(name="PRINT_COUNT")
    private Integer printCount = 1;

    @Transient
    private List<LabelTemplateParameter> labelTemplateParameterList;

    public String getBartenderDestination(WorkStation workStation) {
        return destination.replace(BARTENDER_REPLACE_URL_PARAMETER, workStation.getPrintMachineIpAddress());
    }

}
