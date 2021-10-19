package com.newbiest.mms.state.model;

import com.newbiest.commom.sm.model.Event;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guoxunbo on 2019/2/12.
 */
@Entity
@DiscriminatorValue(MaterialStatusCategory.CATEGORY_MATERIAL)
public class MaterialEvent extends Event {

    /**
     * 接收
     */
    public static final String EVENT_RECEIVE = "Receive";

    /**
     * 来料质量检查
     */
    public static final String EVENT_IQC = "IQC";

    /**
     * 出货质量检查
     */
    public static final String EVENT_OQC = "OQC";

    /**
     * 拒绝
     */
    public static final String EVENT_REJECT = "Reject";

    /**
     * 入库
     */
    public static final String EVENT_STOCK_IN = "StockIn";

    /**
     * 出库
     */
    public static final String EVENT_STOCK_OUT = "StockOut";

    /**
     * 等待退库
     *  一般指创建了退料单进行退料的事件
     */
    public static final String EVENT_WAIT_RETURN = "WaitReturn";

    /**
     * 等待退库
     *  一般指创建了退料单进行退料的事件
     */
    public static final String EVENT_RETURN = "Return";

    public static final String EVENT_RESERVED = "Reserved";
    public static final String EVENT_UN_RESERVED = "UnReserved";

    /**
     * 发料
     */
    public static final String EVENT_ISSUE = "Issue";

    public static final String EVENT_SHIP = "Ship";
    public static final String EVENT_UN_SHIP = "UnShip";

    public static final String EVENT_PICK = "Pick";

    public static final String EVENT_CHECK = "Check";

    public static final String EVENT_CONSUME = "Consume";

    public static final String EVENT_USE_UP = "UseUp";

    public static final String EVENT_PACKAGE = "Package";

    public static final String EVENT_UN_PACKAGE = "UnPackage";

    public static final String EVENT_IQC_APPROVE = "IqcApprove";

    public static final String EVENT_SCRAP= "Scrap";

}
