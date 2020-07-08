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

    public static final String DEFAULT_COMPANY_NAME = "";
    public static final String DEFAULT_PERSON = "";
    public static final String DEFAULT_PHONE = "";
    public static final String DEFAULT_MOBILE = "";
    public static final String DEFAULT_PROVINCE_NAME = "";
    public static final String DEFAULT_CITY_NAME = "";
    public static final String DEFAULT_COUNTRY_NAME = "";
    public static final String DEFAULT_ADDRESS = "";

//    "preWaybillDelivery": {
//                "companyName": "寄件公司001",
//                        "person": "张三",
//                        "phone": "18379151111",
//                        "mobile": "",
//                        "provinceName": "广东省",
//                        "cityName": "深圳市",
//                        "countyName": "宝安区",
//                        "address": "福永街道福永二路深翔物流园"
//            },

    private String companyName;

    private String person;

    private String phone;

    private String mobile;

    private String provinceName;

    private String cityName;

    private String countyName;

    private String address;

    public WaybillDelivery build() {
        if (StringUtils.isNullOrEmpty(companyName)) {
            companyName = DEFAULT_COMPANY_NAME;
        }
        if (StringUtils.isNullOrEmpty(person)) {
            person = DEFAULT_PERSON;
        }
        if (StringUtils.isNullOrEmpty(phone)) {
            phone = DEFAULT_PHONE;
        }
        if (StringUtils.isNullOrEmpty(mobile)) {
            mobile = DEFAULT_MOBILE;
        }
        if (StringUtils.isNullOrEmpty(provinceName)) {
            provinceName = DEFAULT_PROVINCE_NAME;
        }
        if (StringUtils.isNullOrEmpty(cityName)) {
            cityName = DEFAULT_CITY_NAME;
        }
        if (StringUtils.isNullOrEmpty(countyName)) {
            countyName = DEFAULT_COUNTRY_NAME;
        }
        if (StringUtils.isNullOrEmpty(address)) {
            address = DEFAULT_ADDRESS;
        }
        return this;
    }
}
