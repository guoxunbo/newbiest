package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 物料批次判定历史
 * Created by guoxunbo on 2019-09-02 18:09
 */
@Data
@Entity
@Table(name="MMS_MATERIAL_LOT_JUDGE_HIS")
public class MaterialLotJudgeHis extends NBHis {

    public static final String TRANS_TYPE_OQC = "OQC";

    public static final String TRANS_TYPE_IQC = "IQC";

    @Column(name = "MATERIAL_LOT_RRN")
    private String materialLotRrn;

    @Column(name = "MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "RESULT")
    private String result;

}
