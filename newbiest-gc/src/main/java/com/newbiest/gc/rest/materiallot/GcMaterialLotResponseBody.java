package com.newbiest.gc.rest.materiallot;

import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;


@Data
public class GcMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<NBOwnerReferenceList> judgePackCaseItemList;

	List<NBOwnerReferenceList> judgeWltPackCaseItemList;

	List<MaterialLot> materialLotList;

	MaterialLot materialLot;

}
