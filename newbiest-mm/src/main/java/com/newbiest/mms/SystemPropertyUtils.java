package com.newbiest.mms;

/**
 * Created by guoxunbo on 2019-09-02 14:48
 */
public class SystemPropertyUtils {

    public static final String SYSTEM_PROPERTY_AUTO_CREATE_STORAGE_FLAG = "mms.auto_create_storage_flag";

    public static final String SYSTEM_PROPERTY_UNPACK_RECOVERY_LOT_QTY_FLAG = "unpack.recovery_lot_qty_flag";

    public static boolean getAutoCreateStorageFlag() {
        Object autoCreateStorageFlag = System.getProperty(SYSTEM_PROPERTY_AUTO_CREATE_STORAGE_FLAG);
        if (autoCreateStorageFlag != null) {
            return Boolean.valueOf(autoCreateStorageFlag.toString());
        }
        return false;
    }

    public static boolean getUnpackRecoveryLotQtyFlag() {
        Object unpackRecoveryLotQtyFlag = System.getProperty(SYSTEM_PROPERTY_UNPACK_RECOVERY_LOT_QTY_FLAG);
        if (unpackRecoveryLotQtyFlag != null) {
            return Boolean.valueOf(unpackRecoveryLotQtyFlag.toString());
        }
        return false;
    }


}
