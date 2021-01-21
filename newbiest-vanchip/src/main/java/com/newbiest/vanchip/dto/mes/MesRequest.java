package com.newbiest.vanchip.dto.mes;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class MesRequest implements Serializable {

    private MesRequestHeader header;
    private MesRequestBody body;

    public abstract MesRequestBody getBody();
}
