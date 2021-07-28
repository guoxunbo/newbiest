package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by guozhangluo on 2020/8/11.
 */
@Entity
@DiscriminatorValue(Material.CLASS_PRODUCT)
public class Product extends Material {


}
