package com.newbiest.gc.rest.materiallot;

import com.newbiest.ui.model.NBOwnerReferenceList;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.base.msg.ResponseBody;
import lombok.Data;

import java.util.List;


@Data
public class GcMaterialLotResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	List<NBOwnerReferenceList> judgePackCaseItemList;

}
