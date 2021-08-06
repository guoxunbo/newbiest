package com.newbiest.vanchip.dto.erp;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErpResponse implements Serializable {

    private ErpResponseReturn Return;
    
}
