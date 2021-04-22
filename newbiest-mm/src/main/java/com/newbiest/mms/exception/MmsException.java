package com.newbiest.mms.exception;

/**
 * Created by guoxunbo on 2019/2/15.
 */
public class MmsException {


    public static final String MM_RAW_MATERIAL_IS_EXIST = "mm.raw_material_is_exist";
    public static final String MM_RAW_MATERIAL_IS_NOT_EXIST = "mm.raw_material_is_not_exist";
    public static final String MM_RAW_MATERIAL_IS_NOT_SAME = "mm.raw_material_is_not_same";

    public static final String MM_MATERIAL_LOT_HOLD_IS_NOT_EXIST = "mm.material_lot_is_not_exist";
    public static final String MM_MATERIAL_LOT_HOLD_PASSWORD_IS_ERROR = "mm.material_lot_hold_pwd_is_error";


    public static final String MM_MATERIAL_LOT_HAS_EXPIRED = "mm.material_lot_has_expired";

    public static final String MM_MATERIAL_LOT_IS_NOT_EXIST = "mm.material_lot_is_not_exist";
    public static final String MM_STORAGE_IS_NOT_EXIST = "mm.storage_is_not_exist";
    public static final String MM_MATERIAL_LOT_IS_EXIST = "mm.material_lot_is_exist";
    public static final String MM_MATERIAL_LOT_ALREADY_HOLD = "mm.material_lot_already_hold";
    public static final String MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO = "mm.material_lot_qty_cant_less_then_zero";

    public static final String MM_MATERIAL_LOT_STOCK_QTY_CANT_LESS_THEN_ZERO = "mm.material_lot_stock_qty_cant_less_then_zero";
    public static final String MM_MATERIAL_LOT_IN_INVENTORY = "mm.material_lot_in_inventory";
    public static final String MM_MATERIAL_LOT_NOT_IN_INVENTORY = "mm.material_lot_not_in_inventory";
    public static final String MM_MATERIAL_LOT_MUST_STOCK_OUT_ALL = "mm.material_lot_must_stock_out_all";
    public static final String MM_MATERIAL_LOT_MUST_PICK_ALL = "mm.material_lot_must_pick_all";
    public static final String MM_MATERIAL_LOT_MUST_TRANSFER_ALL = "mm.material_lot_must_transfer_all";
    public static final String MM_MATERIAL_LOT_TRANSFER_MUST_DIFFERENT_STORAGE = "mm.material_lot_transfer_must_different_storage";
    public static final String MM_MATERIAL_LOT_ALREADY_FIN = "mm.material_lot_already_finish";

    public static final String MM_PACKAGE_TYPE_IS_NOT_EXIST = "mm.package_type_is_not_exist";
    public static final String MM_PACKAGE_MATERIAL_TYPE_IS_NOT_THE_SAME = "mm.package_material_type_is_not_the_same";
    public static final String MM_PACKAGE_OVER_MAX_QTY = "mm.package_over_max_qty";
    public static final String MM_IMPORT_FILE_CONTAINS_EMPTY_DATA = "mm.import_file_contains_empty_data";
    public static final String MM_IMPORT_FILE_AND_TYPE_IS_NOT_SAME = "mm.import_file_and_type_is_not_same";

    public static final String MM_PRODUCT_IS_EXIST = "mm.product_is_exist";
    public static final String MM_PRODUCT_IS_NOT_EXIST = "mm.product_is_not_exist";

    public static final String MM_RECEIVE_QTY_OVER_INCOMING_QTY = "mm.receive_qty_over_incoming_qty";

    public static final String MM_WORK_STATION_IS_NOT_EXIST = "mm.work_station_is_not_exist";
    public static final String MM_LBL_TEMPLATE_IS_NOT_EXIST = "mm.lbl_template_is_not_exist";
    public static final String MM_LBL_TEMPLATE_TYPE_IS_NOT_ALLOW = "mm.lbl_template_is_not_allow";

    public static final String MM_LAB_MATERIAL_IS_EXIST = "mm.lab_material_is_exist";
    public static final String MM_LAB_MATERIAL_IS_NOT_EXIST = "mm.lab_material_is_not_exist";

    public static final String MM_IQC_IS_NOT_EXIST = "mm.iqc_is_not_exist";
    public static final String MM_WAREHOUSE_IS_NOT_EXIST = "mm.warehouse_is_not_exist";
}
