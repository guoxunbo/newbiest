package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guoxunbo on 2019/2/20.
 */
@Entity
@DiscriminatorValue(Material.CLASS_RAW)
public class RawMaterial extends Material {


}
