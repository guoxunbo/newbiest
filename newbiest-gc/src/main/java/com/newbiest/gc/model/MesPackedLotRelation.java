package com.newbiest.gc.model;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Zhi Cao on 2020-07-09
 */
@Data
@Entity
@Table(name="MES_PACKED_LOT_RELATION")
public class MesPackedLotRelation implements Serializable {


    @Id
    @Column(name = "MATERIAL_RRN")
    private Long materialRrn;

    @Column(name = "FACILITY_RRN")
    private Long facilityRrn;

    //晶圆号
    @Column(name = "WAFER_ID")
    private String waferId;

    //批号
    @Column(name = "CST_ID")
    private String cstId;

    //工单号
    @Column(name = "WORKORDER_ID")
    private String workOrderId;

    //物料类型
    @Column(name = "MATERIAL_TYPE")
    private String materialType;

    //物料编码
    @Column(name = "MATERIAL_CODE")
    private String materialCode;

    //物料数量
    @Column(name = "MATERIAL_QTY")
    private Integer materialQty;

    //物料二级代码
    @Column(name = "MATERIAL_VERSION")
    private String materialVersion;

    //物料保税属性
    @Column(name = "MATERIAL_BONDED")
    private String materialBonded;

    //物料等级
    @Column(name = "MATERIAL_GRADE")
    private String materialGrade;

    //packedLotRrn
    @Column(name = "PACKED_LOT_RRN")
    private Long packedLotRrn;

    /**
     * 片号
     */
    @Column(name = "OBJECT_ID")
    private String objectId;

    /**
     * 测试型号
     */
    @Column(name = "TEST_MODEL_ID")
    private String testModelId;

    /**
     * 入库型号
     */
    @Column(name = "INSTORE_MODEL_ID")
    private String instoreModelId;

    /**
     * bin1
     */
    @Column(name = "BIN_ID1")
    private String binId1;

    /**
     * bin2
     */
    @Column(name = "BIN_ID2")
    private String binId2;

    /**
     * bin3
     */
    @Column(name = "BIN_ID3")
    private String binId3;

    /**
     * bin4
     */
    @Column(name = "BIN_ID4")
    private String binId4;

    /**
     * 供应商
     */
    @Column(name = "VENDER")
    private String vender;

    /**
     * 数量
     */
    @Column(name = "GROSS_DIES")
    private String grossDies;

    /**
     * 产品类型
     */
    @Column(name = "PRODUCT_TYPE")
    private String productType;

}
