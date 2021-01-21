package com.newbiest.vanchip.dto.mes;

import lombok.Data;

import java.io.Serializable;

@Data
public class MesResponse implements Serializable {

    private MesResponseBody body;

    private MesResponseHeader header;

}
