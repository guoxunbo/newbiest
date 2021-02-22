package com.newbiest.mms;

/**
 * Created by guoxunbo on 2019-09-02 14:48
 */
public class SystemPropertyUtils {

    private static final String SYSTEM_PROPERTY_AUTO_CREATE_STORAGE_FLAG = "mms.auto_create_storage_flag";

    private static final String SYSTEM_PROPERTY_UNPACK_RECOVERY_LOT_FLAG = "unpack.recovery_lot_flag";

    private static final String SYSTEM_PROPERTY_CONNECT_SCM_FLAG = "gc.connect_scm_flag";

    private static final String SYSTEM_PROPERTY_CONNECT_MSCM_FLAG = "gc.connect_mscm_flag";

    private static final String SYSTEM_PROPERTY_WAFER_ISSUE_WITH_DOC_FLAG = "gc.wafer_issue_with_doc_flag";

    private static final String SYSTEM_PROPERTY_WAFER_ISSUE_TO_MES_PLAN_LOT_FLAG = "gc.wafer_issue_to_mes_plan_lot_flag";

    private static final String SYSTEM_PROPERTY_IMPORT_MLOT_POOL_SIZE = "gc.import_mlot_pool_size";

    public static Integer getImportMLotPoolSize() {
        Object importMLotPoolSize = System.getProperty(SYSTEM_PROPERTY_IMPORT_MLOT_POOL_SIZE);
        if (importMLotPoolSize != null) {
            return Integer.parseInt(importMLotPoolSize.toString());
        }
        return null;
    }


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

    public static boolean getConnectScmFlag() {
        Object connectScmFlag = System.getProperty(SYSTEM_PROPERTY_CONNECT_SCM_FLAG);
        if (connectScmFlag != null) {
            return Boolean.valueOf(connectScmFlag.toString());
        }
        return false;
    }

    public static boolean getWaferIssueWithDocFlag() {
        Object connectScmFlag = System.getProperty(SYSTEM_PROPERTY_WAFER_ISSUE_WITH_DOC_FLAG);
        if (connectScmFlag != null) {
            return Boolean.valueOf(connectScmFlag.toString());
        }
        return false;
    }

    public static boolean getWaferIssueToMesPlanLot() {
        Object planLotFlag = System.getProperty(SYSTEM_PROPERTY_WAFER_ISSUE_TO_MES_PLAN_LOT_FLAG);
        if (planLotFlag != null) {
            return Boolean.valueOf(planLotFlag.toString());
        }
        return false;
    }

    public static boolean getConnectMscmFlag() {
        Object connectMscmFlag = System.getProperty(SYSTEM_PROPERTY_CONNECT_MSCM_FLAG);
        if (connectMscmFlag != null) {
            return Boolean.valueOf(connectMscmFlag.toString());
        }
        return false;
    }
}
