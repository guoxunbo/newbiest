alter table MMS_MATERIAL move tablespace TS_NB_DAT;
alter table MMS_MATERIAL_HIS move tablespace TS_NB_DAT;
alter table MMS_MATERIAL_LOT move tablespace TS_NB_DAT;
alter table MMS_MATERIAL_LOT_HIS move tablespace TS_NB_DAT;
alter table MMS_MATERIAL_LOT_INVENTORY move tablespace TS_NB_DAT;
alter table MMS_PACKAGE_TYPE move tablespace TS_NB_DAT;
alter table MMS_WAREHOUSE move tablespace TS_NB_DAT;

alter index PK_MMS_MATERIAL rebuild tablespace TS_NB_IDX;
alter index UK_MAT_ORG_NAME_CLASS_VERSION rebuild tablespace TS_NB_IDX;
alter index PK_MMS_MATERIAL_HIS  rebuild tablespace TS_NB_IDX;
alter index PK_MMS_MATERIAL_LOT  rebuild tablespace TS_NB_IDX;
alter index UK_MLOT_ORG_MATERIAL_LOT_ID  rebuild tablespace TS_NB_IDX;
alter index PK_MMS_MATERIAL_LOT_INVENTORY  rebuild tablespace TS_NB_IDX;
alter index PK_MMS_PACKAGE_TYPE  rebuild tablespace TS_NB_IDX;
alter index UK_PACK_TYPE_ORG_NAME_CLASS  rebuild tablespace TS_NB_IDX;
alter index PK_MMS_WAREHOUSE  rebuild tablespace TS_NB_IDX;
alter index UK_MMS_WAREHOUSE_ORG_RRN_NAME  rebuild tablespace TS_NB_IDX;


