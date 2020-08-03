package com.newbiest.gc.express.dto;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 寄件人或者收件人信息
 * @author guoxunbo
 * @date 2020-07-08 15:47
 */
@Data
public class WaybillDelivery implements Serializable {

    private String companyName;

    private String person;

    private String phone;

    private String mobile;

    private String provinceName;

    private String cityName;

    private String countyName;

    private String address;

}
