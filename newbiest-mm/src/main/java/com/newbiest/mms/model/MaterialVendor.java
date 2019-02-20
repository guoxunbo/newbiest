package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 物料供应商
 *  此处物料不做版本管控。即会查出所有版本的物料供应商
 * Created by guoxunbo on 2019/1/4.
 */
@Entity
@Table(name = "MM_MATERIAL_PARTNER")
@Data
public class MaterialVendor extends NBUpdatable{

    @Column(name = "MATERIAL_NAME")
    private String materialName;

    @Column(name = "MATERIAL_DESC")
    private String materialDesc;

    @Column(name = "PARTNER_NAME")
    private String partnerName;

    @Column(name = "PARTNER_DESC")
    private String partnerDesc;

    
}
