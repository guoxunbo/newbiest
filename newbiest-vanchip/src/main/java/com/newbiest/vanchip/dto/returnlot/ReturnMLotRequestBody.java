package com.newbiest.vanchip.dto.returnlot;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReturnMLotRequestBody implements Serializable {

    public static final String ACTION_TYPE_RETURN_MLOT = "ReturnMLot" ;
    public static final String MATERIAL_NAME = "Material" ;
    public static final String ACTION_TYPE_RETURN_MATERIAL = "ReturnMaterial" ;

    private String actionType;

    private List<String> materialLotIds;

}
