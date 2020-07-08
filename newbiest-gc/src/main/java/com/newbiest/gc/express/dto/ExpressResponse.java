package com.newbiest.gc.express.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoxunbo
 * @date 2020-07-08 17:34
 */
@Data
public class ExpressResponse implements Serializable {

    private String waybillNumber;

    private String areaCode;

    private String network;

    private String serviceMode;

    private String threeCode;

    private String orderId;

}
