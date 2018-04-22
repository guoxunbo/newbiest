package com.newbiest.base.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 需要导出的栏位
 * Created by guoxunbo on 2018/4/2.
 */
@Data
@NoArgsConstructor
public class SortingField implements Serializable{

    private static final long serialVersionUID = -3218955330022295433L;

    /**
     * 名称
     */
    private String name;

    /**
     * 位置
     */
    private int index;

    /**
     * 当前值
     */
    private Object value;

    /**
     * 是否验证通过
     */
    private boolean validated;

    public SortingField(String name, int index, Object value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

}
