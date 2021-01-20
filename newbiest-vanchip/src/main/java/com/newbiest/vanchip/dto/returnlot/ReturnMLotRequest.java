package com.newbiest.vanchip.dto.returnlot;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnMLotRequest implements Serializable {

        private ReturnMLotRequestBody body;

        private ReturnMLotRequestHeader header;

}
