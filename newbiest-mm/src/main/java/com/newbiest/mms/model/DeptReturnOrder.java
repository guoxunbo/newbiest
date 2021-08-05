package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 部门退料单
 */
@Entity
@DiscriminatorValue(Document.CATEGORY_DEPT_RETURN)
public class DeptReturnOrder extends Document {

    public static final String GENERATOR_DEPT_RETURN_ORDER_RULE = "CreateDeptReturnOrder";

    @Override
    public String getCategory(){
        return Document.CATEGORY_DEPT_RETURN;
    }
}
