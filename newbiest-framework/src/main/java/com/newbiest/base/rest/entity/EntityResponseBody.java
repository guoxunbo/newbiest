package com.newbiest.base.rest.entity;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@Data
public class EntityResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	private NBBase data;
}
