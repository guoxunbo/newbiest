package com.newbiest.gc.rest.GCRawMaterialImportManager.RawMaterialImport;

import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class GCRawMaterialImportResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List dataList;
}
