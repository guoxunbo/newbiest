package com.newbiest.vanchip.dto.vlm;

import lombok.Data;

import java.io.Serializable;

@Data
public class VLMResult implements Serializable {

    public static final String STATUS_FAIL = "E";
    public static final String STATUS_SUCCESS = "T";

    private String return_status;
    private String return_message;
    private String return_result;
}
