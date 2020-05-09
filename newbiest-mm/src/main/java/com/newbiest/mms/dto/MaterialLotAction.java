package com.newbiest.mms.dto;

import com.newbiest.base.dto.Action;
import lombok.Data;

import java.math.BigDecimal;

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
     * 等级
     */
    private String grade;

    /**
     * 来源仓库主键
     */
    private String fromWarehouseRrn;

    /**
     * 来源仓库名称
     *  client无需传递，server自己根据fromWarehouseRrn查询之后赋值
     */
    private String fromWarehouseId;

    /**
     * 来源库位主键
     */
    private String fromStorageRrn;

    /**
     * 来源库位名称
     *  client无需传递，server自己根据fromStorageRrn查询之后赋值
     */
    private String fromStorageId;

    /**
     * 目标仓库主键
     */
    private String targetWarehouseRrn;

    /**
     * 目标仓库名称
     *  client无需传递，server自己根据targetWarehouseRrn查询之后赋值
     */
    private String targetWarehouseId;

    /**
     * 目标库位主键
     */
    private String targetStorageRrn;

    /**
     * 目标库位名称
     *  client无需传递，server自己根据targetStorageRrn查询之后赋值
     */
    private String targetStorageId;

    /**
     * 携带的单据号
     */
    private String transDocId;

}
