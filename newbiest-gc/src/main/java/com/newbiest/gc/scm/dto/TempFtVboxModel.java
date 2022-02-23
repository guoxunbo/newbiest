package com.newbiest.gc.scm.dto;

import lombok.Data;
import java.io.Serializable;


/**
 * @author luoguozhang
 * @date 22/2/22 15:57 PM
 */
@Data
public class TempFtVboxModel implements Serializable {

    public static final String WAFER_SOURCE_2 = "2";

    private String boxId;

    private String qty;

    private String grade;

    private String warehouseName;

    private String productId;

    private String secondCode;

    private String location;

    private String wo;

    private String waferSource;

    private String saleNote;

    private String treasuryNote;

    private String storageId;

    private String subName;

    private String soureProductId;

}
