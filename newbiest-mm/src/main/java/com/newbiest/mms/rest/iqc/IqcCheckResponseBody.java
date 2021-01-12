package com.newbiest.mms.rest.iqc;

import com.newbiest.base.msg.ResponseBody;
import com.newbiest.mms.model.MLotCheckSheetLine;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class IqcCheckResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private List<MLotCheckSheetLine> MLotCheckSheetLines;

}
