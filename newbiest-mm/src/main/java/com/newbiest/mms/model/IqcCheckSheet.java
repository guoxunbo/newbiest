package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author guoxunbo
 * @date 12/23/20 11:10 AM
 */
@Entity
@DiscriminatorValue(CheckSheet.CATEGORY_IQC)
public class IqcCheckSheet extends CheckSheet {





}
