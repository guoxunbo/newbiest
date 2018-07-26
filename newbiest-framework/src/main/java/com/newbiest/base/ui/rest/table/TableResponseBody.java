package com.newbiest.base.ui.rest.table;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.ResponseBody;
import com.newbiest.security.model.NBRole;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class TableResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;
	
	private NBTable table;

	private List<? extends NBBase> dataList;
}
