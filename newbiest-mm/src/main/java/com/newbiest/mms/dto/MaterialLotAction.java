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
     * 此次操作的数量
     */
    private BigDecimal transQty;

    /**
     * 来源仓库主键
     */
    private Long fromWarehouseRrn;

    /**
     * 目标仓库主键
     */
    private Long targetWarehouseRrn;

}
