package com.newbiest.mms.model;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Data
@Entity
@DiscriminatorValue(Material.CLASS_LAB)
public class LabMaterial extends Material {

    @Transient
    private BigDecimal pickQty;

}
