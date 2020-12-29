package com.newbiest.vanchip.rest.incoming.mlot.imp;

import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class IncomingMaterialImportResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List dataList;
}
