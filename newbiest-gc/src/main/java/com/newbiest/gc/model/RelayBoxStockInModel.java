package com.newbiest.gc.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class RelayBoxStockInModel  implements Serializable {

    private String materialLotId;

    private String storageId;

}
