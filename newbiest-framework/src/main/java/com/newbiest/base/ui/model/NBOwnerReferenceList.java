package com.newbiest.base.ui.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 用户栏位参考值
 * Created by guoxunbo on 2018/2/7.
 */
@Entity
@DiscriminatorValue(NBReferenceList.CATEGORY_OWNER)
public class NBOwnerReferenceList extends NBReferenceList {

    private static final long serialVersionUID = 6386249265802846381L;
}
