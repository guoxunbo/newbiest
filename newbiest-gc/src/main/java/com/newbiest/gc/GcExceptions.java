package com.newbiest.gc;

/**
 * Created by guoxunbo on 2019-09-12 14:02
 */
public class GcExceptions {

    public static final String ABNORMAL_FILE_QUANTITY = "gc.abnormal_file_quantity";
    public static final String PRINT_TYPE_IS_NOT_SUPPORTED = "gc.print_type_is_not_supported";
    public static final String VBOX_AND_WAFER_CANNOT_TOGETHER_IMPORT = "gc.vbox_and_wafer_cannot_together_import";
    public static final String COB_AND_OTHER_WAFER_CANNOT_TOGETHER_IMPORT = "gc.cob_and_other_wafer_cannot_together_import";
    public static final String VBOX_AND_BBOX_CANNOT_TOGETHER_IMPORT = "gc.vbox_and_bbox_cannot_together_import";

    public static final String ERP_RETEST_ORDER_IS_NOT_EXIST = "gc.erp_retest_order_is_not_exist";
    public static final String ERP_RAW_MATERIAL_ISSUE_ORDER_IS_NOT_EXIST = "gc.erp_raw_material_issue_order_is_not_exist";
    public static final String ERP_RECEIVE_ORDER_IS_NOT_EXIST = "gc.erp_receive_order_is_not_exist";
    public static final String ERP_SOB_IS_NOT_EXIST = "gc.erp_sob_is_not_exist";
    public static final String RESERVED_OVER_QTY = "gc.reserved_over_qty";
    public static final String ERP_SOA_IS_NOT_EXIST = "gc.erp_soa_is_not_exist";
    public static final String ERP_SOA_CUSCODE_IS_ERROR = "gc.erp_soa_cuscode_is_error";
    public static final String CHOOSE_STOCK_OUT_ORDER_PLEASE = "gc.choose_stock_out_order_please";
    public static final String MATERIAL_LOT_HAS_BEEN_SOLD_BY_THREE_PARTIES = "gc.the_materialLot_has_been_sold_by_three_parties";
    public static final String ERP_ORDER_CANNOT_EMPTY = "gc.erp_order_seq_cannot_empty";
    public static final String OVER_DOC_QTY = "gc.over_doc_qty";
    public static final String ORDER_IS_NOT_EXIST = "gc.order_is_not_exist";
    public static final String THERE_ARE_MULTIPLE_DOCUMENTS_PLEASE_MERGE_DOC = "gc.there_are_multiple_documents_please_merge_doc";
    public static final String DOCUMENT_LINE_MERGE_RULE_IS_NOE_EXIST = "gc.document_line_merge_rule_is_not_exist";
    public static final String THE_DOCUMENT_HAS_BEEN_OPERATED_AND_CANNOT_BE_CANCELED = "gc.the_document_has_been_operated_and_cannot_be_canceled";
    public static final String THE_DOCUMENT_IS_ERROR_PLEASE_CALL_ENGINNER = "gc.the_document_is_error_please_call_enginner";
    public static final String THE_DOCUMENT_GRADE_OR_SUBCODE_IS_NULL = "gc.the_order_grade_or_subcode_is_null";
    public static final String THE_DOC_TYPE_IS_ERROR_PLEASE_CALL_ENGINEER = "gc.the_doc_type_is_error_please_call_engineer";

    public static final String MATERIAL_LOT_ALREADY_IN_INVENTORY = "gc.meterial_lot_already_in_inventory";
    public static final String MATERIAL_LOT_MUST_BE_COB_LOT = "gc.meterial_lot_must_be_cob";
    public static final String MATERIAL_LOT_IS_NOT_EXIST_SUBCODE_UNIT = "gc.material_lot_is_not_exist_subcode_unit";
    public static final String MATERIAL_LOT_NOT_MATCH_ORDER = "gc.meterial_lot_not_match_order";
    public static final String MATERIAL_LOT_RESERVED_BY_ANOTHER = "gc.meterial_lot_reserved_by_another";
    public static final String MATERIAL_LOT_IMPORT_FILE_IS_ERRROR = "gc.meterial_lot_import_file_is_error";
    public static final String MATERIAL_LOT_ID_IS_EXIST = "gc.material_lot_id_is_exist";
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
    public static final String UNIT_ID_ALREADY_BONDING_WORKORDER_ID = "gc.unitId_already_bonding_workorderId";
    public static final String MATERIAL_LOT_TAG_INFO_IS_NOT_SAME = "gc.material_lot_tag_info_is_not_same";
    public static final String WORKORDER_GRADE_HOLD_INFO_IS_EXIST = "gc.workorderId_grade_hold_info_is_exist";
    public static final String MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST = "gc.mlot_doc_validate_rule_is_not_exist";
    public static final String MATERIALLOT_MATERIAL_NAME_IS_NOT_SAME = "gc.material_lot_material_name_is_not_same";
    public static final String MATERIALLOT_PACKAGE_RULE_IS_ERROR = "gc.material_lot_package_rule_is_error";
    public static final String MATERIALLOT_PACKAGE_MUST_REMARK_ALL = "gc.material_lot_package_must_remark_all";
    public static final String MATERIALLOT_RESERVED_DOCID_IS_NOT_SAME = "gc.material_lot_reserved_docId_is_not_same";
    public static final String MATERIALLOT_RESERVED_ORDER_IS_NULL = "gc.material_lot_reserved_order_is_null";
    public static final String WAFER_ID__IS_NOT_EXIST = "gc.wafer_id_is_not_exist";
    public static final String MATERIAL_LOT_CUSTOMER_NAME_IS_NOT_SAME = "gc.material_lot_customer_name_is_not_same";
    public static final String MATERIAL_LOT_ABBREVIATION_IS_NOT_SAME = "gc.material_lot_abbreviation_is_not_same";
    public static final String MATERIAL_LOT_SHIP_ORDER_ID_IS_NOT_SAME = "gc.material_lot_ship_order_id_is_not_same";
    public static final String MATERIAL_LOT_IS_NOT_PACKED = "gc.material_lot_is_not_packed";
    public static final String MATERIAL_LOT_ALREADY_RECEIVE = "gc.material_lot_already_receive";
    public static final String MATERIAL_LOT_UNIT_QTY_IS_ERROR = "gc.material_lot_unit_qty_is_error";
    public static final String THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO = "gc.the_quantity_field_must_be_greater_than_zero";
    public static final String MATERIAL_LOT_NOT_IN_RULE_CONTROL_CANNOT_IMPORT = "gc.material_lot_not_in_rule_control_cannot_import";

    public static final String WAREHOUSE_CANNOT_EMPTY = "gc.warehouse_cannot_empty";
    public static final String ERP_WAREHOUSE_CODE_IS_UNDEFINED = "gc.erp_undefined_warehouse_code";
    public static final String WAREHOUSE_AND_BONDPRO_ARE_INCONSISTENT = "gc.warehouse_and_bondPro_are_inconsistent";

    public static final String CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY = "gc.corresponding_raw_material_info_is_empty";
    public static final String INCOMINGMLOT_QTY_AND_SENTOUT_QTY_DISCREPANCY = "gc.incomingMlot_qty_and_sentOut_qty_discrepancy";

    public static final String GET_EXPRESS_TOKEN_ERROR = "gc.get_express_token_error";
    public static final String SHIPPING_ADDRESS_IS_NULL = "gc.shipping_address_is_null";
    public static final String EXPRESS_NETWORK_ERROR = "gc.express_network_error";
    public static final String PICKUP_ADDRESS_IS_NULL = "gc.pickup_address_is_null";
    public static final String PICKUP_ADDRESS_MORE_THEN_ONE = "gc.pickup_address_more_then_one";
    public static final String SHIPPER_IS_NOT_SAME = "gc.shipper_is_not_same";
    public static final String BOOKS_IS_NOT_SAME = "gc.books_is_not_same";

    //产品相关
    public static final String PRODUCT_NUMBER_RELATION_IS_EXIST = "gc.product_number_relation_is_exist";
    public static final String PRODUCT_NUMBER_RELATION_IS_ERROR = "gc.product_number_relation_is_error";
    public static final String PRODUCT_NUMBER_RELATION_IS_NOT_EXIST = "gc.product_number_relation_is_not_exist";

    //MSCM
    public static final String MSCM_ERROR = "gc.mscm_error";
    public static final String SCM_LOT_INFO_CONTAINS_EMPTY_VALUE = "gc.lotInfo_contains_empty_value";
    public static final String SCM_LOT_ID_ALREADY_EXISTS = "gc.lotId_already_exists";
    public static final String SCM_LOT_ID_IS_NOT_EXIST = "gc.lotId_is_not_exist";

    //原材料相关
    public static final String TAPA_MATERIAL_CODE_IS_ERROR = "gc.tape_material_code_is_error";
    public static final String BLADE_MATERIAL_CODE_IS_ERROR = "gc.Blade_material_code_is_error";
    public static final String IRA_RAW_MATERIAL_BOX_ID_CANNOT_EMPTY = "gc.ira_raw_material_box_id_cannot_empty";
    public static final String IRA_RAW_MATERIAL_BOX_ID_IS_EXISTS = "gc.ira_raw_material_box_id_is_exists";
    public static final String TAPE_MATERIAL_LOT_ID_IS_REPEAT = "gc.tape_material_lot_id_is_repeat";
    public static final String RAW_MATERIAL_LOT_EXPDATE_LESS_THAN_WARNING_LIFE = "gc.raw_material_lot_expdate_less_than_warning_life";
    public static final String IRA_MATERIAL_LOT_BOX_MUST_SATRT_WITH_GCB = "gc.ira_material_lot_box_must_start_with_GCB";
    public static final String MATERIAL_TYPE_IS_NOT_SAME = "gc.material_type_is_not_same";
    public static final String MATERIAL_TYPE_AND_MATERIAL_LOT_IS_NOT_SAME = "gc.material_type_and_material_lot_is_not_same";
    public static final String RAW_DOCUMENT_LINE_IS_EMPTY = "gc.raw_document_line_is_empty";
    public static final String PLEASE_ISSUE_MATERIAL_LOT_EXP_DATE_EARLIER = "gc.please_issue_material_lot_exp_date_earlier";
    public static final String GLUE_MATERIAL_HAS_EXPIRED = "gc.glue_material_has_expired";
    public static final String RAW_MATERIAL_LOT_MFG_DATE_IS_AFTER_EXP_DATE = "gc.raw_material_lot_mfg_date_is_after_exp_date";
    public static final String RAW_MATERIAL_LOT_MFG_DATE_IS_AFTER_CURRENT_TIME = "gc.raw_material_lot_mfg_date_is_after_current_time";

    public static final String CANNOT_FIND_TEST_DATA = "gc.cannot_find_test_data";
}
