/**
 * 盘点表GC_CHECK_HISTORY使用到的reserved栏位做备注
 */
comment on column GC_CHECK_HISTORY.RESERVED1 is '二级代码';
comment on column GC_CHECK_HISTORY.RESERVED2 is 'MES完成品的waferId';
comment on column GC_CHECK_HISTORY.RESERVED3 is 'MES完成品的salesNote';
comment on column GC_CHECK_HISTORY.RESERVED4 is 'MES完成品的treasuryNote';
comment on column GC_CHECK_HISTORY.RESERVED5 is 'MES完成品的productionNote';
comment on column GC_CHECK_HISTORY.RESERVED6 is 'MES完成品的bondedProperty';
comment on column GC_CHECK_HISTORY.RESERVED7 is 'MES完成品的productCategory';
comment on column GC_CHECK_HISTORY.RESERVED8 is '中转箱号';
comment on column GC_CHECK_HISTORY.RESERVED9 is '装箱检验判定等级';
comment on column GC_CHECK_HISTORY.RESERVED10 is '装箱检验判定码';