package com.newbiest.mms.model;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guozhangluo on 2020/9/1.
 */
@Entity
@Data
@DiscriminatorValue(Material.CLASS_PARTS)
public class Parts extends Material {

    /**
     * 备件规格
     */
    @Column(name="SPARE_SPECS")
    private String spareSpecs;

    /**
     * 备件型号
     */
    @Column(name="SPARE_MODEL")
    private String spareModel;

    /**
     * 备件线别
     */
    @Column(name="SPARE_PARTS_LINE")
    private String sparePartsLine;

}
