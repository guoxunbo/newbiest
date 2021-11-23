package com.newbiest.gc.model;

import com.newbiest.mms.model.Document;
import javax.persistence.*;

/**
 * 原材料其它出库单 type = 'MO'
 * Created by guoxunbo on 2021-07-27 18:37
 */
@Entity
@DiscriminatorValue(RawMaterialOtherOutOrder.CATEGORY_RAW_MATERIAL_OTHER_SHIP)
public class RawMaterialOtherOutOrder extends Document {

    public static final String CATEGORY_RAW_MATERIAL_OTHER_SHIP = "RawMOtherShip";

}
