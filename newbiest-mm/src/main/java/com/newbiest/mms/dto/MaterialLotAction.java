package com.newbiest.mms.dto;

import com.newbiest.base.dto.Action;
import com.newbiest.commom.sm.model.StatusModel;
import com.newbiest.mms.model.Storage;
import com.newbiest.mms.model.Warehouse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by guoxunbo on 2019/2/28.
 */
@Data
public class MaterialLotAction extends Action {

    /**
     * 物料批次号
     */
    private String materialLotId;

    /**
     * 此次操作的数量
     */
    private BigDecimal transQty;

    /**
     * 判等
     */
    private String grade;

    /**
     * 入库备注
     */
    private String reserved4;

    /**
     * 重置库位号
     */
    private String resetStorageId;

    /**
     * 来源仓库主键
     */
    private Long fromWarehouseRrn;

    /**
     * 来源仓库名称
     *  client无需传递，server自己根据fromWarehouseRrn查询之后赋值
     */
    private String fromWarehouseId;


    /**
     * 来源库位主键
     */
    private Long fromStorageRrn;

    /**
     * 来源库位名称
     *  client无需传递，server自己根据fromStorageRrn查询之后赋值
     */
    private String fromStorageId;

    /**
     * 目标仓库主键
     */
    private Long targetWarehouseRrn;

    /**
     * 目标库位主键
     */
    private Long targetStorageRrn;

    /**
     * 目标库位名称
     */
    private String targetStorageId;

    /**
     * 创建或者接收时候因为客户的不同，额外的栏位值需要赋值到预留栏位上
     * 创建或者接收的时候往往会有其他系统比如ERP或MES接收数据，赋值到预留栏位上。此处进行传递
     */
    private Map<String, Object> propsMap;

    /**
     * 操作的晶圆片数
     */
    private BigDecimal transCount;

    /**
     * 原产品型号（打印型号替换之前）
     */
    private String sourceModelId;

    /**
     * RW退料入库标记
     */
    private String returnMaterialFlag;

    /**
     * RW退料入库标记
     */
    private String workOrderId;

    /**
     * 箱子包装初始化状态
     */
    private String boxStatusUseFlag;

    /**
     * COB导入自动装箱
     */
    private String cobImportPack;

    /**
     * 操作人
     */
    private String transUser;

    private Storage storage;

    private Warehouse warehouse;

    public MaterialLotAction(){
    }

    public MaterialLotAction(String materialLotId, String grade, Map<String, Object> propsMap, BigDecimal totalQty, BigDecimal currentSubQty, String workOrderId){
        this.setMaterialLotId(materialLotId);
        this.setGrade(grade);
        this.setPropsMap(propsMap);
        this.setTransQty(totalQty);
        this.setTransCount(currentSubQty);
        this.setWorkOrderId(workOrderId);
    }
}
