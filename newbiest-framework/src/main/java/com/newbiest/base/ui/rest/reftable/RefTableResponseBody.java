package com.newbiest.base.ui.rest.reftable;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBReferenceTable;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RefTableResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<? extends NBBase> dataList;

	private NBReferenceTable referenceTable;

}
