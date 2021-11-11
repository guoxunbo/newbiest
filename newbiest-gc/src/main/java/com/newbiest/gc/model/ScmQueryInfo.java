package com.newbiest.gc.model;

import lombok.Data;
import java.io.Serializable;

/**
 * Created by guozhangLuo on 2021-11-05 15:00
 */
@Data
public class ScmQueryInfo implements Serializable {

    private String lotNo;

    private String waferSeq;

    private String woNo;

}
