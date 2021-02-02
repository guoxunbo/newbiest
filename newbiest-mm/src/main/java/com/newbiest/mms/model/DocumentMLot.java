package com.newbiest.mms.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 单据绑定的物料批次
 *  单据指定物料批次进行绑定/预留
 * @author guoxunbo
 */
@Data
@Entity
@Table(name = "MMS_DOCUMENT_MLOT")
public class DocumentMLot extends NBBase {

    @Column(name="DOCUMENT_ID")
    private String documentId;

    @Column(name="MATERIAL_LOT_ID")
    private String materialLotId;

}
