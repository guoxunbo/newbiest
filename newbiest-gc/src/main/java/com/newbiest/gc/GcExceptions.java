package com.newbiest.gc;

/**
 * Created by guoxunbo on 2019-09-12 14:02
 */
public class GcExceptions {

    public static final String ERP_RETEST_ORDER_IS_NOT_EXIST = "gc.erp_retest_order_is_not_exist";
    public static final String ERP_WAFER_ISSUE_ORDER_IS_NOT_EXIST = "gc.erp_wafer_issue_order_is_not_exist";

    public static final String ERP_RECEIVE_ORDER_IS_NOT_EXIST = "gc.erp_receive_order_is_not_exist";

    public static final String ERP_SO_IS_NOT_EXIST = "gc.erp_so_is_not_exist";
    public static final String ERP_SOB_IS_NOT_EXIST = "gc.erp_sob_is_not_exist";
    public static final String RESERVED_OVER_QTY = "gc.reserved_over_qty";

    public static final String MATERIAL_LOT_NOT_MATCH_ORDER = "gc.meterial_lot_not_match_order";
    public static final String MATERIAL_LOT_RESERVED_BY_ANOTHER = "gc.meterial_lot_reserved_by_another";

    public static final String OVER_DOC_QTY = "gc.over_doc_qty";
    public static final String MATERIAL_LOT_WAREHOUSE_IS_NULL = "gc.material_lot_is_not_def_warehouse";
    public static final String MATERIAL_LOT_TREASURY_INFO_IS_NOT_SAME = "gc.material_lot_treaseury_is_not_same";
    public static final String PRODUCT_AND_SUBCODE_IS_EXIST = "gc.productId_and_subcode_is_exist";
    public static final String PRODUCT_AND_SUBCODE_IS_NOT_EXIST = "gc.productId_and_subcode_is_not_exist";
    public static final String PRODUCT_ID_CANNOT_EMPTY = "gc.productId_cannot_empty";
    public static final String FILE_NAME_CANNOT_BONDED_PROPERTY_INFO = "gc.file_name_cannot_bonded_property_innfo";
    public static final String BOXAID_AND_BOXBID_IS_EXIST = "gc.boxaId_and_boxbId_is_exist";
    public static final String MATERIALNAME_AND_WAFERQTY_IS_NOT_SAME = "gc.materialName_and_waferqty_is_not_same";
    public static final String MATERIALNAME_IS_ERROR = "gc.materialName_is_error";
    public static final String MATERIALNAME_IS_NOT_SAME = "gc.materialName_is_not_same";
    public static final String MATERIAL_LOT_FOUR_CODE_ERROR = "gc.material_lot_four_code_error";
    public static final String MATERIAL_LOT_WAFER_QTY_IS_NOT_SAME_REAL_QTY = "gc.material_lot_wafer_qty_is_not_same_real_qty";
    public static final String MUST_DELETE_FULL_BOX_DATA = "gc.must_delete_full_box_data";
    public static final String MATERIALLOT_VENDER_IS_NOT_SAME = "gc.material_lot_vender_is_not_same";
    public static final String MATERIAL_LOT_TAG_QTY_OVER_PO_QTY = "gc.material_lot_tag_qty_over_po_qty";
    public static final String MATERIAL_NAME_DESCRIPTION_IS_NOT_CONFIGURED = "gc.material_name_description_is_not_configured";

    public static final String WAREHOUSE_CANNOT_EMPTY = "gc.warehouse_cannot_empty";
    public static final String ERP_WAREHOUSE_CODE_IS_UNDEFINED = "gc.erp_undefined_warehouse_code";

    public static final String CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY = "gc.corresponding_raw_material_info_is_empty";

    public static final String INCOMINGMLOT_QTY_AND_SENTOUT_QTY_DISCREPANCY = "gc.incomingMlot_qty_and_sentOut_qty_discrepancy";


    //快递相关
    public static final String MATERIAL_LOT_ALREADY_RECORD_EXPRESS = "gc.material_lot_recorded_express";
    public static final String MATERIAL_LOT_NOT_RECORD_EXPRESS = "gc.material_lot_not_recorded_express";
    public static final String EXPRESS_NUMBER_IS_INCONSISTENT = "gc.express_number_is_inconsistent";

    public static final String GET_EXPRESS_TOKEN_ERROR = "gc.get_express_token_error";
    public static final String SHIPPING_ADDRESS_IS_NULL = "gc.shipping_address_is_null";
    public static final String EXPRESS_NETWORK_ERROR = "gc.express_network_error";
    public static final String PICKUP_ADDRESS_IS_NULL = "gc.pickup_address_is_null";
    public static final String PICKUP_ADDRESS_MORE_THEN_ONE = "gc.pickup_address_more_then_one";


}
