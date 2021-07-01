package com.newbiest.vanchip.rest.erp.material;

import com.newbiest.base.msg.RequestBody;
import com.newbiest.mms.model.Material;
import lombok.Data;

import java.util.List;

@Data
public class ErpMaterialRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String actionType;

    private List<Material> materialList;

}
