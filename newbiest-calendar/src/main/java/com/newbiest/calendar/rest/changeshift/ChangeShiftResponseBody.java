package com.newbiest.calendar.rest.changeshift;

import com.newbiest.calendar.model.ChangeShift;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

@Data
public class ChangeShiftResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private ChangeShift changeShift;

}
