package com.newbiest.gc.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by jason zhou
 */
@Entity
@Table(name="GC_VENDER_ADDRESS")
@Data
public class GCVenderAddressRelation extends NBBase {

    /**
     * 客户名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 地址
     */
    @Column(name="ADDRESS")
    private String address;
}
