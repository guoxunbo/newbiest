package com.newbiest.mms.exception;

/**
 * @author guoxunbo
 * @date 12/24/20 4:58 PM
 */
public class DocumentException {

    public static final String DOCUMENT_IS_NOT_EXIST = "mms.doc_is_not_exist";
    public static final String DOCUMENT_IS_EXIST = "mms.doc_is_exist";

    public static final String DOCUMENT_IS_NOT_SAME = "mms.doc_is_not_same";

    public static final String DOCUMENT_QTY_NOT_ENOUGH = "mms.doc_qty_is_not_enough";
    public static final String DOCUMENT_STATUS_IS_NOT_ALLOW= "mms.doc_status_is_not_allow";

    public static final String MATERIAL_LOT_ALREADY_BOUND_ORDER = "mms.material_lot_already_bound_order";
    public static final String MATERIAL_LOT_NOT_BOUND_ORDER = "mms.material_lot_not_bound_order";

    public static final String DOCUMENT_NOT_CANT_DELETE = "mm_document_not_cant_delete";

    public static final String DOCUMENT_CATEGORY_IS_NOT_EXIST = "mms.document_category_is_not_exist";
    public static final String OPERATIONS_QTY_GREATER_THAN_ACTUAL_QTY = "mms.operations_qty_greater_than_actual_qty";

    public static final String OPERATIONS_QTY_IS_NOT_EQUAL_STOCK_QTY= "mms.operations_quantity_is_not_equal_to_inventory_quantity";

    public static final String SHIP_TYPE_IS_NOT_EXIST = "mms.ship_type_is_not_exist";

    public static final String DOC_CAN_NOT_BE_MODIFIED = "mms.doc_can_not_be_modified";
}
