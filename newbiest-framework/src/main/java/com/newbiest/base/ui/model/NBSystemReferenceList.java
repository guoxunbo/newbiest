package com.newbiest.base.ui.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 系统栏位参考值
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue(NBReferenceList.CATEGORY_SYSTEM)
public class NBSystemReferenceList extends NBReferenceList {

    private static final long serialVersionUID = 2282157532805072358L;
}
