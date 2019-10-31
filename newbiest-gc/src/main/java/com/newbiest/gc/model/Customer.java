package com.newbiest.gc.model;

import com.newbiest.base.model.NBBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2019-10-31 15:00
 */
@Entity
@Table(name="GC_CUSTOMER")
@Data
public class Customer extends NBBase {

    @Column(name="NAME")
    private String name;

    @Column(name="DESCRIPTION")
    private String description;

}
