package com.newbiest.vanchip.dto.returnlot;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnMLotResponse implements Serializable {

        private ReturnMLotResponseBody body;

        private ReturnMLotResponseHeader header;

}
