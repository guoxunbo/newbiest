package com.newbiest.base.rest.entityList;

import com.newbiest.base.model.NBBase;
import com.newbiest.msg.ResponseBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Data
public class EntityListResponseBody extends ResponseBody {

    private static final long serialVersionUID = 1L;

    private List<? extends NBBase> dataList;

}
