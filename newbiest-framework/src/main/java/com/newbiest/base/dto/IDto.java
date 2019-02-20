package com.newbiest.base.dto;

import com.newbiest.base.exception.ClientException;

/**
 *
 * Created by guoxunbo on 2019/1/22.
 */
public interface IDto {

    /**
     * 源对象转换成Dto
     * @param source dto
     * @throws ClientException
     */
    void transfer2Dto(Object source) throws ClientException;

    /**
     * Dto转换成source对象
     * @param source
     * @throws ClientException
     */
    void transfer2Object(Object source) throws ClientException;

}
