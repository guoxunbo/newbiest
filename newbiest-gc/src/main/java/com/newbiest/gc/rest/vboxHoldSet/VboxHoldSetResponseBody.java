package com.newbiest.gc.rest.vboxHoldSet;

import com.newbiest.gc.model.GCWorkorderRelation;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guozhangLuo 2020-10-13
 */
@Data
public class VboxHoldSetResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private GCWorkorderRelation workorderRelation;

}
