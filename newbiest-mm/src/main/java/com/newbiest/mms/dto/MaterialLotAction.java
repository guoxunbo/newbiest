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
     * 判等
     */
    private String grade;

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

}
