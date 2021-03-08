package com.newbiest.mms.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(CheckSheet.CATEGORY_OQC)
public class OqcCheckSheet extends CheckSheet {





}
