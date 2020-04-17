package com.newbiest.gc.rest.productSubcodeSet;

import com.newbiest.gc.model.GCProductSubcode;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class ProductSubcodeSetResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private GCProductSubcode productSubcode;

}
