package com.newbiest.vanchip.rest.labmaterial.imp;

import com.newbiest.base.msg.Response;
import lombok.Data;

@Data
public class LabMaterialImportResponse extends Response {

    private static final long serialVersionUID = 1L;

    private LabMaterialImportResponseBody body;
}
