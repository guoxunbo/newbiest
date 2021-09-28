/**
 * 更新Vanchip的MMS_MARTERIAL_lot表栏位 备注
 */
comment on column MMS_MATERIAL_LOT.INFERIOR_PRODUCTS_FLAG is '成品N/次品Y/RA品R';
comment on column MMS_MATERIAL_LOT.RETEST_FLAG is '重测标识-重测Y/正常N';
comment on column MMS_MATERIAL_LOT.RMA_FLAG is 'RMA来料类型/自身/非自身';

comment on column MMS_MATERIAL_LOT_HIS.INFERIOR_PRODUCTS_FLAG is '成品N/次品Y/RA品R';
comment on column MMS_MATERIAL_LOT_HIS.RETEST_FLAG is '重测标识-重测Y/正常N';
comment on column MMS_MATERIAL_LOT_HIS.RMA_FLAG is 'RMA来料类型/自身/非自身';
