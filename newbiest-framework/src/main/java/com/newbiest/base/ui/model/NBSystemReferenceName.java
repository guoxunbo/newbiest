package com.newbiest.base.ui.model;

import com.google.common.collect.Lists;

import javax.persistence.*;

/**
 * 系统栏位参考名称 不分区域
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue(NBReferenceName.CATEGORY_SYSTEM)
public class NBSystemReferenceName extends NBReferenceName {

    private static final long serialVersionUID = 2068951329216814530L;

}
