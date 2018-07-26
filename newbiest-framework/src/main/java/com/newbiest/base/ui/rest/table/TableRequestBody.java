package com.newbiest.base.ui.rest.table;

import com.newbiest.base.ui.model.NBTable;
import com.newbiest.msg.RequestBody;
import lombok.Data;

/**
 * Created by guoxunbo on 2018/7/26.
 */
@Data
public class TableRequestBody extends RequestBody {

    private String actionType;

    private Long authorityRrn;

    private NBTable table;
}
