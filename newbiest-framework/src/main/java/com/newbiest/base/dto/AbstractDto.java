package com.newbiest.base.dto;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.utils.PropertyUtils;
import lombok.Data;

/**
 * 所有DTO的父类
 * Created by guoxunbo on 2019/1/22.
 */
@Data
public class AbstractDto implements IDto {

    private static final long serialVersionUID = 2142161200805035229L;

    /**
     * 源对象
     */
    private Object sourceObject;

    @Override
    public void transfer2Dto(Object source) throws ClientException {
        PropertyUtils.copyProperties(sourceObject, this);
    }

    @Override
    public void transfer2Object(Object source) throws ClientException {
        PropertyUtils.copyProperties(this, source);
    }
}
