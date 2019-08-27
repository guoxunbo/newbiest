package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * Created by guoxunbo on 2019-08-20 16:05
 */
@Data
@Entity
@Table(name="MMS_STORAGE")
public class Storage extends NBUpdatable {

    public static final String DEFAULT_STORAGE_NAME = "DefaultStorage";

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="STORAGE_TYPE")
    private String storageType;

    @Column(name="WAREHOUSE_RRN")
    private Long warehouseRrn;

    /**
     * 父级节点
     */
    @Column(name="PARENT_RRN")
    private Long parentRrn;


}
