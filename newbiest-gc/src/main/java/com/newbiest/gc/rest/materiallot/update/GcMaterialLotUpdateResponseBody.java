package com.newbiest.gc.rest.materiallot.update;


import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

@Data
public class GcMaterialLotUpdateResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<NBOwnerReferenceList> referenceList;

}
