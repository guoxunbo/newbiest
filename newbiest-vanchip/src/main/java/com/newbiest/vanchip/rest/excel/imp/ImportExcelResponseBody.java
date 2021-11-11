package com.newbiest.vanchip.rest.excel.imp;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class ImportExcelResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List<MaterialLot> materialLotList;
}
