package com.newbiest.mms;

/**
 * Created by guoxunbo on 2019-09-02 14:48
 */
public class SystemPropertyUtils {

    public static final String SYSTEM_PROPERTY_AUTO_CREATE_STORAGE_FLAG = "mms.auto_create_storage_flag";

    public static final String SYSTEM_PROPERTY_UNPACK_RECOVERY_LOT_FLAG = "unpack.recovery_lot_flag";

    public static final String SYSTEM_PROPERTY_WAFER_ISSUE_WITH_DOC_FLAG = "gc.wafer_issue_with_doc_flag";

    public static boolean getAutoCreateStorageFlag() {
        Object autoCreateStorageFlag = System.getProperty(SYSTEM_PROPERTY_AUTO_CREATE_STORAGE_FLAG);
        if (autoCreateStorageFlag != null) {
            return Boolean.valueOf(autoCreateStorageFlag.toString());
        }
        return false;
    }

    public static boolean getUnpackRecoveryLotFlag() {
        Object unpackRecoveryLotQtyFlag = System.getProperty(SYSTEM_PROPERTY_UNPACK_RECOVERY_LOT_FLAG);
        if (unpackRecoveryLotQtyFlag != null) {
            return Boolean.valueOf(unpackRecoveryLotQtyFlag.toString());
        }
        return false;
    }

    public static boolean getWaferIssueWithDocFlag() {
        Object waferIssueWithDocFlag = System.getProperty(SYSTEM_PROPERTY_WAFER_ISSUE_WITH_DOC_FLAG);
        if (waferIssueWithDocFlag != null) {
            return Boolean.valueOf(waferIssueWithDocFlag.toString());
        }
        return false;
    }


}
