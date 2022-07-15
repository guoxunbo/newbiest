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
    public static final String PRINT_COM_VBOX_LABEL = "PrintComVBoxLabel";//COM的VBOX标签打印
    public static final String PRINT_FT_VBOX_LABEL = "PrintFtVBoxLabel";//FT/COG  VBOX标签打印
    public static final String PRINT_RW_STOCK_OUT_LABEL = "PrintRWStockOutLabel";//RW出货标签打印
    public static final String PRINT_QR_CODE_LABEL = "PrintBoxQRCode";//二维码标签打印
    public static final String PRINT_WLT_BBOX_LABEL = "PrintWltBboxLabel";//WLT来料晶圆箱标签打印
    public static final String PRINT_WAFER_LABEL = "PrintWaferLabel";//来料晶圆箱标签打印
    public static final String PRINT_RMA_BOX_LABEL = "PrintRMABoxLabel";//RMA来料接收箱标签打印
    public static final String PRINT_RW_LOT_ISSUE_LABEL = "PrintRwLotIdIssueLabel";//RW发料箱标签打印
    public static final String PRINT_IR_LABEL = "PrintIRLabel";//IR单包标签打印
    public static final String PRINT_GLUE_LABEL = "PrintGlueLabel";//胶水标签打印
    public static final String PRINT_IRA_BOX_LABEL = "PrintIRABoxLabel";//IR箱号标签打印
    public static final String PRINT_WAFER_LOT_LABEL = "PrintWaferLotLabel";//wafer拆箱箱号标签打印
    public static final String PRINT_RW_BOX_LABEL = "PrintRwBoxLabel"; //RW出货箱标签打印
    public static final String PRINT_SAMSUNG_OUTER_BOX_LABEL = "PrintSamsungOuterBoxLabel";//三星外箱标签
    public static final String PRINT_LCD_BBOX_LABEL = "PrintLCDBBoxLabel";//LCD箱标签

    //物料编码打印相关
    public static final String PRINT_OPHELION_MLOT_LABEL = "PrintOphelionMLotLabel";//欧菲光
    public static final String PRINT_BAI_CHEN_MLOT_LABEL = "PrintBaichenMLotLabel";//白辰
    public static final String PRINT_GUANG_BAO_VBOX_LABEL = "PrintGuangBaoVBoxLabel";//光宝真空包
    public static final String PRINT_COB_GUANG_BAO_LABEL = "PrintCobGuangBaoLabel";//COB光宝标签
    public static final String PRINT_HUA_TIAN_LABEL = "PrintHuatianLabel";//华天
    public static final String PRINT_SHENG_TAI_VBOX_LABEL = "PrintShengTaiVBoxLabel";//盛泰真空包
    public static final String PRINT_BYD_LABEL = "PrintBydLabel";//比亚迪内箱/外箱
    public static final String PRINT_XLGD_BOX_LABEL = "PrintXLGDBoxLabel";//信利光电
    public static final String PRINT_SHUN_YU_LABEL = "PrintShunYuLabel";//舜宇
    public static final String PRINT_ZHONG_KONG_LABEL = "PrintZhongKongLabel";//中控智慧
    public static final String PRINT_XING_ZHI_MLOT_LABEL = "PrintXingZhiMLotLabel";//芯智物料标签
    public static final String PRINT_GENERAL_MLOT_LABEL = "PrintGeneralMLotLabel";//一般物料标签/光宝箱/盛泰箱
    public static final String PRINT_LONGTEN_MLOT_LABEL = "PrintLongTenMLotLabel";//龙腾光电


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
