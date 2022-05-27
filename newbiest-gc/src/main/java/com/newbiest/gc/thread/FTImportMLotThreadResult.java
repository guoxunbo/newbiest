package com.newbiest.gc.thread;

import com.newbiest.msg.ResponseHeader;
import lombok.Data;
import java.io.Serializable;

/**
 * @author guozhangLuo
 * @date 2022-05-26
 */
@Data
public class FTImportMLotThreadResult implements Serializable {

    private String result = ResponseHeader.RESULT_SUCCESS;

    private String resultMessage;
}