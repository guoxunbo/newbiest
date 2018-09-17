package com.newbiest.base.ui.rest.reflist;

import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class RefListResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<? extends NBReferenceList> dataList;

}
