package com.newbiest.gc.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="GC_WORKORDER_RELATION")
@Data
public class GCWorkorderRelation extends NBUpdatable {

    private static final long serialVersionUID = -8075936261995774501L;

    /**
     * 工单号
     */
    @Column(name="WORK_ORDER_ID")
    private String workOrderId;

    /**
     * 等级
     */
    @Column(name="GRADE")
    private String grade;

    /**
     * HOLD原因
     */
    @Column(name="HOLD_REASON")
    private String holdReason;

    /**
     * 包装盒号
     */
    @Column(name = "BOX_ID")
    private String boxId;

}
