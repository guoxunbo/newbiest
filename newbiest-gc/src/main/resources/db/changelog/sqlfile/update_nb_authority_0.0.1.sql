update nb_authority  n set   parameter1 = '7728' where   n.name = 'MESFinishGoodReceive';
update nb_authority  n set   parameter1 = '71075' where   n.name = 'WLTFinishGoodReceive';
update nb_authority  n set   parameter1 = '467985' where   n.name = 'COBFinishGoodReceiveManager';

update nb_authority  n set   parameter1 = '464433' where   n.name = 'GCComMLotReservedManager';
update nb_authority  n set   parameter1 = '9753' where   n.name = 'ReservedCase';
update nb_authority  n set   parameter1 = '9751' where   n.name = 'MESFinishGoodReceive';
update nb_authority  n set   parameter1 = '9913' where   n.name = 'DeliveryDocManager';
update nb_authority  n set   parameter1 = '9912',parameter2 = '9914' where   n.name = 'ReTestOrderManager';
update nb_authority  n set   parameter1 = '78221' where   n.name = 'GCWltOrCpStockOutManager';
update nb_authority  n set   parameter1 = '466121' where   n.name = 'GCFTStockOutManager';

update nb_authority  n set   parameter1 = '468980' where   n.name = 'GCRawMaterialReceiveManager';
update nb_authority  n set   parameter1 = '469565', parameter2 = '469576' where   n.name = 'GCRawMaterialIssueManager';
update nb_authority  n set   parameter1 = '2603848' where   n.name = 'GCRawMaterialStockInManager';

update nb_authority  n set   parameter1 = '99121', parameter2 = '99141' where   n.name = 'GCWaferReceive';
update nb_authority  n set   parameter1 = '75233' where   n.name = 'GCPurchaseOutsoureReceiveManager';
update nb_authority  n set   parameter1 = '99122', parameter2 = '70656' where   n.name = 'GCWaferOut';
update nb_authority  n set   parameter1 = '77131' where   n.name = 'GCCOMWaferIssueManager';
update nb_authority  n set   parameter1 = '469275' where   n.name = 'GCWaferIssueOutOrderManager';
update nb_authority  n set   parameter1 = '106274' where   n.name = 'GCFTMaterialLotReceiveManager';
update nb_authority  n set   parameter1 = '468118' where   n.name = 'GCRMAMaterialLotReceiveManager';
update nb_authority  n set   parameter1 = '350972', parameter2 = '350983'  where   n.name = 'GCFTWaferIssueManager';
update nb_authority  n set   parameter1 = '469412' where   n.name = 'GCFTOutOrderWaferIssue';

update nb_authority  n set   parameter1 = '465261' where   n.name = 'HKWarehouseMLotReceiveManager';
update nb_authority  n set   parameter1 = '465464' where   n.name = 'HKWarehouseOrderStockOutManger';