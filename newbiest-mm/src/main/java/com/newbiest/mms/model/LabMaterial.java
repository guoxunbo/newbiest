package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Material.CLASS_LAB)
public class LabMaterial extends Material {


}
