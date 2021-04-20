package com.newbiest.vanchip.rest.labmaterial.imp;

import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class LabMaterialImportResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List dataList;
}
