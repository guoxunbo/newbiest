package com.newbiest.base.ui.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 用户栏位参考名称 by 区域的
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue("OWNER")
public class NBOwnerReferenceName extends NBReferenceName {

    private static final long serialVersionUID = 3116220955270849126L;
}
