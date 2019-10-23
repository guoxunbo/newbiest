package com.newbiest.gc.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * Created by luoguozhang on 2019-10-12 17:12
 */
@Data
@Entity
@Table(name="GC_CUSTOMER")
public class Customer implements Serializable {

    /**
     * 客户名称
     */
    @Column(name="CUSTOMER")
    @Id
    private String customer;

}
