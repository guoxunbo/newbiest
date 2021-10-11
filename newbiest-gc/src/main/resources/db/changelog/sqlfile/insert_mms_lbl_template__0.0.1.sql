insert into MMS_LBL_TEMPLATE (OBJECT_RRN, ACTIVE_FLAG, ORG_RRN, NAME, DESCRIPTION, TYPE, DESTINATION, PRINT_COUNT)
values (hibernate_sequence.nextval,'Y', 1, 'PrintWaferLotLabel', 'Wafer拆箱箱标签打印', 'Bartender', 'http://${remote_address}:10035/Integration/wms-print-WaferLot/Execute', 1);

