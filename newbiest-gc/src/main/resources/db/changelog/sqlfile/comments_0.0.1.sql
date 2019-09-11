/**
 * GC使用到的reserved栏位做备注
 */
comment on column MMS_MATERIAL_LOT.RESERVED1 is 'ES完成品的levelTwoCode';
comment on column MMS_MATERIAL_LOT.RESERVED2 is 'MES完成品的waferId';
comment on column MMS_MATERIAL_LOT.RESERVED3 is 'MES完成品的salesNote';
comment on column MMS_MATERIAL_LOT.RESERVED4 is 'MES完成品的treasuryNote';
comment on column MMS_MATERIAL_LOT.RESERVED5 is 'MES完成品的productionNote';
comment on column MMS_MATERIAL_LOT.RESERVED6 is 'MES完成品的bondedProperty';
comment on column MMS_MATERIAL_LOT.RESERVED7 is 'MES完成品的productCategory';
comment on column MMS_MATERIAL_LOT.RESERVED8 is '中转箱号';
comment on column MMS_MATERIAL_LOT.RESERVED9 is '装箱检验判定等级';
comment on column MMS_MATERIAL_LOT.RESERVED10 is '装箱检验判定码';

comment on column MMS_DOCUMENT_LINE.RESERVED1 is '关联ERP LINE的SEQ主键';
comment on column MMS_DOCUMENT_LINE.RESERVED2 is '关联ERP secondcode';
comment on column MMS_DOCUMENT_LINE.RESERVED3 is '关联ERP grade';
comment on column MMS_DOCUMENT_LINE.RESERVED4 is '关联ERP cfree3';
comment on column MMS_DOCUMENT_LINE.RESERVED5 is '关联ERP CMAKER';
comment on column MMS_DOCUMENT_LINE.RESERVED6 is '关联ERP CHANDLER';
comment on column MMS_DOCUMENT_LINE.RESERVED7 is '关联 ERP OTHER1';
comment on column MMS_DOCUMENT_LINE.RESERVED8 is ' 光联erp CUSNAME';
comment on column MMS_DOCUMENT_LINE.RESERVED9 is '单据类型 GC特殊要求。不显示document主表，只显示line信息。所以所有信息都得带过来';
