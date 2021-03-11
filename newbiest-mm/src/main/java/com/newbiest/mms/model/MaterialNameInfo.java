package com.newbiest.mms.model;

import com.newbiest.base.model.NBUpdatable;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guozhangLuo
 */
@Entity
@Table(name="MATERIAL_NAME_INFO")
@Data
public class MaterialNameInfo extends NBUpdatable {

    @Column(name="NAME")
    private String name;

    @Column(name="TYPE")
    private String type;

}
