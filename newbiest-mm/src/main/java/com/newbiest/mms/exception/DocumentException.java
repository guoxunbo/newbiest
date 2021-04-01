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

    public static final String DOCUMENT_NOT_RESERVED_MLOT = "mms.doc_not_reserved_mlot";

    public static final String MATERIAL_LOT_ALREADY_BOUND_ORDER = "mms.material_lot_already_bound_order";
    public static final String MATERIAL_LOT_ALREADY_RESERVED = "mms.material_lot_already_reserved";

    public static final String MLOT_TOTAL_QTY_GREATER_THAN_DOCLINE_UNHANDLED_QTY = "mms.mlot_total_qty_greater_than_docline_unhandled_qty";
}
