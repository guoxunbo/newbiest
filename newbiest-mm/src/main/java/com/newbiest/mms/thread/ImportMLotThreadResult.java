package com.newbiest.mms.thread;

import com.newbiest.mms.model.MaterialLotUnit;
import com.newbiest.msg.ResponseHeader;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author guoxunbo
 * @date 2020-10-15 14:39
 */
@Data
public class ImportMLotThreadResult implements Serializable {

    private String result = ResponseHeader.RESULT_SUCCESS;

    private List<MaterialLotUnit> materialLotUnits;
}
