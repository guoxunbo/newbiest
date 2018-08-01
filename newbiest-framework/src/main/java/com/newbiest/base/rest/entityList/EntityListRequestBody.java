package com.newbiest.base.rest.entityList;

import com.newbiest.msg.RequestBody;
import lombok.Data;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/31.
 */
@Data
public class EntityListRequestBody extends RequestBody {

    private static final long serialVersionUID = 1L;

    private String entityModel;

    private String whereClause;

    private String orderBy;

    private int maxResult;

    private int firstResult;

    private List<String> fields;
}
