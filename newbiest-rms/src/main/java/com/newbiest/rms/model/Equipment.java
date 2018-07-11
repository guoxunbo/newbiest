package com.newbiest.rms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guoxunbo on 2018/7/5.
 */
@Entity
@Table(name="RMS_EQUIPMENT")
@Data
public class Equipment extends NBUpdatable {

    @Column(name="EQUIPMENT_ID")
    private String equipmentId;

    @Column(name="EQUIPMENT_TYPE")
    private String equipmentType;

}
