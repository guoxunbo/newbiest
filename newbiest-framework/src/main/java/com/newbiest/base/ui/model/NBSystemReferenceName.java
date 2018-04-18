package com.newbiest.base.ui.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 系统栏位参考名称 不分区域
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue("SYSTEM")
public class NBSystemReferenceName extends NBReferenceName {

    private static final long serialVersionUID = 2068951329216814530L;
}
