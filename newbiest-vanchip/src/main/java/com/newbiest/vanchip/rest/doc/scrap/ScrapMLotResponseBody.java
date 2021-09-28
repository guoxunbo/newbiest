package com.newbiest.vanchip.rest.doc.scrap;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;

import java.util.List;

@Data
public class ScrapMLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MaterialLot> materialLotList;
}
