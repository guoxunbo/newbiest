package com.newbiest.gc.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo on 20200907
 */
@Entity
@Table(name="GC_SUPPLIER")
@Data
public class Supplier extends NBBase {

    public static final String TYPE_PO_SUPPLER = "PO_SUPPLER";
    public static final String TYPE_VENDER = "VENDER";

    @Column(name="NAME")
    private String name;

    @Column(name="TYPE")
    private String type;

}
