package com.newbiest.mms.exception;

/**
 * Created by guoxunbo on 2019/2/15.
 */
public class MmsException {

    public static final String MM_RAW_MATERIAL_IS_EXIST = "mm.raw_material_is_exist";
    public static final String MM_RAW_MATERIAL_IS_NOT_EXIST = "mm.raw_material_is_not_exist";
    public static final String MM_WAREHOUSE_IS_NOT_EXIST = "mm.warehouse_is_not_exist";
    public static final String MM_PRODUCT_ID_IS_NOT_EXIST = "mm.prductId_is_not_exist";
    public static final String MM_SPARE_ID_IS_EXIST = "mm.spareId_is_exist";
    public static final String MM_PARTS_IS_NOT_EXIST = "mm.parts_is_not_exist";

    public static final String MM_MATERIAL_LOT_HAS_EXPIRED = "mm.material_lot_has_expired";

    public static final String MM_MATERIAL_LOT_IS_NOT_EXIST = "mm.material_lot_is_not_exist";
    public static final String MM_STORAGE_IS_NOT_EXIST = "mm.storage_is_not_exist";
    public static final String MM_MATERIAL_LOT_IS_EXIST = "mm.material_lot_is_exist";
    public static final String MM_MATERIAL_LOT_ALREADY_HOLD = "mm.material_lot_already_hold";
    public static final String MM_MATERIAL_LOT_QTY_CANT_LESS_THEN_ZERO = "mm.material_lot_qty_cant_less_then_zero";

    public static final String MM_MATERIAL_LOT_STOCK_QTY_CANOT_LESS_THEN_ZERO = "mm.material_lot_stock_qty_cant_less_then_zero";
    public static final String MM_MATERIAL_LOT_NOT_SUPPORT_MULTI_INVENTORY = "mm.material_lot_not_support_multi_inventory";
    public static final String MM_MATERIAL_LOT_IN_INVENTORY = "mm.material_lot_in_inventory";
    public static final String MM_MATERIAL_LOT_NOT_IN_INVENTORY = "mm.material_lot_not_in_inventory";
    public static final String MM_MATERIAL_LOT_MUST_STOCK_OUT_ALL = "mm.material_lot_must_stock_out_all";
    public static final String MM_MATERIAL_LOT_MUST_PICK_ALL = "mm.material_lot_must_pick_all";
    public static final String MM_MATERIAL_LOT_MUST_TRANSFER_ALL = "mm.material_lot_must_transfer_all";
    public static final String MM_MATERIAL_LOT_ALREADY_FIN = "mm.material_lot_already_finish";

    public static final String MM_PACKAGE_TYPE_IS_NOT_EXIST = "mm.package_type_is_not_exist";
    public static final String MM_PACKAGE_OVER_MAX_QTY = "mm.package_over_max_qty";

    public static final String MM_MATERIAL_LOT_RESERVED_INFO_IS_NOT_SAME = "mm.material_lot_reserved_info_is_not_same";
    public static final String MM_MATERIAL_LOT_IS_NOT_RESERVED_ALL = "mm.material_lot_is_not_reserved_all";

    public static final String MM_MATERIAL_LOT_CURRENT_QTY_LESS_THAN_ZERO= "mm.material_lot_currentQty_less_than_zero";
    public static final String MM_MATERIAL_LOT_UNIT_ID_REPEATS = "mm.material_lot_unitId_repeats";

    public static final String MM_IMPORT_FILE_AND_TYPE_IS_NOT_SAME = "mm.material_lot_import_type_is_not_same";
    public static final String MM_IMPORT_FILE_CONTAINS_EMPTY_DATA = "mm.the_import_file_contains_empty_data";

    public static final String MM_MATERIAL_LOT_IMPORT_TIME_OUT = "mm.material_lot_imp_time_out";

    public static final String MM_RAW_MATERIAL_TYPE_NOT_SAME = "mm.raw_material_type_not_same";
    public static final String MM_MATERIAL_LOT_UNIT_SIZE_MORE_THAN_THIRTEEN = "mm.material_lot_unit_size_more_than_thirteen";

    public static final String MM_WLA_IMPORT_MATERIAL_LOT_UNIT_SIZE_IS_OVER_THIRTEEN = "mm.wla_import_material_lot_unit_size_is_over_thirteen";

    public static final String MM_WORK_STATION_IS_NOT_EXIST = "mm.work_station_is_not_exist";
    public static final String MM_LBL_TEMPLATE_IS_NOT_EXIST = "mm.lbl_template_is_not_exist";
    public static final String MM_LBL_TEMPLATE_TYPE_IS_NOT_ALLOW = "mm.lbl_template_is_not_allow";

    //标签打印相关
    public static final String MATERIAL_LOT_NOT_RECORD_EXPRESS = "gc.material_lot_not_recorded_express";
    public static final String MATERIALLOT_PACKED_DETIAL_IS_NULL = "gc.material_lot_packed_detial_is_null";
    public static final String MATERIALLOT_WAFER_QTY_MORE_THAN_THIRTEEN = "gc.material_lot_wafer_qty_more_than_thirteen";

    public static final String MATERIAL_LOT_IS_HOLD_BY_SCM = "gc.material_lot_is_hold_by_scm";
    public static final String MATERIAL_LOT_IS_HOLD_BY_OTHERS = "gc.material_lot_is_hold_by_others";

}
