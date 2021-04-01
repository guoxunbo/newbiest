/**
 * 更新Vanchip的MMS_MARTERIAL_LOT表以及历史表栏位备注
 */
comment on column MMS_MATERIAL_LOT.RESERVED1  is 'lotNo';
comment on column MMS_MATERIAL_LOT.RESERVED11  is 'ctnNo';
comment on column MMS_MATERIAL_LOT.RESERVED14  is 'REMARK';


comment on column MMS_MATERIAL_LOT_HIS.RESERVED1  is 'lotNo';
comment on column MMS_MATERIAL_LOT_HIS.RESERVED11  is 'ctnNo';
comment on column MMS_MATERIAL_LOT_HIS.RESERVED14  is 'REMARK';