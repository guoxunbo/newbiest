package com.newbiest.msg.base.entitylist;

import com.newbiest.base.model.NBBase;
import com.newbiest.msg.ResponseBody;
import com.newbiest.security.model.NBUser;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by guoxunbo on 2017/9/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class EntityListResponseBody extends ResponseBody {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name="DataList")
	@XmlElementRef
	private List<NBBase> datas;

	public List<NBBase> getDatas() {
		return datas;
	}

	public void setDatas(List<NBBase> datas) {
		this.datas = datas;
	}
}
