package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue(Material.CLASS_PARTS)
public class Parts extends Material {

}
