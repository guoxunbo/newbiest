package com.newbiest.base.utils;

import com.newbiest.base.annotation.Export;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by guoxunbo on 2018/4/2.
 */
@Data
public class People implements Serializable {
    private static final long serialVersionUID = 18497319627710589L;

    @Export
    private String name;

    @Export
    private int age;

    @Export
    private Date birthDay;

}
