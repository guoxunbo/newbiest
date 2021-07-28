package com.newbiest.gc.rest.unConfirmWaferSet;

import com.newbiest.gc.model.GcUnConfirmWaferSet;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

/**
 * Created by guozhangLuo on 2020/12/11
 */
@Data
public class UnConfirmWaferSetResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private GcUnConfirmWaferSet unConfirmWaferSet;

}
