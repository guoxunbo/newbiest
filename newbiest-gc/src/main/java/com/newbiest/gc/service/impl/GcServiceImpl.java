package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.exception.NewbiestException;
import com.newbiest.base.model.NBHis;
import com.newbiest.base.model.NBQuery;
import com.newbiest.base.repository.QueryRepository;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.VersionControlService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.*;
import com.newbiest.commom.sm.exception.StatusMachineExceptions;
import com.newbiest.common.exception.ContextException;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.model.*;
import com.newbiest.gc.repository.*;
import com.newbiest.gc.rest.stockout.wltStockout.WltStockOutRequest;
import com.newbiest.gc.scm.send.mlot.state.MaterialLotStateReportRequestBody;
import com.newbiest.gc.service.GcService;
import com.newbiest.gc.service.MesService;
import com.newbiest.gc.service.ScmService;
import com.newbiest.mms.SystemPropertyUtils;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
import com.newbiest.mms.service.MaterialLotUnitService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.PackageService;
import com.newbiest.mms.service.PrintService;
import com.newbiest.mms.state.model.MaterialEvent;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.mms.utils.CollectorsUtils;
import freemarker.template.utility.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.newbiest.mms.exception.MmsException.MM_PRODUCT_ID_IS_NOT_EXIST;
import static com.newbiest.mms.exception.MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST;

/**
 * Created by guoxunbo on 2019-08-21 12:41
 */
@Service
@Slf4j
@Transactional
@Data
public class GcServiceImpl implements GcService {

    public static final String TRANS_TYPE_BIND_RELAY_BOX = "BindRelayBox";
    public static final String TRANS_TYPE_UNBIND_RELAY_BOX = "UnbindRelayBox";
    public static final String TRANS_TYPE_JUDGE = "Judge";
    public static final String TRANS_TYPE_OQC = "OQC";
    public static final String TRANS_TYPE_UPDATE_TREASURY_NOTE = "UpdateTreasuryNote";
    public static final String TRANS_TYPE_UPDATE_LOCATION = "UpdateLocation";
    public static final String TRANS_TYPE_TRANSFER_WAREHOUSE = "TransferWarehouse";

    public static final String REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST = "StockOutCheckItemList";
    public static final String REFERENCE_NAME_WLTSTOCK_OUT_CHECK_ITEM_LIST = "WltStockOutCheckItemList";
    public static final String REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST = "PackCaseCheckItemList";
    public static final String REFERENCE_NAME_WLTPACK_CASE_CHECK_ITEM_LIST = "WltPackCaseCheckItemList";
    public static final String REFERENCE_NAME_PRODUCT_DECS_LIST = "ProductDescList";
    public static final String REFERENCE_NAME_ENCRYPTION_SUBCODE_LIST = "EncryptionSubcodeList";
    public static final String REFERENCE_NAME_RESERVED_CUSTOMER_LIST = "ReservedCustomerList";

    public static final String EVENT_OQC = "OQC";

    public static final String WAREHOUSE_SH = "SH_STOCK";
    public static final String WAREHOUSE_ZJ = "ZJ_STOCK";
    public static final String WAREHOUSE_HK = "HK_STOCK";

    public static final String BONDED_PROPERTITY_SH = "SH";
    public static final String BONDED_PROPERTITY_ZSH = "ZSH";
    public static final String BONDED_PROPERTITY_HK = "HK";

    public static final String PRE_FIX_GCB = "GCB";

    @Autowired
    MesPackedLotRepository mesPackedLotRepository;

    @Autowired
    MesPackedLotRelationRepository mesPackedLotRelationRepository;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotUnitService materialLotUnitService;

    @Autowired
    BaseService baseService;

    @Autowired
    UIService uiService;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ErpSoRepository erpSoRepository;

    @Autowired
    ErpMaterialOutOrderRepository erpMaterialOutOrderRepository;

    @Autowired
    ErpMaterialOutAOrderRepository erpMaterialOutAOrderRepository;

    @Autowired
    ErpSoaOrderRepository erpSoaOrderRepository;

    @Autowired
    OtherStockOutOrderRepository otherStockOutOrderRepository;

    @Autowired
    MaterialIssueOrderRepository materialIssueOrderRepository;

    @Autowired
    ErpSobOrderRepository erpSobOrderRepository;

    @Autowired
    OtherShipOrderRepository otherShipOrderRepository;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    ReTestOrderRepository reTestOrderRepository;

    @Autowired
    WaferIssueOrderRepository waferIssueOrderRepository;

    @Autowired
    ReceiveOrderRepository receiveOrderRepository;

    @Autowired
    CogReceiveOrderRepository cogReceiveOrderRepository;

    @Autowired
    RawMaterialOtherOutOrderRepository rawMaterialOtherOutOrderRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    DocumentHistoryRepository documentHistoryRepository;

    @Autowired
    DocumentLineHistoryRepository documentLineHistoryRepository;

    @Autowired
    MaterialLotJudgeHisRepository materialLotJudgeHisRepository;

    @Autowired
    CheckHistoryRepository checkHistoryRepository;

    @Autowired
    MaterialLotInventoryRepository materialLotInventoryRepository;

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    EntityManager em;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    VersionControlService versionControlService;

    @Autowired
    MaterialStatusModelRepository materialStatusModelRepository;

    @Autowired
    PackageService packageService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ErpMoRepository erpMoRepository;

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    MaterialLotUnitHisRepository materialLotUnitHisRepository;

    @Autowired
    MesWaferReceiveRepository mesWaferReceiveRepository;

    @Autowired
    MesWaferReceiveHisRepository mesWaferReceiveHisRepository;

    @Autowired
    GCProductSubcodeSetRepository gcProductSubcodeSetRepository;

    @Autowired
    GCProductModelConversionRepository gcProductModelConversionRepository;

    @Autowired
    PackagedLotDetailRepository packagedLotDetailRepository;

    @Autowired
    GCLcdCogDetialRepository gcLcdCogDetialRepository;

    @Autowired
    GCLcdCogDetialHisRepository gcLcdCogDetialHisRepository;

    @Autowired
    ErpInStockRepository erpInStockRepository;

    @Autowired
    ErpMoaRepository erpMoaRepository;

    @Autowired
    ScmService scmService;

    @Autowired
    MesService mesService;

    @Autowired
    PrintService printService;

    @Autowired
    GCProductRelationRepository productRelationRepository;

    @Autowired
    GCOutSourcePoRepository outSourcePoRepository;

    @Autowired
    GCOutSourcePoHisRepository outSourcePoHisRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    GCWorkorderRelationRepository workorderRelationRepository;

    @Autowired
    GCWorkorderRelationHisRepository workorderRelationHisRepository;

    @Autowired
    MLotDocRuleRepository mLotDocRuleRepository;

    @Autowired
    GCProductNumberRelationRepository productNumberRelationRepository;

    @Autowired
    GCProductWeightRelationRepository productWeightRelationRepository;

    @Autowired
    GCProductNumberRelationHisRepository productNumberRelationHisRepository;

    @Autowired
    GCUnConfirmWaferSetRepository unConfirmWaferSetRepository;

    @Autowired
    GCUnConfirmWaferSetHisRepository unConfirmWaferSetHisRepository;

    @Autowired
    ErpMaterialOutRepository erpMaterialOutRepository;

    @Autowired
    ErpMaterialInRepository erpMaterialInRepository;

    @Autowired
    MaterialNameInfoRepository materialNameInfoRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    MaterialHistoryRepository materialHistoryRepository;

    @Autowired
    FutureHoldConfigRepository futureHoldConfigRepository;

    @Autowired
    WaferHoldRelationRepository waferHoldRelationRepository;

    @Autowired
    WlatoFtTestBitRepository wlatoFtTestBitRepository;

    @Autowired
    WlatoFtTestBitHisRepository wlatoFtTestBitHisRepository;

    @Autowired
    ComThrowWaferTabRepository comThrowWaferTabRepository;

    @Autowired
    MesGcWltUploadRepository mesGcWltUploadRepository;

    /**
     * 根据单据和动态表RRN获取可以被备货的批次
     * @param
     * @param
     * @return
     */
    public List<MaterialLot> getWaitForReservedMaterialLot(Long documentLineRrn, Long tableRrn, String stockLocation, String ruleId)  throws ClientException {
        try {
            List<MaterialLot> waitForReservedMaterialLots = Lists.newArrayList();
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            if(StringUtils.isNullOrEmpty(documentLine.getReserved2()) || StringUtils.isNullOrEmpty(documentLine.getReserved3())){
                throw new ClientParameterException(GcExceptions.THE_DOCUMENT_GRADE_OR_SUBCODE_IS_NULL, documentLine.getDocId());
            }
            String subcode = documentLine.getReserved2().substring(0, documentLine.getReserved2().length() - documentLine.getReserved3().length());
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            StringBuffer whereClause = new StringBuffer();
            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                whereClause.append(nbTable.getWhereClause());
            }
            whereClause.append(" and reserved16 is null");
            whereClause.append(" and materialName = '" + documentLine.getMaterialName() + "'");
            whereClause.append(" and grade ='" + documentLine.getReserved3() + "'");
            if(MaterialLot.MLOT_RESERVED_DOC_VALIDATE_RULE_ID.equals(ruleId)){
                if(StringUtils.isNullOrEmpty(documentLine.getReserved17())){
                    whereClause.append("and  reserved4 is null ");
                } else {
                    whereClause.append("and  reserved4 ='" + documentLine.getReserved17() + "'");
                }
            }
            whereClause.append("and  reserved6 ='" + documentLine.getReserved7() + "'");
            whereClause.append("and  reserved1 ='" + subcode + "'");
            if(!StringUtils.isNullOrEmpty(stockLocation)){
                whereClause.append(" and reserved14 like '"+ stockLocation + '%' + "'");
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause.toString(), nbTable.getOrderBy());
            if (CollectionUtils.isNotEmpty(materialLots)) {
                if(nbTable.getName().equals("MMReservedCase")) {
                    for (MaterialLot materialLot : materialLots) {
                        List<String> packedLotIdList = new ArrayList<String>();
                        packedLotIdList.add(materialLot.getMaterialLotId());
                        List<MaterialLot> packedMaterialLots = getPackedDetailsAndNotReserved(packedLotIdList);
                        if(CollectionUtils.isNotEmpty(packedMaterialLots)){
                            waitForReservedMaterialLots.add(materialLot);
                        }
                    }
                } else {
                    waitForReservedMaterialLots.addAll(materialLots);
                }
            }
            return waitForReservedMaterialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * @param packedLotIdList
     * @return
     */
    public List<MaterialLot> getPackedDetailsAndNotReserved(List<String> packedLotIdList) throws ClientException{
        return materialLotRepository.getPackedDetailsAndNotReserved(packedLotIdList);
    }

    /**
     * 备货物料批次 不管是真空包还是箱
     * @param documentLineRrn
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public DocumentLine reservedMaterialLot(Long documentLineRrn, List<MaterialLotAction> materialLotActions ,String stockNote, String ruleId) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> holdMaterialLot = materialLots.stream().filter(materialLot -> MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(holdMaterialLot)){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD, holdMaterialLot.get(0).getMaterialLotId());
            }
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            List<NBOwnerReferenceList> customerAddressList = getReferenceListByName(REFERENCE_NAME_RESERVED_CUSTOMER_LIST);
            Map<String, List<NBOwnerReferenceList>> customerAddressMap = customerAddressList.stream().collect(Collectors.groupingBy(NBOwnerReferenceList:: getKey));
            BigDecimal unReservedQty = documentLine.getUnReservedQty();
            BigDecimal reservedQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                if (!StringUtils.isNullOrEmpty(materialLot.getReserved16())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_RESERVED_BY_ANOTHER);
                }
                validateMLotAndDocLineByRule(documentLine, materialLot, ruleId);
                BigDecimal currentQty = materialLot.getCurrentQty();
                reservedQty = reservedQty.add(currentQty);
                if (unReservedQty.compareTo(reservedQty) < 0) {
                    throw new ClientParameterException(GcExceptions.RESERVED_OVER_QTY);
                }
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot.setReserved16(documentLine.getObjectRrn().toString());
                materialLot.setReserved17(documentLine.getDocId());
                materialLot.setReserved18(stockNote);

                materialLot.setDocDate(documentLine.getErpCreated());
                materialLot.setShipper(documentLine.getReserved12());
                materialLot.setReserved51(documentLine.getReserved15());
                materialLot.setReserved52(documentLine.getReserved20());
                materialLot.setReserved53(documentLine.getReserved21());
                if(ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31()) || ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())){
                    if(customerAddressMap.containsKey(documentLine.getReserved12())){
                        List<NBOwnerReferenceList> customerNameList = customerAddressMap.get(documentLine.getReserved12());
                        materialLot.setReserved55(customerNameList.get(0).getValue());
                    } else if(!StringUtils.isNullOrEmpty(documentLine.getReserved15()) && documentLine.getReserved15().length() > 2){
                        materialLot.setReserved55(documentLine.getReserved12() + "("+ documentLine.getReserved15().substring(0, 2)+")");
                    } else {
                        materialLot.setReserved55(documentLine.getReserved12());
                    }
                }
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RESERVED);
                materialLotHistoryRepository.save(history);
            }

            Map<String, List<MaterialLot>> parentMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));

            for (String parentMLotId : parentMaterialLots.keySet()) {
                List<MaterialLot> packMLots = parentMaterialLots.get(parentMLotId);
                MaterialLot parentMLot = mmsService.getMLotByMLotId(parentMLotId);
                BigDecimal totalReservedQty = parentMaterialLots.get(parentMLotId).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));
                BigDecimal parentMaterialLotReservedQty = parentMLot.getReservedQty() == null ? BigDecimal.ZERO : parentMLot.getReservedQty();
                parentMLot.setReservedQty(parentMaterialLotReservedQty.add(totalReservedQty));

                parentMLot.setReserved16(documentLine.getObjectRrn().toString());
                parentMLot.setReserved17(documentLine.getDocId());
                parentMLot.setReserved18(stockNote);

                parentMLot.setDocDate(documentLine.getErpCreated());
                parentMLot.setShipper(documentLine.getReserved12());
                parentMLot.setReserved51(documentLine.getReserved15());
                parentMLot.setReserved52(documentLine.getReserved20());
                parentMLot.setReserved53(documentLine.getReserved21());
                parentMLot.setReserved55(packMLots.get(0).getReserved55());
                materialLotRepository.saveAndFlush(parentMLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(parentMLot, MaterialLotHistory.TRANS_TYPE_RESERVED);
                materialLotHistoryRepository.save(history);

            }

            documentLine.setUnReservedQty(unReservedQty.subtract(reservedQty));
            BigDecimal lineReservedQty = documentLine.getReservedQty() == null ? BigDecimal.ZERO : documentLine.getReservedQty();
            documentLine.setReservedQty(lineReservedQty.add(reservedQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RESERVED);

            Document document = (Document) documentRepository.findByObjectRrn(documentLine.getDocRrn());
            BigDecimal docReservedQty = document.getReservedQty() == null ? BigDecimal.ZERO : document.getReservedQty();
            document.setUnReservedQty(document.getUnReservedQty().subtract(reservedQty));
            document.setReservedQty(docReservedQty.add(reservedQty));
            document = documentRepository.saveAndFlush(document);
            baseService.saveHistoryEntity(document, MaterialLotHistory.TRANS_TYPE_RESERVED);
            return documentLine;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void unReservedMaterialLot(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            // 根据documentLine分组 依次还原数量
            Map<String, List<MaterialLot>> docLineReservedMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            Map<Long, BigDecimal> docUnReservedQtyMap = Maps.newHashMap();

            //还原箱备货数量
            Map<String, List<MaterialLot>> parentMaterialLots = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));

            parentMaterialLots.keySet().forEach(parentMLotId -> {
                MaterialLot parentMLot = mmsService.getMLotByMLotId(parentMLotId);
                BigDecimal totalUnReservedQty = parentMaterialLots.get(parentMLotId).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getReservedQty));
                parentMLot.setReservedQty(parentMLot.getReservedQty().subtract(totalUnReservedQty));
                parentMLot.setReserved19(StringUtils.EMPTY);
                parentMLot.setReserved20(StringUtils.EMPTY);

                if (parentMLot.getReservedQty().compareTo(BigDecimal.ZERO) == 0) {
                    parentMLot.clearReservedInfo();
                }
                materialLotRepository.saveAndFlush(parentMLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(parentMLot, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
                materialLotHistoryRepository.save(history);
            });

            for (String docLine : docLineReservedMaterialLotMap.keySet()) {
                List<MaterialLot> docLineReservedMaterialLots = docLineReservedMaterialLotMap.get(docLine);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLine));
                BigDecimal unReservedQty = BigDecimal.ZERO;
                for (MaterialLot materialLot : docLineReservedMaterialLots) {
                    unReservedQty = unReservedQty.add(materialLot.getReservedQty());

                    materialLot.setReservedQty(BigDecimal.ZERO);
                    materialLot.clearReservedInfo();
                    materialLot = materialLotRepository.saveAndFlush(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
                    materialLotHistoryRepository.save(history);
                }
                documentLine.setReservedQty(documentLine.getReservedQty().subtract(unReservedQty));
                documentLine.setUnReservedQty(documentLine.getUnReservedQty().add(unReservedQty));
                documentLine = documentLineRepository.saveAndFlush(documentLine);
                baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);

                // 还原主单据数量
                BigDecimal docUnReservedQty = BigDecimal.ZERO;
                if (docUnReservedQtyMap.containsKey(documentLine.getDocRrn())) {
                    docUnReservedQty = docUnReservedQtyMap.get(documentLine.getDocRrn());
                }
                docUnReservedQty.add(unReservedQty);
                docUnReservedQtyMap.put(documentLine.getDocRrn(), unReservedQty);
            }

            for (Long docRrn : docUnReservedQtyMap.keySet()) {
                Document document = (Document) documentRepository.findByObjectRrn(docRrn);
                document.setUnReservedQty(document.getUnReservedQty().add(docUnReservedQtyMap.get(docRrn)));
                document.setReservedQty(document.getReservedQty().subtract(docUnReservedQtyMap.get(docRrn)));
                document = documentRepository.saveAndFlush(document);

                baseService.saveHistoryEntity(document, MaterialLotHistory.TRANS_TYPE_UN_RESERVED);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号及表单主键获取物料批次信息，不存在的抛出异常
     * @param materialLotId
     * @return
     */
    public MaterialLot getMaterialLotByMaterialLotIdAndTableRrn(String materialLotId, long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            List<MaterialLot> materialLotList = queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, materialLotId);

            if(CollectionUtils.isEmpty(materialLotList)){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
            } else {
                materialLot = materialLotList.get(0);
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved56()) && StringUtils.isNullOrEmpty(materialLot.getReserved51())){
                    String subCode = materialLot.getReserved1() + materialLot.getGrade();
                    List<DocumentLine> documentLines = documentLineRepository.findByDocIdAndMaterialNameAndReserved3AndReserved2AndReserved7AndReserved17AndUnHandledQtyGreaterThan(materialLot.getReserved56(), materialLot.getMaterialName(), materialLot.getGrade(), subCode, materialLot.getReserved6(),materialLot.getReserved4(), BigDecimal.ZERO);
                    if(CollectionUtils.isNotEmpty(documentLines)){
                        materialLot.setShipper(documentLines.get(0).getReserved12());
                        materialLot.setReserved51(documentLines.get(0).getReserved15());
                        materialLot.setReserved52(documentLines.get(0).getReserved20());
                        materialLot.setReserved53(documentLines.get(0).getReserved21());
                        materialLot = materialLotRepository.saveAndFlush(materialLot);
                    }
                }
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号或者LOT_ID获取可以库位调整的物料批次信息
     * @param mLotId
     * @return materialLot
     */
    public MaterialLot getWaitStockInStorageMaterialLotByLotIdOrMLotId(String mLotId, Long tableRrn) throws ClientException{
        try {
            MaterialLot materialLot = getMaterialLotByTableRrnAndMaterialLotIdOrLotId(tableRrn, mLotId);
            if(StringUtils.isNullOrEmpty(materialLot.getMaterialLotId())){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mLotId);
            } else{
                materialLot.isFinish();
            }
            return materialLot;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取到可以入库的批次
     *  当前只验证了物料批次是否是完结
     * @param lotId
     * @return
     */
    public MaterialLot getWaitStockInStorageWaferByLotId(String lotId, Long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" lotId = ");
            clauseBuffer.append("'" + lotId + "'");

            if (!StringUtils.isNullOrEmpty(_whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(_whereClause);
            }
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);

            if(CollectionUtils.isNotEmpty(materialLots)){
                materialLot = materialLots.get(0);
            }
            if (StringUtils.isNullOrEmpty(materialLot.getMaterialLotId())) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, lotId);
            }
            materialLot.isFinish();
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取原材料入库位物料批次信息
     * @param mLotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> queryRawMaterialByMaterialLotOrLotIdAndTableRrn(String mLotId, Long tableRrn) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            if(mLotId.startsWith(Material.IRA_MATERIAL_BOX_ID_START)){
                materialLots = getIRARawMaterialByLotIdAndTableRrn(mLotId, tableRrn);
            } else {
                materialLots = queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, mLotId);
            }
            return materialLots;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * IRA原材料批次信息查询
     * @param lotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    private List<MaterialLot> getIRARawMaterialByLotIdAndTableRrn(String lotId, Long tableRrn) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" lotId = ");
            clauseBuffer.append("'" + lotId + "'");
            clauseBuffer.append(" and  materialType = 'IRA' ");

            if (!StringUtils.isNullOrEmpty(_whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(_whereClause);
            }
            _whereClause = clauseBuffer.toString();
            materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据单据信息获取满足备料条件的批次条码信息
     * @param docLineRrn
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getWaitSpareRawMaterialLotListByOrderAndTableRrn(Long docLineRrn, Long tableRrn) throws ClientException{
        try {
            List<MaterialLot> waitSpareRawMLotList = Lists.newArrayList();
            DocumentLine rawMaterialIssueOrder = (DocumentLine) documentLineRepository.findByObjectRrn(docLineRrn);
            if (rawMaterialIssueOrder.getUnReservedQty().compareTo(BigDecimal.ZERO) > 0) {
                NBTable nbTable = uiService.getDeepNBTable(tableRrn);
                String orderBy = nbTable.getOrderBy();
                StringBuffer whereClause = new StringBuffer();
                if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                    whereClause.append(nbTable.getWhereClause());
                }
                whereClause.append(" and materialName = '" + rawMaterialIssueOrder.getMaterialName() + "'");

                List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause.toString(), orderBy);
                if (CollectionUtils.isNotEmpty(materialLots)) {
                    for (MaterialLot materialLot : materialLots) {
                        try {
                            validateMLotAndDocLineByRule(rawMaterialIssueOrder, materialLot, MaterialLot.RAW_MATERIAL_ISSUE_DOC_VALIDATE_RULE_ID);
                            waitSpareRawMLotList.add(materialLot);
                        } catch (Exception e) {
                        }
                    }
                }
            } else {
                throw new ClientException("");
            }
            return waitSpareRawMLotList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取等待备料的批次条码信息
     * 原材料信息只会是IRA、胶水、金线其中一种，不会出现多种类型的数据出现
     * @param materialLotList
     * @param docLineRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getSpareRawMaterialLotListByDocLineRrrn(List<MaterialLot> materialLotList, Long docLineRrn) throws ClientException{
        try {
            DocumentLine documentLine = (DocumentLine)documentLineRepository.findByObjectRrn(docLineRrn);
            BigDecimal unReservedQty = documentLine.getUnReservedQty();
            List<MaterialLot> waitSpareRawMLotList = getWaitSpareRawMaterialByReservedQty(materialLotList, unReservedQty);
            return waitSpareRawMLotList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据备料数量筛选出备料的原材料批次信息
     * @param materialLotList
     * @param unReservedQty
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getWaitSpareRawMaterialByReservedQty(List<MaterialLot> materialLotList, BigDecimal unReservedQty) throws ClientException{
        try {
            List<MaterialLot> waitSpareRawMLotList = Lists.newArrayList();
            boolean unPackedFlag = false;
            String materialType = materialLotList.get(0).getMaterialType();
            List<Date> dateList = Lists.newArrayList();
            if(Material.MATERIAL_TYPE_IRA.equals(materialType)){
                Map<Date, List<MaterialLot>> mLotDateMap = Maps.newHashMap();
                for(MaterialLot materialLot: materialLotList){
                    if(mLotDateMap.containsKey(materialLot.getEarlierExpDate())){
                        mLotDateMap.get(materialLot.getEarlierExpDate()).add(materialLot);
                    } else {
                        List<MaterialLot> materialLots = Lists.newArrayList();
                        materialLots.add(materialLot);
                        mLotDateMap.put(materialLot.getEarlierExpDate(), materialLots);
                        dateList.add(materialLot.getEarlierExpDate());
                    }
                }
                Collections.sort(dateList);
                //同一天的原材料可能包含多个箱子，整箱数量可能存在相同的，先挑整箱数量少的，数量不够的，从整箱中挑选部分
                for(Date mfgDate : dateList){
                    if(unPackedFlag){
                        break;
                    }
                    List<MaterialLot> materialLots = mLotDateMap.get(mfgDate);
                    Map<Long, List<MaterialLot>> boxQtyMap = Maps.newHashMap();
                    List<Long> totalQtyList = Lists.newArrayList();
                    Map<String, List<MaterialLot>> mLotIdMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getLotId));
                    for(String lotId : mLotIdMap.keySet()){
                        List<MaterialLot> iraLotList = mLotIdMap.get(lotId);
                        Long totalQty = iraLotList.stream().collect(Collectors.summingLong(mLot -> mLot.getCurrentQty().longValue()));
                        if(boxQtyMap.containsKey(totalQty)){
                            boxQtyMap.get(totalQty).addAll(iraLotList);
                        } else {
                            boxQtyMap.put(totalQty, iraLotList);
                            totalQtyList.add(totalQty);
                        }
                    }
                    Collections.sort(totalQtyList);
                    for(Long totalQty : totalQtyList){
                        if(unPackedFlag){
                            break;
                        }
                        Map<String, List<MaterialLot>> lotIdMap = boxQtyMap.get(totalQty).stream().collect(Collectors.groupingBy(MaterialLot :: getLotId));
                        for(String lotId : lotIdMap.keySet()){
                            List<MaterialLot> iraLotList = lotIdMap.get(lotId);
                            Long boxQty = iraLotList.stream().collect(Collectors.summingLong(mLot -> mLot.getCurrentQty().longValue()));
                            if(unReservedQty.compareTo(new BigDecimal(boxQty)) >= 0){
                                waitSpareRawMLotList.addAll(iraLotList);
                                unReservedQty = unReservedQty.subtract(new BigDecimal(boxQty));
                            } else {
                                //需要拆箱时，按照箱子中materialLotId进行排序，拆箱多拆一包，防止出现拆多箱情况
                                iraLotList = iraLotList.stream().sorted(Comparator.comparing(MaterialLot :: getMaterialLotId)).collect(Collectors.toList());
                                for(MaterialLot materialLot : iraLotList){
                                    if(unReservedQty.compareTo(BigDecimal.ZERO) > 0){
                                        waitSpareRawMLotList.add(materialLot);
                                        unReservedQty = unReservedQty.subtract(materialLot.getCurrentQty());
                                        unPackedFlag = true;
                                    } else {
                                        break;
                                    }
                                }
                            }
                            if(unPackedFlag || unReservedQty.compareTo(BigDecimal.ZERO) <= 0){
                                break;
                            }
                        }
                    }
                    if(unReservedQty.compareTo(BigDecimal.ZERO) <= 0){
                        break;
                    }
                }
            } else {
                for(MaterialLot materialLot : materialLotList){
                    if(unReservedQty.compareTo(materialLot.getCurrentQty()) >= 0){
                        waitSpareRawMLotList.add(materialLot);
                        unReservedQty = unReservedQty.subtract(materialLot.getCurrentQty());
                    }
                }
            }
            return waitSpareRawMLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料无单据备料
     * @param materialLotList
     * @throws ClientException
     */
    public String spareRawMLotOutDoc(List<MaterialLot> materialLotList) throws ClientException {
        try {
            String spareRuleId = generatorMLotsTransId(MaterialLot.GENERATOR_RAW_MATERIAL_SPARE_RULE);
            for (MaterialLot materialLot : materialLotList) {
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot.setReserved17(spareRuleId);
                materialLot = mmsService.changeMaterialLotState(materialLot, MaterialEvent.EVENT_MATEREIAL_SPARE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);
                materialLotHistoryRepository.save(history);
            }
            return spareRuleId;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料备料
     * @param materialLotList
     * @param docLineRrn
     * @throws ClientException
     */
    public void rawMaterialMLotSpare(List<MaterialLot> materialLotList, Long docLineRrn) throws ClientException{
        try {
            DocumentLine documentLine = (DocumentLine)documentLineRepository.findByObjectRrn(docLineRrn);
            Double totalMLotQty = materialLotList.stream().collect(Collectors.summingDouble(mLot -> mLot.getCurrentQty().doubleValue()));
            BigDecimal spareQty = new BigDecimal(totalMLotQty);
            if(documentLine.getUnReservedQty().compareTo(new BigDecimal(totalMLotQty)) < 0){
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }

            for(MaterialLot materialLot: materialLotList){
                materialLot.setReserved16(documentLine.getObjectRrn().toString());
                materialLot.setReserved17(documentLine.getDocId());
                materialLot.setReservedQty(materialLot.getCurrentQty());
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_MATEREIAL_SPARE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);
                materialLotHistoryRepository.save(history);
            }

            documentLine.setReservedQty(documentLine.getReservedQty().add(spareQty));
            documentLine.setUnReservedQty(documentLine.getUnReservedQty().subtract(spareQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);

            MaterialIssueOrder materialIssueOrder = (MaterialIssueOrder) materialIssueOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            materialIssueOrder.setReservedQty(materialIssueOrder.getReservedQty().add(spareQty));
            materialIssueOrder.setUnReservedQty(materialIssueOrder.getUnReservedQty().subtract(spareQty));
            materialIssueOrderRepository.save(materialIssueOrder);
            baseService.saveHistoryEntity(materialIssueOrder, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 入库位
     * @return
     */
    public void stockIn(List<StockInModel> stockInModels) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            Map<String, StockInModel> stockInModelMap = stockInModels.stream().collect(Collectors.toMap(StockInModel :: getMaterialLotId, Function.identity()));

            //1. 把箱批次和普通的物料批次区分出来
            List<MaterialLot> materialLots = stockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> normalMaterialLots = materialLots.stream().filter(materialLot -> materialLot.getParentMaterialLotRrn() == null).collect(Collectors.toList());
            List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> MaterialLot.STATUS_CREATE.equals(materialLot.getStatus()) && Material.TYPE_MATERIAL.equals(materialLot.getMaterialCategory())).collect(Collectors.toList());

            //2. 普通批次才做绑定中转箱功能，直接release原来的中转箱号
            for (MaterialLot materialLot : normalMaterialLots) {
                StockInModel stockInModel = stockInModelMap.get(materialLot.getMaterialLotId());
                // 为空则不处理
                if (StringUtils.isNullOrEmpty(stockInModel.getRelaxBoxId())) {
                    continue;
                }
                bindRelaxBox(Lists.newArrayList(materialLot), stockInModel.getRelaxBoxId());
            }
            //3. 入库
            for (MaterialLot materialLot : materialLots) {
                StockInModel stockInModel = stockInModelMap.get(materialLot.getMaterialLotId());
                String storageId = stockInModel.getStorageId();
                // 为空则不处理
                if (StringUtils.isNullOrEmpty(storageId)) {
                    continue;
                }
                if (StringUtils.isNullOrEmpty(materialLot.getReserved13())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAREHOUSE_IS_NULL, materialLot.getMaterialLotId());
                }
                materialLot.setReserved14(storageId);

                MaterialLotAction action = new MaterialLotAction();
                action.setTargetWarehouseRrn(Long.parseLong(materialLot.getReserved13()));
                action.setTargetStorageId(storageId);
                action.setTransQty(materialLot.getCurrentQty());
                action.setTransCount(materialLot.getCurrentSubQty());

                List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                // 如果为空就是做入库事件 如果不是空则做转库事件
                if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                    //GC一个批次只会入库一次
                    MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
                    action.setFromWarehouseRrn(materialLotInventory.getWarehouseRrn());
                    action.setFromStorageRrn(materialLotInventory.getStorageRrn());
                    mmsService.transfer(materialLot, action);
                } else {
                    materialLot = mmsService.stockIn(materialLot, action);
                }
                materialLotRepository.save(materialLot);

                //如果箱号入库位，将箱中所有真空包或Lot的库位号更新
                validatePackageLotAndUpdateStorage(materialLot, storageId);
            }

            //原材料接收入库时，如果未入库需将原材料信息写入中间表
            if(CollectionUtils.isNotEmpty(materialLotList)){
                for(MaterialLot materialLot: materialLotList){
                    if(!MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7()) && !MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7())){
                        ErpMaterialIn erpMaterialIn = new ErpMaterialIn();
                        erpMaterialIn.setMaterialLot(materialLot);
                        erpMaterialInRepository.save(erpMaterialIn);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *  如果箱号入库位，将箱中所有真空包或Lot的库位号更新
     * @param materialLot
     * @param storageId
     * @throws ClientException
     */
    private void validatePackageLotAndUpdateStorage(MaterialLot materialLot, String storageId) throws ClientException{
        try {
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                List<MaterialLot> packDetials = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                for(MaterialLot packedLot : packDetials){
                    packedLot.setReserved14(storageId);
                    if(!StringUtils.isNullOrEmpty(materialLot.getReserved8())){
                        packedLot.setReserved8(materialLot.getReserved8());
                    }
                    packedLot = materialLotRepository.saveAndFlush(packedLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packedLot, MaterialLotHistory.TRANS_TYPE_TRANSFER_PARENT);
                    materialLotHistoryRepository.save(history);

                    updateMaterialLotUnitStorage(packedLot);
                }
            } else {
                updateMaterialLotUnitStorage(materialLot);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆库位号并记录历史
     * @param materialLot
     * @throws ClientException
     */
    private void updateMaterialLotUnitStorage(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits){
                if(MaterialLotUnit.STATE_CREATE.equals(materialLotUnit.getState())){
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                }
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved8())){
                    materialLotUnit.setReserved8(materialLot.getReserved8());
                }
                materialLotUnit.setReserved14(materialLot.getReserved14());
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_STOCK_IN);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * GC盘点。
     * 这个盘点不是传统上的盘点库存数量，而是只记录盘点历史。会传递不存在的
     * 应格科要求要记录到一个单独表中。记录这个盘点历史。
     *
     */
    public void checkMaterialInventory(List<MaterialLot> existMaterialLots, List<MaterialLot> notExistMaterialLots) throws ClientException {
        try {
            if (CollectionUtils.isNotEmpty(existMaterialLots)) {
                for (MaterialLot materialLot : existMaterialLots) {
                    materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CHECK);
                    history.setTransQty(materialLot.getCurrentQty());
                    materialLotHistoryRepository.save(history);

                    CheckHistory checkHistory = new CheckHistory();
                    PropertyUtils.copyProperties(materialLot, checkHistory, new HistoryBeanConverter());
                    checkHistory.setTransQty(materialLot.getCurrentQty());
                    checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                    checkHistory.setObjectRrn(null);
                    checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                    checkHistoryRepository.save(checkHistory);

                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(packageDetailLots)) {
                        for (MaterialLot packageDetailLot : packageDetailLots) {
                            history = (MaterialLotHistory) baseService.buildHistoryBean(packageDetailLot, MaterialLotHistory.TRANS_TYPE_CHECK);
                            history.setTransQty(packageDetailLot.getCurrentQty());
                            materialLotHistoryRepository.save(history);

                            checkHistory = new CheckHistory();
                            PropertyUtils.copyProperties(packageDetailLot, checkHistory, new HistoryBeanConverter());
                            checkHistory.setTransQty(packageDetailLot.getCurrentQty());
                            checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                            checkHistory.setObjectRrn(null);
                            checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                            checkHistoryRepository.save(checkHistory);

                            this.saveUnitCheckHis(packageDetailLot.getMaterialLotId());
                        }
                    }

                    this.saveUnitCheckHis(materialLot.getMaterialLotId());
                }
            }

            if (CollectionUtils.isNotEmpty(notExistMaterialLots)) {
                for (MaterialLot materialLot : notExistMaterialLots) {
                    CheckHistory checkHistory = new CheckHistory();
                    PropertyUtils.copyProperties(materialLot, checkHistory, new HistoryBeanConverter());
                    checkHistory.setTransQty(BigDecimal.ZERO);
                    checkHistory.setErrorFlag(true);
                    checkHistory.setActionCode("Error");
                    checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                    checkHistory.setObjectRrn(null);
                    checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                    checkHistoryRepository.save(checkHistory);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存unit的盘点历史
     * @param materialLotId
     */
    private void saveUnitCheckHis(String materialLotId) {
        List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLotId);
        if (CollectionUtils.isNotEmpty(materialLotUnitList)) {
            for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_CHECK);
                materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(materialLotUnitHistory);

                CheckHistory checkHistory = new CheckHistory();
                checkHistory.setMaterialLotUnit(materialLotUnit);
                checkHistory.setTransQty(materialLotUnit.getCurrentQty());
                checkHistory.setTransType(MaterialLotHistory.TRANS_TYPE_CHECK);
                checkHistory.setObjectRrn(null);
                checkHistory.setHisSeq(ThreadLocalContext.getTransRrn());
                checkHistoryRepository.save(checkHistory);
            }
        }
    }


    /**
     * Wafer发料时同时同步wafer发料单和其他发料单
     * @throws ClientException
     */
    public void asyncWaferIssueOrderAndOtherIssueOrder() throws ClientException {
        asyncWaferIssueOrder();
        asyncOtherIssueOrder();
    }

    /**
     * WLT|CP发货单、三方销售单据
     * @throws ClientException
     */
    public void asyncWltCpShipOrder() throws ClientException {
        asyncOtherStockOutOrder();
        asyncOtherShipOrder();
    }

    /**
     * 同步重测单据
     * @throws ClientException
     */
    public void asyncReTestOrder() throws ClientException {
        try {
            List<ErpMaterialOutOrder> erpMaterialOutOrders = erpMaterialOutOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutOrder.TYPE_RO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();

            if (CollectionUtils.isNotEmpty(erpMaterialOutOrders)) {
                Map<String, List<ErpMaterialOutOrder>> documentIdMap = erpMaterialOutOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCreateSeq));
                    List<ReTestOrder> reTestOrderList = reTestOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(reTestOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            updateErpMaterialOutErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    ReTestOrder reTestOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(reTestOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                            }
                            continue;
                        }
                        reTestOrder = new ReTestOrder();
                        reTestOrder.setName(documentId);
                        reTestOrder.setStatus(Document.STATUS_OPEN);
                        reTestOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    } else {
                        reTestOrder = reTestOrderList.get(0);
                        totalQty = reTestOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(reTestOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    reTestOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (reTestOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(reTestOrder.getObjectRrn(), String.valueOf(erpMaterialOutOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpMaterialOutOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutOrder.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpMaterialOutOrder.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpMaterialOutOrder.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutOrder.getDdate()));
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());
                                documentLine.setReserved9(ReTestOrder.CATEGORY_RETEST);

                                erpMaterialOutOrderSetDocumentLine(documentLine, erpMaterialOutOrder);
                            }
                            documentLine.setReserved8(erpMaterialOutOrder.getCusname());
                            documentLine.setReserved12(erpMaterialOutOrder.getOther8());
                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            reTestOrder.setOwner(erpMaterialOutOrder.getChandler());
                            reTestOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        reTestOrder.setQty(totalQty);
                        reTestOrder.setUnHandledQty(reTestOrder.getQty().subtract(reTestOrder.getHandledQty()));
                        reTestOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                        reTestOrder = (ReTestOrder) baseService.saveEntity(reTestOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(reTestOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(reTestOrder.getSupplierName())) {
                        savaCustomer(reTestOrder.getSupplierName());
                    }
                }
                updateErpMaterialOutSyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步wafer发料单据
     * @throws ClientException
     */
    public void asyncWaferIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutOrder> waferIssueOrders = erpMaterialOutOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutOrder.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();

            if (CollectionUtils.isNotEmpty(waferIssueOrders)) {
                Map<String, List<ErpMaterialOutOrder>> documentIdMap = waferIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutOrder :: getCreateSeq));
                    List<WaferIssueOrder> waferIssueOrderList = waferIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(waferIssueOrderList)){
                        List<Document> documents = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documents)){
                            updateErpMaterialOutErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    WaferIssueOrder waferIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(waferIssueOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                            }
                            continue;
                        }
                        waferIssueOrder = new WaferIssueOrder();
                        waferIssueOrder.setName(documentId);
                        waferIssueOrder.setStatus(Document.STATUS_OPEN);
                        waferIssueOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                    } else {
                        waferIssueOrder = waferIssueOrderList.get(0);
                        totalQty = waferIssueOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(waferIssueOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    waferIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutOrder erpMaterialOutOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (waferIssueOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(waferIssueOrder.getObjectRrn(), String.valueOf(erpMaterialOutOrder.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpMaterialOutOrder.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutOrder.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getRawMaterialByName(erpMaterialOutOrder.getCinvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpMaterialOutOrder.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutOrder.getDdate()));
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());
                                documentLine.setReserved9(waferIssueOrder.CATEGORY_WAFER_ISSUE);
                                erpMaterialOutOrderSetDocumentLine(documentLine, erpMaterialOutOrder);
                            }
                            documentLine.setReserved8(erpMaterialOutOrder.getCusname());
                            documentLine.setReserved12(erpMaterialOutOrder.getOther8());
                            totalQty = totalQty.add(erpMaterialOutOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLines.add(documentLine);

                            waferIssueOrder.setOwner(erpMaterialOutOrder.getChandler());
                            waferIssueOrder.setReserved32(erpMaterialOutOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        waferIssueOrder.setQty(totalQty);
                        waferIssueOrder.setUnHandledQty(waferIssueOrder.getQty().subtract(waferIssueOrder.getHandledQty()));
                        waferIssueOrder.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
                        waferIssueOrder = (WaferIssueOrder) baseService.saveEntity(waferIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(waferIssueOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(waferIssueOrder.getSupplierName())) {
                        savaCustomer(waferIssueOrder.getSupplierName());
                    }
                }
                updateErpMaterialOutSyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录单据错误信息
     * @param documentIdList
     * @throws ClientException
     */
    private void updateErpMaterialOutErrorInfo(List<ErpMaterialOutOrder> documentIdList) throws ClientException{
        try {
            for(ErpMaterialOutOrder erpMaterialOutOrder : documentIdList){
                erpMaterialOutOrder.setUserId(Document.SYNC_USER_ID);
                erpMaterialOutOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                erpMaterialOutOrder.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改erpmaerialOut表
     * @param asyncSuccessSeqList
     * @param asyncDuplicateSeqList
     * @throws ClientException
     */
    private void updateErpMaterialOutSyncStatusAndErrorMemoAndUserId(List<Long> asyncSuccessSeqList, List<Long> asyncDuplicateSeqList) throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                    for(List seqGroup : asyncSuccessSeqGroupList){
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                }
            }
            if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                    for(List seqGroup : asyncDuplicateSeqGroupList){
                        erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * erpMaterialOutOrder表栏位赋值
     * @param documentLine
     * @param erpMaterialOutOrder
     * @throws ClientException
     */
    private void erpMaterialOutOrderSetDocumentLine(DocumentLine documentLine, ErpMaterialOutOrder erpMaterialOutOrder) throws ClientException{
        try {
            documentLine.setReserved1(String.valueOf(erpMaterialOutOrder.getSeq()));
            documentLine.setReserved2(erpMaterialOutOrder.getSecondcode());
            documentLine.setReserved3(erpMaterialOutOrder.getGrade());
            documentLine.setReserved4(erpMaterialOutOrder.getCfree3());
            documentLine.setReserved5(erpMaterialOutOrder.getCmaker());
            documentLine.setReserved6(erpMaterialOutOrder.getChandler());
            documentLine.setReserved7(erpMaterialOutOrder.getOther1());
            documentLine.setReserved8(erpMaterialOutOrder.getCusname());
            documentLine.setReserved13(erpMaterialOutOrder.getCmemo());
            documentLine.setReserved10(erpMaterialOutOrder.getGCode());
            documentLine.setDocType(erpMaterialOutOrder.getCvouchtype());
            documentLine.setDocName(erpMaterialOutOrder.getCvouchname());
            documentLine.setDocBusType(erpMaterialOutOrder.getCbustype());
            documentLine.setDocSource(erpMaterialOutOrder.getCsource());
            documentLine.setWarehouseCode(erpMaterialOutOrder.getCwhcode());
            documentLine.setWarehouseName(erpMaterialOutOrder.getCwhName());
            documentLine.setReserved11(erpMaterialOutOrder.getGName());
            documentLine.setReserved12(erpMaterialOutOrder.getOther8());
            documentLine.setReserved15(erpMaterialOutOrder.getOther18());
            documentLine.setReserved17(erpMaterialOutOrder.getOther3());
            documentLine.setReserved20(erpMaterialOutOrder.getOther9());
            documentLine.setReserved21(erpMaterialOutOrder.getOther10());
            documentLine.setReserved27(erpMaterialOutOrder.getOther7());
            documentLine.setReserved30(erpMaterialOutOrder.getOther5());
            documentLine.setCuscode(erpMaterialOutOrder.getCuscode());
            documentLine.setProductType(erpMaterialOutOrder.getOther15());
            documentLine.setPoId(erpMaterialOutOrder.getCfree4());
            documentLine.setCrdCode(erpMaterialOutOrder.getCrdcode());
            documentLine.setCrdName(erpMaterialOutOrder.getCrdname());
            documentLine.setAutoid(erpMaterialOutOrder.getOther16());
            documentLine.setReserved31(ErpMaterialOutOrder.SOURCE_TABLE_NAME);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆根据匹配规则自动匹配单据
     * F等级的COB晶圆接收不需要匹配单据
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationAndReceiveWafer(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String receiveWithDoc) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            for(MaterialLotAction materialLotAction : materialLotActions){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true);
                materialLot.setReserved4(materialLotAction.getReserved4());
                materialLots.add(materialLot);
            }
            if(!StringUtils.isNullOrEmpty(receiveWithDoc)){
                List<MaterialLot> materialLotList = new ArrayList<>();
                List<MaterialLot> fGradeMLotList = new ArrayList<>();
                for(MaterialLot materialLot : materialLots){
                    if(MaterialLot.IMPORT_COB.equals(materialLot.getReserved49()) && MaterialLot.GEADE_F.equals(materialLot.getGrade())){
                        fGradeMLotList.add(materialLot);
                    } else {
                        materialLotList.add(materialLot);
                    }
                }
                if(CollectionUtils.isNotEmpty(materialLotList)){
                    Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByImportType(materialLotList, MaterialLot.WAFER_RECEIVE_DOC_VALIDATE_RULE_ID, MaterialLot.COB_WAFER_RECEIVE_DOC_VALIDATE_RULE_ID);
                    documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                    Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.WAFER_RECEIVE_DOC_VALIDATE_RULE_ID);
                    // 确保所有的物料批次都能匹配上单据, 并且数量足够
                    for (String key : materialLotMap.keySet()) {
                        if (!documentLineMap.keySet().contains(key)) {
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getLotId());
                        }
                        Long totalMaterialLotQty = getTotalMaterialLotQtyByMLotUnitImportType(materialLotMap.get(key));
                        Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                        if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                            throw new ClientException(GcExceptions.OVER_DOC_QTY);
                        }
                        receiveWafer(documentLineMap.get(key), materialLotMap.get(key));
                    }
                }

                if(CollectionUtils.isNotEmpty(fGradeMLotList)){
                    for(MaterialLot materialLot : fGradeMLotList){
                        materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                    }
                }
            } else {
                for(MaterialLot materialLot : materialLots){
                    materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 湖南仓的入库导入
     * 成品导入时，存在有箱号的真空包，当有箱号时，真空包会Create和Package，最后生成一个箱号
     * @param materialLotList
     * @throws ClientException
     */
    @Override
    public void saveHNWarehouseImportList(List<MaterialLot> materialLotList) throws ClientException {
        try {
            for (MaterialLot materialLot : materialLotList) {
                MaterialLot oldMLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                MaterialLot oldParentMLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getParentMaterialLotId(), ThreadLocalContext.getSessionContext().getOrgRrn());
                if (oldMLot != null || oldParentMLot!= null) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_ID_IS_EXIST, oldMLot == null ? oldParentMLot.getMaterialLotId() : oldMLot.getMaterialLotId());
                }
            }
            String importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            //没有箱号，正常导入
            Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.groupingBy(MaterialLot:: getParentMaterialLotId));
            Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName : materialNameMap.keySet()) {
                List<MaterialLot> materialLots = materialNameMap.get(materialName);
                for (MaterialLot materialLot : materialLots) {
                    Material material = mmsService.getProductByName(materialName);
                    if (material == null) {
                        material = saveProductAndSetStatusModelRrn(materialName);
                    }
                    materialLot.setMaterial(material);
                    materialLot.setStatusModelRrn(material.getStatusModelRrn());
                    materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                    materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                    materialLot.initialMaterialLot();
                    if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                        materialLot.setReserved2("N");
                    }
                    materialLot.setParentMaterialLotId(null);
                    materialLot.setReserved7(MaterialLotUnit.PRODUCT_CATEGORY_FT);
                    materialLot.setReserved48(importCode);//导入编码
                    materialLot.setReserved49(MaterialLotUnit.PRODUCT_CATEGORY_FT);
                    materialLot.setReserved50(MaterialLot.FT_WAFER_SOURCE); //10

                    materialLot = materialLotRepository.saveAndFlush(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                    materialLotHistoryRepository.save(history);
                }
            }
            //有箱号的，自动装箱
            importMLotAutoPackage(packedMLotMap, MaterialLot.HN_PACKCASE);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<MaterialLot>> groupMaterialLotByImportType(List<MaterialLot> materialLots, String ruleName, String cobRuleName) throws ClientException{
        try {
            Map<String, List<MaterialLot>> materialLotMap = Maps.newHashMap();
            List<MaterialLot> cobMaterialLotList = Lists.newArrayList();
            List<MaterialLot> materialLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots){
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved49()) && MaterialLot.IMPORT_COB.equals(materialLot.getReserved49())){
                    cobMaterialLotList.add(materialLot);
                } else {
                    materialLotList.add(materialLot);
                }
            }
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, ruleName);
            }
            if(CollectionUtils.isNotEmpty(cobMaterialLotList)){
                Map<String, List<MaterialLot>>  cobMLotMap = groupMaterialLotByMLotDocRule(cobMaterialLotList, cobRuleName);
                if(cobMLotMap != null && cobMLotMap.keySet().size() > 0){
                    for(String mLotInfo : cobMLotMap.keySet()){
                        if(materialLotMap.containsKey(mLotInfo)){
                            materialLotMap.get(mLotInfo).addAll(cobMLotMap.get(mLotInfo));
                        } else {
                            materialLotMap.put(mLotInfo, cobMLotMap.get(mLotInfo));
                        }
                    }
                }
            }

            return materialLotMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Long getTotalMaterialLotQtyByMLotUnitImportType(List<MaterialLot> materialLotList) throws ClientException {
        try{
            Long totalMaterialLotQty = 0L;
            for (MaterialLot materialLot : materialLotList){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                    totalMaterialLotQty += materialLot.getCurrentSubQty().longValue();
                } else {
                    totalMaterialLotQty += materialLot.getCurrentQty().longValue();
                }
            }
            return totalMaterialLotQty;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    public void validationAndWaferIssue(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String issueWithDoc, String unPlanLot) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            //首先验证物料批次是否装箱，如果存在装箱，自动拆箱后发料
            materialLots = packageService.getWaitPackMaterialLots(materialLots);

            if (!StringUtils.isNullOrEmpty(issueWithDoc)) {
                documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID);
                Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByImportType(materialLots, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID, MaterialLot.COB_WAFER_ISSUE_DOC_VALIDATE_RULE_ID);

                // 确保所有的物料批次都能匹配上单据, 并且数量足够
                for (String key : materialLotMap.keySet()) {
                    if (!documentLineMap.keySet().contains(key)) {
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                    }
                    Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                    Long totalMaterialLotQty = getTotalMaterialLotQtyByMLotUnitImportType(materialLotMap.get(key));
                    if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                        throw new ClientException(GcExceptions.OVER_DOC_QTY);
                    }
                    waferIssue(documentLineMap.get(key), materialLotMap.get(key));
                }

            } else {
                waferIssueWithOutDocument(materialLots);
            }

            //将晶圆信息保存至Mes backendWaferReceive表中
            mesService.saveBackendWaferReceive(materialLots);

            if(StringUtils.isNullOrEmpty(unPlanLot)){
                boolean waferIssueToMesPlanLot = SystemPropertyUtils.getWaferIssueToMesPlanLot();
                log.info("wafer issue to mes plan lot flag is " + waferIssueToMesPlanLot);
                if(waferIssueToMesPlanLot){
                    log.info("wafer issue to mes plan lot start ");
                    mesService.materialLotUnitPlanLot(materialLots);
                    log.info("wafer issue to mes plan lot end ");
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 “COB晶圆发料”
     * @param erpTime
     * @param materialLotActions
     * @param issueWithDoc
     * @param unPlanLot
     * @throws ClientException
     */
    public void mobileValidationAndWaferIssue(String erpTime, List<MaterialLotAction> materialLotActions, String issueWithDoc, String unPlanLot) throws ClientException {
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_COM_WAFER_ISSUE_MANAGER_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            validationAndWaferIssue(documentLineList, materialLotActions, issueWithDoc, unPlanLot);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆发料 但是不和单据挂钩
     * @param materialLots
     */
    public void waferIssueWithOutDocument(List<MaterialLot> materialLots) throws ClientException {
        try {
            for (MaterialLot materialLot : materialLots) {
                String importType = materialLot.getReserved49();
                if (MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType)
                        || MaterialLot.IMPORT_WLA.equals(importType) || MaterialLot.IMPORT_SOC.equals(importType)) {
                    materialLot.setCurrentSubQty(BigDecimal.ZERO);
                    mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                    materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                    changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                } else {
                    materialLot.setCurrentQty(BigDecimal.ZERO);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                        materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                        changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                    }
                }
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆发料
     * @param documentLines
     * @param materialLots
     */
    public void waferIssue(List<DocumentLine> documentLines, List<MaterialLot> materialLots) {
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {

                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    String importType = materialLot.getReserved49();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                        BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(currentSubQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentSubQty);
                            currentSubQty = BigDecimal.ZERO;
                        } else {
                            currentSubQty = currentSubQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(currentSubQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0) {
                            mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                            iterator.remove();
                            changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                        } else {
                            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                            if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                                //GC一个批次只会在一个库存
                                MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                                materialLotInv.setStockQty(materialLot.getCurrentQty());
                                materialLot.setCurrentSubQty(currentSubQty);
                                materialLotInventoryRepository.save(materialLotInv);
                            }
                        }
                    } else {
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty = BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                            iterator.remove();
                            changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(materialLot);
                        } else {
                            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                            if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                                //GC一个批次只会在一个库存
                                MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                                materialLotInv.setStockQty(currentQty);
                                materialLotInv.setCurrentSubQty(materialLot.getCurrentSubQty());
                                materialLotInventoryRepository.save(materialLotInv);
                            }
                        }
                    }

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                    history.setReserved12(documentLine.getObjectRrn().toString());
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, GCMaterialEvent.EVENT_WAFER_ISSUE);

                    updateWaferIssueOrderByDocumentLineAndIssueQty(documentLine, handledQty, GCMaterialEvent.EVENT_WAFER_ISSUE);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void changeMLotUnitStateAndSaveMesWaferBackendWaferReceive(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setState(MaterialLotUnit.STATE_ISSUE);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, GCMaterialEvent.EVENT_WAFER_ISSUE);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆接收
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void receiveWafer(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            Map<String, BigDecimal> mLotQty = Maps.newHashMap();
            for(MaterialLot materialLot : materialLots) {
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
                    mLotQty.put(materialLot.getMaterialLotId(), materialLot.getCurrentSubQty());
                } else {
                    mLotQty.put(materialLot.getMaterialLotId(), materialLot.getCurrentQty());
                }
            }
            for (DocumentLine documentLine: documentLines) {
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    String importType = materialLot.getReserved49();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if(MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)) {
                        BigDecimal waferQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(waferQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(waferQty);
                            waferQty =  BigDecimal.ZERO;
                        } else {
                            waferQty = waferQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(waferQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0) {
                            //数量进行还原。不能扣减。
                            materialLot.setCurrentSubQty(mLotQty.get(materialLot.getMaterialLotId()));
                            materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                            iterator.remove();
                        }
                    } else {
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty =  BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            //数量进行还原。不能扣减。
                            materialLot.setCurrentQty(mLotQty.get(materialLot.getMaterialLotId()));
                            materialLotUnitService.receiveMLotWithUnit(materialLot, WAREHOUSE_ZJ);
                            iterator.remove();
                        }
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    ReceiveOrder receiveOrder = (ReceiveOrder) receiveOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    receiveOrder.setHandledQty(receiveOrder.getHandledQty().add(handledQty));
                    receiveOrder.setUnHandledQty(receiveOrder.getUnHandledQty().subtract(handledQty));
                    receiveOrder = receiveOrderRepository.saveAndFlush(receiveOrder);
                    baseService.saveHistoryEntity(receiveOrder, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    validateDocAndUpdateErpSo(documentLine, handledQty);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COM真空包重测发料
     * @param documentLineList
     * @param materialLotActions
     * @throws ClientException
     */
    public void reTest(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String retestType) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            //将装箱的物料批次筛选出来
            materialLots = packageService.getWaitPackMaterialLots(materialLots);
            String docRuleName = MaterialLot.MLOT_RETEST_DOC_VALIDATE_RULE_ID;
            if(MaterialLot.RETEST_TYPE_FT.equals(retestType)){
                docRuleName = MaterialLot.FT_RETEST_DOC_VALIDATE_RULE_ID;
            }
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, docRuleName);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, docRuleName);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientException(GcExceptions.OVER_DOC_QTY);
                }
                reTestMaterialLots(documentLineMap.get(key), materialLotMap.get(key), retestType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 “COM真空包重测发料”
     * @param materialLotActions
     * @param erpTime
     * @throws ClientException
     */
    public void mobileReTest(List<MaterialLotAction> materialLotActions, String erpTime) throws ClientException{
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_RETEST_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            reTest(documentLineList, materialLotActions, MaterialLot.RETEST_TYPE_COM);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 重测发料。更新单据信息。
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void reTestMaterialLots(List<DocumentLine> documentLines, List<MaterialLot> materialLots, String retestType) throws ClientException{
        try {
            List<MaterialLot> ftRetestVboxList = Lists.newArrayList();
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty = BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_RETEST, StringUtils.EMPTY);
                        materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                        if(MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(materialLot.getReserved7())){
                            ftRetestVboxList.add(materialLot);
                        }
                        iterator.remove();
                    } else {
                        List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                        if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                            //GC一个批次只会在一个库存
                            MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                            materialLotInv.setStockQty(currentQty);
                            materialLotInventoryRepository.save(materialLotInv);
                        }
                    }
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_RETEST);
                    materialLotHistoryRepository.save(history);
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, GCMaterialEvent.EVENT_RETEST);

                    if(MaterialLot.RETEST_TYPE_COM.equals(retestType)){
                        ReTestOrder reTestOrder = (ReTestOrder) reTestOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                        reTestOrder.setHandledQty(reTestOrder.getHandledQty().add(handledQty));
                        reTestOrder.setUnHandledQty(reTestOrder.getUnHandledQty().subtract(handledQty));
                        reTestOrder = reTestOrderRepository.saveAndFlush(reTestOrder);
                        baseService.saveHistoryEntity(reTestOrder, GCMaterialEvent.EVENT_RETEST);

                        Optional<ErpMaterialOutOrder> erpMaterialOutOrderOptional = erpMaterialOutOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                        if (!erpMaterialOutOrderOptional.isPresent()) {
                            throw new ClientParameterException(GcExceptions.ERP_RETEST_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                        }

                        ErpMaterialOutOrder erpMaterialOutOrder = erpMaterialOutOrderOptional.get();
                        erpMaterialOutOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                        erpMaterialOutOrder.setLeftNum(erpMaterialOutOrder.getLeftNum().subtract(handledQty));
                        if (StringUtils.isNullOrEmpty(erpMaterialOutOrder.getDeliveredNum())) {
                            erpMaterialOutOrder.setDeliveredNum(handledQty.toPlainString());
                        } else {
                            BigDecimal docHandledQty = new BigDecimal(erpMaterialOutOrder.getDeliveredNum());
                            docHandledQty = docHandledQty.add(handledQty);
                            erpMaterialOutOrder.setDeliveredNum(docHandledQty.toPlainString());
                        }
                        erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
                    } else if(MaterialLot.RETEST_TYPE_FT.equals(retestType)){
                        updateWaferIssueOrderByDocumentLineAndIssueQty(documentLine, handledQty, GCMaterialEvent.EVENT_RETEST);
                    }
                }
            }

            //FT重测的真空包发料投批
            if(CollectionUtils.isNotEmpty(ftRetestVboxList)){
                log.info("Ft Retest to mes plan lot start， matreiallotList is " + ftRetestVboxList);
                mesService.materialLotPlanLot(ftRetestVboxList);
                log.info("Ft Retest to mes plan lot end ");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆发料单据数量
     * 晶圆发料单的回写来源表有两个，分别判断是否存在并回写数据
     * FT重测发料与晶圆发料共用单据
     * @param documentLine
     * @param handledQty
     */
    private void updateWaferIssueOrderByDocumentLineAndIssueQty(DocumentLine documentLine, BigDecimal handledQty, String transType) throws ClientException{
        try {
            WaferIssueOrder waferIssueOrder = (WaferIssueOrder) waferIssueOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            waferIssueOrder.setHandledQty(waferIssueOrder.getHandledQty().add(handledQty));
            waferIssueOrder.setUnHandledQty(waferIssueOrder.getUnHandledQty().subtract(handledQty));
            waferIssueOrderRepository.save(waferIssueOrder);
            baseService.saveHistoryEntity(waferIssueOrder, transType);

            Optional<ErpMaterialOutOrder> erpMaterialOutOrderOptional = erpMaterialOutOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
            if(erpMaterialOutOrderOptional.isPresent()) {
                ErpMaterialOutOrder erpMaterialOutOrder = erpMaterialOutOrderOptional.get();
                erpMaterialOutOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpMaterialOutOrder.setLeftNum(erpMaterialOutOrder.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpMaterialOutOrder.getDeliveredNum())) {
                    erpMaterialOutOrder.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpMaterialOutOrder.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpMaterialOutOrder.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpMaterialOutOrderRepository.save(erpMaterialOutOrder);
            } else {
                Optional<ErpMaterialOutaOrder> erpMaterialOutaOrderOptional = erpMaterialOutAOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpMaterialOutaOrderOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_RETEST_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpMaterialOutaOrder erpMaterialOutaOrder = erpMaterialOutaOrderOptional.get();
                erpMaterialOutaOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpMaterialOutaOrder.setLeftNum(erpMaterialOutaOrder.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getDeliveredNum())) {
                    erpMaterialOutaOrder.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpMaterialOutaOrder.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpMaterialOutaOrder.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
            }
        } catch (Exception e) {
           throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证装箱的真空包备货单号必须一致
     * 验证发货单据是否存在，是否满足出货匹配规则
     * @param documentLineList
     * @param materialLotList
     * @throws ClientException
     */
    public void validationStockMLotReservedDocLineByRuleId(List<DocumentLine> documentLineList, List<MaterialLot> materialLotList, String ruleId) throws ClientException{
        try {
            Map<String, List<MaterialLot>> mLotDocMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mLotDocMap.keySet()){
                List<MaterialLot> materialLots = mLotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                if(!documentLineList.contains(documentLine)){
                    throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_DOCID_IS_NOT_SAME, documentLine.getDocId());
                }
                Long totalUnhandledQty = materialLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                if(documentLine.getUnHandledQty().compareTo(new BigDecimal(totalUnhandledQty)) < 0){
                    throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
                }
                for (MaterialLot materialLot : materialLots) {
                    //验证出货单与物料批次是否匹配
                    validateMLotAndDocLineByRule(documentLine, materialLot, ruleId);
                    //验证装箱的真空包备货单信息是否一致
                    if(!StringUtils.isNullOrEmpty(materialLot.getPackageType()) && !MaterialLot.RW_WAFER_SOURCE.equals(materialLot.getReserved50())){
                        List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                        for (MaterialLot packagedMaterialLot : packageDetailLots) {
                            if(!materialLot.getReserved16().equals(packagedMaterialLot.getReserved16())){
                                throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "reservedDocRrn", materialLot.getReserved16(), packagedMaterialLot.getReserved16());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证
     * @param documentLineList
     * @param materialLot
     * @throws ClientException
     */
    public void validationDocLine(List<DocumentLine> documentLineList, MaterialLot materialLot) throws ClientException {
        if (CollectionUtils.isEmpty(documentLineList)) {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            for (DocumentLine documentLine : documentLineList) {
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.WAFER_ISSUE_DOC_VALIDATE_RULE_ID);
            }
        }
    }

    /**
     * 验证单据和物料批次是否吻合
     *  1，物料名称
     *  2. 二级代码
     *  3. 等级
     *  4. 保税属性
     * @param documentLine
     * @param materialLot
     * @throws ClientException
     */
    public void validationDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        try {
            Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", documentLine.getMaterialName(), materialLot.getMaterialName());
        }

        String materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
        try {
            Assert.assertEquals(documentLine.getReserved2(), materialSecondCode);
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", documentLine.getReserved2(),  materialSecondCode);
        }

        try {
            Assert.assertEquals(documentLine.getReserved3(), materialLot.getGrade());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "grade", documentLine.getReserved3(), materialLot.getGrade());
        }
        try {
            Assert.assertEquals(documentLine.getReserved7(), materialLot.getReserved6());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", documentLine.getReserved7(), materialLot.getReserved6());
        }
    }

    /**
     * 物料批次根据发货单进行发货，更新单据数据以及，更改ERP的中间表数据
     *  documentLine 产品型号 materialName，二级代码 reserved2，等级 reserved3,  物流 reserved7 一致
     *  materialLot 产品型号 materialName，二级代码 reserved1，等级 grade,  物料 reserved6 一致
     */
    public void stockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            documentLineList = documentLineList.stream().map(documentLine -> documentLineRepository.getOne(documentLine.getObjectRrn())).collect(Collectors.toList());
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set treasuryNoteInfo = materialLots.stream().map(materialLot -> materialLot.getReserved4()).collect(Collectors.toSet());
            if (treasuryNoteInfo != null &&  treasuryNoteInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TREASURY_INFO_IS_NOT_SAME);
            }
            validationStockMLotReservedDocLineByRuleId(documentLineList, materialLots, MaterialLot.MLOT_SHIP_DOC_VALIDATE_RULE_ID);

            //按照备料单自动匹配单据发货
            Map<String, List<MaterialLot>> mlotDocMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mlotDocMap.keySet()){
                List<MaterialLot> materialLotList = mlotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                comStockOut(documentLine, materialLotList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COM真空包正常出货
     * @param documentLine
     * @param materialLotList
     * @throws ClientException
     */
    public void comStockOut(DocumentLine documentLine, List<MaterialLot> materialLotList) throws ClientException{
        try {
            //获取发货的物料批次的快递单号
            String expressNumber = getExpressNumberByMaterialLots(materialLotList);
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLotList) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                // 变更事件，并清理掉库存
                materialLot.setCurrentQty(BigDecimal.ZERO);
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                changeMaterialLotStatusAndSaveHistory(materialLot);

                //箱中的真空包也触发出货事件，修改状态、记录历史
                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for (MaterialLot packageLot : packageDetailLots){
                        changeMaterialLotStatusAndSaveHistory(packageLot);
                    }
                }
            }

            // 验证当前操作数量是否超过待检查数量
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine.setExpressNumber(expressNumber);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            DeliveryOrder deliveryOrder = (DeliveryOrder) deliveryOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            deliveryOrder.setHandledQty(deliveryOrder.getHandledQty().add(handledQty));
            deliveryOrder.setUnHandledQty(deliveryOrder.getUnHandledQty().subtract(handledQty));
            deliveryOrder = deliveryOrderRepository.saveAndFlush(deliveryOrder);
            baseService.saveHistoryEntity(deliveryOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateDocAndUpdateErpSo(documentLine, handledQty);

            if (SystemPropertyUtils.getConnectMscmFlag()) {
                scmService.addScmTracking(documentLine.getDocId(), materialLotList);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 依调拨单出货
     * @param documentLineList
     * @param materialLotActions
     * @param warehouseId
     * @throws ClientException
     */
    @Override
    public void transferShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String warehouseId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            validateCobMaterialLotDocInfo(materialLots);
            validationStockMLotReservedDocLineByRuleId(documentLineList, materialLots, MaterialLot.MLOT_TRANSFER_SHIP_VALIDATE_RULE_ID);
            Warehouse warehouse = mmsService.getWarehouseByName(warehouseId);
            String location = StringUtils.EMPTY;
            if(MaterialLot.WAREHOUSE_SH.equals(warehouseId) || MaterialLot.WAREHOUSE_HN.equals(warehouseId)){
                location = MaterialLot.LOCATION_SH;
            } else if(MaterialLot.WAREHOUSE_ZJ.equals(warehouseId)){
                location = MaterialLot.BONDED_PROPERTY_ZSH;
            } else if(MaterialLot.WAREHOUSE_HK.equals(warehouseId) || MaterialLot.WAREHOUSE_BS.equals(warehouseId)){
                location = MaterialLot.BONDED_PROPERTY_HK;
            }

            Map<String, List<MaterialLot>> mlotDocMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mlotDocMap.keySet()){
                List<MaterialLot> materialLotList = mlotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                BigDecimal docUnHandledQty = documentLine.getUnHandledQty();
                BigDecimal handledQty = BigDecimal.ZERO;
                for(MaterialLot materialLot : materialLotList){
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                    String materialName = materialLot.getMaterialName();
                    if(materialName.endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialName.endsWith(MaterialLot.STOCKOUT_TYPE_4) || materialName.endsWith(MaterialLot.STOCKOUT_TYPE_47)){
                        handledQty = handledQty.add(materialLot.getCurrentQty());
                        docUnHandledQty =  docUnHandledQty.subtract(materialLot.getCurrentQty());
                        materialLot.setCurrentQty(BigDecimal.ZERO);
                    } else {
                        handledQty = handledQty.add(materialLot.getCurrentSubQty());
                        docUnHandledQty =  docUnHandledQty.subtract(materialLot.getCurrentSubQty());
                        materialLot.setCurrentSubQty(BigDecimal.ZERO);
                    }
                    materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    changeMaterialLotStatusAndSaveHistory(materialLot);
                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                        for (MaterialLot packageLot : packageDetailLots){
                            changeMaterialLotStatusAndSaveHistory(packageLot);
                            updateMaterialLotUnitWarehouse(packageLot, MaterialLotUnit.STATE_OUT, MaterialLotHistory.TRANS_TYPE_SHIP);
                        }
                    } else {
                        updateMaterialLotUnitWarehouse(materialLot, MaterialLotUnit.STATE_OUT, MaterialLotHistory.TRANS_TYPE_SHIP);
                    }

                    //批次做完出货之后，根据选择的仓库修改仓库和保税属性，变为Create状态清除备货等信息
                    materialLot.setCurrentSubQty(currentSubQty);
                    materialLot.setCurrentQty(currentQty);
                    materialLot.setReservedQty(BigDecimal.ZERO);
                    changeMLotWarehouseAndSaveHistory(materialLot, warehouse, location);
                    if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                        for (MaterialLot packageLot : packageDetailLots){
                            packageLot.setReservedQty(BigDecimal.ZERO);
                            changeMLotWarehouseAndSaveHistory(packageLot, warehouse, location);
                            updateMaterialLotUnitWarehouse(materialLot, MaterialLotUnit.STATE_CREATE, MaterialLotHistory.TRANS_TYPE_CREATE);
                        }
                    } else{
                        updateMaterialLotUnitWarehouse(materialLot, MaterialLotUnit.STATE_CREATE, MaterialLotHistory.TRANS_TYPE_CREATE);
                    }
                }
                documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
                documentLine = documentLineRepository.saveAndFlush(documentLine);
                baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

                updateDocQyAndErpSobSynStatusAndQty(documentLine, handledQty);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆仓库和保税属性
     * @param materialLot
     * @throws ClientException
     */
    private void updateMaterialLotUnitWarehouse(MaterialLot materialLot, String state, String trnasType) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for (MaterialLotUnit materialLotUnit : materialLotUnits){
                materialLotUnit.setState(state);
                materialLotUnit.setReserved4(materialLot.getReserved6());
                materialLotUnit.setReserved13(materialLot.getReserved13());
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, trnasType);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 变更物料批次的状态以及清除备货信息
     * @param materialLot
     * @param warehouse
     * @param location
     * @throws ClientException
     */
    private void changeMLotWarehouseAndSaveHistory(MaterialLot materialLot, Warehouse warehouse, String location) throws ClientException{
        try {
            materialLot.resetMLotInfo();
            materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
            materialLot.setStatus(MaterialStatus.STATUS_CREATE);
            materialLot.setReserved6(location);
            materialLot.setReserved13(warehouse.getObjectRrn().toString());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CREATE);
            materialLotHistoryRepository.save(history);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据出货物料获取快递单号
     * @param materialLotList
     * @return
     * @throws ClientException
     */
    private String getExpressNumberByMaterialLots(List<MaterialLot> materialLotList) throws ClientException{
        try{
            String expressNumber = StringUtils.EMPTY;
            Map<String, List<MaterialLot>> mLotExpressMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getExpressNumber()))
                    .collect(Collectors.groupingBy(MaterialLot :: getExpressNumber));
            for (String expressId : mLotExpressMap.keySet()){
                if(StringUtils.isNullOrEmpty(expressNumber)){
                    expressNumber = expressId;
                } else {
                    expressNumber += StringUtils.SEMICOLON_CODE + expressId;
                }
            }
            return expressNumber;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void changeMaterialLotStatusAndSaveHistory(MaterialLot materialLot) throws ClientException {
        try {
            mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_SHIP);
            materialLotHistoryRepository.save(history);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步接收单据
     * @throws ClientException
     */
    public void asyncReceiveOrder() throws ClientException {
        try {
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSo>> sameCreateSeqOrder = documentIdList.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<ReceiveOrder> receiveOrderList = receiveOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(receiveOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    ReceiveOrder receiveOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(receiveOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : documentIdList) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        receiveOrder = new ReceiveOrder();
                        receiveOrder.setName(documentId);
                        receiveOrder.setStatus(Document.STATUS_OPEN);
                        receiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        receiveOrder = receiveOrderList.get(0);
                        totalQty = receiveOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(receiveOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSo erpSo : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (receiveOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,receiveOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, Document.CATEGORY_RECEIVE);

                            documentLine.setReserved8(erpSo.getCusname());
                            documentLine.setReserved12(erpSo.getOther8());
                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            receiveOrder.setSupplierName(erpSo.getCusname());
                            receiveOrder.setOwner(erpSo.getChandler());
                            receiveOrder.setReserved32(erpSo.getCreateSeq());
                            if (receiveOrder.getErpCreated() == null) {
                                receiveOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (receiveOrder.getErpCreated().after(erpCreatedDate)) {
                                    receiveOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        receiveOrder.setQty(totalQty);
                        receiveOrder.setUnHandledQty(receiveOrder.getQty().subtract(receiveOrder.getHandledQty()));
                        receiveOrder.setUnReservedQty(receiveOrder.getQty().subtract(receiveOrder.getReservedQty()));
                        receiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        receiveOrder = (ReceiveOrder) baseService.saveEntity(receiveOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(receiveOrder);
                        baseService.saveEntity(documentLine);
                    }
                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(receiveOrder.getSupplierName())) {
                        savaCustomer(receiveOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步COG接收单据
     * @throws ClientException
     */
    public void asyncCogReceiveOrder() throws ClientException {
        try {
            List<ErpSo> erpSoList = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_COG, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSoList)) {
                Map<String, List<ErpSo>> documentIdMap = erpSoList.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> erpSos = documentIdMap.get(documentId);
                    Map<String, List<ErpSo>> sameCreateSeqOrder = erpSos.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<CogReceiveOrder> cogReceiveOrderList = cogReceiveOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(cogReceiveOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(erpSos);
                            continue;
                        }
                    }
                    CogReceiveOrder cogReceiveOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(cogReceiveOrderList)) {
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : erpSos) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        cogReceiveOrder = new CogReceiveOrder();
                        cogReceiveOrder.setName(documentId);
                        cogReceiveOrder.setStatus(Document.STATUS_OPEN);
                        cogReceiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        cogReceiveOrder = cogReceiveOrderList.get(0);
                        totalQty = cogReceiveOrder.getQty();
                        boolean difCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(cogReceiveOrder.getReserved32())){
                                difCreateSeq = true;
                                for  (ErpSo erpSo : erpSos) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(difCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (cogReceiveOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,cogReceiveOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, CogReceiveOrder.CATEGORY_COG_RECEIVE);
                            documentLine.setReserved8(erpSo.getCusname());
                            documentLine.setReserved12(erpSo.getOther8());
                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            cogReceiveOrder.setReserved32(erpSo.getCreateSeq());
                            cogReceiveOrder.setOwner(erpSo.getChandler());
                            cogReceiveOrder.setSupplierName(erpSo.getCusname());
                            if (cogReceiveOrder.getErpCreated() == null) {
                                cogReceiveOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (cogReceiveOrder.getErpCreated().after(erpCreatedDate)) {
                                    cogReceiveOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        cogReceiveOrder.setQty(totalQty);
                        cogReceiveOrder.setUnHandledQty(cogReceiveOrder.getQty().subtract(cogReceiveOrder.getHandledQty()));
                        cogReceiveOrder.setUnReservedQty(cogReceiveOrder.getQty().subtract(cogReceiveOrder.getReservedQty()));

                        cogReceiveOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        cogReceiveOrder = (CogReceiveOrder) baseService.saveEntity(cogReceiveOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(cogReceiveOrder);
                        baseService.saveEntity(documentLine);
                    }
                    if (!StringUtils.isNullOrEmpty(cogReceiveOrder.getSupplierName())) {
                        savaCustomer(cogReceiveOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private DocumentLine validateDocQtyAndGetDocument(DocumentLine documentLine ,Long objectRrn, ErpSo erpSo) throws ClientException{
        try {
            documentLine = documentLineRepository.findByDocRrnAndReserved1(objectRrn, String.valueOf(erpSo.getSeq()));
            if (documentLine != null) {
                if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSo.getSynStatus())) {
                    if (documentLine != null && documentLine.getHandledQty().compareTo(erpSo.getIquantity()) > 0) {
                        throw new ClientException("gc.order_handled_qty_gt_qty");
                    }
                }
            }
            return documentLine;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void savaErpSoErrorInfo(List<ErpSo> documentIdList) throws ClientException{
        try {
            if(CollectionUtils.isNotEmpty(documentIdList)){
                for(ErpSo erpSo : documentIdList){
                    erpSo.setUserId(Document.SYNC_USER_ID);
                    erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                    erpSo.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                    erpSoRepository.save(erpSo);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void updateErpSoOrderSynStatusAndErrorMemoAndUserId(List<Long> asyncSuccessSeqList, List<Long> asyncDuplicateSeqList) throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> successSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                    for(List seqGroupList : successSeqGroupList){
                        erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroupList);
                    }
                } else {
                    erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                }
            }

            if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> duplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                    for(List seqGroupList : duplicateSeqGroupList){
                        erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroupList);
                    }
                } else {
                    erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR,ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private DocumentLine validateAndSetErpSoToDocumentLine(DocumentLine documentLine, ErpSo erpSo, Date erpCreatedDate, String docType) throws ClientException{
        try {
            if (documentLine == null) {
                documentLine = new DocumentLine();
                Material material = new Material();
                if(DeliveryOrder.CATEGORY_DELIVERY.equals(docType) || CogReceiveOrder.CATEGORY_COG_RECEIVE.equals(docType)){
                    material = mmsService.getProductByName(erpSo.getCinvcode());
                    if (material == null) {
                        throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpSo.getCinvcode());
                    }
                } else {
                    material = mmsService.getRawMaterialByName(erpSo.getCinvcode());
                    if (material == null) {
                        throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpSo.getCinvcode());
                    }
                }
                documentLine.setErpCreated(erpCreatedDate);
                documentLine.setMaterialRrn(material.getObjectRrn());
                documentLine.setMaterialName(material.getName());
                documentLine.setReserved1(String.valueOf(erpSo.getSeq()));
                documentLine.setReserved2(erpSo.getSecondcode());
                documentLine.setReserved3(erpSo.getGrade());
                documentLine.setReserved4(erpSo.getCfree3());
                documentLine.setReserved5(erpSo.getCmaker());
                documentLine.setReserved6(erpSo.getChandler());
                documentLine.setReserved7(erpSo.getOther1());

                documentLine.setReserved8(erpSo.getCusname());
                documentLine.setReserved9(docType);

                documentLine.setReserved10(erpSo.getGCode());
                documentLine.setReserved11(erpSo.getGName());
                documentLine.setReserved12(erpSo.getOther8());
                documentLine.setReserved13(erpSo.getCmemo());
                documentLine.setReserved15(erpSo.getOther18());
                documentLine.setReserved17(erpSo.getOther3());
                documentLine.setReserved20(erpSo.getOther9());
                documentLine.setReserved21(erpSo.getOther10());
                documentLine.setReserved27(erpSo.getOther7());
                documentLine.setReserved28(erpSo.getOther4());
                documentLine.setReserved30(erpSo.getOther5());
                documentLine.setReserved34(erpSo.getOther7());
                documentLine.setDocType(erpSo.getCvouchtype());
                documentLine.setDocName(erpSo.getCvouchname());
                documentLine.setDocBusType(erpSo.getCbustype());
                documentLine.setDocSource(erpSo.getCsource());
                documentLine.setWarehouseCode(erpSo.getCwhcode());
                documentLine.setWarehouseName(erpSo.getCwhname());
                documentLine.setProductType(erpSo.getOther15());
                documentLine.setCrdCode(erpSo.getCrdcode());
                documentLine.setCrdName(erpSo.getCrdname());
                documentLine.setCuscode(erpSo.getCuscode());
                documentLine.setThreeSideTransaction(erpSo.getCuscode());
                documentLine.setReserved31(ErpSo.SOURCE_TABLE_NAME);
            }
            return documentLine;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 对同步的seq进行分组，每组小于1000
     * @param asyncSeqList
     * @return
     * @throws ClientException
     */
    private List<List<Long>> getSeqListGroupByCount(List<Long> asyncSeqList) throws ClientException{
        try {
            List<List<Long>> seqGroupList = Lists.newArrayList();
            int length = Document.SEQ_LENGTH;

            int size = asyncSeqList.size();
            int count = (size + length - 1) / length;
            for (int i = 0; i < count; i++) {
                List<Long> seqGroup = asyncSeqList.subList(i * length, ((i + 1) * length > size ? size : length * (i + 1)));
                seqGroupList.add(seqGroup);
            }
            return seqGroupList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步原材料其它出货单
     * ERP_SO中type='MO'
     * @throws ClientException
     */
    @Override
    public void asyncRawMaterialOtherShipOrder() throws ClientException {
        try{
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_MO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> documentIdList = documentIdMap.get(documentId);
                    Map<String, List<ErpSo>> sameCreateSeqOrder = documentIdList.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq())).collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<RawMaterialOtherOutOrder> rawMaterialOtherOutOrders = rawMaterialOtherOutOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(rawMaterialOtherOutOrders)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(erpSos);
                            continue;
                        }
                    }
                    RawMaterialOtherOutOrder rawMaterialOtherOutOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(rawMaterialOtherOutOrders)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : documentIdList) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        rawMaterialOtherOutOrder = new RawMaterialOtherOutOrder();
                        rawMaterialOtherOutOrder.setName(documentId);
                        rawMaterialOtherOutOrder.setStatus(Document.STATUS_OPEN);
                        rawMaterialOtherOutOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        rawMaterialOtherOutOrder = rawMaterialOtherOutOrders.get(0);
                        totalQty = rawMaterialOtherOutOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(rawMaterialOtherOutOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSo erpSo : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (rawMaterialOtherOutOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,rawMaterialOtherOutOrder.getObjectRrn(), erpSo);
                            }
                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, RawMaterialOtherOutOrder.CATEGORY_RAW_MATERIAL_OTHER_SHIP);
                            documentLine.setReserved8(erpSo.getCusname());
                            documentLine.setReserved12(erpSo.getOther8());
                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLine.setMaterialType(erpSo.getCfree4());
                            documentLines.add(documentLine);

                            rawMaterialOtherOutOrder.setSupplierName(erpSo.getCusname());// 同一个单据下，所有的客户都是一样的。
                            rawMaterialOtherOutOrder.setOwner(erpSo.getChandler());
                            rawMaterialOtherOutOrder.setReserved32(erpSo.getCreateSeq());
                            if (rawMaterialOtherOutOrder.getErpCreated() == null) {
                                rawMaterialOtherOutOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (rawMaterialOtherOutOrder.getErpCreated().after(erpCreatedDate)) {
                                    rawMaterialOtherOutOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);//同步失败，状态修改为2
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        rawMaterialOtherOutOrder.setQty(totalQty);
                        rawMaterialOtherOutOrder.setUnHandledQty(rawMaterialOtherOutOrder.getQty().subtract(rawMaterialOtherOutOrder.getHandledQty()));
                        rawMaterialOtherOutOrder.setUnReservedQty(rawMaterialOtherOutOrder.getQty().subtract(rawMaterialOtherOutOrder.getReservedQty()));
                        rawMaterialOtherOutOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        rawMaterialOtherOutOrder = (RawMaterialOtherOutOrder) baseService.saveEntity(rawMaterialOtherOutOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(rawMaterialOtherOutOrder);
                        baseService.saveEntity(documentLine);
                    }
                    if (!StringUtils.isNullOrEmpty(rawMaterialOtherOutOrder.getSupplierName())) {
                        savaCustomer(rawMaterialOtherOutOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步出货单
     * @throws ClientException
     */
    public void asyncShipOrder() throws ClientException {
        try {
            List<ErpSo> erpSos = erpSoRepository.findByTypeAndSynStatusNotIn(ErpSo.TYPE_SO, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS, ErpSo.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSo>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSo :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSo> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSo>> sameCreateSeqOrder = documentIdList.stream().filter(erpSo -> !StringUtils.isNullOrEmpty(erpSo.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSo :: getCreateSeq));
                    List<DeliveryOrder> deliveryOrderList = deliveryOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(deliveryOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            savaErpSoErrorInfo(erpSos);
                            continue;
                        }
                    }
                    DeliveryOrder deliveryOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(deliveryOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSo erpSo : documentIdList) {
                                asyncDuplicateSeqList.add(erpSo.getSeq());
                            }
                            continue;
                        }
                        deliveryOrder = new DeliveryOrder();
                        deliveryOrder.setName(documentId);
                        deliveryOrder.setStatus(Document.STATUS_OPEN);
                        deliveryOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                    } else {
                        deliveryOrder = deliveryOrderList.get(0);
                        totalQty = deliveryOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(deliveryOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSo erpSo : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSo.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSo erpSo : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (deliveryOrder.getObjectRrn() != null) {
                                documentLine = validateDocQtyAndGetDocument(documentLine ,deliveryOrder.getObjectRrn(), erpSo);
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSo.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            documentLine = validateAndSetErpSoToDocumentLine(documentLine, erpSo, erpCreatedDate, DeliveryOrder.CATEGORY_DELIVERY);
                            documentLine.setReserved8(erpSo.getCusname());
                            documentLine.setReserved12(erpSo.getOther8());
                            totalQty = totalQty.add(erpSo.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSo.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            deliveryOrder.setSupplierName(erpSo.getCusname());
                            deliveryOrder.setOwner(erpSo.getChandler());
                            deliveryOrder.setReserved32(erpSo.getCreateSeq());
                            if (deliveryOrder.getErpCreated() == null) {
                                deliveryOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (deliveryOrder.getErpCreated().after(erpCreatedDate)) {
                                    deliveryOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSo.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSo.setUserId(Document.SYNC_USER_ID);
                            erpSo.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSo.setErrorMemo(e.getMessage());
                            erpSoRepository.save(erpSo);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        deliveryOrder.setQty(totalQty);
                        deliveryOrder.setUnHandledQty(deliveryOrder.getQty().subtract(deliveryOrder.getHandledQty()));
                        deliveryOrder.setUnReservedQty(deliveryOrder.getQty().subtract(deliveryOrder.getReservedQty()));
                        deliveryOrder.setReserved31(ErpSo.SOURCE_TABLE_NAME);
                        deliveryOrder = (DeliveryOrder) baseService.saveEntity(deliveryOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(deliveryOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(deliveryOrder.getSupplierName())) {
                        savaCustomer(deliveryOrder.getSupplierName());
                    }
                }
                updateErpSoOrderSynStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 出货前检查。
     *  直接以检查结果做状态 当stockOutCheckList为空的时候就是OK。因为GC要求，OK时候不记录检查项
     * @param materialLots
     * @param stockOutCheckList 检查项
     * @return
     */
    public void stockOutCheck(List<MaterialLot> materialLots, List<StockOutCheck> stockOutCheckList) throws ClientException {
        try {
            String checkResult = StockOutCheck.RESULT_OK;
            List<StockOutCheck> ngStockOutCheckList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(stockOutCheckList)) {
                ngStockOutCheckList = stockOutCheckList.stream().filter(checkItem -> StockOutCheck.RESULT_NG.equals(checkItem.getResult())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(ngStockOutCheckList)) {
                checkResult = StockOutCheck.RESULT_NG;
            }

            for (MaterialLot materialLot : materialLots) {
                //20190917 GC要求 如果判了NG。并且装箱检查是PASS的，将PASS改成PASS0
                if (StockOutCheck.RESULT_NG.equals(checkResult) && StockOutCheck.RESULT_PASS.equals(materialLot.getReserved9())) {
                    materialLot.setReserved9(materialLot.getReserved9() + "0");

                    // 20190921 GC要求，被包装的批次都需要也需要改成materialLot相关信息
                    List<MaterialLot> packedMaterialLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                    if (CollectionUtils.isNotEmpty(packedMaterialLots)) {
                        for (MaterialLot packedMaterialLot : packedMaterialLots) {
                            packedMaterialLot.setReserved9(materialLot.getReserved9());
                            materialLotRepository.save(packedMaterialLot);
                        }
                    }
                } else if(StockOutCheck.RESULT_OK.equals(checkResult) && MaterialLot.STATUS_STOCK.equals(materialLot.getStatusCategory())){
                    materialLot.setReserved9(StockOutCheck.RESULT_PASS );
                } else if(StockOutCheck.RESULT_NG.equals(checkResult) && MaterialLot.STATUS_STOCK.equals(materialLot.getStatusCategory())){
                    materialLot.setReserved9(StockOutCheck.RESULT_PASS + "0");
                }

                materialLot = mmsService.changeMaterialLotState(materialLot, EVENT_OQC, checkResult);
//              GC要求只记录NG的判定历史即可
                if (CollectionUtils.isNotEmpty(ngStockOutCheckList)) {
                    // 保存每个项目的判定结果
                    MaterialLot finalMaterialLot = materialLot;
                    ngStockOutCheckList.forEach(stockOutCheck -> {
                        MaterialLotJudgeHis materialLotJudgeHis = new MaterialLotJudgeHis();
                        materialLotJudgeHis.setMaterialLotRrn(finalMaterialLot.getObjectRrn());
                        materialLotJudgeHis.setMaterialLotId(finalMaterialLot.getMaterialLotId());
                        materialLotJudgeHis.setItemName(stockOutCheck.getName());
                        materialLotJudgeHis.setResult(stockOutCheck.getResult());
                        materialLotJudgeHis.setTransType(MaterialLotJudgeHis.TRANS_TYPE_OQC);
                        materialLotJudgeHis.setHisSeq(ThreadLocalContext.getTransRrn());
                        materialLotJudgeHisRepository.save(materialLotJudgeHis);
                    });
                }
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_OQC);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取装箱检验的检查项
     * @return
     * @throws ClientException
     */
    public List<NBOwnerReferenceList> getJudgePackCaseCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_PACK_CASE_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getWltJudgePackCaseCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_WLTPACK_CASE_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getStockOutCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_STOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getWltStockOutCheckList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_WLTSTOCK_OUT_CHECK_ITEM_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public List<NBOwnerReferenceList> getReferenceListByName(String reserenceName) throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(reserenceName, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    public MesPackedLot findByPackedLotId(String boxId) throws ClientException {
        return mesPackedLotRepository.findByBoxId(boxId);
    }

    public List<MesPackedLot> findByParentRrn(Long parentRrn) throws ClientException {
        return mesPackedLotRepository.findByParentRrn(parentRrn);
    }
    /**
     * 接收MES的完成品
     * @param packedLotList
     */
    public void receiveFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            Map<String, Warehouse> warehouseMap = Maps.newHashMap();

            List<ErpMo> erpMos = Lists.newArrayList();
            List<ErpMoa> erpMoaList = Lists.newArrayList();
            MesPackedLotRelation mesPackedLotRelation;

            for (String productId : packedLotMap.keySet()) {
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(productId);
                Material material = getMaterialByMesPackedLot(mesPackedLotList);

                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    String productCateGory = mesPackedLot.getProductCategory();
                    //WLT产线接收时验证MM_PACKEND_LOT_RELATION表中有没有记录物料编码信息
                    if(!StringUtils.isNullOrEmpty(mesPackedLot.getCstId())){
                        List<MesPackedLot> mesPackedLotUnits = mesPackedLotRepository.findByCstIdAndPackedStatusAndTypeNotInAndWaferIdIsNotNull(mesPackedLot.getCstId(), MesPackedLot.PACKED_STATUS_IN, Lists.newArrayList(MesPackedLot.PACKED_TYPE_CPCST_PREIN));
                        mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotUnits.get(0).getPackedLotRrn());
                    } else {
                        mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());
                    }
                    if(!MesPackedLot.VBOX_CATEGORY_LIST.contains(mesPackedLot.getProductCategory())){
                        if(mesPackedLotRelation == null){
                            throw new ClientException(GcExceptions.CORRESPONDING_RAW_MATERIAL_INFO_IS_EMPTY);
                        } else {
                            //获取mm_packend_lot_Relation表中的Vender（供应商）存入GC_SUPPLIER表中
                            String vender = mesPackedLotRelation.getVender();
                            if(!StringUtils.isNullOrEmpty(vender)){
                                Supplier supplier = supplierRepository.getByNameAndType(vender, Supplier.TYPE_VENDER);
                                if(supplier == null){
                                    supplier = new Supplier();
                                    supplier.setName(vender);
                                    supplier.setType(Supplier.TYPE_VENDER);
                                    supplierRepository.save(supplier);
                                }
                            }
                        }
                    }

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));
                    materialLotAction.setSourceModelId(mesPackedLot.getProductId());

                    // 真空包产地是SH的入SH仓库，是ZJ的入浙江仓库(COM和FT的保税属性是上海的入上海仓库，其他入浙江仓库)
                    String warehouseName = WAREHOUSE_ZJ;
                    if(MesPackedLot.PRODUCT_CATEGORY_COM.equals(mesPackedLot.getProductCategory()) || MesPackedLot.PRODUCT_CATEGORY_FT.equals(productCateGory)){
                        if(!StringUtils.isNullOrEmpty(mesPackedLot.getBondedProperty()) && mesPackedLot.getBondedProperty().equals(MaterialLot.LOCATION_SH)){
                            warehouseName = WAREHOUSE_SH;
                        }
                    } else if (!StringUtils.isNullOrEmpty(mesPackedLot.getLocation()) && mesPackedLot.getLocation().equalsIgnoreCase(MaterialLot.LOCATION_SH)) {
                        warehouseName = WAREHOUSE_SH;
                    }
                    if (MesPackedLot.PACKED_TYPE.equals(mesPackedLot.getType()) && MesPackedLot.LEVEL_TWO_CODE_FFFFF.equals(mesPackedLot.getLevelTwoCode())
                            && MesPackedLot.GRADE_F3.equals(mesPackedLot.getGrade()) && !StringUtils.isNullOrEmpty(mesPackedLot.getBondedProperty())) {
                        if (MaterialLot.LOCATION_SH.equals(mesPackedLot.getBondedProperty())) {
                            warehouseName = WAREHOUSE_SH;
                        }
                    }

                    Warehouse warehouse;
                    if (!warehouseMap.containsKey(warehouseName)) {
                        warehouse = getWarehouseByName(warehouseName);
                        warehouseMap.put(warehouseName, warehouse);
                    }
                    warehouse = warehouseMap.get(warehouseName);

                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                    getDefaultStorageIdByBondedProperty(materialLotAction, mesPackedLot.getBondedProperty());

                    // 需要赋值的Map
                    Map<String, Object> otherReceiveProps = Maps.newHashMap();
                    otherReceiveProps.put("reserved1", mesPackedLot.getLevelTwoCode());
                    otherReceiveProps.put("reserved3", mesPackedLot.getSalesNote());
                    otherReceiveProps.put("reserved4", mesPackedLot.getTreasuryNote());
                    otherReceiveProps.put("reserved5", mesPackedLot.getProductionNote());
                    otherReceiveProps.put("reserved6", mesPackedLot.getBondedProperty());
                    otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
                    otherReceiveProps.put("earlierExpDate", mesPackedLot.getCreated());
                    if(MaterialLotUnit.PRODUCT_CATEGORY_CP.equals(mesPackedLot.getProductCategory())
                            || MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(mesPackedLot.getProductCategory())
                            || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(mesPackedLot.getProductCategory())
                            || MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory())){
                        otherReceiveProps.put("workOrderId", StringUtils.EMPTY);
                    } else {
                        otherReceiveProps.put("workOrderId", mesPackedLot.getWorkorderId());
                    }
                    otherReceiveProps.put("productType", mesPackedLot.getProductType());
                    if(!StringUtils.isNullOrEmpty(mesPackedLot.getTestSource())){
                        otherReceiveProps.put("reserved49", mesPackedLot.getTestSource());
                    } else {
                        otherReceiveProps.put("reserved49", mesPackedLot.getImportType());
                    }
                    otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
                    otherReceiveProps.put("reserved24", mesPackedLot.getFabDevice());
                    if(mesPackedLotRelation != null && MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(mesPackedLot.getProductCategory())){
                        otherReceiveProps.put("reserved22", mesPackedLotRelation.getVender());
                    } else if(MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory())){
                        if(MaterialLotUnit.BOX_TYPE.equals(mesPackedLot.getType())){
                            otherReceiveProps.put("reserved22", MesPackedLot.ZJ_SUB_NAME);
                        } else {
                            otherReceiveProps.put("reserved22", mesPackedLot.getVender());
                        }
                    } else {
                        otherReceiveProps.put("reserved22", mesPackedLot.getSubName());
                    }
                    if(mesPackedLotRelation != null){
                        otherReceiveProps.put("reserved25", mesPackedLotRelation.getWaferProperty());
                    }
                    otherReceiveProps.put("lotId", mesPackedLot.getCstId());
                    String productCategory = mesPackedLot.getProductCategory();
                    if(!StringUtils.isNullOrEmpty(productCategory)){
                        mLotSetWaferSourceAndReserved7(otherReceiveProps, productCategory, mesPackedLot);
                    }
                    if(mesPackedLot.getWaferQty() != null){
                        BigDecimal waferQty = new BigDecimal(mesPackedLot.getWaferQty().toString());
                        materialLotAction.setTransCount(waferQty);
                    }

                    if (MesPackedLot.PRODUCT_CATEGORY_FT.equals(mesPackedLot.getProductCategory())){
                        otherReceiveProps.put("sourceProductId", mesPackedLot.getOrgProductId());
                    }
                    materialLotAction.setPropsMap(otherReceiveProps);

                    materialLotActions.add(materialLotAction);

                    if((MesPackedLot.PRODUCT_CATEGORY_FT.equals(productCateGory) && !MaterialLotUnit.BOX_TYPE.equals(mesPackedLot.getType()))){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getQuantity());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded(mesPackedLot.getIssueLocation());
                        erpMoa.setMaterialCode(mesPackedLot.getMaterialName());
                        erpMoa.setMaterialQty(mesPackedLot.getQuantity());
                        erpMoa.setMaterialGrade(mesPackedLot.getIssueGrade());
                        erpMoa.setMaterialVersion(mesPackedLot.getSubcode());
                        erpMoa.setProdCate(mesPackedLot.getProductType());
                        erpMoaList.add(erpMoa);
                    } else if(MesPackedLot.PRODUCT_CATEGORY_CP.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_WLT.equals(productCateGory)
                            || MesPackedLot.PRODUCT_CATEGORY_LSP.equals(productCateGory) || MesPackedLot.PRODUCT_CATEGORY_LCP.equals(productCateGory)
                            || MesPackedLot.PRODUCT_CATEGORY_SCP.equals(productCateGory)){
                        // ERP_MOA插入数据
                        ErpMoa erpMoa = new ErpMoa();
                        erpMoa.setFQty(mesPackedLot.getWaferQty());
                        erpMoa.setWarehouseCode(warehouseName);
                        erpMoa.setMesPackedLot(mesPackedLot);

                        //从MM_PACKED_LOT_RELATION表中获取物料型号、物料数据等相关数据
                        erpMoa.setCMemo("EMPTY");
                        erpMoa.setMaterialBonded(mesPackedLotRelation.getMaterialBonded());
                        erpMoa.setMaterialCode(mesPackedLotRelation.getMaterialCode());
                        erpMoa.setMaterialQty(mesPackedLot.getWaferQty());
                        erpMoa.setMaterialGrade(mesPackedLotRelation.getMaterialGrade());
                        erpMoa.setMaterialVersion(mesPackedLotRelation.getMaterialVersion());
                        erpMoa.setProdCate(mesPackedLotRelation.getProductType());

                        erpMoaList.add(erpMoa);
                    } else if(MesPackedLot.PRODUCT_CATEGORY_COM.equals(productCateGory)){
                        // ERP MO插入数据
                        ErpMo erpMo = new ErpMo();
                        erpMo.setCCode(mesPackedLot.getShipSerialNumber());
                        erpMo.setDDate(mesPackedLot.getFinalOperationTime());
                        erpMo.setSecondcode(mesPackedLot.getLevelTwoCode());
                        erpMo.setCWHCode(warehouseName);
                        if (StringUtils.isNullOrEmpty(mesPackedLot.getWorkorderId())) {
                            erpMo.setCmoCode(ErpMo.DEFAULT_WO_ID);
                        } else {
                            erpMo.setCmoCode(mesPackedLot.getWorkorderId());
                        }
                        if(StringUtils.isNullOrEmpty(mesPackedLot.getErpProductId())){
                            erpMo.setCinVCode(mesPackedLot.getProductId());
                        } else {
                            erpMo.setCinVCode(mesPackedLot.getErpProductId());
                        }
                        erpMo.setFQty(mesPackedLot.getQuantity());

                        erpMo.setCGrade(mesPackedLot.getGrade());
                        erpMo.setBonded(mesPackedLot.getBondedProperty());
                        erpMos.add(erpMo);
                    }
                }
                List<MaterialLot> materialLotList = mmsService.receiveMLotList2Warehouse(material, materialLotActions);
                for(MaterialLot materialLot : materialLotList){
                    //COM以及FT的，若保税属性为ZSH，进行转库操作，转至SH_STOCK，保税属性修改为SH
                    if(((MesPackedLot.PRODUCT_CATEGORY_COM.equals(materialLot.getReserved7()) && MaterialLot.COM_TRANS_WH_BIN_LIST.contains(materialLot.getGrade()))
                            || MesPackedLot.PRODUCT_CATEGORY_FT.equals(materialLot.getReserved7()))
                            && MaterialLot.BONDED_PROPERTY_ZSH.equals(materialLot.getReserved6())) {
                        Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_SH);
                        materialLotTransferWareHouse(materialLot, MaterialLot.LOCATION_SH, warehouse);
                    }
                    String workOrderId = materialLot.getWorkOrderId();
                    String grade = materialLot.getGrade();
                    String boxId = materialLot.getMaterialLotId();
                    GCWorkorderRelation workorderRelation = findWorkorderRelationByBoxIdOrWorkOrderIdOrGrade(boxId, workOrderId, grade);
                    if(workorderRelation != null){
                        MaterialLotAction materialLotAction = new MaterialLotAction();
                        materialLotAction.setActionReason(workorderRelation.getHoldReason());
                        materialLotAction.setTransUser(workorderRelation.getUpdatedBy());
                        mmsService.holdMaterialLot(materialLot, materialLotAction);
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getLotId())){
                        mmsService.validateFutureHoldByReceiveTypeAndProductAreaAndLotId(MaterialLot.WLT_PACKAGED_LOT_SCAN, materialLot.getReserved49(), materialLot.getLotId());
                    }
                }
            };

            packedLotList = packedLotList.stream().filter(packedLot -> packedLot.getPackedLotRrn() != null).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(packedLotList)){
                mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
            }

            //TODO 如果后续有ERP dblink问题。此处需要考虑批量插入。JPA提供的saveAll本质也是for循环调用save。
            if(CollectionUtils.isNotEmpty(erpMos)){
                erpMoRepository.saveAll(erpMos);
            }
            if(CollectionUtils.isNotEmpty(erpMoaList)){
                erpMoaRepository.saveAll(erpMoaList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过boxId或workOrderId或grade来查询预约hold的条件
     * @param boxId
     * @param workOrderId
     * @param grade
     * @return
     * @throws ClientException
     */
    private GCWorkorderRelation findWorkorderRelationByBoxIdOrWorkOrderIdOrGrade(String boxId, String workOrderId, String grade) throws ClientException {
        try {
            GCWorkorderRelation workorderRelation = workorderRelationRepository.findByBoxIdAndWorkOrderIdIsNullAndGradeIsNull(boxId);
            if(workorderRelation == null){
                workorderRelation = workorderRelationRepository.findByWorkOrderIdAndGradeIsNullAndBoxIdIsNull(workOrderId);
            }
            if(workorderRelation == null){
                workorderRelation = workorderRelationRepository.findByBoxIdAndGradeAndWorkOrderIdIsNull(boxId, grade);
            }
            if(workorderRelation == null){
                workorderRelation = workorderRelationRepository.findByWorkOrderIdAndBoxIdAndGradeIsNull(workOrderId, boxId);
            }
            if(workorderRelation == null){
                workorderRelation = workorderRelationRepository.findByWorkOrderIdAndGradeAndBoxIdIsNull(workOrderId, grade);
            }
            if(workorderRelation == null) {
                workorderRelation = workorderRelationRepository.findByBoxIdAndWorkOrderIdAndGrade(boxId, workOrderId, grade);
            }
            return workorderRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次转仓库操作，并修改保税属性
     * @param materialLot
     * @param bondedProperty
     * @param warehouse
     */
    private void materialLotTransferWareHouse(MaterialLot materialLot, String bondedProperty, Warehouse warehouse) throws ClientException{
        try{
            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
            MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
            materialLotInventory.setWarehouse(warehouse);
            materialLotInventoryRepository.saveAndFlush(materialLotInventory);

            materialLot.setReserved6(bondedProperty);
            materialLot.setReserved13(warehouse.getObjectRrn().toString());
            materialLot.setReserved14(MesPackedLot.STORAGE_ID_SH);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setReserved4(bondedProperty);
                materialLotUnit.setReserved13(warehouse.getObjectRrn().toString());
                materialLotUnit.setReserved14(MesPackedLot.STORAGE_ID_SH);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory =  (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_TRANSFER_WAREHOUSE);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_TRANSFER_WAREHOUSE);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void mLotSetWaferSourceAndReserved7(Map<String,Object> otherReceiveProps, String productCategory, MesPackedLot mesPackedLot) throws ClientException{
        try {
            String inFlag = mesPackedLot.getInFlag();
            String type = mesPackedLot.getType();
            String productId = mesPackedLot.getProductId();

            if(MaterialLot.PRODUCT_CATEGORY.equals(productCategory)){
                otherReceiveProps.put("reserved50", MaterialLot.COM_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", productCategory);
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory)){
                if(productId.endsWith("-2.5")){
                    otherReceiveProps.put("reserved50", MaterialLot.WLT_IN_FLAG_WAFER_SOURCE);
                    otherReceiveProps.put("reserved7", MaterialLot.WLT_IN_FLAG_PRODUCTCATEGORY);
                } else if(productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.WLT_WAFER_SOURCE);
                    otherReceiveProps.put("reserved7", productCategory);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLot.CP_IN_FLAG_PRODUCTCATEGORY);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-1") || productId.endsWith("-2.5")){
                    otherReceiveProps.put("reserved50", MaterialLot.LCP_IN_FLAG_WAFER_SOURCE);
                } else if(productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.LCP_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLot.CP_IN_FLAG_PRODUCTCATEGORY);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-0") || productId.endsWith("-1") || productId.endsWith("-1.3") || productId.endsWith("-1.5") || productId.endsWith("-1.6") || productId.endsWith("-2")){
                    otherReceiveProps.put("reserved50", MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);
                } else if(productId.endsWith("-1.1") || productId.endsWith("-1.4") || productId.endsWith("-2.1") ||
                        productId.endsWith("-2.5") || productId.endsWith("-2.55") || productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.SCP_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_FT.equals(productCategory)){
                otherReceiveProps.put("reserved7", productCategory);
                if(StringUtils.isNullOrEmpty(mesPackedLot.getWaferSource()) || MaterialLot.SENSOR_WAFER_SOURCE.equals(mesPackedLot.getWaferSource())){
                    otherReceiveProps.put("reserved50", MaterialLot.FT_WAFER_SOURCE);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.WLFT_WAFER_SOURCE);
                }
            } else if(MaterialLotUnit.PRODUCT_CATEGORY_RW.equals(productCategory) && MaterialLotUnit.BOX_TYPE.equals(type)){
                otherReceiveProps.put("reserved50", MaterialLot.RW_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", MaterialLotUnit.PRODUCT_CATEGORY_FT_COB);
            }  else if(MaterialLotUnit.PRODUCT_CATEGORY_FT_COB.equals(productCategory)){
                otherReceiveProps.put("reserved50", MaterialLot.COB_WAFER_SOURCE);
                otherReceiveProps.put("reserved7", productCategory);
            }else if(MaterialLotUnit.PRODUCT_CATEGORY_SOC.equals(productCategory)){
                if(MaterialLot.MM_PACKED_LOTIN_FLAG.equals(inFlag)){
                    otherReceiveProps.put("reserved7", MaterialLotUnit.PRODUCT_CLASSIFY_SOC);
                } else {
                    otherReceiveProps.put("reserved7", productCategory);
                }
                if(productId.endsWith("-2.6")){
                    otherReceiveProps.put("reserved50", MaterialLot.SOC_WAFER_SOURCE_MEASURE);
                } else if(productId.endsWith("-2.5") || productId.endsWith("-2.55") ){
                    otherReceiveProps.put("reserved50", MaterialLot.SOC_WAFER_SOURCE_UNMEASUREN);
                } else {
                    otherReceiveProps.put("reserved50", MaterialLot.ERROR_WAFER_SOUCE);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次绑定中转箱
     * @throws ClientException
     */
    public void bindRelaxBox(List<MaterialLot> materialLots, String relaxBoxId) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(relaxBoxId);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_BIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    /**
     * 物料批次取消绑定中转箱
     * @throws ClientException
     */
    public void unbindRelaxBox(List<MaterialLot> materialLots) throws ClientException{
        try {
            materialLots.forEach(materialLot -> {
                materialLot.setReserved8(StringUtils.EMPTY);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UNBIND_RELAY_BOX);
                materialLotHistoryRepository.save(history);
            });
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    public void judgeMaterialLot(MaterialLot materialLot, String judgeGrade, String judgeCode, List<StockOutCheck> ngCheckList) {
        materialLot.setReserved9(judgeGrade);
        materialLot.setReserved10(judgeCode);
        materialLotRepository.save(materialLot);
        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_JUDGE);
        materialLotHistoryRepository.save(history);

        if (CollectionUtils.isNotEmpty(ngCheckList)) {
            // 保存每个项目的判定结果 GC要求只记录NG的判定历史即可
            MaterialLot finalMaterialLot = materialLot;
            ngCheckList.forEach(stockOutCheck -> {
                MaterialLotJudgeHis materialLotJudgeHis = new MaterialLotJudgeHis();
                materialLotJudgeHis.setMaterialLotRrn(finalMaterialLot.getObjectRrn());
                materialLotJudgeHis.setMaterialLotId(finalMaterialLot.getMaterialLotId());
                materialLotJudgeHis.setItemName(stockOutCheck.getName());
                materialLotJudgeHis.setResult(stockOutCheck.getResult());
                materialLotJudgeHis.setTransType(TRANS_TYPE_JUDGE);
                materialLotJudgeHis.setHisSeq(ThreadLocalContext.getTransRrn());
                materialLotJudgeHisRepository.save(materialLotJudgeHis);
            });
        }
    }

    /**
     * 装箱判定
     * @throws ClientException
     */
    public void judgePackedMaterialLot(List<MaterialLot> materialLots,List<StockOutCheck> checkList) throws ClientException{
        try {
            String judgeGrade = StockOutCheck.RESULT_PASS;
            String judgeCode = StockOutCheck.RESULT_PASS;
            List<StockOutCheck> ngCheckList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(checkList)) {
                ngCheckList = checkList.stream().filter(checkItem -> StockOutCheck.RESULT_NG.equals(checkItem.getResult())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(ngCheckList)) {
                judgeGrade = StockOutCheck.RESULT_NG;
                judgeCode = StockOutCheck.RESULT_NG;
            }

            //WLT对未装箱的LOT也要做LOT装箱检验，ParentMaterialLotId可能为空(只能出现整箱或者单个LOT)
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for (String packageMLotId : packedLotMap.keySet())  {
                    MaterialLot parentMLot = mmsService.getMLotByMLotId(packageMLotId, true);
                    judgeMaterialLot(parentMLot, judgeGrade, judgeCode, ngCheckList);

                    for (MaterialLot packagedMLot : packedLotMap.get(packageMLotId)) {
                        judgeMaterialLot(packagedMLot, judgeGrade, judgeCode, ngCheckList);
                    }
                }
            } else {
                for (MaterialLot materialLot : materialLots) {
                    judgeMaterialLot(materialLot, judgeGrade, judgeCode, ngCheckList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }

    }

    @Override
    public void cancelCheckMaterialLot(List<MaterialLot> materialLots, String cancelReason) throws ClientException {
        for (MaterialLot materialLot : materialLots) {
            String packageType = materialLot.getPackageType();
            if (StringUtils.isNullOrEmpty(packageType)) {
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_UN_CHECK, MaterialStatus.STATUS_IN);
            } else {
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_UN_CHECK, MaterialStatus.STATUS_WAIT);
                List<MaterialLot> materialLotList = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                for (MaterialLot lot : materialLotList) {
                    lot.setReserved9(StringUtils.EMPTY);
                    lot.setReserved10(StringUtils.EMPTY);
                    materialLotRepository.save(lot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(lot, MaterialLotHistory.TRANS_TYPE_CANCEL_CHECK);
                    history.setActionReason(cancelReason);
                    materialLotHistoryRepository.save(history);
                }
            }
            materialLot.setReserved9(StringUtils.EMPTY);
            materialLot.setReserved10(StringUtils.EMPTY);

            materialLotRepository.save(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CANCEL_CHECK);
            history.setActionReason(cancelReason);
            materialLotHistoryRepository.save(history);
        }
    }

    /**
     * 获取能和箱信息匹配的订单信息
     * @return
     * @throws ClientException
     */
    public List<DocumentLine> validationAndGetDocumentLineList(List<DocumentLine> documentLines, MaterialLot materialLot) throws ClientException {
        List<DocumentLine> documentLineList = new ArrayList<>();
        for (DocumentLine documentLine : documentLines) {
            String materialSecondCode = materialLot.getReserved1() + materialLot.getGrade();
            if(documentLine.getMaterialName().equals(materialLot.getMaterialName()) && documentLine.getReserved2().equals(materialSecondCode) &&
                    documentLine.getReserved3().equals(materialLot.getGrade()) && documentLine.getReserved7().equals(materialLot.getReserved6()) ){
                documentLineList.add(documentLine);
            }
        }
        return  documentLineList;
    }

    /**
     * 格科同步MES的产品号、描述、单位
     */
    public void asyncMesProduct() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Product product = new Product();
            List<Map> materialList = findEntityMapListByQueryName(Material.QUERY_PRODUCTINFO,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialList)){
                for (Map<String, String> m :materialList)  {
                    String productId = m.get("INSTANCE_ID");
                    String productDesc = m.get("INSTANCE_DESC");
                    String storeUom = m.get("STORE_UOM");
                    Integer packageTotalQty = 0;
                    if(m.get("BOX_STANDARD_QTY") != null && m.get("PACKAGE_STANDARD_QTY") != null){
                        Integer boxStandardQty = Integer.parseInt(String.valueOf((Object)m.get("BOX_STANDARD_QTY")));
                        Integer packageStandardQty = Integer.parseInt(String.valueOf((Object)m.get("PACKAGE_STANDARD_QTY")));
                        packageTotalQty = boxStandardQty * packageStandardQty;
                    }

                    product = mmsService.getProductByName(productId);
                    if(product == null){
                        product = new Product();
                        product.setName(productId);
                        product.setDescription(productDesc);
                        product.setStoreUom(storeUom);
                        product.setMaterialCategory(Material.TYPE_PRODUCT);
                        product.setMaterialType(Material.TYPE_PRODUCT);
                        product.setReserved1(packageTotalQty.toString());
                        product = mmsService.saveProduct(product);

                        List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
                        if (CollectionUtils.isNotEmpty(statusModels)) {
                            product.setStatusModelRrn(statusModels.get(0).getObjectRrn());
                        } else {
                            throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
                        }
                        productRepository.save(product);
                    } else {
                        product.setMaterialCategory(Material.TYPE_PRODUCT);
                        product.setMaterialType(Material.TYPE_PRODUCT);
                        product.setReserved1(packageTotalQty.toString());
                        productRepository.saveAndFlush(product);
                    }
                }
            }
            asyncMaterialName();
            asyncMesWarehouseProductType();
            asyncMesProductPrintModelId();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品入库型号
     */
    public void asyncMesWarehouseProductType() {
        try {
            Product product = new Product();
            List<Map> warehouseModelList = findEntityMapListByQueryName(Material.QUERY_WAREHOUSE_PRODUCT_MODEL,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(warehouseModelList)){
                for (Map<String, String> m :warehouseModelList)  {
                    String productId = m.get("IN_STORAGE_MODEL_ID");
                    product = mmsService.getProductByName(productId);
                    if(product == null){
                        saveProductAndSetStatusModelRrn(productId);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES金线、IRA型号、TAPE、BLADE
     */
    public void asyncMesMaterialModel() throws ClientException{
        try {
            RawMaterial material = new RawMaterial();
            List<Map> materialModelList = findEntityMapListByQueryName(Material.QUERY_MATERIAL_MODEL,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialModelList)){
                for(Map<String, String>  map : materialModelList){
                    String materialId = map.get("INSTANCE_ID");
                    String materialType = map.get("OBJECT");
                    String materialDesc = map.get("INSTANCE_DESC");
                    String storeuUom = map.get("STORE_UOM");

                    material = mmsService.getRawMaterialByName(materialId);
                    if(material == null){
                        material = new RawMaterial();
                        material.setName(materialId);
                        material.setDescription(materialDesc);
                        material.setStoreUom(storeuUom);
                        material.setMaterialCategory(Material.TYPE_MATERIAL);
                        if(Material.MATERIAL_TYPE_IR.equals(materialType)){
                            material.setMaterialType(Material.MATERIAL_TYPE_IRA);
                        } else if(Material.MATERIAL_TYPE_WIRE.equals(materialType)){
                            material.setMaterialType(Material.MATERIAL_TYPE_GOLD);
                        } else if(Material.MATERIAL_TYPE_TAPE.equals(materialType)) {
                            material.setMaterialType(Material.MATERIAL_TYPE_TAPE);
                        } else if(Material.MATERIAL_TYPE_BLADE.equals(materialType)) {
                            material.setMaterialType(Material.MATERIAL_TYPE_BLADE);
                        }

                        mmsService.createRawMaterial(material);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步MES胶水型号
     */
    public void asyncMesGlueType() throws ClientException{
        try {
            RawMaterial material = new RawMaterial();
            List<Map> glueTypeList = findEntityMapListByQueryName(Material.QUERY_GLUE_TYPE,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(glueTypeList)){
                for(Map<String, String>  map : glueTypeList) {
                    String glueTypeName = map.get("NAME");
                    String glueTypeDesc = map.get("DESCRIPTION");

                    material = mmsService.getRawMaterialByName(glueTypeName);
                    if(material == null){
                        material = new RawMaterial();
                        material.setName(glueTypeName);
                        material.setDescription(glueTypeDesc);
                        material.setMaterialCategory(Material.TYPE_MATERIAL);
                        material.setMaterialType(Material.MATERIAL_TYPE_GLUE);

                        mmsService.createRawMaterial(material);
                    }

                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品打印型号
     */
    public void asyncMesProductPrintModelId() {
        try {
            Product product = new Product();
            List<Map> productPrintModelList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_PRINT_MODELID,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(productPrintModelList)){
                for (Map<String, String> m :productPrintModelList)  {
                    String prodcutPrintModelId = m.get("PRINT_PRODUCT_MODEL");
                    product = mmsService.getProductByName(prodcutPrintModelId);
                    if(product == null) {
                        saveProductAndSetStatusModelRrn(prodcutPrintModelId);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建产品号
     * @param name
     * @throws ClientException
     */
    public Material saveProductAndSetStatusModelRrn(String name) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Product product = new Product();
            product.setName(name);
            product.setMaterialCategory(Material.TYPE_PRODUCT);
            product.setMaterialType(Material.TYPE_PRODUCT);
            product = mmsService.saveProduct(product);

            List<MaterialStatusModel> statusModels = materialStatusModelRepository.findByNameAndOrgRrn(Material.DEFAULT_STATUS_MODEL, sc.getOrgRrn());
            if (CollectionUtils.isNotEmpty(statusModels)) {
                product.setStatusModelRrn(statusModels.get(0).getObjectRrn());
            } else {
                throw new ClientException(StatusMachineExceptions.STATUS_MODEL_IS_NOT_EXIST);
            }
            Material material = productRepository.save(product);
            return material;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步产品号或者晶圆型号至GC_PRODUCT_NAME表中
     * @throws ClientException
     */
    public void asyncMaterialName() throws ClientException{
        try {
            List<String> materialNameList = materialRepository.findNameByMaterialCategory(Lists.newArrayList(Material.TYPE_PRODUCT, Material.MATERIAL_TYPE));
            for(String materialName : materialNameList){
                if(!StringUtils.isNullOrEmpty(materialName)) {
                    List<MaterialNameInfo> materialNameInfoList = materialNameInfoRepository.findByNameAndOrgRrn(materialName, ThreadLocalContext.getOrgRrn());
                    if (CollectionUtils.isEmpty(materialNameInfoList)) {
                        MaterialNameInfo materialNameInfo = new MaterialNameInfo();
                        materialNameInfo.setName(materialName);
                        materialNameInfoRepository.saveAndFlush(materialNameInfo);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的晶圆型号、描述、单位
     */
    public void asyncMesWaferType() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            RawMaterial rawMaterial = new RawMaterial();
            List<Map> materialList = findEntityMapListByQueryName(Material.QUERY_WAFERTYPEINFO,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(materialList)){
                for (Map<String, String> m :materialList)  {
                    String productId = m.get("INSTANCE_ID");
                    String productDesc = m.get("INSTANCE_DESC");
                    String storeUom = m.get("STORE_UOM");

                    rawMaterial = mmsService.getRawMaterialByName(productId);
                    if(rawMaterial == null){
                        rawMaterial = new RawMaterial();
                        rawMaterial.setName(productId);
                        rawMaterial.setDescription(productDesc);
                        rawMaterial.setStoreUom(storeUom);
                        mmsService.createRawMaterial(rawMaterial);
                    } else {
                        rawMaterial.setMaterialCategory(Material.TYPE_WAFER);
                        rawMaterial.setMaterialType(Material.TYPE_WAFER);
                        rawMaterialRepository.saveAndFlush(rawMaterial);
                    }
                }
            }
            asyncMesMaterialModel();
            asyncMesGlueType();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的晶圆型号、描述、单位
     */
    public void asyncMesProductAndSubcode() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Material material = new Material();
            List<GCProductSubcode> productSubcodes = Lists.newArrayList();
            List<Map> productSubcodeList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_SUBCODE,null,0,999,"","");
            if(CollectionUtils.isNotEmpty(productSubcodeList)){
                for(Map<String, String> m : productSubcodeList){
                    String productId = m.get("MODEL_ID");
                    String subcode = m.get("SUB_CODE");
                    String modelClass = m.get("MODEL_CLASS");
                    if(modelClass.equals(Material.CLASS_PRODUCT)){
                        material = mmsService.getProductByName(productId);
                    } else {
                        material = mmsService.getRawMaterialByName(productId);
                    }
                    if(material != null){
                        GCProductSubcode productSubcode = gcProductSubcodeSetRepository.findByProductIdAndSubcode(productId, subcode);
                        if(productSubcode == null){
                            productSubcode = new GCProductSubcode();
                            productSubcode.setProductId(productId);
                            productSubcode.setSubcode(subcode);
                            productSubcode = gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
                        }
                        productSubcodes.add(productSubcode);
                    }
                }

                //删除MES中不存在的产品二级代码信息
                List<GCProductSubcode> gcProductSubcodes = gcProductSubcodeSetRepository.findAll();
                gcProductSubcodes.removeAll(productSubcodes);
                if(CollectionUtils.isNotEmpty(gcProductSubcodes)){
                    for(GCProductSubcode productSubcode : gcProductSubcodes){
                        gcProductSubcodeSetRepository.deleteById(productSubcode.getObjectRrn());
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科同步MES的产品型号转换信息
     */
    public void asyncMesProductModelConversion() throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            sc.buildTransInfo();
            Material material = new Material();
            List<Map> productModelList = findEntityMapListByQueryName(Material.QUERY_PRODUCT_MODEL_CONVERSION,null,0,999,"","");
            List<GCProductModelConversion> productModelConversionList = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(productModelList)){
                for(Map<String, String> m : productModelList){
                    String productId = m.get("MODEL_ID");
                    String conversionModelId = m.get("CONVERSION_MODEL_ID");
                    String modelCategory = m.get("MODEL_CATEGORY");
                    material = mmsService.getProductByName(productId);
                    if(material == null){
                        material = mmsService.getRawMaterialByName(productId);
                    }
                    if(material != null){
                        GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductId(productId);
                        if(productModelConversion == null){
                            productModelConversion = new GCProductModelConversion();
                            productModelConversion.setProductId(productId);
                            productModelConversion.setConversionModelId(conversionModelId);
                            productModelConversion.setModelCategory(modelCategory);
                            productModelConversion = gcProductModelConversionRepository.saveAndFlush(productModelConversion);
                            productModelConversionList.add(productModelConversion);
                        } else {
                            GCProductModelConversion oldProductModelConversion = gcProductModelConversionRepository.findByProductIdAndConversionModelId(productId, conversionModelId);
                            if(oldProductModelConversion == null){
                                oldProductModelConversion = new GCProductModelConversion();
                                oldProductModelConversion.setProductId(productId);
                                oldProductModelConversion.setConversionModelId(conversionModelId);
                                oldProductModelConversion.setModelCategory(modelCategory);
                                oldProductModelConversion = gcProductModelConversionRepository.saveAndFlush(oldProductModelConversion);
                            }
                            productModelConversionList.add(oldProductModelConversion);
                        }
                    }
                }
            }
            //MES中删除的产品信息，WMS中也需要删除
            List<GCProductModelConversion> allProductModelConversion = gcProductModelConversionRepository.findAll();
            Map<String, GCProductModelConversion> productModelConversionMap = productModelConversionList.stream().collect(Collectors.toMap(GCProductModelConversion :: getProductId, Function.identity()));
            for(GCProductModelConversion productModelConversion : allProductModelConversion){
                if(!productModelConversionMap.containsKey(productModelConversion.getProductId())){
                    gcProductModelConversionRepository.deleteById(productModelConversion.getObjectRrn());
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 定时同步mms_material_lot中的产品、等级、二级代码信息
     * @throws ClientException
     */
    public void asyncProductGradeAndSubcode() throws ClientException {
        try {
            List<Product> productList = productRepository.findByMaterialType(Material.TYPE_PRODUCT);
            GCProductRelation productRelation = new GCProductRelation();
            for (Product product : productList){
                List<String> gradeList = materialLotRepository.getGradeByMaterialNameAndStatusCategory(product.getName(), MaterialLot.STATUS_FIN);
                for (String grade : gradeList){
                    productRelation = productRelationRepository.findByProductIdAndGradeSubcodeAndType(product.getName(), grade, GCProductRelation.GRADE_TYPE);
                    if(productRelation == null){
                        productRelation = new GCProductRelation();
                        productRelation.setProductId(product.getName());
                        productRelation.setGradeSubcode(grade);
                        productRelation.setType(GCProductRelation.GRADE_TYPE);
                        productRelationRepository.save(productRelation);
                    }
                }
                List<String> subCodeList = materialLotRepository.getSubcodeByMaterialNameAndStatusCategory(product.getName(), MaterialLot.STATUS_FIN);
                for (String subcode : subCodeList){
                    productRelation = productRelationRepository.findByProductIdAndGradeSubcodeAndType(product.getName(), subcode, GCProductRelation.SUBCODE_TYPE);
                    if(productRelation == null){
                        productRelation = new GCProductRelation();
                        productRelation.setProductId(product.getName());
                        productRelation.setGradeSubcode(subcode);
                        productRelation.setType(GCProductRelation.SUBCODE_TYPE);
                        productRelationRepository.save(productRelation);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 定时同步PO表中的供应商名称
     * @throws ClientException
     */
    public void asyncPoSupplier() throws ClientException{
        try {
            List<String> supplierNameList = outSourcePoRepository.getSupplierName();
            for(String sulipperName : supplierNameList){
                Supplier supplier = supplierRepository.getByNameAndType(sulipperName, Supplier.TYPE_PO_SUPPLER);
                if(supplier == null){
                    supplier = new Supplier();
                    supplier.setName(sulipperName);
                    supplier.setType(Supplier.TYPE_PO_SUPPLER);
                    supplierRepository.save(supplier);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public List<Map> findEntityMapListByQueryName(String queryName, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            NBQuery nbQuery = queryRepository.findByName(queryName);
            if (nbQuery == null) {
                throw new ClientParameterException(NewbiestException.COMMON_QUERY_IS_NOT_EXIST, queryName);
            }
            return findEntityMapListByQueryText(nbQuery.getQueryText(), paramMap, firstResult, maxResult, whereClause, orderByClause);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public List<Map> findEntityMapListByQueryText(String queryText, Map<String, Object> paramMap, int firstResult, int maxResult, String whereClause, String orderByClause) throws ClientException {
        try {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT * FROM (");
            sqlBuffer.append(queryText);
            sqlBuffer.append(")");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                sqlBuffer.append(" WHERE ");
                sqlBuffer.append(whereClause);
            }
            if (!StringUtils.isNullOrEmpty(orderByClause)) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(orderByClause);
            }

            Query query = em.createNativeQuery(sqlBuffer.toString());
            if (firstResult > 0) {
                query.setFirstResult(firstResult);
            }

            if (paramMap != null) {
                for (String key : paramMap.keySet()) {
                    query.setParameter(key, paramMap.get(key));
                }
            }
            query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return query.getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取需要称重的箱信息
     * @return
     * @throws ClientException
     */
    public MaterialLot getWaitWeightMaterialLot(String materialLotId, Long tableRrn) throws ClientException {
        try {
            MaterialLot materialLot = getMaterialLotByTableRrnAndMaterialLotIdOrLotId(tableRrn, materialLotId);
            if(materialLot.getObjectRrn() == null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
            } else if(MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7()) && !StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                //获取物料批次的理论重量
                materialLot = queryMaterialLotTheoryWeightAndFolatValue(materialLot);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料批次号和tableRrn获取物料信息
     * @param materialLotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    private List<MaterialLot> queryMaterialLotByTableRrnAndMaterialLotId(Long tableRrn, String materialLotId) throws ClientException{
        try {
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" materialLotId = ");
            clauseBuffer.append("'" + materialLotId + "'");
            if (!StringUtils.isNullOrEmpty(whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(whereClause);
            }
            whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            return materialLots;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取箱号的理论重量及浮点值
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private MaterialLot queryMaterialLotTheoryWeightAndFolatValue(MaterialLot materialLot) throws ClientException{
        try {
            GCProductWeightRelation productWeightRelation = new GCProductWeightRelation();
            BigDecimal totalWeight = BigDecimal.ZERO;
            String materialName = materialLot.getMaterialName();
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                List<GCProductWeightRelation> productWeightRelations = productWeightRelationRepository.findByProductId(materialName);
                if(CollectionUtils.isNotEmpty(productWeightRelations)){
                    BigDecimal minPackageQty = productWeightRelations.get(0).getMinPackedQty();
                    BigDecimal maxPackageQty = productWeightRelations.get(0).getMaxPackedQty();
                    //获取箱中真空包的总重量
                    BigDecimal vboxTotalWeight = getPackedDetialTotalWeight(minPackageQty, maxPackageQty, packageDetailLots);
                    if(vboxTotalWeight.compareTo(BigDecimal.ZERO) > 0){
                        //获取总重量的配置信息
                        productWeightRelation = validateAndGetProductRelationByPackedLotDetial(packageDetailLots, minPackageQty, maxPackageQty);
                        if(productWeightRelation != null && productWeightRelation.getPackageQty().compareTo(BigDecimal.ZERO) > 0){
                            totalWeight = totalWeight.add(vboxTotalWeight);
                            BigDecimal packageChipWeight = productWeightRelation.getPackageChipWeight();//整包芯片数量
                            BigDecimal packageNumber = productWeightRelation.getPackageQty();//整包颗数
                            BigDecimal floatValue = productWeightRelation.getFloatQty();//整包颗数
                            totalWeight = productWeightRelation.getBoxWeight().add(totalWeight).add(packageChipWeight.divide(packageNumber, 6, BigDecimal.ROUND_HALF_UP).multiply(materialLot.getCurrentQty()));
                            materialLot.setTheoryWeight(totalWeight.setScale(4, BigDecimal.ROUND_HALF_UP));
                            materialLot.setFloatValue(floatValue);
                        }
                    }
                }
            }
            return  materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据真空包数量确定箱子总重量的配置规则
     * @param packageDetailLots
     * @return
     * @throws ClientException
     */
    private GCProductWeightRelation validateAndGetProductRelationByPackedLotDetial(List<MaterialLot> packageDetailLots, BigDecimal minPackageQty, BigDecimal maxPackageQty) throws  ClientException{
        try {
            String materialName = packageDetailLots.get(0).getMaterialName();
            GCProductWeightRelation productWeightRelation = productWeightRelationRepository.findByProductIdAndPackageQty(materialName, minPackageQty);
            for(MaterialLot materialLot: packageDetailLots){
                if(materialLot.getCurrentQty().compareTo(minPackageQty) > 0){
                    productWeightRelation = productWeightRelationRepository.findByProductIdAndPackageQty(materialName, maxPackageQty);
                    break;
                }
            }
            return productWeightRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取箱中真空包的总重量
     * @param minPackageQty
     * @param packageDetailLots
     * @return
     * @throws ClientException
     */
    private BigDecimal getPackedDetialTotalWeight(BigDecimal minPackageQty, BigDecimal maxPackageQty, List<MaterialLot> packageDetailLots) throws ClientException{
        try {
            boolean flag = false;
            //每包真空包重量=盘重量*（每包实际颗数/每盘芯片数+1）+盖重量*（每包实际颗数/每盘芯片数/盘数+1）+管夹重量*（每包实际颗数/每盘芯片数/盘数）
            BigDecimal totalWeight = BigDecimal.ZERO;
            GCProductWeightRelation relation = new GCProductWeightRelation();
            for(MaterialLot materialLot : packageDetailLots){
                BigDecimal currentQty = materialLot.getCurrentQty();
                if(currentQty.compareTo(minPackageQty) <= 0 ){
                    relation = productWeightRelationRepository.findByProductIdAndPackageQty(materialLot.getMaterialName(), minPackageQty);
                } else {
                    relation = productWeightRelationRepository.findByProductIdAndPackageQty(materialLot.getMaterialName(), maxPackageQty);
                }
                if(relation == null){
                    flag = true;
                    break;
                } else if(BigDecimal.ZERO.compareTo(relation.getDiscChipQty()) == 0 || BigDecimal.ZERO.compareTo(relation.getDiscQty()) == 0){
                    flag = true;
                    break;
                }
                BigDecimal discWeight = relation.getDiscWeight();//盘重量
                BigDecimal coverWeight = relation.getCoverWeight();//盖重量
                BigDecimal clipWeight = relation.getClipWeight();//管夹重量
                BigDecimal discQty = relation.getDiscQty();//盘数
                BigDecimal chipWeight = currentQty.divide(relation.getDiscChipQty(), 6, BigDecimal.ROUND_HALF_UP);//每包平均芯片重量
                BigDecimal vboxWeight = discWeight.multiply(chipWeight.add(BigDecimal.ONE)).add(coverWeight.multiply(chipWeight.divide(discQty, 6, BigDecimal.ROUND_HALF_UP).add(BigDecimal.ONE))).add(clipWeight.multiply(chipWeight.divide(discQty, 6, BigDecimal.ROUND_HALF_UP)));
                totalWeight = totalWeight.add(vboxWeight);
            }
            if(flag){
                totalWeight = BigDecimal.ZERO;
            }
            return totalWeight;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次称重
     * @param weightModels
     * @throws ClientException
     */
    public void materialLotWeight(List<WeightModel> weightModels) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            //将单箱称重与多箱称重区分开
            List<WeightModel> boxsWeightList = weightModels.stream().filter(weightModel -> !StringUtils.isNullOrEmpty(weightModel.getBoxsWeightFlag())).collect(Collectors.toList());
            List<WeightModel> boxWeightList = weightModels.stream().filter(weightModel -> StringUtils.isNullOrEmpty(weightModel.getBoxsWeightFlag())).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(boxsWeightList)){
                //多箱称重需要按照多箱扫描序号赋值多箱称重事务号
                boxsWeightList = boxsWeightList.stream().sorted(Comparator.comparing(WeightModel::getBoxsScanSeq)).collect(Collectors.toList());
                Map<Long, List<WeightModel>> weightModelMap =  boxsWeightList.stream().collect(Collectors.groupingBy(WeightModel :: getBoxsScanSeq));
                for(Long boxsScanSeq : weightModelMap.keySet()){
                    String transId = generatorMLotsTransId(MaterialLot.GENERATOR_MATERIAL_LOT_WEIGHT_RULE);
                    List<WeightModel>  boxsWeightModelList = weightModelMap.get(boxsScanSeq);
                    for(WeightModel weightModel : boxsWeightModelList){
                        MaterialLot materialLot = mmsService.getMLotByMLotId(weightModel.getMaterialLotId());
                        materialLot.setReserved19(weightModel.getWeight());
                        materialLot.setReserved20(transId);
                        materialLot.setScanSeq(weightModel.getScanSeq());
                        materialLotList.add(materialLot);
                    }
                }
            }
            if(CollectionUtils.isNotEmpty(boxWeightList)){
                for(WeightModel weightModel : boxWeightList){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(weightModel.getMaterialLotId());
                    materialLot.setScanSeq(weightModel.getScanSeq());
                    materialLot.setReserved19(weightModel.getWeight());
                    materialLot.setReserved20(StringUtils.EMPTY);
                    materialLotList.add(materialLot);
                }
            }

            materialLotList = materialLotList.stream().sorted(Comparator.comparing(MaterialLot::getScanSeq)).collect(Collectors.toList());

            //称重记录
            for (MaterialLot materialLot : materialLotList) {
                String weightSeq = generatorMLotsTransId(MaterialLot.GENERATOR_QRCODE_LABEL_WEIGHT_SEQ_RULE);
                materialLot.setWeightSeq(weightSeq);
                materialLotRepository.save(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_WEIGHT);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据ID生成规则设置的生成多箱称重事务号/来料导入编号
     * @return
     * @throws ClientException
     */
    public String generatorMLotsTransId(String ruleId) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(ruleId);
            String id = generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
            return id;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过对象和ID生成规则 来生成包号
     * @param object
     * @param ruleId
     * @return
     * @throws ClientException
     */
    private String generatorByObjectAndRule(Object object, String ruleId) throws ClientException {
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setObject(object);
            generatorContext.setRuleName(ruleId);
            return generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取到可以入库的批次
     * @param relayBoxId
     * @return
     */
    public List<MaterialLot> getWaitChangeStorageMaterialLotByRelayBoxId(String relayBoxId) throws ClientException {
        try {
            StringBuffer whereClause = new StringBuffer();
            whereClause.append(" reserved8 = '" + relayBoxId + "'");
            whereClause.append(" and reserved14 is not null ");
            whereClause.append(" and statusCategory <> 'Fin' ");

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause.toString(), "");
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void transferStorage(List<RelayBoxStockInModel> relayBoxStockInModels) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            Map<String, RelayBoxStockInModel> relayBoxStockInModelMap = relayBoxStockInModels.stream().collect(Collectors.toMap(RelayBoxStockInModel :: getMaterialLotId, Function.identity()));

            //1. 把箱批次和普通的物料批次区分出来
            List<MaterialLot> materialLots = relayBoxStockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());

            //3. 入库
            for (MaterialLot materialLot : materialLots) {
                RelayBoxStockInModel relayBoxStockInModel = relayBoxStockInModelMap.get(materialLot.getMaterialLotId());
                String storageId = relayBoxStockInModel.getStorageId();

                if (StringUtils.isNullOrEmpty(materialLot.getReserved13())) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAREHOUSE_IS_NULL, materialLot.getMaterialLotId());
                }
                MaterialLotAction action = new MaterialLotAction();
                action.setTargetWarehouseRrn(Long.parseLong(materialLot.getReserved13()));
                action.setTargetStorageId(storageId);
                action.setTransQty(materialLot.getCurrentQty());
                action.setTransCount(materialLot.getCurrentSubQty());
                materialLot.setReserved14(storageId);

                List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                // 如果为空就是做入库事件 如果不是空则做转库事件
                if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                    MaterialLotInventory materialLotInventory = materialLotInvList.get(0);
                    action.setFromWarehouseRrn(materialLotInventory.getWarehouseRrn());
                    action.setFromStorageRrn(materialLotInventory.getStorageRrn());
                    mmsService.transfer(materialLot, action);
                } else {
                    materialLot = mmsService.stockIn(materialLot, action);
                }
                materialLotRepository.save(materialLot);

                validatePackageLotAndUpdateStorage(materialLot, storageId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证出货的物料信息是否全部备货、
     *      如果是包装批次，要验证内部所有的物料批次是否都备货到了相同的DocLine以及备货备注是否一致
     *      验证扫描的物料批次基础信息是否一致
     * @param waitValidationLot 待验证的物料批次
     * @param validatedMLotActions 以验证过的物料批次动作
     */
    public void validationStockOutMaterialLot(MaterialLot waitValidationLot, List<MaterialLotAction> validatedMLotActions)throws ClientException{
        try {
            waitValidationLot = mmsService.getMLotByMLotId(waitValidationLot.getMaterialLotId(), true);
            if (waitValidationLot.getReservedQty().compareTo(waitValidationLot.getCurrentQty()) != 0) {
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_RESERVED_ALL, waitValidationLot.getMaterialLotId());
            }
            List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(waitValidationLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                Set reservedInfo = packageDetailLots.stream().map(mLot -> mLot.getReserved16() + StringUtils.SPLIT_CODE + mLot.getReserved18()).collect(Collectors.toSet());
                if (reservedInfo == null || reservedInfo.size() > 1) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_RESERVED_INFO_IS_NOT_SAME, packageDetailLots.get(0).getReserved16() + StringUtils.SPLIT_CODE + packageDetailLots.get(0).getReserved18());
                }
            }
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationLot.getMaterialName(), validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", waitValidationLot.getMaterialName(), validatedMLot.getMaterialName());
                }

                try {
                    Assert.assertEquals(waitValidationLot.getReserved1(), validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", waitValidationLot.getReserved1(),  validatedMLot.getReserved1());
                }

                try {
                    Assert.assertEquals(waitValidationLot.getGrade(), validatedMLot.getGrade());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "grade", waitValidationLot.getGrade(), validatedMLot.getGrade());
                }
                try {
                    Assert.assertEquals(waitValidationLot.getReserved6(), validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", waitValidationLot.getReserved6(), validatedMLot.getReserved6());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 格科要求扫描箱号的时候不满足条件的箱号信息显示表单的异常栏位信息，不做异常提示
     * 验证出货的物料信息是否全部备货、
     *      如果是包装批次，要验证内部所有的物料批次是否都备货到了相同的DocLine以及备货备注是否一致
     *      验证扫描的物料批次基础信息是否一致
     * @param waitValidationLot 待验证的物料批次
     * @param validatedMLotActions 以验证过的物料批次动作
     */
    public boolean validateStockOutMaterialLot(MaterialLot waitValidationLot, List<MaterialLotAction> validatedMLotActions)throws ClientException{
        try {
            boolean falg = true;
            waitValidationLot = mmsService.getMLotByMLotId(waitValidationLot.getMaterialLotId(), true);
            if (waitValidationLot.getReservedQty().compareTo(waitValidationLot.getCurrentQty()) != 0) {
                falg = false;
            }
            List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(waitValidationLot.getObjectRrn());
            if(CollectionUtils.isNotEmpty(packageDetailLots)){
                Set reservedInfo = packageDetailLots.stream().map(mLot -> mLot.getReserved16() + StringUtils.SPLIT_CODE + mLot.getReserved18()).collect(Collectors.toSet());
                if (reservedInfo == null || reservedInfo.size() > 1) {
                    falg = false;
                }
            }
            falg = validationMaterialLotInfo(waitValidationLot, validatedMLotActions, falg);

            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存来料信息
     * @param materialLotList
     * @param importType
     * @return
     */
    public String saveIncomingMaterialList(List<MaterialLot> materialLotList, String importType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            String importCode = "";
            if(importType.equals(MaterialLotUnit.SAMSUING_PACKING_LIST)){
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                for(MaterialLot materialLot: materialLotList){
                    materialLot.setReserved48(importCode);
                    List<MaterialLotUnit> materialLotUnitList = getMaterialLotUnitList(materialLot);
                    materialLotUnitList = materialLotUnitAssignEng(materialLotUnitList);
                    materialLotUnitService.createMLot(materialLotUnitList);
                }
            } else if (importType.equals(MaterialLotUnit.LCD_COG_FINISH_PRODUCT)){
                //来料导入模板LCD（COG成品-ECRETIVE）数据处理
                List<MaterialLot> zeroMLotList = materialLotList.stream().filter(materialLot -> materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) <= 0).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(zeroMLotList)){
                    throw new ClientParameterException(GcExceptions.THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO, zeroMLotList.get(0).getMaterialLotId());
                }
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.groupingBy(MaterialLot:: getParentMaterialLotId));
                Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
                for(String materialName : materialNameMap.keySet()){
                    List<MaterialLot> materialLots = materialNameMap.get(materialName);
                    for(MaterialLot materialLot : materialLots){
                        MaterialLot oldMaterialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                        if (oldMaterialLot != null) {
                            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLot.getMaterialLotId());
                        }
                        Material material = mmsService.getProductByName(materialLots.get(0).getMaterialName());
                        if (material == null) {
                            material = saveProductAndSetStatusModelRrn(materialName);
                        }
                        materialLot.setMaterial(material);
                        if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                            materialLot.setReserved2("N");
                        }
                        materialLot.setParentMaterialLotId(null);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(material.getStatusModelRrn());
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COG);
                        initMaterialLotStorageId(materialLot);
                        materialLot.setReserved48(importCode);
                        materialLot.setReserved49(MaterialLot.IMPORT_COG);
                        materialLot.setReserved50("17");
                        materialLot = materialLotRepository.saveAndFlush(materialLot);

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        history.setTransQty(materialLot.getCurrentQty());
                        materialLotHistoryRepository.save(history);
                    }
                }
                importMLotAutoPackage(packedMLotMap, MaterialLot.LCD_PACKCASE);
            } else if(importType.equals(MaterialLotUnit.SENSOR_RMA_GOOD_PRODUCT) || importType.equals(MaterialLotUnit.RMA_RETURN)
                    ||importType.equals(MaterialLotUnit.RMA_PURE) || importType.equals(MaterialLotUnit.WLT_RMA_GOOD_PRODUCT)){
                importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
                Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot:: getMaterialName));
                for(String materialName : materialLotMap.keySet()){
                    List<MaterialLot> materialLots = materialLotMap.get(materialName);
                    Material material = mmsService.getProductByName(materialName);
                    if (material == null) {
                        material = saveProductAndSetStatusModelRrn(materialName);
                    }
                    //删除已经存在的物料批次信息，重新导入
                    deleteRmaMaterialLotAndUnit(materialLots);
                    for(MaterialLot materialLot : materialLots){
                        if(materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) <= 0){
                            throw new ClientParameterException(GcExceptions.THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO, materialLot.getCurrentQty());
                        }
                        materialLot.setMaterial(material);
                        materialLot.setLotId(materialLot.getMaterialLotId());
                        materialLot.setReserved48(importCode);
                        materialLot.initialMaterialLot();
                        materialLot.setStatusModelRrn(material.getStatusModelRrn());
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        if(StringUtils.isNullOrEmpty(materialLot.getReserved35())){
                            materialLot.setReserved35("0");
                        }
                        initMaterialLotStorageId(materialLot);
                        if(MaterialLotUnit.SENSOR_RMA_GOOD_PRODUCT.equals(importType)){
                            materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
                            materialLot.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);
                            materialLot.setReserved49(MaterialLot.IMPORT_SENSOR);
                        } else if(MaterialLotUnit.WLT_RMA_GOOD_PRODUCT.equals(importType)){
                            materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
                            materialLot.setReserved50(MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE);
                            materialLot.setReserved49(MaterialLot.IMPORT_WLT);
                        } else if(MaterialLotUnit.RMA_RETURN.equals(importType)){
                            materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
                            materialLot.setReserved50("12");
                            materialLot.setReserved49(MaterialLot.IMPORT_RETURN);
                        } else if(MaterialLotUnit.RMA_PURE.equals(importType)){
                            materialLot.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_RMA);
                            materialLot.setReserved50("15");
                            materialLot.setReserved49(MaterialLot.IMPORT_CRMA);
                        }
                        MaterialLot oldMLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                        if(oldMLot != null){
                            materialLotRepository.delete(oldMLot);
                        }
                        materialLot = materialLotRepository.saveAndFlush(materialLot);
                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                        materialLotHistoryRepository.save(history);

                        if(!importType.equals(MaterialLotUnit.RMA_PURE)){
                            MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                            materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                            materialLotUnit.setLotId(materialLot.getMaterialLotId());
                            materialLotUnit.setReserved14(materialLot.getReserved14());
                            materialLotUnit.setState(MaterialStatus.STATUS_CREATE);
                            materialLotUnit.setMaterial(material);
                            materialLotUnit.setMaterialLot(materialLot);
                            materialLotUnit.setRmaMaterialLot(materialLot);
                            materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                            MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                            materialLotUnitHisRepository.save(materialLotUnitHistory);
                        }
                    }
                }
            }
            return importCode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料导入初始化库位号
     * @param materialLot
     * @throws ClientException
     */
    private void initMaterialLotStorageId(MaterialLot materialLot) throws ClientException{
        try {
            if(MaterialLot.LOCATION_SH.equals(materialLot.getReserved6())){
                materialLot.setReserved14(MaterialLotInventory.SH_DEFAULT_STORAGE_ID);
            } else if(MaterialLot.BONDED_PROPERTY_ZSH.equals(materialLot.getReserved6())){
                materialLot.setReserved14(MaterialLotInventory.ZSH_DEFAULT_STORAGE_ID);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料成品导入，自动包装
     * @param packedMLotMap
     * @throws ClientException
     */
    private void importMLotAutoPackage(Map<String,List<MaterialLot>> packedMLotMap, String packageType) throws ClientException{
        try {
            for (String parentMaterialLotId : packedMLotMap.keySet()) {
                MaterialLot oldMLot = mmsService.getMLotByMLotId(parentMaterialLotId);
                if (oldMLot != null) {
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, parentMaterialLotId);
                }
                List<MaterialLot> materialLots = packedMLotMap.get(parentMaterialLotId);
                Set<String> vboxIdList = materialLots.stream().map(MaterialLot :: getMaterialLotId).collect(Collectors.toSet());
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (String vboxId : vboxIdList) {
                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    MaterialLot materialLot = mmsService.getMLotByMLotId(vboxId);
                    materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotAction.setTransQty(materialLot.getCurrentQty());
                    materialLotAction.setBoxStatusUseFlag("1");
                    materialLotActions.add(materialLotAction);
                }
                packageService.packageMLots(materialLotActions, parentMaterialLotId, packageType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证RMA导入的物料批次是否已经存在、Hold、装箱
     * @param materialLotList
     * @return
     * @throws ClientException
     */
    @Override
    public boolean validateRmaImportMaterialLot(List<MaterialLot> materialLotList) throws ClientException {
        try {
            boolean importFlag = false;
            for(MaterialLot materialLot : materialLotList){
                materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                if(materialLot != null){
                    if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_ALREADY_HOLD, materialLot.getMaterialLotId());
                    } else if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                        throw new ClientParameterException(MmsException.MATERIALLOT_HAS_BEEN_PACKED, materialLot.getMaterialLotId());
                    } else {
                        importFlag = true;
                    }
                }
            }
            return importFlag;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除RMA已经存在的物料批次及Unit信息，重新导入
     * @param materialLots
     * @throws ClientException
     */
    private void deleteRmaMaterialLotAndUnit(List<MaterialLot> materialLots) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLots){
                MaterialLot oldMaterialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                if(oldMaterialLot != null){
                    materialLotRepository.deleteById(oldMaterialLot.getObjectRrn());
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(oldMaterialLot, NBHis.TRANS_TYPE_DELETE);
                    materialLotHistoryRepository.save(history);

                    List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(oldMaterialLot.getObjectRrn());
                    if(CollectionUtils.isNotEmpty(materialLotInvList)){
                        materialLotInventoryRepository.deleteByMaterialLotRrn(oldMaterialLot.getObjectRrn());
                    }
                }

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnitRepository.deleteById(materialLotUnit.getObjectRrn());

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_DELETE);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<MaterialLotUnit> getMaterialLotUnitList(MaterialLot materialLot) {
        try {
            List<MaterialLotUnit> materialLotUnitList = new ArrayList<>();
            String waferId = materialLot.getReserved31();
            String [] unitIdArray = waferId.split(",");
            Arrays.sort(unitIdArray);
            String fabLotId = materialLot.getReserved30().split("\\.")[0];
            String lotId = fabLotId + "." + unitIdArray[0].split("\\.")[1];
            for(int i=0; i< unitIdArray.length; i++){
                MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                materialLotUnit.setMaterialLot(materialLot);
                materialLotUnit.setLotId(lotId);
                materialLotUnit.setGrade(MaterialLotUnit.SAMSUING_GRADE);
                materialLotUnit.setReserved4(materialLot.getReserved6());
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                materialLotUnit.setCurrentQty(materialLot.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved13(materialLot.getReserved13());
                materialLotUnit.setReserved14(materialLot.getReserved14());
                materialLotUnit.setReserved47(materialLot.getReserved47());
                materialLotUnit.setReserved48(materialLot.getReserved48());
                materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR_CP);
                String waferSource = valiateMaterialNameAndGetWaferSource(materialLot.getMaterialName());
                materialLotUnit.setReserved50(waferSource);
                materialLotUnit.setUnitId(unitIdArray[i]);
                materialLotUnitList.add(materialLotUnit);
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteIncomingMaterialLot(List<MaterialLotUnit> materialLotUnitList, String deleteNote) throws ClientException{
        try {
            //卡控已经排料的晶圆不能删除
            List<MaterialLotUnit> bindWorkorderMLotUnits = materialLotUnitList.stream().filter(materialLotUnit -> !StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(bindWorkorderMLotUnits)){
                throw new ClientParameterException(GcExceptions.UNIT_ID_ALREADY_BONDING_WORKORDER_ID, bindWorkorderMLotUnits.get(0).getLotId());
            }

            //按照箱号分组
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(String materialLotId : materialLotUnitMap.keySet()){
                materialLotUnitRepository.deleteByMaterialLotId(materialLotId);
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotRepository.delete(materialLot);

                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotHistoryRepository.save(history);
            }

            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotUnitHisRepository.save(history);
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收WLT的完成品
     * @param packedLotList
     * @return
     */
    public List<MesPackedLot> receiveWltFinishGood(List<MesPackedLot> packedLotList, String printLabel, String printCount) throws ClientException {
        try {
            List<MaterialLot> scmReportHoldMLotList = Lists.newArrayList();
            List<MaterialLot> materialLotList = Lists.newArrayList();
            List<MesPackedLot> waitReceivePackedLotList = packedLotList.stream().map(mesPackedLot -> mesPackedLotRepository.findByBoxId(mesPackedLot.getBoxId())).collect(Collectors.toList());
            waitReceivePackedLotList = waitReceivePackedLotList.stream().filter(mesPackedLot -> MesPackedLot.PACKED_STATUS_IN.equals(mesPackedLot.getPackedStatus())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(waitReceivePackedLotList) || waitReceivePackedLotList.size() != packedLotList.size()){
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_ALREADY_RECEIVE, packedLotList.get(0).getBoxId());
            }
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                Material material = getMaterialByMesPackedLot(mesPackedLotList);
                Long totalQuantity = mesPackedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                MesPackedLot mesPackedLot = getWltCPReceicvePackedLotByPackedWafer(mesPackedLotList.get(0), material, totalQuantity, mesPackedLotList.size());
                mesPackedLots.add(mesPackedLot);
            }
            receiveFinishGood(mesPackedLots);
            materialLotList = saveMaterialLotUnitAndSaveHis(packedLotMap, mesPackedLots, materialLotList);

            //验证物料批次是否存在Hold信息，保存Hold
            for(MaterialLot materialLot: materialLotList){
                if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                    if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                            || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                        scmReportHoldMLotList.add(materialLot);
                    }
                }
            }
            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));

            if(CollectionUtils.isNotEmpty(scmReportHoldMLotList)){
                log.info("scmReportHold MLotList is -----> " + scmReportHoldMLotList);
                List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
                for(MaterialLot materialLot : scmReportHoldMLotList){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnitList.addAll(materialLotUnits);
                }
                scmService.sendMaterialStateReport(materialLotUnitList, MaterialLotStateReportRequestBody.ACTION_TYPE_HOLD);
            }

            return mesPackedLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产线接收入库数据获取物料信息
     * @param mesPackedLotList
     * @return
     * @throws ClientException
     */
    private Material getMaterialByMesPackedLot(List<MesPackedLot> mesPackedLotList) throws ClientException{
        try {
            Material material = null;
            String productId = mesPackedLotList.get(0).getProductId();
            Optional<MesPackedLot> firstMesPackedLot = mesPackedLotList.stream().filter(packedLot -> !StringUtils.isNullOrEmpty(packedLot.getInFlag()) && MesPackedLot.IN_FLAG_ONE.equals(packedLot.getInFlag())).findFirst();
            if (firstMesPackedLot.isPresent()) {
                material = mmsService.getRawMaterialByName(productId);
                if (material == null) {
                    material = new RawMaterial();
                    material.setName(productId);
                    material = mmsService.createRawMaterial((RawMaterial)material);
                }
            }else {
                material = mmsService.getProductByName(productId);
                if(material == null){
                    material = saveProductAndSetStatusModelRrn(productId);
                }
            }
            return material;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存晶圆信息，修改MES入库晶圆的状态
     * @param packedLotMap
     * @param mesPackedLots
     * @param materialLotList
     * @throws ClientException
     */
    private List<MaterialLot> saveMaterialLotUnitAndSaveHis(Map<String, List<MesPackedLot>> packedLotMap, List<MesPackedLot> mesPackedLots, List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MesPackedLot mesPackedLot : mesPackedLots){
                String cstId = mesPackedLot.getCstId();
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mesPackedLot.getBoxId(), ThreadLocalContext.getOrgRrn());
                Material material = mmsService.getProductByName(materialLot.getMaterialName());
                if(MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
                    material = mmsService.getRawMaterialByName(materialLot.getMaterialName());
                }
                MesPackedLotRelation mesPackedLotRelation = mesPackedLotRelationRepository.findByPackedLotRrn(mesPackedLotList.get(0).getPackedLotRrn());
                for(MesPackedLot packedLot : mesPackedLotList){
                    MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                    if(MaterialLotUnit.PRODUCT_CATEGORY_RW.equals(mesPackedLot.getProductCategory())){
                        materialLotUnit.setLotCst(packedLot.getLotId());
                        if(MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
                            materialLotUnit.setUnitId(packedLot.getWaferId());
                            List<MaterialLotUnit> sourceMLotUnits = materialLotUnitRepository.findByUnitIdAndWorkOrderIdAndState(packedLot.getWaferId(), packedLot.getWorkorderId(), MaterialLotUnit.STATE_ISSUE);
                            if(CollectionUtils.isNotEmpty(sourceMLotUnits)){
                                materialLotUnit.setReserved30(sourceMLotUnits.get(0).getReserved30());
                                materialLotUnit.setReserved31(sourceMLotUnits.get(0).getReserved31());
                                materialLotUnit.setReserved32(sourceMLotUnits.get(0).getReserved32());
                            }
                        } else {
                            materialLotUnit.setUnitId(packedLot.getBoxId());
                        }
                    } else {
                        materialLotUnit.setUnitId(packedLot.getWaferId());
                    }
                    if (MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(mesPackedLot.getProductCategory())
                            && !StringUtils.isNullOrEmpty(packedLot.getWlaTestBit())){
                        GcWlatoftTesebit wlatoftTesebit = wlatoFtTestBitRepository.findByWaferId(packedLot.getWaferId());
                        if (wlatoftTesebit == null){
                            wlatoftTesebit = new GcWlatoftTesebit();
                            wlatoftTesebit.setWaferId(packedLot.getWaferId());
                            wlatoftTesebit.setWlaTestBit(packedLot.getWlaTestBit());
                            wlatoftTesebit.setWlaProgramBit(packedLot.getProgramBit());
                            wlatoftTesebit = wlatoFtTestBitRepository.save(wlatoftTesebit);

                            GcWlatoftTesebitHis wlatoftTesebitHis = (GcWlatoftTesebitHis)baseService.buildHistoryBean(wlatoftTesebit, NBHis.TRANS_TYPE_CREATE);
                            wlatoFtTestBitHisRepository.save(wlatoftTesebitHis);
                        }else{
                            wlatoftTesebit.setWlaTestBit(packedLot.getWlaTestBit());
                            wlatoftTesebit.setWlaProgramBit(packedLot.getProgramBit());
                            wlatoftTesebit = wlatoFtTestBitRepository.saveAndFlush(wlatoftTesebit);

                            GcWlatoftTesebitHis wlatoftTesebitHis = (GcWlatoftTesebitHis)baseService.buildHistoryBean(wlatoftTesebit, NBHis.TRANS_TYPE_UPDATE);
                            wlatoFtTestBitHisRepository.save(wlatoftTesebitHis);
                        }
                    }
                    materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                    materialLotUnit.setLotId(cstId);
                    materialLotUnit.setMaterial(material);
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnit.setGrade(packedLot.getGrade());
                    materialLotUnit.setWorkOrderId(materialLot.getWorkOrderId());
                    materialLotUnit.setPcode(materialLot.getPcode());
                    materialLotUnit.setDurable(materialLot.getDurable());
                    materialLotUnit.setCurrentQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                    materialLotUnit.setTreasuryNote(packedLot.getTreasuryNote());
                    materialLotUnit.setSourceProductId(packedLot.getOrgProductId());
                    materialLotUnit.setReceiveQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setProductType(materialLot.getProductType());
                    materialLotUnit.setReserved1(packedLot.getLevelTwoCode());
                    materialLotUnit.setReserved3(StringUtils.EMPTY);
                    materialLotUnit.setReserved4(materialLot.getReserved6());
                    materialLotUnit.setReserved9(packedLot.getWlaTestBit());
                    materialLotUnit.setReserved10(packedLot.getProgramBit());
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnit.setReceiveDate(materialLot.getReceiveDate());
                    materialLotUnit.setReserved14(materialLot.getReserved14());
                    materialLotUnit.setReserved18("0");
                    materialLotUnit.setReserved22(materialLot.getReserved22());
                    materialLotUnit.setReserved23(materialLot.getReserved23());
                    materialLotUnit.setReserved24(materialLot.getReserved24());
                    if(mesPackedLotRelation != null){
                        materialLotUnit.setReserved25(mesPackedLotRelation.getWaferProperty());
                    }
                    materialLotUnit.setReserved26(materialLot.getReserved26());
                    materialLotUnit.setReserved38(packedLot.getWaferMark());
                    materialLotUnit.setReserved45(materialLot.getReserved45());
                    materialLotUnit.setReserved49(materialLot.getReserved49());
                    materialLotUnit.setReserved50(materialLot.getReserved50());
                    materialLotUnit =  materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                    materialLotUnitHisRepository.save(history);
                }
                materialLotList.add(materialLot);
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * RW产线接收入库
     * @param packedLots
     * @param printLabel
     * @throws ClientException
     * @return
     */
    public List<MesPackedLot> receiveRWFinishPackedLot(List<MesPackedLot> packedLots, String printLabel, String printCount) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            Map<String, List<MesPackedLot>> packedLotMap = packedLots.stream().collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            List<MesPackedLot> mesPackedLots = Lists.newArrayList();
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> packedLotList = packedLotMap.get(cstId);
                Long totalQuantity = packedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                MesPackedLot mesPackedLot = getRwReceicvePackedLotByBoxId(packedLotList.get(0), totalQuantity, packedLotList.size());
                mesPackedLots.add(mesPackedLot);
            }
            receiveRwFinishGood(mesPackedLots);
            saveMaterialLotUnitAndSaveHis(packedLotMap, mesPackedLots, materialLotList);

            //验证是否需要转仓库，退料入库的物料批次不需要转仓库
//            for(MesPackedLot mesPackedLot : mesPackedLots){
//                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(mesPackedLot.getBoxId(), ThreadLocalContext.getOrgRrn());
//                if(MaterialLot.BONDED_PROPERTY_ZSH.equals(materialLot.getReserved6()) && !MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
//                    Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_SH);
//                    materialLotTransferWareHouse(materialLot, MaterialLot.LOCATION_SH, warehouse);
//                }
//            }

            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLots.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
            return mesPackedLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW完成品接收入库
     * @param packedLotList
     * @throws ClientException
     */
    private void receiveRwFinishGood(List<MesPackedLot> packedLotList) throws ClientException{
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().collect(Collectors.groupingBy(MesPackedLot :: getProductId));
            Map<String, Warehouse> warehouseMap = Maps.newHashMap();
            List<ErpMoa> erpMoaList = Lists.newArrayList();

            for (String productId : packedLotMap.keySet()) {
                Material material = new Material();
                List<MesPackedLot> mesPackedLots = packedLotMap.get(productId);
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                for (MesPackedLot mesPackedLot : mesPackedLots) {
                    if(MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
                        material = mmsService.getRawMaterialByName(productId);
                        if (material == null) {
                            RawMaterial rawMaterial = new RawMaterial();
                            rawMaterial.setName(productId);
                            material = mmsService.createRawMaterial(rawMaterial);
                        }
                    } else {
                        material = mmsService.getProductByName(productId);
                        if (material == null) {
                            material = saveProductAndSetStatusModelRrn(productId);
                        }
                    }
                    //此处验证入库批次是否存在来料信息没有保存的数据，判断是否查询来料批次信息
                    MaterialLot materialLot = null;
                    if(StringUtils.isNullOrEmpty(mesPackedLot.getWaferSource())){
                        String workorderId = mesPackedLot.getWorkorderId();
                        if(!StringUtils.isNullOrEmpty(mesPackedLot.getSourceWorkorderId())){
                            workorderId = mesPackedLot.getSourceWorkorderId();
                        }
                        String lotId = mesPackedLot.getLotId();
                        if(!StringUtils.isNullOrEmpty(mesPackedLot.getSourceLotId())){
                            lotId = mesPackedLot.getSourceLotId();
                        }
                        materialLot = materialLotRepository.findByLotIdAndWorkOrderIdAndStatus(lotId, workorderId, MaterialLotUnit.STATE_ISSUE);
                        if(materialLot == null){
                            throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mesPackedLot.getLotId());
                        } else {
                            if(!StringUtils.isNullOrEmpty(materialLot.getReserved22())){
                                Supplier supplier = supplierRepository.getByNameAndType(materialLot.getReserved22(), Supplier.TYPE_VENDER);
                                if(supplier == null){
                                    supplier = new Supplier();
                                    supplier.setType(Supplier.TYPE_VENDER);
                                    supplier.setName(materialLot.getReserved22());
                                    supplierRepository.save(supplier);
                                }
                            }
                        }
                    } else if(!StringUtils.isNullOrEmpty(mesPackedLot.getSubName())){
                        Supplier supplier = supplierRepository.getByNameAndType(mesPackedLot.getSubName(), Supplier.TYPE_VENDER);
                        if(supplier == null){
                            supplier = new Supplier();
                            supplier.setType(Supplier.TYPE_VENDER);
                            supplier.setName(mesPackedLot.getSubName());
                            supplierRepository.save(supplier);
                        }
                    }

                    MaterialLotAction materialLotAction = new MaterialLotAction();
                    materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
                    materialLotAction.setGrade(mesPackedLot.getGrade());
                    materialLotAction.setTransQty(BigDecimal.valueOf(mesPackedLot.getQuantity()));
                    materialLotAction.setSourceModelId(mesPackedLot.getProductId());
                    materialLotAction.setReturnMaterialFlag(mesPackedLot.getInFlag());
                    materialLotAction.setWorkOrderId(mesPackedLot.getWorkorderId());

                    // 产地是空的话则是ZJ仓库
                    String warehouseName = WAREHOUSE_ZJ;
                    if (!StringUtils.isNullOrEmpty(mesPackedLot.getBondedProperty()) && MaterialLot.LOCATION_SH.equals(mesPackedLot.getBondedProperty())) {
                        warehouseName = WAREHOUSE_SH;
                    }
                    getDefaultStorageIdByBondedProperty(materialLotAction, mesPackedLot.getBondedProperty());

                    Warehouse warehouse;
                    if (!warehouseMap.containsKey(warehouseName)) {
                        warehouse = getWarehouseByName(warehouseName);
                        warehouseMap.put(warehouseName, warehouse);
                    }
                    warehouse = warehouseMap.get(warehouseName);

                    materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());

                    // 需要赋值的Map
                    Map<String, Object> otherReceiveProps = Maps.newHashMap();
                    otherReceiveProps.put("lotId", mesPackedLot.getCstId());
                    otherReceiveProps.put("lotCst", mesPackedLot.getLotId());
                    otherReceiveProps.put("pcode", mesPackedLot.getPcode());
                    otherReceiveProps.put("durable", mesPackedLot.getCstId());
                    otherReceiveProps.put("workOrderId", "");//RW产线接收后清空工单号
                    otherReceiveProps.put("productType", mesPackedLot.getProductType());
                    otherReceiveProps.put("reserved1", mesPackedLot.getLevelTwoCode());
                    otherReceiveProps.put("reserved2", mesPackedLot.getWaferId());
                    otherReceiveProps.put("reserved3", mesPackedLot.getSalesNote());
                    otherReceiveProps.put("reserved4", mesPackedLot.getTreasuryNote());
                    otherReceiveProps.put("reserved5", mesPackedLot.getProductionNote());
                    otherReceiveProps.put("reserved6", mesPackedLot.getBondedProperty());
                    otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
                    otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
                    otherReceiveProps.put("reserved24", mesPackedLot.getFabDevice());
                    otherReceiveProps.put("reserved45", mesPackedLot.getPcode());
                    if(MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
                        otherReceiveProps.put("reserved7", materialLot == null ? mesPackedLot.getCategory() : materialLot.getReserved7());
                        otherReceiveProps.put("reserved22", materialLot == null ? mesPackedLot.getSubName() : materialLot.getReserved22());
                        otherReceiveProps.put("reserved23", materialLot == null ? mesPackedLot.getShipTo() : materialLot.getReserved23());
                        otherReceiveProps.put("reserved49", materialLot == null ? mesPackedLot.getTestSource() : materialLot.getReserved49());
                        otherReceiveProps.put("reserved50", materialLot == null ? mesPackedLot.getWaferSource() : materialLot.getReserved50());
                    } else {
                        otherReceiveProps.put("reserved7", mesPackedLot.getProductCategory());
                        otherReceiveProps.put("reserved22", mesPackedLot.getSubName());
                        otherReceiveProps.put("reserved49", mesPackedLot.getImportType());
                        otherReceiveProps.put("reserved50", MaterialLot.RW_WAFER_SOURCE);
                    }

                    otherReceiveProps.put("reserved25", materialLot == null ? mesPackedLot.getLotType() : materialLot.getReserved25());
                    //记录物料批次的原产品型号和等级
                    otherReceiveProps.put("sourceProductId", mesPackedLot.getOrgProductId());
                    otherReceiveProps.put("reserved26", mesPackedLot.getBinType());
                    otherReceiveProps.put("innerLotId", mesPackedLot.getAssyLotNo());
                    otherReceiveProps.put("reserved32", mesPackedLot.getQuantity());

                    if(mesPackedLot.getWaferQty() != null){
                        BigDecimal waferQty = new BigDecimal(mesPackedLot.getWaferQty().toString());
                        materialLotAction.setTransCount(waferQty);
                    }
                    materialLotAction.setPropsMap(otherReceiveProps);
                    materialLotActions.add(materialLotAction);

                    // ERP_MOA插入数据
                    ErpMoa erpMoa = new ErpMoa();
                    erpMoa.setFQty(mesPackedLot.getWaferQty());
                    erpMoa.setWarehouseCode(warehouseName);
                    erpMoa.setMesPackedLot(mesPackedLot);
                    erpMoa.setCMOCode(mesPackedLot.getShipSequenceNumber());

                    //从发料的Lot上获取物料编码等相关数据
                    erpMoa.setCMemo("EMPTY");
                    erpMoa.setMaterialBonded(materialLot == null ? mesPackedLot.getIssueLocation() : materialLot.getReserved6());
                    erpMoa.setMaterialCode(materialLot == null ? mesPackedLot.getMaterialName() : materialLot.getMaterialName());
                    erpMoa.setMaterialQty(mesPackedLot.getWaferQty());
                    erpMoa.setMaterialGrade(materialLot == null ? mesPackedLot.getIssueGrade() : materialLot.getGrade());
                    erpMoa.setMaterialVersion(materialLot == null ? mesPackedLot.getSubcode() : materialLot.getReserved1());
                    erpMoa.setProdCate(materialLot == null ? mesPackedLot.getProductType() : materialLot.getProductType());

                    erpMoaList.add(erpMoa);
                }
                mmsService.receiveMLotList2Warehouse(material, materialLotActions);
            };

            List<Long> parentRrnList = packedLotList.stream().map(MesPackedLot :: getParentRrn).collect(Collectors.toList());
            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, parentRrnList);

            if(CollectionUtils.isNotEmpty(erpMoaList)){
                erpMoaRepository.saveAll(erpMoaList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证仓库是否存在，不存在则新建
     * @param warehouseName
     * @return
     * @throws ClientException
     */
    private Warehouse getWarehouseByName(String warehouseName) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(warehouseName);
            if (warehouse == null) {
                warehouse = new Warehouse();
                warehouse.setName(warehouseName);
                warehouse = warehouseRepository.saveAndFlush(warehouse);
            }
            return warehouse;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 组建RW待接收完成品
     * @param mesPackedLot
     * @param totalQuantity
     * @param count
     * @return
     */
    private MesPackedLot getRwReceicvePackedLotByBoxId(MesPackedLot mesPackedLot, Long totalQuantity, int count) throws ClientException{
        try {
            MesPackedLot packedLot = new MesPackedLot();
            MaterialLot materialLot = null;
            if(StringUtils.isNullOrEmpty(mesPackedLot.getWaferSource())){
                //先验证物料批次是否是CP预入
                List<MesPackedLot> preInputLotList =  mesPackedLotRepository.findByCstIdAndType(mesPackedLot.getLotId(), MesPackedLot.PACKED_TYPE_CPCST_PREIN);
                if(CollectionUtils.isNotEmpty(preInputLotList)){
                    mesPackedLot.setWorkorderId(preInputLotList.get(0).getSourceWorkorderId());
                }
                String workorderId = mesPackedLot.getWorkorderId();
                if(!StringUtils.isNullOrEmpty(mesPackedLot.getSourceWorkorderId())){
                    workorderId = mesPackedLot.getSourceWorkorderId();
                }
                String lotId = mesPackedLot.getLotId();
                if(!StringUtils.isNullOrEmpty(mesPackedLot.getSourceLotId())){
                    lotId = mesPackedLot.getSourceLotId();
                }
                materialLot = materialLotRepository.findByLotIdAndWorkOrderIdAndStatus(lotId, workorderId, MaterialLotUnit.STATE_ISSUE);
                if(materialLot == null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, lotId);
                }
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13()) && MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
                    packedLot.setLocation(MaterialLot.LOCATION_SH);
                }
            } else {
                if(!StringUtils.isNullOrEmpty(mesPackedLot.getWarehouseId()) && MaterialLot.SH_WAREHOUSE.equals(mesPackedLot.getWarehouseId())){
                    packedLot.setLocation(MaterialLot.LOCATION_SH);
                }
            }


            PropertyUtils.copyProperties(mesPackedLot, packedLot, new HistoryBeanConverter());
            String mLotId = generatorMLotsTransId(MaterialLot.GENERATOR_MATERIAL_LOT_ID_RULE);
            packedLot.setBoxId(mLotId);
            packedLot.setPackedLotRrn(null);
            if(materialLot != null){
                if(MaterialLot.ZJ_WAREHOUSE.equals(materialLot.getReserved13())){
                    packedLot.setSubName(MesPackedLot.ZJ_SUB_NAME);
                } else {
                    packedLot.setSubName(MesPackedLot.SH_SUB_NAME);
                }
                packedLot.setProductType(materialLot.getProductType());
                packedLot.setFabDevice(materialLot.getReserved24());
            }

            packedLot.setImportType(MaterialLot.IMPORT_COB);
            packedLot.setWaferId(StringUtils.EMPTY);
            packedLot.setQuantity(totalQuantity.intValue());
            packedLot.setWaferQty(count);

            return packedLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB产线接收入库
     * @param packedLotList
     * @throws ClientException
     */
    public void receiveCOBFinishGood(List<MesPackedLot> packedLotList) throws ClientException {
        try {
            Map<String, List<MesPackedLot>> packedLotMap = packedLotList.stream().map(packedLot -> mesPackedLotRepository.findByBoxId(packedLot.getBoxId())).collect(Collectors.groupingBy(MesPackedLot :: getCstId));
            //此时的cstId是SBB的箱号
            for(String cstId : packedLotMap.keySet()){
                List<MesPackedLot> mesPackedLotList = packedLotMap.get(cstId);
                Long totalQty = mesPackedLotList.stream().collect(Collectors.summingLong(mesPackedLot -> mesPackedLot.getQuantity().longValue()));
                MesPackedLot mesPackedLot = mesPackedLotList.get(0);
                Material material = mmsService.getProductByName(mesPackedLot.getProductId());
                if (material == null) {
                    material = saveProductAndSetStatusModelRrn(mesPackedLot.getProductId());
                }

                //1、以装箱形式发料上线的批次信息
                MaterialLot issueCobMLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(cstId, ThreadLocalContext.getOrgRrn());
                if(issueCobMLot == null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, mesPackedLot.getCstId());
                }
                //2、如果CST是箱号，删除箱中信息，删除发料时物料批次信息
                String durable = issueCobMLot.getDurable();//CST号
                if(!StringUtils.isNullOrEmpty(issueCobMLot.getPackageType())){
                    MaterialLot  issueMLotCst = materialLotRepository.findByMaterialLotIdAndOrgRrn(durable, ThreadLocalContext.getOrgRrn());
                    deletMaterialLotUnitAndSaveHis(issueMLotCst.getMaterialLotId());
                    materialLotRepository.delete(issueMLotCst);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(issueMLotCst, MaterialLotHistory.TRANS_TYPE_DELETE);
                    materialLotHistoryRepository.save(history);
                } else {
                    deletMaterialLotUnitAndSaveHis(cstId);
                }

                //3、重新创建CST信息和Unit信息
                MaterialLotAction materialLotAction = buildCobFinishReceiveInfo(issueCobMLot, mesPackedLot, durable, totalQty, mesPackedLotList.size());
                MaterialLot materialLot = mmsService.receiveMLot2Warehouse(material, durable, materialLotAction);
                for(MesPackedLot packedLot : mesPackedLotList){
                    MaterialLotUnit materialLotUnit = new MaterialLotUnit();
                    materialLotUnit.setUnitId(packedLot.getWaferId());
                    materialLotUnit.setMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnit.setMaterialLotRrn(materialLot.getObjectRrn());
                    materialLotUnit.setLotId(durable);
                    materialLotUnit.setDurable(durable);
                    materialLotUnit.setMaterial(material);
                    materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                    materialLotUnit.setGrade(packedLot.getGrade());
                    materialLotUnit.setPcode(materialLot.getPcode());
                    materialLotUnit.setCurrentQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                    materialLotUnit.setTreasuryNote(packedLot.getTreasuryNote());
                    materialLotUnit.setSourceProductId(packedLot.getOrgProductId());
                    materialLotUnit.setReceiveQty(BigDecimal.valueOf(packedLot.getQuantity()));
                    materialLotUnit.setProductType(materialLot.getProductType());
                    materialLotUnit.setReserved1(packedLot.getLevelTwoCode());
                    materialLotUnit.setReserved3(StringUtils.EMPTY);
                    materialLotUnit.setReserved4(materialLot.getReserved6());
                    materialLotUnit.setReserved13(materialLot.getReserved13());
                    materialLotUnit.setReceiveDate(materialLot.getReceiveDate());
                    materialLotUnit.setReserved14(materialLot.getReserved14());
                    materialLotUnit.setReserved18("0");
                    materialLotUnit.setReserved22(materialLot.getReserved22());
                    materialLotUnit.setReserved23(materialLot.getReserved23());
                    materialLotUnit.setReserved24(materialLot.getReserved24());
                    materialLotUnit.setReserved26(materialLot.getReserved26());
                    materialLotUnit.setReserved38(packedLot.getWaferMark());
                    materialLotUnit.setReserved45(materialLot.getReserved45());
                    materialLotUnit.setReserved49(materialLot.getReserved49());
                    materialLotUnit.setReserved50(materialLot.getReserved50());
                    materialLotUnit =  materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
                //4、CST为materialLotId的物料批次信息，重新装箱
                materialLotRepository.delete(issueCobMLot);
                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(issueCobMLot, MaterialLotHistory.TRANS_TYPE_DELETE);
                materialLotHistoryRepository.save(materialLotHistory);

                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                MaterialLotAction packMLotAction = new MaterialLotAction();
                packMLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                packMLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotActions.add(packMLotAction);
                packageService.packageMLots(materialLotActions, cstId, "COBPackCase");
            }
            mesPackedLotRepository.updatePackedStatusByPackedLotRrnList(MesPackedLot.PACKED_STATUS_RECEIVED, packedLotList.stream().map(MesPackedLot :: getPackedLotRrn).collect(Collectors.toList()));
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除晶圆历史
     * @param materialLotId
     * @throws ClientException
     */
    private void deletMaterialLotUnitAndSaveHis(String materialLotId) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLotId);
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                materialLotUnitRepository.delete(materialLotUnit);
                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_DELETE);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 构建COB成品接收数据
     * @param issueCobMLot
     * @param mesPackedLot
     * @param durable
     * @throws ClientException
     */
    private MaterialLotAction buildCobFinishReceiveInfo(MaterialLot issueCobMLot, MesPackedLot mesPackedLot, String durable, Long totalQty, Integer currentSubQty) throws ClientException{
        try{
            String subName = MesPackedLot.SH_SUB_NAME;
            if(MesPackedLot.IN_FLAG_ONE.equals(mesPackedLot.getInFlag())){
                subName = issueCobMLot.getReserved22();
            } else {
                if(MesPackedLot.ZH_WAREHOUSE.equals(issueCobMLot.getReserved13())){
                    subName = MesPackedLot.ZJ_SUB_NAME;
                }
            }
            if(MaterialLot.SH_WAREHOUSE.equals(issueCobMLot.getReserved13())){
                mesPackedLot.setLocation(MaterialLot.LOCATION_SH);
            }
            String warehouseName = WAREHOUSE_ZJ;
            if (MaterialLot.LOCATION_SH.equals(mesPackedLot.getLocation())) {
                warehouseName = WAREHOUSE_SH;
            }
            Warehouse warehouse = getWarehouseByName(warehouseName);
            // 需要赋值的Map
            Map<String, Object> otherReceiveProps = Maps.newHashMap();
            otherReceiveProps.put("reserved1", mesPackedLot.getLevelTwoCode());
            otherReceiveProps.put("reserved2", StringUtils.EMPTY);
            otherReceiveProps.put("reserved3", mesPackedLot.getSalesNote());
            otherReceiveProps.put("reserved4", mesPackedLot.getTreasuryNote());
            otherReceiveProps.put("reserved5", mesPackedLot.getProductionNote());
            otherReceiveProps.put("reserved6", mesPackedLot.getBondedProperty());
            otherReceiveProps.put("reserved13", warehouse.getObjectRrn().toString());
            otherReceiveProps.put("earlierExpDate", mesPackedLot.getCreated());
            otherReceiveProps.put("workOrderId", StringUtils.EMPTY);
            otherReceiveProps.put("productType", issueCobMLot.getProductType());
            otherReceiveProps.put("reserved21", mesPackedLot.getErpProductId());
            otherReceiveProps.put("reserved22", subName);
            otherReceiveProps.put("reserved24", issueCobMLot.getReserved24());

            if(!StringUtils.isNullOrEmpty(mesPackedLot.getProductCategory())){
                mLotSetWaferSourceAndReserved7(otherReceiveProps, mesPackedLot.getProductCategory(), mesPackedLot);
            }
            otherReceiveProps.put("reserved49", issueCobMLot.getReserved49());
            otherReceiveProps.put("lotId", durable);
            otherReceiveProps.put("durable", durable);

            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(mesPackedLot.getBoxId());
            materialLotAction.setGrade(mesPackedLot.getGrade());
            materialLotAction.setTransQty(new BigDecimal(totalQty));
            materialLotAction.setSourceModelId(mesPackedLot.getProductId());
            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTransCount(new BigDecimal(currentSubQty));
            getDefaultStorageIdByBondedProperty(materialLotAction, mesPackedLot.getBondedProperty());
            materialLotAction.setPropsMap(otherReceiveProps);
            return materialLotAction;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB成品自动装箱
     * @param materialLotList
     * @throws ClientException
     */
    public void cobMLotAutoPack(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                String parentMaterialLotId = materialLot.getMaterialLotId();
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                materialLot.setMaterialLotId(materialLot.getDurable());
                materialLot.setLotId(materialLot.getDurable());
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                for(MaterialLotUnit materialLotUnit : materialLotUnits){
                    materialLotUnit.setMaterialLotId(materialLot.getDurable());
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                }
                List<MaterialLotAction> materialLotActions = Lists.newArrayList();
                MaterialLotAction packMLotAction = new MaterialLotAction();
                packMLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                packMLotAction.setTransQty(materialLot.getCurrentQty());
                packMLotAction.setResetStorageId("1");
                if(MaterialStatus.STATUS_CREATE.equals(materialLot.getStatus())){
                    packMLotAction.setBoxStatusUseFlag("1");
                }
                materialLotActions.add(packMLotAction);
                MaterialLot packageMLot = packageService.packageMLots(materialLotActions, parentMaterialLotId, "COBPackCase");
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据保税属性获取默认库位信息
     * @param materialLotAction
     * @param bondedProperty
     * @throws ClientException
     */
    private void getDefaultStorageIdByBondedProperty(MaterialLotAction materialLotAction, String bondedProperty) throws ClientException{
        try {
            if (!StringUtils.isNullOrEmpty(bondedProperty)){
                if(bondedProperty.equalsIgnoreCase(MaterialLot.LOCATION_SH)){
                    materialLotAction.setTargetStorageId(MesPackedLot.STORAGE_ID_SH);
                } else if (bondedProperty.equalsIgnoreCase(MaterialLot.BONDED_PROPERTY_ZSH)){
                    materialLotAction.setTargetStorageId(MesPackedLot.STORAGE_ID_ZSH);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP将晶圆信息合并为一个箱号
     * @param mesPackedLot
     * @param material
     * @param totalQty
     * @param number
     * @return
     */
    private MesPackedLot getWltCPReceicvePackedLotByPackedWafer(MesPackedLot mesPackedLot, Material material, Long totalQty, int number) throws ClientException{
        try {
            MesPackedLot packedLot = new MesPackedLot();
            String subName = StringUtils.EMPTY;
            String fabDevice = StringUtils.EMPTY;
            String location = StringUtils.EMPTY;
            String inFlag = mesPackedLot.getInFlag();
            String productCategory = mesPackedLot.getProductCategory();
            PropertyUtils.copyProperties(mesPackedLot, packedLot, new HistoryBeanConverter());

            MaterialLot materialLot = null;
            if(StringUtils.isNullOrEmpty(mesPackedLot.getWaferSource())){
                if(StringUtils.isNullOrEmpty(mesPackedLot.getMaterialLotName())){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByUnitIdAndWorkOrderIdAndState(mesPackedLot.getWaferId(), mesPackedLot.getWorkorderId(), MaterialLotUnit.STATE_ISSUE);
                    if(CollectionUtils.isNotEmpty(materialLotUnits)){
                        materialLot = mmsService.getMLotByMLotId(materialLotUnits.get(0).getMaterialLotId());
                    } else {
                        throw new ClientParameterException(GcExceptions.WAFER_ID__IS_NOT_EXIST, mesPackedLot.getWaferId(), mesPackedLot.getWorkorderId());
                    }
                } else {
                    materialLot = mmsService.getMLotByMLotId(mesPackedLot.getMaterialLotName());
                }
            }

            if(materialLot != null){
                if(!MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory)){
                    if(MesPackedLot.IN_FLAG_ONE.equals(inFlag)){
                        subName = materialLot.getReserved22();
                    } else {
                        if(MesPackedLot.ZH_WAREHOUSE.equals(materialLot.getReserved13())){
                            subName = MesPackedLot.ZJ_SUB_NAME;
                        } else {
                            subName = MesPackedLot.SH_SUB_NAME;
                        }
                    }
                }
            } else {
                if(!MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory)){
                    if(MesPackedLot.IN_FLAG_ONE.equals(inFlag)){
                        subName = mesPackedLot.getSubName();
                    } else {
                        if(MesPackedLot.ZH_WAREHOUSE.equals(mesPackedLot.getWarehouseId())){
                            subName = MesPackedLot.ZJ_SUB_NAME;
                        } else {
                            subName = MesPackedLot.SH_SUB_NAME;
                        }
                    }
                }
            }

            if(MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(productCategory) || MaterialLotUnit.PRODUCT_CATEGORY_CP.equals(productCategory) ||
                    MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(productCategory) || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(productCategory)){
                fabDevice = materialLot == null ? mesPackedLot.getFabDevice() : materialLot.getReserved24();
            }

            if(!StringUtils.isNullOrEmpty(mesPackedLot.getWarehouseId())  && MaterialLot.SH_WAREHOUSE.equals(mesPackedLot.getWarehouseId())){
                location = MaterialLot.LOCATION_SH;
            } else if(materialLot != null && !StringUtils.isNullOrEmpty(materialLot.getReserved13()) && MaterialLot.SH_WAREHOUSE.equals(materialLot.getReserved13())){
                location = MaterialLot.LOCATION_SH;
            }

            packedLot.setLocation(location);
            packedLot.setFabDevice(fabDevice);
            packedLot.setImportType(materialLot == null ? mesPackedLot.getTestSource() : materialLot.getReserved49());
            packedLot.setSubName(subName);
            packedLot.setProductType(materialLot == null ? mesPackedLot.getProductType() : materialLot.getProductType());

            String mLotId = mmsService.generatorMLotId(material);
            packedLot.setBoxId(mLotId);
            packedLot.setPackedLotRrn(null);
            packedLot.setWaferId("");
            packedLot.setQuantity(totalQty.intValue());
            packedLot.setWaferQty(number);
            return packedLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 产品型号二级代码信息
     * @param productId
     * @param subcode
     * @return
     */
    private GCProductSubcode getProductAndSubcodeInfo(String productId, String subcode) throws ClientException {
        try {
            return gcProductSubcodeSetRepository.findByProductIdAndSubcode(productId, subcode);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存产品二级代码信息
     * @param productSubcode
     * @return
     */
    public GCProductSubcode saveProductSubcode(GCProductSubcode productSubcode) throws ClientException {
        try {
            String productId = productSubcode.getProductId().trim();
            GCProductSubcode oldProductSubcode = getProductAndSubcodeInfo(productId ,productSubcode.getSubcode().trim());
            if(oldProductSubcode != null){
                throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_EXIST);
            }
            RawMaterial rawMaterial = mmsService.getRawMaterialByName(productId);
            if(rawMaterial == null){
                rawMaterial = new RawMaterial();
                rawMaterial.setName(productId);
                mmsService.createRawMaterial(rawMaterial);
            }
            productSubcode.setProductId(productId);
            productSubcode.setSubcode(productSubcode.getSubcode().trim());
            productSubcode = gcProductSubcodeSetRepository.saveAndFlush(productSubcode);
            return productSubcode;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void importProductSubCode(List<GCProductSubcode> productSubcodeList) throws ClientException{
        try {
            for(GCProductSubcode productSubcode : productSubcodeList){
                String materialName = productSubcode.getProductId();
                if(StringUtils.isNullOrEmpty(materialName)){
                    throw new ClientParameterException(GcExceptions.PRODUCT_ID_CANNOT_EMPTY, materialName);
                }
                saveProductSubcode(productSubcode);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据导入文件名称获取保税属性
     * @param fileName
     * @return
     */
    public String validationAndGetBondedPropertyByFileName(String fileName) throws ClientException{
        try {
            String bondedProperty = "";
            List<NBOwnerReferenceList> bondedProReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(MaterialLot.BONDED_PROPERTY_LIST, NBReferenceList.CATEGORY_OWNER);
            String bondedPro = fileName.substring(fileName.lastIndexOf("_")+1);
            bondedPro = bondedPro.substring(0, bondedPro.indexOf("."));
            for (NBOwnerReferenceList nbOwnerReferenceList : bondedProReferenceList){
                if(nbOwnerReferenceList.getValue().equals(bondedPro)){
                    bondedProperty = bondedPro;
                }
            }
            if(StringUtils.isNullOrEmpty(bondedProperty)){
                throw new ClientParameterException(GcExceptions.FILE_NAME_CANNOT_BONDED_PROPERTY_INFO);
            }
            return bondedProperty;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证来料信息中产品型号和二级代码是否存在
     */
    public void validateMLotUnitProductAndSubcode(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            for (String lotId : materialLotUnitMap.keySet()){
                MaterialLotUnit materialLotUnit = materialLotUnitMap.get(lotId).get(0);
                GCProductSubcode gcProductSubcode = getProductAndSubcodeInfo(materialLotUnit.getMaterialName(), materialLotUnit.getReserved1());
                if(gcProductSubcode == null ){
                    throw new ClientParameterException(GcExceptions.PRODUCT_AND_SUBCODE_IS_NOT_EXIST, materialLotUnit.getMaterialName() + StringUtils.SPLIT_CODE + materialLotUnit.getReserved1());
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量修改真空包的入库备注
     */
    public void updateMaterialLotTreasuryNote(List<MaterialLot> materialLotList, String treasuryNote) throws ClientException{
        try {
            for (MaterialLot materialLot : materialLotList){
                materialLot.setReserved4(treasuryNote);
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UPDATE_TREASURY_NOTE);
                history.setTransQty(materialLot.getCurrentQty());
                materialLotHistoryRepository.save(history);

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for (MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setTreasuryNote(treasuryNote);
                        materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHis = (MaterialLotUnitHistory)baseService.buildHistoryBean(materialLotUnit, TRANS_TYPE_UPDATE_TREASURY_NOTE);
                        materialLotUnitHis.setTransQty(materialLotUnitHis.getCurrentQty());
                        materialLotUnitHisRepository.save(materialLotUnitHis);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量修改真空包的保税属性
     */
    public void updateMaterialLotLocation(List<MaterialLot> materialLotList, String location, String remarks) throws ClientException{
        try {
            for (MaterialLot materialLot : materialLotList){
                materialLot.setReserved6(location);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for (MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setReserved4(location);
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, TRANS_TYPE_UPDATE_LOCATION);
                        materialLotUnitHistory.setActionComment(remarks);
                        materialLotUnitHistory.setTransQty(materialLotUnit.getCurrentQty());
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_UPDATE_LOCATION);
                history.setActionComment(remarks);
                history.setTransQty(materialLot.getCurrentQty());
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包批量HOLD
     */
    public void materialLotHold(List<MaterialLot> materialLotList, String holdReason, String remarks, String holdType) throws ClientException{
        try {
            //对CP的物料批次Hold时报告状态
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                if(MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7())){
                    materialLotAction.setActionComment(remarks);
                    materialLotAction.setActionReason(holdReason);
                } else {
                    String newHoldReason = ThreadLocalContext.getUsername() + StringUtils.UNDERLINE_CODE + holdType + StringUtils.UNDERLINE_CODE + remarks;
                    materialLotAction.setActionComment(newHoldReason);
                    materialLotAction.setActionReason(remarks);
                }
                mmsService.holdMaterialLot(materialLot,materialLotAction);

                //对箱Hold的时候对箱里面的真空包也做HOLD操作
                List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for(MaterialLot packageLot : packageDetailLots){
                        if(MaterialLot.HOLD_STATE_OFF.equals(packageLot.getHoldState())){
                            materialLotAction.setTransQty(packageLot.getCurrentQty());
                            mmsService.holdMaterialLot(packageLot,materialLotAction);

                            //对箱号Hold时，验证箱中的物料批次是否时CP的，判断是否做SCM报告
                            if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                                    || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                                scmReportMLotList.add(materialLot);
                            }
                        }
                    }
                }

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReportMLotList.add(materialLot);
                }
            }

            //对已经装箱的真空包HOLD时，对箱号也做Hold标记
            Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for (String parentMaterialLotId : packedMLotMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId);
                if(MaterialLot.HOLD_STATE_OFF.equals(materialLot.getHoldState())){
                    materialLot.setHoldState(MaterialLot.HOLD_STATE_ON);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_HOLD);
                    if(MaterialLot.PRODUCT_CATEGORY.equals(materialLot.getReserved7())){
                        history.setActionComment(remarks);
                        history.setActionReason(holdReason);
                    } else {
                        String newHoldReason = ThreadLocalContext.getUsername() + StringUtils.UNDERLINE_CODE + holdType + StringUtils.UNDERLINE_CODE + remarks;
                        history.setActionComment(newHoldReason);
                        history.setActionReason(remarks);
                    }
                    materialLotHistoryRepository.save(history);
                }
            }

            log.info("MaterialLots report scm status is" + scmReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
                for(MaterialLot materialLot : scmReportMLotList){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnitList.addAll(materialLotUnits);
                }
                scmService.sendMaterialStateReport(materialLotUnitList, MaterialLotStateReportRequestBody.ACTION_TYPE_HOLD);
                log.info("MaterialLots report scm status  hold end");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 真空包批量释放
     */
    public void materialLotRelease(List<MaterialLot> materialLotList, String ReleaseReason, String remarks) throws ClientException{
        try {
            List<MaterialLot> scmReleaseReportMLotList = Lists.newArrayList();
            for (MaterialLot materialLot : materialLotList){
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setActionComment(remarks);
                materialLotAction.setActionReason(ReleaseReason);
                mmsService.releaseMaterialLot(materialLot,materialLotAction);

                //对箱RELESAE的时候对箱里面的真空包也做RELESAE操作
                List<MaterialLot> packageDetailLots = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageDetailLots)){
                    for(MaterialLot packageLot : packageDetailLots){
                        materialLotAction.setTransQty(packageLot.getCurrentQty());
                        mmsService.releaseMaterialLot(packageLot,materialLotAction);

                        //对箱号Release时，验证箱中的物料批次是否时CP的，判断是否做SCM报告
                        if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                                || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                            scmReleaseReportMLotList.add(materialLot);
                        }
                    }
                }

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReleaseReportMLotList.add(materialLot);
                }
            }

            //对已经装箱的真空包Release的时候，所有的真空包都release之后，清除箱号hold标记
            Map<String, List<MaterialLot>> packedMLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String packedMLotId : packedMLotMap.keySet()){
                List<MaterialLot> packedMLotDetials = materialLotRepository.getByParentMaterialLotId(packedMLotId);
                List<MaterialLot> holdMLotList = packedMLotDetials.stream().filter(materialLot -> MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(holdMLotList)){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(packedMLotId);
                    materialLot.setHoldState(MaterialLot.HOLD_STATE_OFF);
                    materialLot.setHoldReason(StringUtils.EMPTY);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RELEASE);
                    history.setActionComment(remarks);
                    history.setActionReason(ReleaseReason);
                    materialLotHistoryRepository.save(history);
                }
            }

            log.info("scm report materialLots release  status is " + scmReleaseReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReleaseReportMLotList)){
                List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
                for(MaterialLot materialLot : scmReleaseReportMLotList){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnitList.addAll(materialLotUnits);
                }
                scmService.sendMaterialStateReport(materialLotUnitList, MaterialLotStateReportRequestBody.ACTION_TYPE_RELEASE);
                log.info("scm report materialLots release end");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 查询已经绑定工单并且计划投入日期小于当前日期的物料批次信息
     * @param tableRrn
     * @param whereClause
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> validationAndGetWaitIssueWafer(Long tableRrn,String whereClause) throws ClientException{
        try {
            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.DEFAULT_REVERSE_DATA_PATTERN);
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                if(!StringUtils.isNullOrEmpty(whereClause)){
                    clauseBuffer.append(" AND ");
                    clauseBuffer.append(whereClause);
                }
                _whereClause = clauseBuffer.toString();
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            List<MaterialLot> materialLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLots){
                String workOrderPlanTime = materialLot.getWorkOrderPlanputTime();
                if(!StringUtils.isNullOrEmpty(workOrderPlanTime)){
                    Date workOrderPlanPutTime = formatter.parse(workOrderPlanTime);
                    if(workOrderPlanPutTime.before(new Date())){
                        materialLotList.add(materialLot);
                    }
                }
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端获取晶圆
     * @param tableRrn
     * @param lotId
     * @return
     * @throws ClientException
     */
    public MaterialLot mobileValidationAndGetWait(Long tableRrn,String lotId) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.DEFAULT_REVERSE_DATA_PATTERN);
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            MaterialLot materialLot = new MaterialLot();
            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                if(!StringUtils.isNullOrEmpty(lotId)){
                    clauseBuffer.append(" AND lotId = '" + lotId + "'");
                }
                _whereClause = clauseBuffer.toString();
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if (CollectionUtils.isNotEmpty(materialLots)){
                materialLot = materialLots.get(0);
                String workOrderPlanTime = materialLot.getWorkOrderPlanputTime();
                Date workOrderPlanPutTime = formatter.parse(workOrderPlanTime);
                if(!workOrderPlanPutTime.before(new Date())){
                    materialLot = new MaterialLot();
                }
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取FT待发料的晶圆信息（已经绑定工单的晶圆信息）
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> queryFTWaitIssueMLotUnitList(long tableRrn) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            //获取当前日期，时间格式yyMMdd
            SimpleDateFormat formatter = new SimpleDateFormat(MaterialLot.DEFAULT_REVERSE_DATA_PATTERN);
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                _whereClause = clauseBuffer.toString();
            }

            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                String workOrderPlanTime = materialLotUnit.getWorkOrderPlanputTime();
                if(!StringUtils.isNullOrEmpty(workOrderPlanTime)){
                    Date workOrderPlanPutTime = formatter.parse(workOrderPlanTime);
                    if(workOrderPlanPutTime.before(new Date())){
                        materialLotUnitList.add(materialLotUnit);
                    }
                }
            }
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取COB晶圆接收信息
     * @param tableRrn
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public MaterialLot queryCOBReceiveMaterialLotByTabRrnAndLotId(Long tableRrn, String materialLotId) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(nbTable.getWhereClause());
            clauseBuffer.append(" AND materialLotId = ");
            clauseBuffer.append("'" + materialLotId + "'");
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLot = materialLotList.get(0);
                List<GcUnConfirmWaferSet> confirmWaferSetArrayList = Lists.newArrayList();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved46())){
                    List<Map<String, String>> workNoMapList = scmService.queryScmWaferByWorkOrderNo(materialLot.getReserved46());
                    if(CollectionUtils.isNotEmpty(workNoMapList)){
                        List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                        List<String> lotNumberList = Lists.newArrayList();
                        for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                            String lotNumber = materialLotUnit.getUnitId().split("-")[1].split("\\.")[0];
                            if(!lotNumberList.contains(lotNumber)){
                                lotNumberList.add(lotNumber);
                            }
                        }
                        //scm返回的晶圆信息可能包含多个lot的，按照lotNo做分组
                        List<ScmQueryInfo> scmQueryInfoList = Lists.newArrayList();
                        for(Map waferMap : workNoMapList){
                            String lotNo = (String)waferMap.get("lot_no");
                            String waferId = (String)waferMap.get("wafer_id");
                            String woNo = (String)waferMap.get("wo_no");
                            ScmQueryInfo scmQueryInfo = new ScmQueryInfo();
                            scmQueryInfo.setLotNo(lotNo.split("\\.")[0]);
                            scmQueryInfo.setWaferSeq(StringUtil.leftPad(waferId , 2 , "0"));
                            scmQueryInfo.setWoNo(woNo);
                            scmQueryInfoList.add(scmQueryInfo);
                        }
                        Map<String, List<ScmQueryInfo>> scmQueryInfoMap = scmQueryInfoList.stream().collect(Collectors.groupingBy(ScmQueryInfo:: getLotNo));
                        for(String lotNumber : lotNumberList){
                            List<ScmQueryInfo> scmQueryInfos = scmQueryInfoMap.get(lotNumber);
                            if (scmQueryInfoMap.keySet().contains(lotNumber)) {
                                List<GcUnConfirmWaferSet> unConfirmWaferSetList = unConfirmWaferSetRepository.findByLotId(lotNumber);
                                if(CollectionUtils.isNotEmpty(unConfirmWaferSetList)){
                                    for(GcUnConfirmWaferSet unConfirmWaferSet : unConfirmWaferSetList){
                                        String waferInfo = unConfirmWaferSet.getWaferId();
                                        String[] waferSeqArray = waferInfo.split(",");
                                        List<String> waferIdList = Arrays.asList(waferSeqArray);
                                        for(ScmQueryInfo scmQueryInfo : scmQueryInfos){
                                            if(waferIdList.contains(scmQueryInfo.getWaferSeq())){
                                                confirmWaferSetArrayList.add(unConfirmWaferSet);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                String treasuryNote = materialLot.getReserved4();
                if(CollectionUtils.isNotEmpty(confirmWaferSetArrayList)){
                    confirmWaferSetArrayList = confirmWaferSetArrayList.stream().sorted(Comparator.comparing(GcUnConfirmWaferSet :: getRiskGrade).reversed()).collect(Collectors.toList());
                    if(StringUtils.isNullOrEmpty(treasuryNote)){
                        treasuryNote = confirmWaferSetArrayList.get(0).getRiskGrade() + "类_" + confirmWaferSetArrayList.get(0).getExceptionClassify();
                    } else {
                        treasuryNote =  treasuryNote + StringUtils.SEMICOLON_CODE + confirmWaferSetArrayList.get(0).getRiskGrade() + "类_" + confirmWaferSetArrayList.get(0).getExceptionClassify();
                    }
                }
                materialLotRepository.getEntityManager().detach(materialLot);
                materialLot.setReserved4(treasuryNote);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存来料信息
     * @param materialLotList
     * @param importType
     * @return
     */
    public String saveLCDCOGDetailList(List<MaterialLot> materialLotList, String importType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            String importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            for(MaterialLot materialLot : materialLotList){
                if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) <= 0){
                    throw new ClientParameterException(GcExceptions.THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO, materialLot.getCurrentQty());
                }
                GCLcdCogDetail gcLcdCogDetail = gcLcdCogDetialRepository.findByBoxaIdAndBoxbId(materialLot.getMaterialLotId(), materialLot.getParentMaterialLotId());
                if(gcLcdCogDetail != null){
                    throw new ClientParameterException(GcExceptions.BOXAID_AND_BOXBID_IS_EXIST, gcLcdCogDetail.getBoxaId() + StringUtils.SPLIT_CODE + gcLcdCogDetail.getBoxbId());
                } else {
                    gcLcdCogDetail = new GCLcdCogDetail();
                }
                gcLcdCogDetail.setGcLcdCogDetail(materialLot);
                gcLcdCogDetail.setWarehouseId(materialLot.getReserved13());
                gcLcdCogDetail.setImportCode(importCode);
                gcLcdCogDetail.setImportType(importType);
                gcLcdCogDetialRepository.save(gcLcdCogDetail);
            }
            return importCode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除COG明细信息
     * @param lcdCogDetails
     * @param deleteNote
     * @return
     */
    public void deleteCogDetail(List<GCLcdCogDetail> lcdCogDetails, String deleteNote) throws ClientException{
        try {
            for(GCLcdCogDetail lcdCogDetail : lcdCogDetails){
                gcLcdCogDetialRepository.delete(lcdCogDetail);

                GCLcdCogDetailHis history = (GCLcdCogDetailHis) baseService.buildHistoryBean(lcdCogDetail, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                gcLcdCogDetialHisRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void deleteCogEcretive(List<MaterialLot> lcdCogEcretiveList, String deleteNote) throws ClientException{
        try {
            Map<String, List<MaterialLot>> packedMLotMap = lcdCogEcretiveList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for (String materialLotId : packedMLotMap.keySet()) {
                List<MaterialLot> fullBoxData = materialLotRepository.getByParentMaterialLotId(materialLotId);
                if(packedMLotMap.get(materialLotId).size() < fullBoxData.size()){
                    throw new ClientParameterException(GcExceptions.MUST_DELETE_FULL_BOX_DATA, materialLotId);
                }
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotRepository.delete(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(deleteNote);
                materialLotHistoryRepository.save(history);
            }
            deleteMaterialLotAndSaveHis(lcdCogEcretiveList, deleteNote);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证晶圆是否是ENG
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> materialLotUnitAssignEng(List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            if (SystemPropertyUtils.getConnectScmFlag()) {
                // 请求SCM做是否是ENG产品的验证
                log.info("Request  SCM  validation product ENG or Prod");
                materialLotUnitList = scmService.assignEngFlag(materialLotUnitList);
                log.info("Request  SCM  validation product ENG or Prod  end ");
            }
            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料导入设置waferSource
     * @param importType
     * @param checkFourCodeFlag
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> validateAndSetWaferSource(String importType, String checkFourCodeFlag, List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            //按照载具号分组，相同载具号的产品型号、晶圆数量必须一致(暂时只对WLA未测（-2.5）模板做特殊验证处理)
            String waferSource = StringUtils.EMPTY;
            Map<String, List<MaterialLotUnit>> mLotUnitMap = new HashMap<>();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getLotId));
            if(MaterialLotUnit.WLA_UNMEASURED.equals(importType)){
                Pattern pattern = Pattern.compile("^[_].{3,4}$");
                for (String lotId : materialLotUnitMap.keySet()) {
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitMap.get(lotId);
                    Integer waferCount = materialLotUnits.get(0).getCurrentSubQty().intValue();
                    if(waferCount != materialLotUnits.size()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_WAFER_QTY_IS_NOT_SAME_REAL_QTY, lotId);
                    }
                    mLotUnitMap = groupMaterialLotUnitByMLotIdAndMaterialNameAndWaferQty(materialLotUnits);
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_AND_WAFERQTY_IS_NOT_SAME, lotId);
                    }
                }
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLA);
                    materialLotUnit.setReserved50(MaterialLot.WLA_WAFER_SOURCE);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_WLA);
                    materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                    Matcher matcher = pattern.matcher(materialLotUnit.getReserved38());
                    if(!matcher.find()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                    }
                }
            } else if(MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                    || MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType) || MaterialLotUnit.SOC_WAFER_UNMEASURED.equals(importType)){
                //根据页面是否勾选四位码检测flag，验证来料信息四位码是否符合，不符合不让导入
                if(MaterialLot.PRINT_CHECK.equals(checkFourCodeFlag)){
                    validateMaterialLotUnitFourCode(materialLotUnitList);
                }
                //验证同一个载具号的晶圆型号是否一致
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                    for(String materialName : mLotUnitMap.keySet()){
                        //通过晶圆型号末尾的数字获取不同的Wafer Source
                        waferSource = valiateMaterialNameAndGetWaferSource(materialName);

                        List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                        String productCategory = MaterialLotUnit.PRODUCT_CLASSIFY_CP;
                        String importClass = MaterialLot.IMPORT_SENSOR_CP;
                        if(MaterialLotUnit.SOC_WAFER_UNMEASURED.equals(importType)){
                            productCategory = MaterialLotUnit.PRODUCT_CLASSIFY_SOC;
                            importClass = MaterialLot.IMPORT_SOC;
                            if(materialName.endsWith("-1.1")) {
                                waferSource = MaterialLot.SOC_WAFER_SOURCE_MEASURE;
                            }
                        }
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setReserved7(productCategory);
                            materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                            materialLotUnit.setReserved50(waferSource);
                            materialLotUnit.setReserved49(importClass);
                        }
                    }
                }
            }  else if(MaterialLotUnit.FAB_LCD_PTC.equals(importType) || MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType)
                    || MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType)){
                for (String lotId : materialLotUnitMap.keySet()) {
                    mLotUnitMap = materialLotUnitMap.get(lotId).stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
                    if(mLotUnitMap.size() > 1){
                        throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_NOT_SAME, lotId);
                    }
                    for(String materialName : mLotUnitMap.keySet()){
                        if(materialName.endsWith("-1") || materialName.endsWith("-2.5") || materialName.endsWith("-2.55")){
                            waferSource = MaterialLot.WAFER_SOURCE_END3;
                        } else if(materialName.endsWith("-2.6")) {
                            waferSource = MaterialLot.WAFER_SOURCE_END4;
                        } else {
                            throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
                        }
                        List<MaterialLotUnit> materialLotUnits = mLotUnitMap.get(materialName);
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_CP);
                            materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                            materialLotUnit.setReserved50(waferSource);
                            materialLotUnit.setReserved49(MaterialLot.IMPORT_LCD_CP);
                        }
                    }
                }
            } else if(MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType)|| MaterialLotUnit.SENSOR_TPLCC.equals(importType)){
                for (MaterialLotUnit materialLotUnit : materialLotUnitList) {
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);
                    materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                    materialLotUnit.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                }
            } else {
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    if (materialLotUnit.getCurrentQty().compareTo(BigDecimal.ZERO) <= 0){
                        throw new ClientParameterException(GcExceptions.THE_QUANTITY_FIELD_MUST_BE_GREATER_THAN_ZERO, materialLotUnit.getCurrentQty());
                    }
                    if ((MaterialLotUnit.COB_RAW_MATERIAL_PRODUCT.equals(importType) || MaterialLotUnit.COB_FINISH_PRODUCT.equals(importType))
                             && (MaterialLot.SH_WAREHOUSE.equals(materialLotUnit.getReserved13()) && !(MaterialLot.LOCATION_SH.equals(materialLotUnit.getReserved4()))
                                 || (MaterialLot.ZJ_WAREHOUSE.equals(materialLotUnit.getReserved13()) && !MaterialLot.BONDED_PROPERTY_ZSH.equals(materialLotUnit.getReserved4())))){
                        throw new ClientParameterException(GcExceptions.WAREHOUSE_AND_BONDPRO_ARE_INCONSISTENT, materialLotUnit.getUnitId());
                    }
                    if(MaterialLotUnit.WLT_PACK_RETURN.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                        materialLotUnit.setReserved50(MaterialLot.WLT_PACK_RETURN_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                    } else if(MaterialLotUnit.COB_RAW_MATERIAL_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_COB);
                        materialLotUnit.setReserved50(MaterialLot.COB_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_COB);
                    } else if(MaterialLotUnit.SOC_FINISH_PRODUCT.equals(importType)){
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SOC);
                        materialLotUnit.setReserved50(MaterialLot.SOC_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_SOC);
                    } else if (MaterialLotUnit.COB_FINISH_PRODUCT.equals(importType)) {
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CATEGORY_FT_COB);
                        materialLotUnit.setReserved50(MaterialLot.RW_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_COB);
                    } else if (MaterialLotUnit.MASK_FINISH_PRODUCT.equals(importType)) {
                        materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_MASK);
                        materialLotUnit.setReserved50(MaterialLot.MASK_WAFER_SOURCE);
                        materialLotUnit.setReserved49(MaterialLot.IMPORT_MASK);
                    }
                }
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * wlt封装回货与sensor封装回货获取waferSource信息
     * 导入型号是GC02M1-3的  如果FAB lot id首位是C 且 FAB device ID 以H00开头，
     * 或者 FAB lot id首位是A 且 FAB device ID 以C000开头,
     * 或者FAB lot id首位是E 且 FAB device ID 以P018L开头;
     * 满足上述条件的GC02M1-3的型号才能导入,
     * 否则报错提示：型号为GC02M1，LOT ID首位是：XXXX 且FAB DEVICE是:XXXXX 不在管控规则内，不能导入;
     * 导入型号是GC02M1-3的  如果FAB lot id首位是C 且 FAB device ID 以H00开头，
     * 或者 FAB lot id首位是A 且 FAB device ID 以C000开头
     * 满足上述条件的GC02M1-3的型号 导入后变成GC02M1C-3.5
     * @param importType
     * @param materialLotUnitList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> packReturnSetWaferSource(String importType,  List<MaterialLotUnit> materialLotUnitList) throws ClientException{
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                String materialName = materialLotUnit.getMaterialName();
                String fablotId = materialLotUnit.getReserved30();
                String fabDeviceId = materialLotUnit.getReserved24();
                if(MaterialLotUnit.RETURN_PRODUCT_ID.equals(materialName)){
                    if((fablotId.startsWith(MaterialLotUnit.FAB_LOT_ID_C) && fabDeviceId.startsWith(MaterialLotUnit.FAB_DEVICE_ID_H00)) ||
                            (fablotId.startsWith(MaterialLotUnit.FAB_LOT_ID_A) && fabDeviceId.startsWith(MaterialLotUnit.FAB_DEVICE_ID_C000)) ){
                        materialLotUnit.setMaterialName(MaterialLotUnit.RETURN_RESET_PRODUCT_ID);
                    } else if(!(fablotId.startsWith(MaterialLotUnit.FAB_LOT_ID_E) && fabDeviceId.startsWith(MaterialLotUnit.FAB_DEVICE_ID_P018L))){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_IN_RULE_CONTROL_CANNOT_IMPORT, materialName + StringUtils.SEMICOLON_CODE + fablotId + StringUtils.SEMICOLON_CODE + fabDeviceId);
                    }
                }
                materialLotUnit.setReserved30(fablotId.split("\\.")[0]);
                if(MaterialLotUnit.WLT_PACK_RETURN.equals(importType)){
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                    materialLotUnit.setReserved50("7");
                } else if(MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType)){
                    materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);
                    materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                    materialLotUnit.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);
                }
            }
            return materialLotUnitList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录装箱批次出货历史
     * @param materialLotList
     * @throws ClientException
     */
    @Override
    public void saveMLotPackageShipHis(List<MaterialLot> materialLotList) throws ClientException {
        try {
            for(MaterialLot materialLot : materialLotList){
                MaterialLotHistory materialLotHistory = materialLotHistoryRepository.findTopByMaterialLotIdAndTransTypeOrderByCreatedDesc(materialLot.getMaterialLotId(), MaterialLotHistory.TRANS_TYPE_SHIP);
                List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                for (MaterialLot packageLot : packageDetailLots){
                    if(MaterialStatus.STATUS_PACKAGE.equals(packageLot.getStatus())){
                        packageLot.setReserved6(materialLot.getReserved6());
                        mmsService.changeMaterialLotState(packageLot,  MaterialEvent.EVENT_SHIP, StringUtils.EMPTY);
                    } else {
                        materialLotInventoryRepository.deleteByMaterialLotRrn(packageLot.getObjectRrn());

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packageLot, MaterialLotHistory.TRANS_TYPE_SHIP);
                        history.setHisSeq(materialLotHistory.getHisSeq());
                        materialLotHistoryRepository.save(history);

                        materialLotHistoryRepository.updateCreatedAndCreateByByMaterialLotIdAndTrandType(materialLotHistory.getCreated(), materialLotHistory.getCreatedBy(), packageLot.getMaterialLotId(), MaterialLotHistory.TRANS_TYPE_SHIP);
                    }
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(packageLot.getMaterialLotId());
                    if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                        for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                            if(!MaterialLotUnit.STATE_OUT.equals(materialLotUnit.getState())){
                                materialLotUnit.setReserved4(materialLot.getReserved6());
                                materialLotUnit.setState(MaterialLotUnit.STATE_OUT);
                                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                            }
                            MaterialLotUnitHistory mUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                            mUnitHistory.setHisSeq(materialLotHistory.getHisSeq());
                            materialLotUnitHisRepository.save(mUnitHistory);
                        }
                        materialLotUnitHisRepository.updateCreatedAndCreateByByMaterialLotIdAndTrandType(materialLotHistory.getCreated(), materialLotHistory.getCreatedBy(), packageLot.getMaterialLotId(), MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * SensorCp型号的晶圆型号验证并获取WaferSource
     * @param materialName
     * @return
     * @throws ClientException
     */
    private String valiateMaterialNameAndGetWaferSource(String materialName) throws ClientException{
        try {
            String waferSource = StringUtils.EMPTY;
            if(materialName.endsWith("-1") || materialName.endsWith("-2") || materialName.endsWith("-1.3")|| materialName.endsWith("-1.5")){
                waferSource = MaterialLot.WAFER_SOURCE_END1;
            } else if(materialName.endsWith("-2.1") || materialName.endsWith("-1.1") || materialName.endsWith("-1.4")) {
                waferSource = MaterialLot.WAFER_SOURCE_END2;
            } else if(materialName.endsWith("-2.5") || materialName.endsWith("-2.55")){
                waferSource = MaterialLot.SOC_WAFER_SOURCE_UNMEASUREN;
            } else if(materialName.endsWith("-2.6")) {
                waferSource = MaterialLot.SOC_WAFER_SOURCE_MEASURE;
            } else {
                throw new ClientParameterException(GcExceptions.MATERIALNAME_IS_ERROR, materialName);
            }
            return waferSource;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证来料信息的四位码
     * @param materialLotUnitList
     */
    private void validateMaterialLotUnitFourCode(List<MaterialLotUnit> materialLotUnitList) {
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                String waferMark = materialLotUnit.getReserved38();
                if(!StringUtils.isNullOrEmpty(waferMark)){
                    if(waferMark.length() == 4){
                        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]$");
                        Matcher matcher = pattern.matcher(waferMark);
                        if(!matcher.matches()){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                        }
                    } else {
                        Pattern pattern = Pattern.compile("^[_][a-zA-Z0-9]{4}$");
                        Matcher matcher = pattern.matcher(waferMark);
                        if(!matcher.matches()){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_FOUR_CODE_ERROR, materialLotUnit.getLotId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 来料根据晶圆型号、晶圆数量分组
     * @param materialLotUnitList
     * @return
     */
    public Map<String, List<MaterialLotUnit>> groupMaterialLotUnitByMLotIdAndMaterialNameAndWaferQty(List<MaterialLotUnit> materialLotUnitList) {
        return  materialLotUnitList.stream().collect(Collectors.groupingBy(materialLotUnit -> {
            StringBuffer key = new StringBuffer();

            key.append(materialLotUnit.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            key.append(materialLotUnit.getCurrentSubQty());
            key.append(StringUtils.SPLIT_CODE);

            return key.toString();
        }));
    }

    /**
     * 委外采购晶圆接收
     * @param materialLotActions
     */
    public void purchaseOutsourceWaferReceive(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Warehouse warehouse = new Warehouse();
            for(MaterialLot materialLot : materialLots){
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                String warehouseName = warehouse.getName();

                log.info("receive materialLot and materialLotUnits");
                materialLotUnitService.receiveMLotWithUnit(materialLot, warehouseName);
            }

            for(MaterialLot materialLot : materialLots){
                warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                log.info("insert materialLot to mte_in_stock");
                saveErpInStock(materialLot, materialLot.getProductType(), warehouse.getName());
                log.info("insert materialLot to  mte_in_stock end");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 给中间表保存数据
     * @param materialLot
     * @param prodCate
     * @param warehouseName
     * @throws ClientException
     */
    private void saveErpInStock(MaterialLot materialLot, String prodCate, String warehouseName) throws ClientException{
        try {
            if(!MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7()) && !MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7())) {
                ErpInStock erpInStock = new ErpInStock();
                erpInStock.setProdCate(prodCate);
                erpInStock.setMaterialLot(materialLot);
                if(ErpInStock.WAREHOUSE_ZJ_STOCK.equals(warehouseName)){
                    erpInStock.setWarehouse(ErpInStock.ZJ_STOCK);
                } else if(ErpInStock.WAREHOUSE_SH_STOCK.equals(warehouseName)){
                    erpInStock.setWarehouse(ErpInStock.SH_STOCK);
                } else if(ErpInStock.WAREHOUSE_HK_STOCK.equals(warehouseName)){
                    erpInStock.setWarehouse(ErpInStock.HK_STOCK);
                } else if(ErpInStock.WAREHOUSE_BS_STOCK.equals(warehouseName)){
                    erpInStock.setWarehouse(ErpInStock.BS_STOCK);
                } else {
                    throw new ClientParameterException(GcExceptions.ERP_WAREHOUSE_CODE_IS_UNDEFINED, warehouseName);
                }
                erpInStockRepository.save(erpInStock);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *物料编码打印
     * @throws ClientException
     */
    public void printMaterialCodeLabel(MaterialLot materialLot, String printType) throws ClientException{
        try{
            List<Map<String, String>> mlotCodePrintParameter = getMlotCodePrintParameter(materialLot, printType);
            if(MLotCodePrint.GENERAL_MLOT_LABEL.equals(printType)){//一般物料标签
                printService.PrintGeneralMLotLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.OPHELION_MLOT_LABEL.equals(printType)){//欧菲光物料标签
                printService.printOphelionMLotLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.BAICHEN_MLOT_LABEL.equals(printType)){//百辰物料标签
                printService.printBaichenMLotLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.GUANGBAO_BOX_LABEL.equals(printType)){//光宝箱标签
                //箱标签在方法内部已经打印
                printService.printGuangBaoVBoxLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.GUANGBAO_VBOX_LABEL.equals(printType)){//光宝真空包标签
                printService.printGuangBaoVBoxLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.COB_GUANGBAO_LABEL.equals(printType)){//COB光宝标签
                printService.printCobGuangBaoLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.HUATIAN_LABEL.equals(printType)){//华天标签
                printService.printHuatianLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.SHENGTAI_BOX_LABEL.equals(printType)){//盛泰箱标签
                printService.printShengTaiVBoxLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.SHENGTAI_VBOX_LABEL.equals(printType)){//盛泰真空包标签
                printService.printShengTaiVBoxLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.BYD_LABEL.equals(printType)){//比亚迪内箱/外箱标签
                printService.prinBydLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.XLGD_BOX_LABEL.equals(printType)){//信利光电标签
                printService.printXLGDBoxLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.SHUN_YU_LABEL.equals(printType)){//舜宇标签
                printService.printShunYuLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.ZHONG_KONG_LABEL.equals(printType)){//中控智慧标签
                printService.printZhongKongLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.XING_ZHI_MLOT_LABEL.equals(printType)){//芯智物料标签
                printService.printXingZhiMLotLabel(mlotCodePrintParameter);
            }else if(MLotCodePrint.LONGTEN_MLOT_LABEL.equals(printType)){//龙腾光电
                printService.printLongTenMLotLabel(mlotCodePrintParameter);
            }else{
                throw new ClientParameterException(GcExceptions.PRINT_TYPE_IS_NOT_SUPPORTED, printType);
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取物料编码标签打印参数
     * @param materialLot
     * @param printType
     * @return
     */
    public List<Map<String, String>> getMlotCodePrintParameter(MaterialLot materialLot, String printType) throws ClientException {
        try {
            ThreadLocalContext.getSessionContext().buildTransInfo();
            List<MaterialLot> packageMLotList = Lists.newArrayList();
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            //获取当前日期，时间格式yyyy-MM-dd
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar calendar = Calendar.getInstance();
            String date = formatter.format(new Date());
            Warehouse warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
            ErpSoa erpSoa = getErpSoaByReserved16(materialLot.getReserved16());
            String productType = getProductType(materialLot.getMaterialName());

            //将物料编码记录到真空包上
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                packageMLotList = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                for(MaterialLot packedMLot : packageMLotList){
                    packedMLot.setMaterialCode(erpSoa.getOther10());
                    materialLotRepository.saveAndFlush(packedMLot);
                }
            }

            if(MLotCodePrint.GENERAL_MLOT_LABEL.equals(printType)){
                Integer boxPrintCount = 1;
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> vBoxParameterMap = getGeneralMLotPrintParamater(erpSoa, packageMLot, warehouse, date, productType, MLotCodePrint.VBOX_LABEL, new Integer(1));
                        parameterMapList.add(vBoxParameterMap);
                    }
//                    boxPrintCount = 2;
                }
                Map<String, String> boxParameterMap = getGeneralMLotPrintParamater(erpSoa, materialLot, warehouse, date, productType, MLotCodePrint.BOX_LABEL, boxPrintCount);
                parameterMapList.add(boxParameterMap);
            } else if (MLotCodePrint.OPHELION_MLOT_LABEL.equals(printType)){
                String startDate = formatter.format(materialLot.getReceiveDate());
                calendar.setTime(materialLot.getReceiveDate());
                calendar.add(Calendar.YEAR, +1);
                String endDate = formatter.format(calendar.getTime());
                String [] endDateStrArray = endDate.split(StringUtils.SPLIT_CODE);
                if(endDateStrArray[1].equals("02") && endDateStrArray[2].equals("29")){
                    endDate = endDateStrArray[0] + StringUtils.SPLIT_CODE + endDateStrArray[1] + StringUtils.SPLIT_CODE + "28";
                }
                formatter = new SimpleDateFormat(MLotCodePrint.DATE_PATTERN);
                String effectiveDate = formatter.format(materialLot.getReceiveDate());//有效日期
                String expirationDate = formatter.format(calendar.getTime());//失效日期
                String printSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_OPHELION_MLOT_LABEL_PRINT_SEQ_RULE).substring(2, 7);

                Integer printCount = 1;
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    for(MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getOphelionMLotPrintParamater(erpSoa, packageMLot, startDate, date, endDate, effectiveDate, expirationDate, printSeq);
//                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    printCount = 2;
                }
                Map<String, String> ophelionParamaterMap = getOphelionMLotPrintParamater(erpSoa, materialLot, startDate, date, endDate, effectiveDate, expirationDate, printSeq);
//                ophelionParamaterMap.put("printCount", printCount + StringUtils.EMPTY);
                parameterMapList.add(ophelionParamaterMap);
            } else if(MLotCodePrint.BAICHEN_MLOT_LABEL.equals(printType)){
                String firstVboxSeq = "";
                String poName = erpSoa.getOther10();
                String ponoPrefix = "";
                if(!StringUtils.isNullOrEmpty(poName)){
                    int poSize = poName.length();
                    if(poSize < 20){
                        ponoPrefix = StringUtil.leftPad("" , 20 - poSize , "#");
                    }
                }
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BAICHEN_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 16);
                        if(StringUtils.isNullOrEmpty(firstVboxSeq)){
                            firstVboxSeq = vboxSeq;
                        }
                        Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSoa, packageMLot, vboxSeq, ponoPrefix);
//                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSoa, materialLot, firstVboxSeq, ponoPrefix);
//                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                } else {
                    String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BAICHEN_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 16);
                    vboxSeq = vboxSeq.substring(vboxSeq.length() - 8, vboxSeq.length());
                    Map<String, String> parameterMap = getBaiChenMLotPrintParamater(erpSoa, materialLot, vboxSeq, ponoPrefix);
//                    parameterMap.put("printCount", "1");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.GUANGBAO_BOX_LABEL.equals(printType)){
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    Map<String, String> boxParamaterMap = getGeneralMLotPrintParamater(erpSoa, materialLot, warehouse, date, productType, MLotCodePrint.BOX_LABEL, new Integer(2));
                    // 一般物料标签
                    printService.PrintGeneralMLotLabel(Lists.newArrayList(boxParamaterMap));

                    for(MaterialLot packageMLot : packageMLotList){
                        Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSoa, packageMLot);
                        parameterMapList.add(parameterMap);
                    }
                } else {
                    Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSoa, materialLot);
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.GUANGBAO_VBOX_LABEL.equals(printType)){
                Map<String, String> parameterMap = getGuangBaoVboxMLotPrintParamater(erpSoa, materialLot);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.COB_GUANGBAO_LABEL.equals(printType)){
                String materialDesc = StringUtils.EMPTY;
                List<NBOwnerReferenceList> productDescList = getProductDecsList();
                if(CollectionUtils.isNotEmpty(productDescList)){
                    for (NBOwnerReferenceList referenceList : productDescList) {
                        if(referenceList.getKey().equals(materialLot.getMaterialName())){
                            materialDesc = referenceList.getValue();
                        }
                    }
                }
                if(StringUtils.isNullOrEmpty(materialDesc)){
                    throw new ClientParameterException(GcExceptions.MATERIAL_NAME_DESCRIPTION_IS_NOT_CONFIGURED, materialLot.getMaterialName());
                }
                Map<String, String> parameterMap = getCOBGuangBaoMLotPrintParamater(erpSoa, materialLot, materialDesc);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.HUATIAN_LABEL.equals(printType)){
                String huaTianPrintSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_HUATIAN_LABEL_PRINT_SEQ_RULE);
                calendar.setTime(new Date());
                calendar.add(Calendar.YEAR, +1);
                String effectiveDate = formatter.format(calendar.getTime());
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                    for(MaterialLot packageMLot : packageMLotList){
                        Map<String, String> parameterMap = getHuaTianMLotPrintParamater(erpSoa, packageMLot, warehouse, huaTianPrintSeq, productType, date, effectiveDate);
//                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getHuaTianMLotPrintParamater(erpSoa, materialLot, warehouse, huaTianPrintSeq, productType, date, effectiveDate);
//                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.SHENGTAI_BOX_LABEL.equals(printType)){
                formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
                String stockOutDate = formatter.format(new Date());
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_LABEL_PRINT_SEQ_RULE);
                    seq = seq.substring(2,6) + seq.substring(8,11);
                    Map<String, String> boxParameterMap = getGeneralMLotPrintParamater(erpSoa, materialLot, warehouse, date, productType, MLotCodePrint.BOX_LABEL, new Integer(2));
                    printService.PrintGeneralMLotLabel(Lists.newArrayList(boxParameterMap));

                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getShengTaiVboxMLotPrintParamater(packageMLot, productType, stockOutDate, seq);
                        parameterMapList.add(parameterMap);
                    }
                }
            } else if(MLotCodePrint.SHENGTAI_VBOX_LABEL.equals(printType)){
                formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
                String stockOutDate = formatter.format(new Date());
                String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_LABEL_PRINT_SEQ_RULE);
                seq = seq.substring(2,6) + seq.substring(8,11);
                Map<String, String> parameterMap = getShengTaiVboxMLotPrintParamater(materialLot, productType, stockOutDate, seq);
                parameterMapList.add(parameterMap);
            } else if (MLotCodePrint.BYD_LABEL.equals(printType)){
                String strLabel = MLotCodePrint.BYD_ORDER_ID + StringUtils.SEMICOLON_CODE;
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    String boxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BYD_BOX_LABEL_PRINT_SEQ_RULE).substring(6, 10);
                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getBYDMLotPrintParamater(erpSoa, packageMLot, productType, date);
                        String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_BYD_VBOX_LABEL_PRINT_SEQ_RULE).substring(7, 11);
                        //截取箱号数字起6位作为标签号
                        String vboxLabelId = getLabelIdByMLotId(packageMLot.getMaterialLotId());
                        parameterMap.put("STRLABEL", strLabel + vboxLabelId + vboxSeq);
//                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getBYDMLotPrintParamater(erpSoa, materialLot, productType, date);
                    String boxLabelId = getLabelIdByMLotId(materialLot.getMaterialLotId());
                    parameterMap.put("STRLABEL", strLabel + boxLabelId + boxSeq);
//                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if(MLotCodePrint.XLGD_BOX_LABEL.equals(printType)){
                Map<String, String> parameterMap = getXLGDMLotPrintParamater(erpSoa, materialLot, productType);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.SHUN_YU_LABEL.equals(printType)){
                if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())) {
                    for (MaterialLot packageMLot : packageMLotList) {
                        Map<String, String> parameterMap = getShunYuMLotPrintParamater(erpSoa, packageMLot, productType);
                        //截取箱号数字起6位作为生产批号
                        String batchNumber = getLabelIdByMLotId(packageMLot.getMaterialLotId());
                        parameterMap.put("BATCHNUMBER",  batchNumber );
//                        parameterMap.put("printCount", "1");
                        parameterMapList.add(parameterMap);
                    }
                    Map<String, String> parameterMap = getShunYuMLotPrintParamater(erpSoa, materialLot, productType);
                    String batchNumber = getLabelIdByMLotId(materialLot.getMaterialLotId());
                    parameterMap.put("BATCHNUMBER", batchNumber);
//                    parameterMap.put("printCount", "2");
                    parameterMapList.add(parameterMap);
                }
            } else if (MLotCodePrint.ZHONG_KONG_LABEL.equals(printType)){
                Map<String, String> parameterMap = getZhongKongMLotPrintParamater(erpSoa, materialLot, date, productType);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.XING_ZHI_MLOT_LABEL.equals(printType)){
                Map<String, String> parameterMap = getXingZhiMLotPrintParamater(erpSoa, materialLot, date);
                parameterMapList.add(parameterMap);
            } else if(MLotCodePrint.LONGTEN_MLOT_LABEL.equals(printType)){
                Map<String, String> parameterMap = getLongTenMLotPrintParamater(erpSoa, materialLot, date, productType);
                parameterMapList.add(parameterMap);
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 龙腾光电标签
     * @param materialLot
     * @param date
     * @return
     * @throws ClientException
     */
    private Map<String,String> getLongTenMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String date, String productType) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("PRODUCTID", productType);
            parameterMap.put("NUM", materialLot.getCurrentQty().toString());
            parameterMap.put("SALEID", erpSoa.getOther6());
            parameterMap.put("DATE", date);
            parameterMap.put("VENDER", materialLot.getReserved22());
            parameterMap.put("portId", MLotCodePrint.LONGTEN_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,String> getXingZhiMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String date) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String printSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_XINZHI_MLOT_LABEL_PRINT_SEQ_RULE).substring(8, 11);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.SHUNYU_PRINT_DATE_PATTERN);
            String createDate = formatter.format(materialLot.getCreated());
            parameterMap.put("STRPONO", erpSoa.getOther6());
            parameterMap.put("STRPL", date + printSeq);
            parameterMap.put("STRBOXID", materialLot.getMaterialLotId());
            parameterMap.put("STRWEIGHT", "/");
            String materialCode = erpSoa.getOther10();
            if(StringUtils.isNullOrEmpty(materialCode)){
                materialCode = "/";
            }
            parameterMap.put("STRPN", materialCode);
            parameterMap.put("STRDC", createDate);
            parameterMap.put("STRTOTALQTY", materialLot.getCurrentQty().toString());
            String poNo = StringUtil.rightPad(erpSoa.getOther6() , 30 , "@");
            String strPL = StringUtil.rightPad(date + printSeq , 20 , "@");
            String strPN = StringUtil.rightPad(materialCode , 25 , "@");
            String origin = StringUtil.rightPad(MLotCodePrint.ORIGIN , 9 , "@");
            String totalQty = StringUtil.rightPad(materialLot.getCurrentQty().toString() , 6 , "@");
            String qrCode = poNo + strPL + strPN;
            List<MaterialLot> materialLotList = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            Integer seq = 1;
            for(MaterialLot mLot : materialLotList){
                qrCode += StringUtil.rightPad(mLot.getMaterialLotId() , 20 , "@");
                qrCode += StringUtil.rightPad(createDate , 10 , "@");
                qrCode += StringUtil.leftPad(seq.toString() , 2 , "0");
                qrCode += StringUtil.rightPad(mLot.getCurrentQty().toString() , 6 , "@");
                qrCode += MLotCodePrint.VBOX_SEQ;
                ++seq;
            }
            qrCode = qrCode + origin + totalQty + MLotCodePrint.END_STR;
            parameterMap.put("STRQRCODE", qrCode);
            parameterMap.put("portId", MLotCodePrint.XING_ZHI_PORTID);
//            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 中控智慧标签
     * @param erpSoa
     * @param materialLot
     * @param date
     * @return
     * @throws ClientException
     */
    private Map<String,String> getZhongKongMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String date, String productType) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("NUMBER", erpSoa.getOther10());
            parameterMap.put("PRODUCTNAME", productType);
            parameterMap.put("NUM", materialLot.getCurrentQty().toString());
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("DATE", date);
            parameterMap.put("ADDRESS", erpSoa.getShipAddress());
            String qrCode = ",," + materialLot.getCurrentQty().toString() + ",,,,ICGKW004A," + erpSoa.getOther10() + "," + materialLot.getMaterialName() + ",," + date + ",";
            parameterMap.put("QRCODE", qrCode);
            parameterMap.put("portId", MLotCodePrint.ZHONG_KONG_PORTID);
//            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 信利光电标签打印
     * @param erpSoa
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getShunYuMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String productType) throws ClientException{
        try {
            SessionContext sc = ThreadLocalContext.getSessionContext();
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.SHUNYU_PRINT_DATE_PATTERN);
            String createDate = formatter.format(materialLot.getCreated());
            String materialLotId =  materialLot.getMaterialLotId();
            String batchNumber = StringUtils.EMPTY;
            if(StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                batchNumber = materialLotId.substring(materialLotId.length() - 10, materialLotId.length() - 4);
            } else {
                batchNumber = materialLotId.substring(materialLotId.length() - 9, materialLotId.length() - 3);
            }
            parameterMap.put("NAME", productType);
            parameterMap.put("CLIENTNAME", erpSoa.getOther10());
            parameterMap.put("USERID", sc.getUsername());
            parameterMap.put("DATE", createDate);
            parameterMap.put("BATCHNUMBER", batchNumber);
            parameterMap.put("TOTALNUM", materialLot.getCurrentQty().toString());
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.SHUNYU_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 信利光电标签打印
     * @param erpSoa
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getXLGDMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String productType) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(materialLot.getCreated());
            calendar.add(Calendar.YEAR, +1);
            calendar.add(Calendar.MONTH, +6);
            String effectiveDate = formatter.format(calendar.getTime());
            String createDate = formatter.format(materialLot.getCreated());
            parameterMap.put("STRORDERNUMBER", erpSoa.getOther6());
            parameterMap.put("STRCODE", erpSoa.getOther10());
            parameterMap.put("STRQUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("STRBATCH", materialLot.getReserved1());
            parameterMap.put("STRTRADETYPE", MLotCodePrint.XLGD_TRADETYPE);
            parameterMap.put("STRNAME", MLotCodePrint.XLGD_NAME);
            parameterMap.put("STRMODEL", productType);
            parameterMap.put("STRPRODUCTIONDATE", createDate);
            parameterMap.put("STRDATE", effectiveDate);
            parameterMap.put("STRBRAND", MLotCodePrint.XLGD_BRAND);
            parameterMap.put("STRBBOXID", materialLot.getMaterialLotId());
            String strQrCode = erpSoa.getOther10() + StringUtils.SEMICOLON_CODE + materialLot.getCurrentQty().toString() +StringUtils.SEMICOLON_CODE + materialLot.getReserved1() +
                    StringUtils.SEMICOLON_CODE + createDate + StringUtils.SEMICOLON_CODE + effectiveDate + StringUtils.SEMICOLON_CODE + "GALAXYCORE.INC;" + erpSoa.getOther6();
            parameterMap.put("STRQRCODE", strQrCode);
            parameterMap.put("portId", MLotCodePrint.XLGD_PORTID);
            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 截取物料批次号数字起6位作为标签号
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    private String getLabelIdByMLotId(String materialLotId) throws ClientException{
        try {
            Pattern pattern = Pattern.compile("([0-9].{5,5})");
            Matcher isCheck = pattern.matcher(materialLotId);
            isCheck.find();
            String labelId = isCheck.group(1);
            return labelId;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 比亚迪内箱标签
     * @param erpSoa
     * @param materialLot
     * @param productType
     * @param date
     * @return
     * @throws ClientException
     */
    private Map<String,String> getBYDMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String productType, String date) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("STRCODING", erpSoa.getOther10());
            parameterMap.put("STRDESCRIPTION", productType);
            parameterMap.put("STRCLIENT", erpSoa.getOther6());
            parameterMap.put("STRDATE", date);
            parameterMap.put("STRBATCH", materialLot.getMaterialLotId());
            parameterMap.put("STRNUM", materialLot.getCurrentQty().toString() + "/PCS");
            parameterMap.put("portId", MLotCodePrint.BYD_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盛泰真空包打印
     * @param materialLot
     * @param productType
     * @param stockOutDate
     * @param seq
     * @return
     * @throws ClientException
     */
    private Map<String,String> getShengTaiVboxMLotPrintParamater(MaterialLot materialLot, String productType, String stockOutDate, String seq) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_SHENGTAI_VBOX_LABEL_PRINT_SEQ_RULE).substring(2,7);
            String strRno = "R" + MLotCodePrint.STR_SUPPLIER + stockOutDate + vboxSeq;
            parameterMap.put("STRPN", productType);
            parameterMap.put("STRPNNAME", productType);
            parameterMap.put("STRSUPPLIER", MLotCodePrint.STR_SUPPLIER);
            parameterMap.put("STRCOUNT", materialLot.getCurrentQty().toString());
            parameterMap.put("STRVID", materialLot.getMaterialLotId());
            parameterMap.put("STRDATE", stockOutDate);
            parameterMap.put("STRNUMBER", seq);
            parameterMap.put("STRRNO", strRno);

            String strQrCode = strRno + "|" + materialLot.getMaterialName() + "|" + seq + "|" + stockOutDate + "|" + MLotCodePrint.STR_SUPPLIER + "|" + materialLot.getCurrentQty().toString();
            parameterMap.put("STRQRCODE", strQrCode);
//            parameterMap.put("printCount", "1");
            parameterMap.put("portId", MLotCodePrint.COB_SHENGTAI_VBOX_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产品号截取产品信息
     * @param materialName
     * @return
     * @throws ClientException
     */
    private String getProductType(String materialName) throws ClientException{
        try {
            String productType = StringUtils.EMPTY;
            String [] materialNameArray = materialName.split(StringUtils.SPLIT_CODE);
            if(materialNameArray.length >= 3){
                productType = materialName.substring(0, materialName.indexOf(StringUtils.SPLIT_CODE,materialName.indexOf(StringUtils.SPLIT_CODE)+1 ));
            } else {
                productType = materialName;
            }
            return productType;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 华天标签打印
     * @param erpSoa
     * @param materialLot
     * @param warehouse
     * @param huaTianPrintSeq
     * @param productType
     * @param stockOutDate
     * @param effectiveDate
     * @return
     * @throws ClientException
     */
    private Map<String,String> getHuaTianMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, Warehouse warehouse, String huaTianPrintSeq,
                                                            String productType, String stockOutDate, String effectiveDate) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("CODE", erpSoa.getOther10());
            if(warehouse.getName().equals(WAREHOUSE_HK)){
                parameterMap.put("SUPPLIERNAME", MLotCodePrint.HK_SUPPLIER);
            } else {
                parameterMap.put("SUPPLIERNAME", MLotCodePrint.SH_SUPPLIER);
            }
            parameterMap.put("TYPE", productType);
            parameterMap.put("ORDER", huaTianPrintSeq);
            parameterMap.put("DATE2", effectiveDate);
            parameterMap.put("NUM", materialLot.getCurrentQty().toString());
            parameterMap.put("DATE", stockOutDate);
            parameterMap.put("ID", erpSoa.getOther6());
            parameterMap.put("portId", MLotCodePrint.COB_HUATIAN_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB光宝标签打印
     * @param erpSoa
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getCOBGuangBaoMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String materialDesc) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String seq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_SEQ_RULE).substring(2, 13);
            String monthSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_MONTH_SEQ_RULE).substring(2, 14);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            String reelId = "I50111" + date.substring(2,4);
            //月份转为16进制
            String month = Integer.toHexString(Integer.parseInt(date.substring(4,6)));
            Integer cobMonthSeq = Integer.parseInt(monthSeq.substring(monthSeq.length() - 6, monthSeq.length()));
            //十进制转36进制
            reelId = reelId + month + StringUtil.leftPad(tenTo36(cobMonthSeq) , 6 , "0");

            parameterMap.put("PARTNUM", erpSoa.getOther10());
            parameterMap.put("MATERIALDESC", materialDesc);
            parameterMap.put("DATECODE", date);
            parameterMap.put("LOTNO", seq);
            parameterMap.put("QUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("REELID", reelId);
            parameterMap.put("CODE", "P" + erpSoa.getOther10() + StringUtils.SEMICOLON_CODE + "D" + date + StringUtils.SEMICOLON_CODE + "L" + seq + StringUtils.SEMICOLON_CODE
                    + "VI50111" + "Q" + materialLot.getCurrentQty().toString()+ StringUtils.SEMICOLON_CODE + "R" + reelId + StringUtils.SEMICOLON_CODE + "U000000");
            parameterMap.put("portId", MLotCodePrint.COB_GUANGBAO_PORTID);
//            parameterMap.put("printCount", "2");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private List<NBOwnerReferenceList> getProductDecsList() throws ClientException {
        List<NBOwnerReferenceList> nbReferenceList = (List<NBOwnerReferenceList>) uiService.getReferenceList(REFERENCE_NAME_PRODUCT_DECS_LIST, NBReferenceList.CATEGORY_OWNER);
        if (CollectionUtils.isNotEmpty(nbReferenceList)) {
            return nbReferenceList;
        }
        return Lists.newArrayList();
    }

    /**
     * 光宝真空包打印参数
     * @param erpSoa
     * @param materialLot
     * @return
     * @throws ClientException
     */
    private Map<String,String> getGuangBaoVboxMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            String vboxSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_SEQ_RULE).substring(2, 13);
            String vboxMonthSeq = generatorMLotsTransId(MLotCodePrint.GENERATOR_GUANGBAO_VBOX_LABEL_PRINT_MONTH_SEQ_RULE).substring(2, 14);
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.PRINT_DATE_PATTERN);
            String date = formatter.format(new Date());
            String reelId = "I50111" + date.substring(2,4);
            String month = Integer.toHexString(Integer.parseInt(date.substring(4,6)));
            Integer monthSeq = Integer.parseInt(vboxMonthSeq.substring(vboxMonthSeq.length() - 6, vboxMonthSeq.length()));
            //十进制转36进制
            reelId = reelId + month + StringUtil.leftPad(tenTo36(monthSeq) , 6 , "0");

            parameterMap.put("PARTNUM", erpSoa.getOther10());
            parameterMap.put("DATECODE", date);
            parameterMap.put("LOTNO", vboxSeq);
            parameterMap.put("QUANTITY", materialLot.getCurrentQty().toString());
            parameterMap.put("REELID", reelId);
            parameterMap.put("WAFERID", materialLot.getMaterialLotId());
            parameterMap.put("CODE", "P" + erpSoa.getOther10() + StringUtils.SEMICOLON_CODE + "D" + date + StringUtils.SEMICOLON_CODE + "L" + vboxSeq + StringUtils.SEMICOLON_CODE
                    + "VI50111" + "Q" + materialLot.getCurrentQty().toString()+ StringUtils.SEMICOLON_CODE + "R" + reelId + StringUtils.SEMICOLON_CODE + "U000000");
            parameterMap.put("portId", MLotCodePrint.GUANGBAO_VBOX_PORTID);
//            parameterMap.put("printCount", "1");
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *  10进制转36进制
     * @param num
     * @return
     */
    public static String tenTo36(int num) {
        StringBuffer sBuffer = new StringBuffer();
        String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
        if(num == 0) {
            sBuffer.append("0");
        }
        while(num > 0) {
            sBuffer.append(X36_ARRAY[num % 36]);
            num = num / 36;
        }
        return sBuffer.reverse().toString();
    }

    /**
     * 欧菲光物料标签
     * @param erpSoa
     * @param materialLot
     * @param startDate
     * @param date
     * @param printSeq
     * @return
     * @throws ClientException
     */
    private Map<String,String> getOphelionMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String startDate, String date,String endDate,String effectiveDate,String expirationDate, String printSeq) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("SUPPLIERCODE", MLotCodePrint.SUPPLIER_CODE);
            parameterMap.put("ORDERID", erpSoa.getDlcode());
            parameterMap.put("MATERIALCODE", erpSoa.getOther10());
            parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
            parameterMap.put("MLOTID", materialLot.getMaterialLotId());
            parameterMap.put("STARTDATE", startDate);
            parameterMap.put("ENDDATE", endDate);
            parameterMap.put("PRINTDATE", date);
            parameterMap.put("QC", MLotCodePrint.QC);
            if(expirationDate.endsWith("0229")){
                expirationDate = expirationDate.substring(0,2) + "0228";
            }
            String code = MLotCodePrint.SUPPLIER_CODE + "|"  + erpSoa.getOther10() + "|" + materialLot.getMaterialLotId() + "|"
                    + materialLot.getCurrentQty().toString() + "|"  + effectiveDate + "|" + expirationDate + "|" + printSeq;
            parameterMap.put("CODE", code);
            parameterMap.put("portId", MLotCodePrint.OPHELION_MLOT_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 一般物料标签/光宝箱标签 参数
     * @param erpSoa
     * @param materialLot
     * @param warehouse
     * @param date
     * @param productType
     * @return
     * @throws ClientException
     */
    private Map<String,String> getGeneralMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot,Warehouse warehouse,
                                                            String date, String productType, String labelName, Integer printCount) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            parameterMap.put("CUSTOMER", erpSoa.getCusname());
            parameterMap.put("MLOTCODE", erpSoa.getOther10());
            if(warehouse.getName().equals(WAREHOUSE_HK)){
                parameterMap.put("SUPPLIER", MLotCodePrint.HK_SUPPLIER);
            } else {
                parameterMap.put("SUPPLIER", MLotCodePrint.SH_SUPPLIER);
            }
            parameterMap.put("CURRENTQTY", materialLot.getCurrentQty().toString());
            parameterMap.put("ORDERID", erpSoa.getOther6());
            parameterMap.put("OUTDATE", date);
            parameterMap.put("DELIVERYPLACE", erpSoa.getOther9());
            parameterMap.put("PRODUCTTYPE", productType);
            parameterMap.put("MLOTID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.GENERAL_MLOT_PORTID);

            parameterMap.put("LABEL", labelName);
//            parameterMap.put("printCount", printCount + "");

            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 百辰物料标签参数
     * @param erpSoa
     * @param materialLot
     * @param vboxSeq
     * @return
     * @throws ClientException
     */
    private Map<String, String> getBaiChenMLotPrintParamater(ErpSoa erpSoa, MaterialLot materialLot, String vboxSeq,String ponoPrefix) throws ClientException{
        try {
            Map<String, String> parameterMap = Maps.newHashMap();
            SimpleDateFormat formatter = new SimpleDateFormat(MLotCodePrint.DATE_PATTERN);
            parameterMap.put("MATERIALCODE", erpSoa.getOther10());
            parameterMap.put("SHIPCODE", MLotCodePrint.SHIP_CODE);
            parameterMap.put("DATEDAY", formatter.format(new Date()));
            parameterMap.put("SERIALCODE", vboxSeq);
            parameterMap.put("TWODCODE1", erpSoa.getOther10() + MLotCodePrint.SHIP_CODE + formatter.format(new Date()) + vboxSeq);

            String packageQty = StringUtil.leftPad(materialLot.getCurrentQty().toString() , 8 , "0");
            parameterMap.put("PONOPREFIX", ponoPrefix);
            parameterMap.put("PONO", erpSoa.getOther6());
            parameterMap.put("PACKAGEQTY", packageQty);
            parameterMap.put("TWODCODE2", ponoPrefix + erpSoa.getOther6() + packageQty);

            parameterMap.put("MEMO", erpSoa.getMemo());
            parameterMap.put("TWODCODE3", erpSoa.getMemo());

            formatter = new SimpleDateFormat(MaterialLot.PRINT_DATE_PATTERN);
            parameterMap.put("DATEMONTH", formatter.format(new Date()));
            parameterMap.put("TWODCODE4", formatter.format(new Date()));
            parameterMap.put("BOXID", materialLot.getMaterialLotId());
            parameterMap.put("portId", MLotCodePrint.BAICHEN_MLOT_PORTID);
            return parameterMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过备货单据rrn获取单据信息
     * @param reserved16
     * @return
     */
    private ErpSoa getErpSoaByReserved16(String reserved16) throws ClientException{
        try {
            long documentLineRrn = Long.parseLong(reserved16);
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);

            Long seq = 0L;
            if(DocumentLine.DOC_MERGE.equals(documentLine.getMergeDoc())){
                seq = Long.parseLong(documentLine.getReserved32());
            } else {
                seq = Long.parseLong(documentLine.getReserved1());
            }
            ErpSoa erpSoa = erpSoaOrderRepository.findBySeq(seq);
            return erpSoa;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 同步其他发料单
     * @throws ClientException
     */
    public void asyncOtherIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutaOrder> otherIssueOrders = erpMaterialOutAOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutaOrder.TYPE_TV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(otherIssueOrders)) {
                Map<String, List<ErpMaterialOutaOrder>> documentIdMap = otherIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutaOrder> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpMaterialOutaOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutaOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCreateSeq));
                    //由于取消值为WaferIssueA的CATEGORY，所以用WaferIssueOrder替代OtherIssueOrder
                    List<WaferIssueOrder> otherIssueOrderList = waferIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherIssueOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            updateErpMaterialOutAErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    WaferIssueOrder otherIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherIssueOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                            }
                            continue;
                        }
                        otherIssueOrder = new WaferIssueOrder();
                        otherIssueOrder.setName(documentId);
                        otherIssueOrder.setStatus(Document.STATUS_OPEN);
                        otherIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                    } else {
                        otherIssueOrder = otherIssueOrderList.get(0);
                        totalQty = otherIssueOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherIssueOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }
                    otherIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherIssueOrder.getObjectRrn() != null) {
                                validationIssueOrderQty(otherIssueOrder.getObjectRrn(), erpMaterialOutaOrder);
                            }
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                setIssueOrderInfoToDocumentLine(documentLine, erpMaterialOutaOrder);
                                documentLine.setDocId(documentId);
                                documentLine.setReserved9(ErpMaterialOutaOrder.CATEGORY_WAFER_ISSUEA);
                            }
                            documentLine.setReserved8(erpMaterialOutaOrder.getCusname());
                            documentLine.setReserved12(erpMaterialOutaOrder.getOther8());
                            totalQty = totalQty.add(erpMaterialOutaOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutaOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            otherIssueOrder.setOwner(erpMaterialOutaOrder.getChandler());
                            otherIssueOrder.setReserved32(erpMaterialOutaOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutaOrder.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpMaterialOutaOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutaOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherIssueOrder.setQty(totalQty);
                        otherIssueOrder.setUnHandledQty(otherIssueOrder.getQty().subtract(otherIssueOrder.getHandledQty()));
                        otherIssueOrder.setUnReservedQty(otherIssueOrder.getQty().subtract(otherIssueOrder.getReservedQty()));

                        otherIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                        otherIssueOrder = (WaferIssueOrder) baseService.saveEntity(otherIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherIssueOrder);
                        baseService.saveEntity(documentLine);
                    }
                }

                updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发料单信息整合
     * @param documentLine
     * @param erpMaterialOutaOrder
     * @throws ClientException
     */
    private void setIssueOrderInfoToDocumentLine(DocumentLine documentLine, ErpMaterialOutaOrder erpMaterialOutaOrder) throws ClientException{
        try {
            Material material = mmsService.getRawMaterialByName(erpMaterialOutaOrder.getCinvcode());
            if (material == null) {
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, erpMaterialOutaOrder.getCinvcode());
            }
            documentLine.setErpCreated(DateUtils.parseDate(erpMaterialOutaOrder.getDdate()));
            documentLine.setMaterialRrn(material.getObjectRrn());
            documentLine.setMaterialName(material.getName());
            documentLine.setReserved1(String.valueOf(erpMaterialOutaOrder.getSeq()));
            documentLine.setReserved2(erpMaterialOutaOrder.getSecondcode());
            documentLine.setReserved3(erpMaterialOutaOrder.getGrade());
            documentLine.setReserved5(erpMaterialOutaOrder.getCmaker());
            documentLine.setReserved6(erpMaterialOutaOrder.getChandler());
            documentLine.setReserved7(erpMaterialOutaOrder.getOther1());
            documentLine.setReserved13(erpMaterialOutaOrder.getCmemo());
            documentLine.setMaterialType(erpMaterialOutaOrder.getCfree4());
            documentLine.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
            documentLine.setReserved4(erpMaterialOutaOrder.getCfree3());
            documentLine.setReserved8(erpMaterialOutaOrder.getCusname());
            documentLine.setReserved10(erpMaterialOutaOrder.getGCode());
            documentLine.setDocType(erpMaterialOutaOrder.getCvouchtype());
            documentLine.setDocName(erpMaterialOutaOrder.getCvouchname());
            documentLine.setDocBusType(erpMaterialOutaOrder.getCbustype());
            documentLine.setDocSource(erpMaterialOutaOrder.getCsource());
            documentLine.setWarehouseCode(erpMaterialOutaOrder.getCwhcode());
            documentLine.setWarehouseName(erpMaterialOutaOrder.getCwhname());
            documentLine.setReserved11(erpMaterialOutaOrder.getGName());
            documentLine.setReserved12(erpMaterialOutaOrder.getOther8());
            documentLine.setReserved15(erpMaterialOutaOrder.getOther18());
            documentLine.setReserved17(erpMaterialOutaOrder.getOther3());
            documentLine.setReserved20(erpMaterialOutaOrder.getOther9());
            documentLine.setReserved21(erpMaterialOutaOrder.getOther10());
            documentLine.setReserved27(erpMaterialOutaOrder.getOther7());
            documentLine.setReserved28(erpMaterialOutaOrder.getOther4());
            documentLine.setReserved30(erpMaterialOutaOrder.getOther5());
            documentLine.setCrdCode(erpMaterialOutaOrder.getCrdcode());
            documentLine.setCrdName(erpMaterialOutaOrder.getCrdname());
            documentLine.setAutoid(erpMaterialOutaOrder.getOther16());
            documentLine.setCuscode(erpMaterialOutaOrder.getCuscode());
            documentLine.setProductType(erpMaterialOutaOrder.getOther15());
        } catch (Exception e) {
            throw  ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证发料单的数量是否正确
     * @param issueOrderRrn
     * @param erpMaterialOutaOrder
     */
    private void validationIssueOrderQty(Long issueOrderRrn, ErpMaterialOutaOrder erpMaterialOutaOrder) throws ClientException{
        try {
            DocumentLine documentLine = documentLineRepository.findByDocRrnAndReserved1(issueOrderRrn, String.valueOf(erpMaterialOutaOrder.getSeq()));
            if (documentLine != null) {
                if (ErpMaterialOutaOrder.SYNC_STATUS_CHANGED.equals(erpMaterialOutaOrder.getSynStatus())) {
                    if (documentLine != null && documentLine.getHandledQty().compareTo(erpMaterialOutaOrder.getIquantity()) > 0) {
                        throw new ClientException("gc.order_handled_qty_gt_qty");
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改同步成功之后的状态
     * @param asyncSuccessSeqList
     * @param asyncDuplicateSeqList
     */
    private void updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(List<Long> asyncSuccessSeqList, List<Long> asyncDuplicateSeqList) throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                    for(List seqGroup : asyncSuccessSeqGroupList){
                        erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                }
            }

            if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                    List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                    for(List seqGroup : asyncDuplicateSeqGroupList){
                        erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);
                    }
                } else {
                    erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpMaterialOutaOrder.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步原材料发料单
     * @throws ClientException
     */
    public void asyncMaterialIssueOrder() throws ClientException {
        try {
            List<ErpMaterialOutaOrder> materialIssueOrders = erpMaterialOutAOrderRepository.findByTypeAndSynStatusNotIn(ErpMaterialOutaOrder.TYPE_MV, Lists.newArrayList(ErpSo.SYNC_STATUS_OPERATION, ErpSo.SYNC_STATUS_SYNC_ERROR, ErpSo.SYNC_STATUS_SYNC_SUCCESS));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(materialIssueOrders)) {
                Map<String, List<ErpMaterialOutaOrder>> documentIdMap = materialIssueOrders.stream().collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCcode));
                for (String documentId : documentIdMap.keySet()) {
                    List<ErpMaterialOutaOrder> documentIdList = documentIdMap.get(documentId);
                    Map<String, List<ErpMaterialOutaOrder>> sameCreateSeqOrder = documentIdList.stream().filter(erpMaterialOutaOrder -> !StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpMaterialOutaOrder :: getCreateSeq));
                    List<MaterialIssueOrder> materialIssueOrderList = materialIssueOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(materialIssueOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            updateErpMaterialOutAErrorInfo(documentIdList);
                            continue;
                        }
                    }
                    MaterialIssueOrder materialIssueOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(materialIssueOrderList)) {
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpMaterialOutaOrder erpMOutaOrder : documentIdList) {
                                asyncDuplicateSeqList.add(erpMOutaOrder.getSeq());
                            }
                            continue;
                        }
                        materialIssueOrder = new MaterialIssueOrder();
                        materialIssueOrder.setName(documentId);
                        materialIssueOrder.setStatus(Document.STATUS_OPEN);
                        materialIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                    } else {
                        materialIssueOrder = materialIssueOrderList.get(0);
                        totalQty = materialIssueOrder.getQty();
                        boolean createSeqFlag = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(materialIssueOrder.getReserved32())){
                                createSeqFlag = true;
                                for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdList) {
                                    asyncDuplicateSeqList.add(erpMaterialOutaOrder.getSeq());
                                }
                                break;
                            }
                        }
                        if(createSeqFlag){
                            continue;
                        }
                    }
                    materialIssueOrder.setName(documentId);

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpMaterialOutaOrder erpMaterialOutaOrder : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (materialIssueOrder.getObjectRrn() != null) {
                                validationIssueOrderQty(materialIssueOrder.getObjectRrn(), erpMaterialOutaOrder);
                            }

                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                setIssueOrderInfoToDocumentLine(documentLine, erpMaterialOutaOrder);
                                documentLine.setReserved9(MaterialIssueOrder.CATEGORY_WAFER_ISSUE);
                                documentLine.setDocId(documentId);
                            }
                            documentLine.setReserved8(erpMaterialOutaOrder.getCusname());
                            documentLine.setReserved12(erpMaterialOutaOrder.getOther8());
                            totalQty = totalQty.add(erpMaterialOutaOrder.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpMaterialOutaOrder.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            materialIssueOrder.setOwner(erpMaterialOutaOrder.getChandler());
                            materialIssueOrder.setReserved32(erpMaterialOutaOrder.getCreateSeq());
                            asyncSuccessSeqList.add(erpMaterialOutaOrder.getSeq());
                        } catch (Exception e) {
                            erpMaterialOutaOrder.setUserId(Document.SYNC_USER_ID);
                            erpMaterialOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpMaterialOutaOrder.setErrorMemo(e.getMessage());
                            erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        materialIssueOrder.setQty(totalQty);
                        materialIssueOrder.setUnHandledQty(materialIssueOrder.getQty().subtract(materialIssueOrder.getHandledQty()));
                        materialIssueOrder.setUnReservedQty(materialIssueOrder.getQty().subtract(materialIssueOrder.getReservedQty()));

                        materialIssueOrder.setReserved31(ErpMaterialOutaOrder.SOURCE_TABLE_NAME);
                        materialIssueOrder = (MaterialIssueOrder) baseService.saveEntity(materialIssueOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(materialIssueOrder);
                        baseService.saveEntity(documentLine);
                    }
                }
                updateErpMaterialOutASyncStatusAndErrorMemoAndUserId(asyncSuccessSeqList, asyncDuplicateSeqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步失败，保存同步错误信息
     * @param documentIdList
     * @throws ClientException
     */
    private void updateErpMaterialOutAErrorInfo(List<ErpMaterialOutaOrder> documentIdList) throws ClientException{
        try {
            for(ErpMaterialOutaOrder erpMOutaOrder : documentIdList){
                erpMOutaOrder.setUserId(Document.SYNC_USER_ID);
                erpMOutaOrder.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                erpMOutaOrder.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                erpMaterialOutAOrderRepository.save(erpMOutaOrder);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步其他出货单
     * @throws ClientException
     */
    public void asyncOtherStockOutOrder() throws ClientException {
        try {
            List<ErpSoa> erpSos = erpSoaOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSoa.SYNC_STATUS_OPERATION, ErpSoa.SYNC_STATUS_SYNC_SUCCESS, ErpSoa.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSos)) {
                Map<String, List<ErpSoa>> documentIdMap = erpSos.stream().collect(Collectors.groupingBy(ErpSoa :: getSocode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSoa> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSoa>> sameCreateSeqOrder = documentIdList.stream().filter(erpSoa -> !StringUtils.isNullOrEmpty(erpSoa.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSoa :: getCreateSeq));
                    List<OtherStockOutOrder> otherStockOutOrderList = otherStockOutOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherStockOutOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpSoa erpSoa : documentIdList){
                                erpSoa.setUserId(Document.SYNC_USER_ID);
                                erpSoa.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpSoa.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpSoaOrderRepository.save(erpSoa);
                            }
                            continue;
                        }
                    }
                    OtherStockOutOrder otherStockOutOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherStockOutOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSoa erpSoa : documentIdList) {
                                asyncDuplicateSeqList.add(erpSoa.getSeq());
                            }
                            continue;
                        }
                        otherStockOutOrder = new OtherStockOutOrder();
                        otherStockOutOrder.setName(documentId);
                        otherStockOutOrder.setStatus(Document.STATUS_OPEN);
                        otherStockOutOrder.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                    } else {
                        otherStockOutOrder = otherStockOutOrderList.get(0);
                        totalQty = otherStockOutOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherStockOutOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSoa erpSoa : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSoa.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSoa erpSoa : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherStockOutOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(otherStockOutOrder.getObjectRrn(), String.valueOf(erpSoa.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSoa.SYNC_STATUS_CHANGED.equals(erpSoa.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSoa.getQuantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSoa.getOrderDate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpSoa.getInvcode());
                                if (material == null) {
                                    throw new ClientParameterException(MM_PRODUCT_ID_IS_NOT_EXIST, erpSoa.getInvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(erpCreatedDate);
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());

                                documentLine.setReserved1(String.valueOf(erpSoa.getSeq()));
                                documentLine.setReserved2(erpSoa.getBatch());
                                documentLine.setReserved3(erpSoa.getGrade());
                                documentLine.setReserved4(erpSoa.getFree3());
                                documentLine.setReserved5(erpSoa.getShipMaker());
                                documentLine.setReserved6(erpSoa.getShipVerifier());
                                documentLine.setReserved7(erpSoa.getOther1());

                                documentLine.setReserved8(erpSoa.getCusname());
                                documentLine.setReserved9(OtherStockOutOrder.CATEGORY_DELIVERYA);

                                documentLine.setReserved10(erpSoa.getOther16());
                                documentLine.setReserved11(erpSoa.getOther17());
                                documentLine.setReserved12(erpSoa.getCusabbName());
                                documentLine.setReserved13(erpSoa.getMemo());
                                documentLine.setReserved14(erpSoa.getItemcode());
                                documentLine.setReserved15(erpSoa.getShipAddress());
                                documentLine.setReserved17(erpSoa.getOther13());
                                documentLine.setReserved18(erpSoa.getOther6());
                                documentLine.setReserved19(erpSoa.getOther8());
                                documentLine.setReserved20(erpSoa.getCusperson());
                                documentLine.setReserved21(erpSoa.getCusphone());
                                documentLine.setReserved22(erpSoa.getOther11());
                                documentLine.setReserved23(erpSoa.getOther12());
                                documentLine.setReserved24(erpSoa.getOther13());
                                documentLine.setReserved27(erpSoa.getOdm());
                                documentLine.setReserved28(erpSoa.getOther19());
                                documentLine.setReserved29(erpSoa.getOther18());
                                documentLine.setDocName(erpSoa.getStcode());
                                documentLine.setDocBusType(erpSoa.getBustype());
                                documentLine.setReserved30(erpSoa.getOther20());
                                documentLine.setProductType(erpSoa.getOther15());
                                documentLine.setSaleCode(erpSoa.getDlcode());
                                documentLine.setShipDate(erpSoa.getShipDate());
                                documentLine.setWarehouseCode(erpSoa.getWhcode());
                                documentLine.setWarehouseName(erpSoa.getWhname());
                                documentLine.setShipAutoid(erpSoa.getShipAutoid());
                                documentLine.setSaleType(erpSoa.getOther2());
                                documentLine.setShipCustomer(erpSoa.getOther9());
                                documentLine.setCargoCode(erpSoa.getOther10());
                                documentLine.setReserved33(erpSoa.getOther5());
                                documentLine.setReserved34(erpSoa.getOther7());
                                documentLine.setReserved35(erpSoa.getOther14());
                                documentLine.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                                documentLine.setThreeSideTransaction(erpSoa.getCuscode());
                            }
                            documentLine.setReserved8(erpSoa.getCusname());
                            documentLine.setReserved12(erpSoa.getCusabbName());
                            totalQty = totalQty.add(erpSoa.getQuantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSoa.getQuantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            otherStockOutOrder.setSupplierName(erpSoa.getCusname());
                            otherStockOutOrder.setOwner(erpSoa.getShipVerifier());
                            otherStockOutOrder.setReserved32(erpSoa.getCreateSeq());
                            if (otherStockOutOrder.getErpCreated() == null) {
                                otherStockOutOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (otherStockOutOrder.getErpCreated().after(erpCreatedDate)) {
                                    otherStockOutOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSoa.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSoa.setUserId(Document.SYNC_USER_ID);
                            erpSoa.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSoa.setErrorMemo(e.getMessage());
                            erpSoaOrderRepository.save(erpSoa);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherStockOutOrder.setQty(totalQty);
                        otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getQty().subtract(otherStockOutOrder.getHandledQty()));
                        otherStockOutOrder.setUnReservedQty(otherStockOutOrder.getQty().subtract(otherStockOutOrder.getReservedQty()));
                        otherStockOutOrder.setReserved31(ErpSoa.SOURCE_TABLE_NAME);
                        otherStockOutOrder = (OtherStockOutOrder) baseService.saveEntity(otherStockOutOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherStockOutOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(otherStockOutOrder.getSupplierName())) {
                        savaCustomer(otherStockOutOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);                        }
                    } else {
                        erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSoa.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void savaCustomer(String cuetomerName) throws ClientException{
        try {
            Customer customer = customerRepository.getByName(cuetomerName);
            if (customer == null) {
                customer = new Customer();
                customer.setName(cuetomerName);
                customer.setDescription(cuetomerName);
                customerRepository.save(customer);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步其他发货单
     * @throws ClientException
     */
    public void asyncOtherShipOrder() throws ClientException {
        try {
            List<ErpSob> erpSobs = erpSobOrderRepository.findBySynStatusNotIn(Lists.newArrayList(ErpSob.SYNC_STATUS_OPERATION, ErpSob.SYNC_STATUS_SYNC_SUCCESS, ErpSob.SYNC_STATUS_MERGE));
            List<Long> asyncSuccessSeqList = Lists.newArrayList();
            List<Long> asyncDuplicateSeqList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(erpSobs)) {
                Map<String, List<ErpSob>> documentIdMap = erpSobs.stream().collect(Collectors.groupingBy(ErpSob :: getCcode));

                for (String documentId : documentIdMap.keySet()) {
                    List<ErpSob> documentIdList = documentIdMap.get(documentId);
                    //把即将同步的同Ccode数据按createseq分组
                    Map<String, List<ErpSob>> sameCreateSeqOrder = documentIdList.stream().filter(erpSob -> !StringUtils.isNullOrEmpty(erpSob.getCreateSeq()))
                            .collect(Collectors.groupingBy(ErpSob :: getCreateSeq));
                    List<OtherShipOrder> otherShipOrderList = otherShipOrderRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                    if(CollectionUtils.isEmpty(otherShipOrderList)){
                        List<Document> documentList = documentRepository.findByNameAndOrgRrn(documentId, ThreadLocalContext.getOrgRrn());
                        if(CollectionUtils.isNotEmpty(documentList)){
                            for(ErpSob erpSob : documentIdList){
                                erpSob.setUserId(Document.SYNC_USER_ID);
                                erpSob.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                                erpSob.setErrorMemo(ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID);
                                erpSobOrderRepository.save(erpSob);
                            }
                            continue;
                        }
                    }
                    OtherShipOrder otherShipOrder;
                    BigDecimal totalQty = BigDecimal.ZERO;
                    if (CollectionUtils.isEmpty(otherShipOrderList)) {
                        //如果有不同create_seq
                        if(sameCreateSeqOrder.keySet().size() > 1 ){
                            for  (ErpSob erpSob : documentIdList) {
                                asyncDuplicateSeqList.add(erpSob.getSeq());
                            }
                            continue;
                        }
                        otherShipOrder = new OtherShipOrder();
                        otherShipOrder.setName(documentId);
                        otherShipOrder.setStatus(Document.STATUS_OPEN);
                        otherShipOrder.setReserved31(ErpSob.SOURCE_TABLE_NAME);
                    } else {
                        otherShipOrder = otherShipOrderList.get(0);
                        totalQty = otherShipOrder.getQty();
                        boolean differentCreateSeq = false;
                        for  (String createSeq : sameCreateSeqOrder.keySet()) {
                            if(!createSeq.equals(otherShipOrder.getReserved32())){
                                differentCreateSeq = true;
                                for  (ErpSob erpSob : documentIdList) {
                                    asyncDuplicateSeqList.add(erpSob.getSeq());
                                }
                                break;
                            }
                        }
                        if(differentCreateSeq){
                            continue;
                        }
                    }

                    List<DocumentLine> documentLines = Lists.newArrayList();
                    for  (ErpSob erpSob : documentIdMap.get(documentId)) {
                        try {
                            DocumentLine documentLine = null;
                            if (otherShipOrder.getObjectRrn() != null) {
                                documentLine = documentLineRepository.findByDocRrnAndReserved1(otherShipOrder.getObjectRrn(), String.valueOf(erpSob.getSeq()));
                                if (documentLine != null) {
                                    if (ErpSo.SYNC_STATUS_CHANGED.equals(erpSob.getSynStatus())) {
                                        if (documentLine != null && documentLine.getHandledQty().compareTo(erpSob.getIquantity()) > 0) {
                                            throw new ClientException("gc.order_handled_qty_gt_qty");
                                        }
                                    }
                                }
                            }

                            Date erpCreatedDate = DateUtils.parseDate(erpSob.getDdate());
                            // 当系统中已经同步过这个数据，则除了数量栏位，其他都不能改
                            if (documentLine == null) {
                                documentLine = new DocumentLine();
                                Material material = mmsService.getProductByName(erpSob.getCinvcode());
                                if (material == null) {
                                    material = saveProductAndSetStatusModelRrn(erpSob.getCinvcode());
                                }
                                documentLine.setDocId(documentId);
                                documentLine.setErpCreated(erpCreatedDate);
                                documentLine.setMaterialRrn(material.getObjectRrn());
                                documentLine.setMaterialName(material.getName());

                                documentLine.setReserved1(String.valueOf(erpSob.getSeq()));
                                documentLine.setReserved2(erpSob.getSecondcode());
                                documentLine.setReserved3(erpSob.getGrade());
                                documentLine.setReserved4(erpSob.getCfree3());
                                documentLine.setReserved5(erpSob.getCmaker());
                                documentLine.setReserved6(erpSob.getChandler());
                                documentLine.setReserved7(erpSob.getOther1());
                                documentLine.setReserved8(erpSob.getOther9());

                                documentLine.setReserved9(OtherShipOrder.CATEGORY_DELIVERYB);
                                documentLine.setReserved13(erpSob.getCmemo());
                                documentLine.setReserved16(erpSob.getOther2());
                                documentLine.setReserved19(erpSob.getOther8());
                                documentLine.setReserved20(erpSob.getOther9());
                                documentLine.setReserved21(erpSob.getOther10());
                                documentLine.setReserved22(erpSob.getOther11());
                                documentLine.setReserved23(erpSob.getOther12());
                                documentLine.setReserved24(erpSob.getOther13());
                                documentLine.setReserved25(erpSob.getOther14());
                                documentLine.setReserved26(erpSob.getOther15());
                                documentLine.setReserved30(erpSob.getOther5());
                                documentLine.setStockCode(erpSob.getCfree4() == null ? null : erpSob.getCfree4().trim());
                                documentLine.setDocType(erpSob.getCvouchtype());
                                documentLine.setDocName(erpSob.getCvouchname());
                                documentLine.setDocBusType(erpSob.getCbustype());
                                documentLine.setDocSource(erpSob.getCsource());
                                documentLine.setWarehouseCode(erpSob.getCwhcode());
                                documentLine.setWarehouseName(erpSob.getCwhname());
                                documentLine.setCrdName(erpSob.getCrdname());
                                documentLine.setCrdCode(erpSob.getCrdcode());
                                documentLine.setAutoid(erpSob.getOther16());
                                documentLine.setReserved31(ErpSob.SOURCE_TABLE_NAME);
                            }
                            documentLine.setReserved8(erpSob.getOther9());
                            totalQty = totalQty.add(erpSob.getIquantity().subtract(documentLine.getQty()));
                            documentLine.setQty(erpSob.getIquantity());
                            documentLine.setUnHandledQty(documentLine.getQty().subtract(documentLine.getHandledQty()));
                            documentLine.setUnReservedQty(documentLine.getQty().subtract(documentLine.getReservedQty()));
                            documentLines.add(documentLine);

                            // 同一个单据下，所有的客户都是一样的。
                            otherShipOrder.setSupplierName(erpSob.getOther9());
                            otherShipOrder.setOwner(erpSob.getChandler());
                            otherShipOrder.setReserved32(erpSob.getCreateSeq());
                            if (otherShipOrder.getErpCreated() == null) {
                                otherShipOrder.setErpCreated(erpCreatedDate);
                            } else {
                                if (otherShipOrder.getErpCreated().after(erpCreatedDate)) {
                                    otherShipOrder.setErpCreated(erpCreatedDate);
                                }
                            }
                            asyncSuccessSeqList.add(erpSob.getSeq());
                        } catch (Exception e) {
                            // 修改状态为2
                            erpSob.setUserId(Document.SYNC_USER_ID);
                            erpSob.setSynStatus(ErpSo.SYNC_STATUS_SYNC_ERROR);
                            erpSob.setErrorMemo(e.getMessage());
                            erpSobOrderRepository.save(erpSob);
                        }
                    }
                    if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                        otherShipOrder.setQty(totalQty);
                        otherShipOrder.setUnHandledQty(otherShipOrder.getQty().subtract(otherShipOrder.getHandledQty()));
                        otherShipOrder.setUnReservedQty(otherShipOrder.getQty().subtract(otherShipOrder.getReservedQty()));
                        otherShipOrder = (OtherShipOrder) baseService.saveEntity(otherShipOrder);
                    }

                    for (DocumentLine documentLine : documentLines) {
                        documentLine.setDoc(otherShipOrder);
                        baseService.saveEntity(documentLine);
                    }

                    // 保存单据的时候同步下客户
                    if (!StringUtils.isNullOrEmpty(otherShipOrder.getSupplierName())) {
                        savaCustomer(otherShipOrder.getSupplierName());
                    }
                }

                if (CollectionUtils.isNotEmpty(asyncSuccessSeqList)) {
                    if(asyncSuccessSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncSuccessSeqGroupList = getSeqListGroupByCount(asyncSuccessSeqList);
                        for(List seqGroup : asyncSuccessSeqGroupList){
                            erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, seqGroup);
                        }
                    } else {
                        erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_SUCCESS, StringUtils.EMPTY, Document.SYNC_USER_ID, asyncSuccessSeqList);
                    }
                }
                if (CollectionUtils.isNotEmpty(asyncDuplicateSeqList)) {
                    if(asyncDuplicateSeqList.size() >= Document.SEQ_MAX_LENGTH){
                        List<List<Long>> asyncDuplicateSeqGroupList = getSeqListGroupByCount(asyncDuplicateSeqList);
                        for(List seqGroup : asyncDuplicateSeqGroupList){
                            erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, seqGroup);                        }
                    } else {
                        erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(ErpSo.SYNC_STATUS_SYNC_ERROR, ErpMaterialOutOrder.ERROR_CODE_DUPLICATE_DOC_ID, Document.SYNC_USER_ID, asyncDuplicateSeqList);
                    }

                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 按条件查询需要取消备货的数据并带出
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotAndDocUserToUnReserved(Long tableRrn, String whereClause) throws ClientException {
        try {
            List<MaterialLot> materialLots = getMaterialLotByTableRrnAndWhereClause(tableRrn, whereClause);
            Map<String, List<MaterialLot>> docLineMaterialLotMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot:: getReserved16));

            for(String docLineRrn : docLineMaterialLotMap.keySet()){
                List<MaterialLot> docLineMaterialLot = docLineMaterialLotMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                for(MaterialLot materialLot : docLineMaterialLot){
                    materialLot.setDocumentLineUser(documentLine.getReserved8());
                }
            }

            return materialLots;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 按条件查询需要标注的物料批次信息
     * @param tableRrn
     * @param whereClause
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByTableRrnAndWhereClause(Long tableRrn, String whereClause) throws ClientException{
        try {
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            if (StringUtils.isNullOrEmpty(_whereClause)) {
                _whereClause = nbTable.getInitWhereClause();
            } else {
                if (!StringUtils.isNullOrEmpty(nbTable.getWhereClause())) {
                    StringBuffer clauseBuffer = new StringBuffer(_whereClause);
                    if(!StringUtils.isNullOrEmpty(whereClause)){
                        clauseBuffer.append(" AND ");
                        clauseBuffer.append(whereClause);
                    }
                    _whereClause = clauseBuffer.toString();
                }
            }

            List<MaterialLot> materialLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            return  materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public String packageIRAs(List<MaterialLotAction> materialLotActions, String packageType) {
        try{
            MaterialLotPackageType materialLotPackageType = packageService.getMaterialPackageTypeByName(packageType);
            String packedMaterialLotId = generatorPackageMLotId(materialLotPackageType);
            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId())).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLot.setLotId(packedMaterialLotId);
                materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_IRA_PACKAGE);
                materialLotHistoryRepository.save(history);
            }
            return packedMaterialLotId;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    @Override
    public void unPackageIRAs(List<MaterialLotAction> materialLotActions, String packageType) {
        try{
            List<MaterialLot> materialLots = materialLotActions.stream().map(action -> mmsService.getMLotByMLotId(action.getMaterialLotId())).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots) {
                materialLot.setLotId(materialLot.getMaterialLotId());
                materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_IRA_UNPACKAGE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public String generatorPackageMLotId(MaterialLotPackageType packageType) throws ClientException{
        try {
            GeneratorContext generatorContext = new GeneratorContext();
            generatorContext.setRuleName(packageType.getPackIdRule());
            return generatorService.generatorId(ThreadLocalContext.getOrgRrn(), generatorContext);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证WLT封装回货模板是否经WLA处理，根据产品型号转换表验证晶圆型号是否需要转换
     * @return
     * @param materialLotUnitList
     * @throws ClientException
     */
    public List<MaterialLotUnit> validateImportWltPackReturn(List<MaterialLotUnit> materialLotUnitList) throws ClientException {
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                // 验证Gross Dies=Sampling Qty+Pass Dies1+Pass Dies2+Pass Dies3+NG Die
                BigDecimal grossDies = materialLotUnit.getCurrentQty();
                String samplingQtyStr = materialLotUnit.getReserved33();
                String passDies1Str = materialLotUnit.getReserved34();
                String ngDieStr = materialLotUnit.getReserved35();
                String passDies2Str = materialLotUnit.getReserved42();
                String passDies3Str = materialLotUnit.getReserved43();

                List<BigDecimal> diesList = Lists.newArrayList(
                        StringUtils.isNullOrEmpty(samplingQtyStr) ? BigDecimal.ZERO : new BigDecimal(samplingQtyStr),
                        StringUtils.isNullOrEmpty(passDies1Str) ? BigDecimal.ZERO : new BigDecimal(passDies1Str),
                        StringUtils.isNullOrEmpty(passDies2Str) ? BigDecimal.ZERO : new BigDecimal(passDies2Str),
                        StringUtils.isNullOrEmpty(passDies3Str) ? BigDecimal.ZERO : new BigDecimal(passDies3Str),
                        StringUtils.isNullOrEmpty(ngDieStr) ? BigDecimal.ZERO : new BigDecimal(ngDieStr));
                BigDecimal totalDies = diesList.stream().collect(CollectorsUtils.summingBigDecimal(dies -> dies));

                if (grossDies.compareTo(totalDies) != 0){
                    throw new ClientParameterException(GcExceptions.ABNORMAL_FILE_QUANTITY,
                            grossDies, samplingQtyStr, passDies1Str, passDies2Str, passDies3Str, ngDieStr);
                }

                //验证GC_WLT_UPLOAD Pass Dies1 ≤ BIN1，Pass Dies2 ≤ BIN2，Pass Dies3 ≤ BIN4
                MesGcWltUpload mesGcWltUpload = mesGcWltUploadRepository.findByWaferId(materialLotUnit.getUnitId());
                if(mesGcWltUpload == null){
                    throw new ClientParameterException(GcExceptions.CANNOT_FIND_TEST_DATA, materialLotUnit.getUnitId());
                }

                Long passDies1 = StringUtils.isNullOrEmpty(passDies1Str) ? 0 : new Long(passDies1Str);
                Long passDies2 = StringUtils.isNullOrEmpty(passDies2Str) ? 0 : new Long(passDies2Str);
                Long passDies3 = StringUtils.isNullOrEmpty(passDies3Str) ? 0 : new Long(passDies3Str);
                if(passDies1 > mesGcWltUpload.getBin1() || passDies2 > mesGcWltUpload.getBin2() || passDies3 > mesGcWltUpload.getBin4()){
                    throw new ClientParameterException(GcExceptions.INCOMINGMLOT_QTY_AND_SENTOUT_QTY_DISCREPANCY, materialLotUnit.getUnitId());
                }
                if (!StringUtils.isNullOrEmpty(mesGcWltUpload.getFlag()) && !MesGcWltUpload.FLAG_NONE.equals(mesGcWltUpload.getFlag())) {
                    materialLotUnit.setSubCode5(mesGcWltUpload.getFlag());
                }
                materialLotUnit.setSourceProductId(mesGcWltUpload.getDevice());

                String materialName = materialLotUnit.getMaterialName();
                materialName = materialName.substring(0, materialName.lastIndexOf(StringUtils.SPLIT_CODE)) + "-3.5";

                Material material = mmsService.getRawMaterialByName(materialName);
                if (material == null){
                    material = new RawMaterial();
                    material.setName(materialName);
                    mmsService.createRawMaterial((RawMaterial) material);
                }
                if(materialName.endsWith("-3.5")){
                    materialLotUnit.setGrade(MaterialLotUnit.MAIN_GRADE);
                }
                materialLotUnit.setMaterialName(materialName);
                materialLotUnit.setReserved6(StringUtils.EMPTY);
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_WLT);
                materialLotUnit.setReserved49(MaterialLot.IMPORT_WLT);
                materialLotUnit.setReserved50("7");
            }
            materialLotUnitList = materialLotUnitService.createFTMLot(materialLotUnitList);
            return materialLotUnitList;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * Wlt/CP验证物料批次的产品、二级代码、保税属性是否一致
     * 不需要验证等级是否一致
     * @param waitValidationMLot 待验证的物料批次
     * @param validatedMLotActions 已经验证过的物料批次
     */
    public boolean validationWltStockOutMaterialLot(MaterialLot waitValidationMLot, List<MaterialLotAction> validatedMLotActions) throws ClientException{
        try {
            boolean flag = true;
            waitValidationMLot = mmsService.getMLotByMLotId(waitValidationMLot.getMaterialLotId(), true);
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationMLot.getMaterialName(),  validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    flag = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved1(),  validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    flag = false;
                }
                try {
                    Assert.assertEquals(waitValidationMLot.getReserved6(),  validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    flag = false;
                }
            }
            return flag;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证扫描的物料批次基础信息是否一致
     * @param waitValidationMLot 待验证的物料批次
     * @param validatedMLotActions 已经验证过的物料批次
     */
    private boolean validationMaterialLotInfo(MaterialLot waitValidationMLot, List<MaterialLotAction> validatedMLotActions, boolean falg)throws ClientException{
        try {
            if (CollectionUtils.isNotEmpty(validatedMLotActions)) {
                MaterialLot validatedMLot = mmsService.getMLotByMLotId(validatedMLotActions.get(0).getMaterialLotId(), true);
                try {
                    Assert.assertEquals(waitValidationMLot.getMaterialName(), validatedMLot.getMaterialName());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved1(), validatedMLot.getReserved1());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getGrade(), validatedMLot.getGrade());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved6(), validatedMLot.getReserved6());
                } catch (AssertionError e) {
                    falg = false;
                }

                try {
                    Assert.assertEquals(waitValidationMLot.getReserved16(), validatedMLot.getReserved16());
                } catch (AssertionError e) {
                    falg = false;
                }
            }
            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 三方销售
     * @param documentLine
     * @param materialLotActionList
     * @throws ClientException
     */
    public void wltCpThreeSideShip(DocumentLine documentLine, List<MaterialLotAction> materialLotActionList) throws ClientException{
        try {
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            List<MaterialLot> materialLotList = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getThreeSideOrder())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(materialLotList)){
                for(MaterialLot materialLot : materialLotList){
                    DocumentLine threeSideOrder = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLot.getThreeSideOrder()));
                    if(documentLine.getObjectRrn() == threeSideOrder.getObjectRrn()){
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_HAS_BEEN_SOLD_BY_THREE_PARTIES, materialLotList.get(0).getMaterialLotId(), documentLine.getDocId());
                    }
                }
            }

            BigDecimal handledQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLots){
                String materialName = materialLot.getMaterialName();
                if(materialName.endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialName.endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                    handledQty = handledQty.add(materialLot.getCurrentQty());
                } else {
                    handledQty = handledQty.add(materialLot.getCurrentSubQty());
                }
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.MLOT_THREESIDE_DOC_VALIDATE_RULE_ID);
            }

            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
            }

            for (MaterialLot materialLot : materialLots) {
                materialLot = changeMLotWarehouseByDocumentLineAndSaveHis(materialLot, documentLine);
                List<MaterialLot> packageLotDetails = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                if(CollectionUtils.isNotEmpty(packageLotDetails)){
                    for (MaterialLot packageLot : packageLotDetails){
                        packageLot.setReserved13(materialLot.getReserved13());
                        packageLot.setReserved6(materialLot.getReserved6());
                        packageLot.setThreeSideOrder(documentLine.getObjectRrn().toString());
                        packageLot = materialLotRepository.saveAndFlush(packageLot);

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(packageLot, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
                        materialLotHistoryRepository.save(history);

                        saveMaterialLotUnitWarehouseAndSaveHis(packageLot, materialLot.getReserved13(), materialLot.getReserved6());
                    }
                } else if(StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                    saveMaterialLotUnitWarehouseAndSaveHis(materialLot, materialLot.getReserved13(), materialLot.getReserved6());
                }
            }

            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);

            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证单据是否合单，并更新中间表ERP_SOA的数量信息
     * @param documentLine
     * @param handledQty
     */
    private void validateAndUpdateErpSoa(DocumentLine documentLine, BigDecimal handledQty) throws ClientException {
        try{
            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSoa> erpSoaOptional = erpSoaOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoaOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_SOA_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSoa erpSoa = erpSoaOptional.get();
                erpSoa.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSoa.setLeftNum(erpSoa.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSoa.getDeliveredNum())) {
                    erpSoa.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSoa.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSoa.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSoaOrderRepository.save(erpSoa);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改晶圆的仓库和保税属性
     * @param materialLot
     * @param warehouseRrn
     * @param bondedProperty
     * @throws ClientException
     */
    private void saveMaterialLotUnitWarehouseAndSaveHis(MaterialLot materialLot, String warehouseRrn, String bondedProperty) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setReserved13(warehouseRrn);
                materialLotUnit.setReserved4(bondedProperty);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据单据的三方标记修改物料批次的仓库和保税属性
     * @param materialLot
     * @param documentLine
     * @throws ClientException
     */
    private MaterialLot changeMLotWarehouseByDocumentLineAndSaveHis(MaterialLot materialLot, DocumentLine documentLine) throws ClientException{
        try {
            String cuscode = documentLine.getThreeSideTransaction();
            if(DocumentLine.CUSCODE_9006.equals(cuscode) || DocumentLine.CUSCODE_C1001.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_SH, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_SH);
            } else if(DocumentLine.CUSCODE_C2837.equals(cuscode) || DocumentLine.CUSCODE_C9009.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_ZJ, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_ZSH);
            } else if(DocumentLine.CUSCODE_C001.equals(cuscode)) {
                List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_HK, ThreadLocalContext.getOrgRrn());
                materialLot.setReserved13(warehouseList.get(0).getObjectRrn().toString());
                materialLot.setReserved6(BONDED_PROPERTITY_HK);
            } else {
                throw new ClientParameterException(GcExceptions.ERP_SOA_CUSCODE_IS_ERROR, cuscode);
            }
            materialLot.setThreeSideOrder(documentLine.getObjectRrn().toString());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_THREE_SIDE);
            materialLotHistoryRepository.save(history);

            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP销售出货
     * @param documentLineList
     * @param materialLotActions
     * @param checkSubCode
     * @throws ClientException
     */
    public void wltCpMaterialLotSaleShip(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode, String actionType) throws ClientException{
        try {
            List<DocumentLine> saleShipOrder = documentLineList.stream().filter(documentLine ->  ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31()) &&
                    !DocumentLine.CUSCODE_9006.equals(documentLine.getThreeSideTransaction()) && !DocumentLine.CUSCODE_C1001.equals(documentLine.getThreeSideTransaction())
                            && !DocumentLine.CUSCODE_C2837.equals(documentLine.getThreeSideTransaction()) && !DocumentLine.CUSCODE_C9009.equals(documentLine.getThreeSideTransaction())
                            && !DocumentLine.CUSCODE_C001.equals(documentLine.getThreeSideTransaction())).collect(Collectors.toList());

            if(CollectionUtils.isEmpty(saleShipOrder)){
                throw new ClientParameterException(GcExceptions.CHOOSE_STOCK_OUT_ORDER_PLEASE, ErpSoa.SOURCE_TABLE_NAME);
            }
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            documentLineList = saleShipOrder.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            Map<String, List<DocumentLine>> saleShipOrderMap = groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(documentLineList, checkSubCode);
            Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(materialLots, checkSubCode);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(key, materialLotMap, saleShipOrderMap);
                wltCpStockOut(saleShipOrderMap.get(key), materialLotMap.get(key), actionType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 “WLT/CP销售出货”
     * @param materialLotActions
     * @param erpTime
     * @param checkSubCode
     * @throws ClientException
     */
    public void mobileWltCpMaterialLotSaleShip(List<MaterialLotAction> materialLotActions, String erpTime, String checkSubCode, String actionType) throws ClientException {
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_WLT_OR_CP_STOCK_OUT_ORDER_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            wltCpMaterialLotSaleShip(documentLineList, materialLotActions, checkSubCode, actionType);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW属性转换
     * @param materialLots
     * @throws ClientException
     */
    @Override
    public void rWAttributeChange(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<MaterialLot> unPackedMLotList = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.toList());
            Map<String, List<MaterialLot>> packedMLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())).collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if (packedMLotMap.size() > 0) {
                for (String parentMaterialLotId : packedMLotMap.keySet()) {
                    List<MaterialLot> packedMLotList = packedMLotMap.get(parentMaterialLotId);
                    List<MaterialLot> materialLotList = materialLotRepository.getByParentMaterialLotId(parentMaterialLotId);
                    if (packedMLotList.size() == materialLotList.size()){
                        for (MaterialLot materialLot : materialLotList) {
                            rwLotAttributeChange(materialLot);
                        }
                        MaterialLot packedMaterialLot = mmsService.getMLotByMLotId(parentMaterialLotId);
                        rwLotAttributeChange(packedMaterialLot);
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKAGE_MUST_REMARK_ALL, parentMaterialLotId);
                    }
                }
            }
            for(MaterialLot materialLot : unPackedMLotList){
                rwLotAttributeChange(materialLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改单个materialLot的属性
     * @param materialLot
     * @throws ClientException
     */
    private void rwLotAttributeChange(MaterialLot materialLot) throws ClientException {
        try {
            String waferSource = materialLot.getReserved50();
            String strProdcutType = materialLot.getMaterialName();
            if (!StringUtils.isNullOrEmpty(waferSource)){
                if (MaterialLot.RW_TO_CP_WAFER_SOURCE.equals(waferSource)) {
                    if(strProdcutType.endsWith("-2.1")){
                        materialLot.setReserved50(MaterialLot.SCP_WAFER_SOURCE);
                    }else {
                        materialLot.setReserved50(MaterialLot.SCP_IN_FLAG_WAFER_SOURCE);
                    }
                }else if (MaterialLot.SCP_WAFER_SOURCE.equals(waferSource) || MaterialLot.SCP_IN_FLAG_WAFER_SOURCE.equals(waferSource)){
                    materialLot.setReserved50(MaterialLot.RW_TO_CP_WAFER_SOURCE);
                }
                if (MaterialLot.RW_WAFER_SOURCE.equals(waferSource)) {
                    materialLot.setReserved50(MaterialLot.COB_WAFER_SOURCE);
                } else if(MaterialLot.COB_WAFER_SOURCE.equals(waferSource)) {
                    materialLot.setReserved50(MaterialLot.RW_WAFER_SOURCE);
                }

                materialLot = materialLotRepository.saveAndFlush(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_WAFER_SOURCE_UPDATE);
                materialLotHistoryRepository.save(history);

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved50(materialLot.getReserved50());
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_WAFER_SOURCE_UPDATE);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料信息是否匹配存在批次单据，并且数量足够
     * @param materialInfo
     * @param materialLotMap
     * @param documentLineMap
     * @throws ClientException
     */
    private void validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(String materialInfo, Map<String,List<MaterialLot>> materialLotMap, Map<String,List<DocumentLine>> documentLineMap) throws ClientException{
        try {
            if (!documentLineMap.keySet().contains(materialInfo)) {
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(materialInfo).get(0).getMaterialLotId());
            }

            Long totalOrderQty = documentLineMap.get(materialInfo).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
            //根据出货形态验证出货时消耗的颗数还是片数
            BigDecimal totalQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLotMap.get(materialInfo)){
                if(materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                    totalQty = totalQty.add(materialLot.getCurrentQty());
                } else {
                    totalQty = totalQty.add(materialLot.getCurrentSubQty());
                }
            }
            Long totalMaterialLotQty = totalQty.longValue();
            if (totalMaterialLotQty.compareTo(totalOrderQty) > 0) {
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP物料批次根据发货单进行发货，更新单据数据以及，更改ERP的中间表数据
     *  documentLine 产品型号 materialName，二级代码 reserved2， 物流 reserved7 一致
     *  materialLot 产品型号 materialName，二级代码 reserved1， 物料 reserved6 一致
     */
    public void wltStockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String checkSubCode, String actionType) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<DocumentLine> shipOrderList = documentLineList.stream().filter(documentLine -> ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(shipOrderList)){
                throw new ClientParameterException(GcExceptions.CHOOSE_STOCK_OUT_ORDER_PLEASE, ErpSob.SOURCE_TABLE_NAME);
            }
            //验证装箱的Lot出货标注信息是否一致，不一致不允许出货
            validateMaterialLotStockTaggingInfo(materialLots);

            documentLineList = shipOrderList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(documentLineList, checkSubCode);
            Map<String, List<MaterialLot>> materialLotMap = groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(materialLots, checkSubCode);

            // 确保所有的物料批次都能匹配上单据, 并且数量足够
            for (String key : materialLotMap.keySet()) {
                validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(key, materialLotMap, documentLineMap);
                wltCpStockOut(documentLineMap.get(key), materialLotMap.get(key), actionType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证出货的物料批次标注信息是否一致，存在不一致不允许出货
     * @param materialLotList
     * @throws ClientException
     */
    private void validateMaterialLotStockTaggingInfo(List<MaterialLot> materialLotList) throws ClientException{
        try {
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);

                    List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    Map<String, List<MaterialLot>> mLotMap =  materialLots.stream().collect(Collectors.groupingBy(mLot -> {
                        StringBuffer key = new StringBuffer();
                        if(StringUtils.isNullOrEmpty(mLot.getReserved54())){
                            key.append(StringUtils.EMPTY);
                        } else {
                            key.append(mLot.getReserved54());
                        }
                        if(StringUtils.isNullOrEmpty(mLot.getReserved56())){
                            key.append(StringUtils.EMPTY);
                        } else {
                            key.append(mLot.getReserved56());
                        }
                        if(StringUtils.isNullOrEmpty(mLot.getVenderAddress())){
                            key.append(StringUtils.EMPTY);
                        } else {
                            key.append(mLot.getVenderAddress());
                        }
                        return key.toString();
                    }));

                    if (mLotMap != null &&  mLotMap.size() > 1) {
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TAG_INFO_IS_NOT_SAME, materialLot.getMaterialLotId());
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 WLT/CP出货 “材料/其他出”
     * @param materialLotActions
     * @param erpTime
     * @param checkSubCode
     * @throws ClientException
     */
    public void mobileWltStockOut(List<MaterialLotAction> materialLotActions, String erpTime, String checkSubCode, String actionType) throws ClientException {
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_WLT_OR_CP_STOCK_OUT_ORDER_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            wltStockOut(documentLineList, materialLotActions, checkSubCode, actionType);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void wltOtherStockOut(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActions, String actionType, String ruleId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<DocumentLine> otherStockOutLines = documentLineList.stream().filter(documentLine -> ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(otherStockOutLines)){
                throw new ClientParameterException(GcExceptions.CHOOSE_STOCK_OUT_ORDER_PLEASE, ErpSob.SOURCE_TABLE_NAME);
            }
            documentLineList = otherStockOutLines.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            Map<String, List<DocumentLine>> documentLineMap  = groupDocLineByMLotDocRule(documentLineList, ruleId);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, ruleId);

            for (String key : materialLotMap.keySet()) {
                validateDocAndMlotShipQtyAndMaterialAndSecondCodeInfo(key, materialLotMap, documentLineMap);
                wltCpStockOut(documentLineMap.get(key), materialLotMap.get(key), actionType);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次（WLT）按照 物料名称+二级代码+保税属性+客户名称分类
     * CP : 物料名称(去掉最后一个'-' + 标注形态)+二级代码+保税属性+客户名称分类
     * @param materialLots
     * @return
     */
    private Map<String,List<MaterialLot>> groupWaferByMaterialAndSecondCodeAndBondPropAndShipper(List<MaterialLot> materialLots, String checkSubCode) {
        return  materialLots.stream().collect(Collectors.groupingBy(materialLot -> {
            StringBuffer mLotShipInfo = new StringBuffer();
            String materialName = StringUtils.EMPTY;
            if(!StringUtils.isNullOrEmpty(materialLot.getReserved7()) && MaterialLotUnit.PRODUCT_CATEGORY_WLT.equals(materialLot.getReserved7())){
                materialName = materialLot.getMaterialName();
            } else {
                materialName = materialLot.getMaterialName().substring(0, materialLot.getMaterialName().lastIndexOf("-")) + materialLot.getReserved54();
            }
            mLotShipInfo.append(materialName);
            mLotShipInfo.append(StringUtils.SPLIT_CODE);

            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                mLotShipInfo.append(materialLot.getReserved1());
                mLotShipInfo.append(StringUtils.SPLIT_CODE);
            }

            mLotShipInfo.append(materialLot.getReserved6());
            mLotShipInfo.append(StringUtils.SPLIT_CODE);

            if(StringUtils.isNullOrEmpty(materialLot.getReserved55())){
                mLotShipInfo.append(materialLot.getReserved55());
            } else{
                mLotShipInfo.append(materialLot.getReserved55().toUpperCase());
            }
            mLotShipInfo.append(StringUtils.SPLIT_CODE);
            return mLotShipInfo.toString();
        }));
    }

    /**
     * 单据按照 物料名称+二级代码+保税属性+客户名称分类
     * @param documentLineList
     * @return
     */
    public Map<String, List<DocumentLine>> groupDocLineByMaterialAndSecondCodeAndBondPropAndShipper(List<DocumentLine> documentLineList, String checkSubCode) {
        return documentLineList.stream().collect(Collectors.groupingBy(documentLine -> {
            StringBuffer key = new StringBuffer();
            key.append(documentLine.getMaterialName());
            key.append(StringUtils.SPLIT_CODE);

            if(!StringUtils.isNullOrEmpty(checkSubCode)){
                key.append(documentLine.getReserved2());
                key.append(StringUtils.SPLIT_CODE);
            }

            key.append(documentLine.getReserved7());
            key.append(StringUtils.SPLIT_CODE);

            if(StringUtils.isNullOrEmpty(documentLine.getReserved8())){
                key.append(documentLine.getReserved8());
            } else {
                key.append(documentLine.getReserved8().toUpperCase());
            }
            key.append(StringUtils.SPLIT_CODE);
            return key.toString();
        }));
    }

    /**
     * WLT、CP依单据出货
     * @param documentLine
     * @param materialLotActions
     * @throws ClientException
     */
    @Override
    public void wltOtherShipByOrder(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            List<ComThrowWaferTab> comThrowWaferTabList = Lists.newArrayList();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            BigDecimal handledQty = BigDecimal.ZERO;
            for(MaterialLot materialLot : materialLots){
                String materialName = materialLot.getMaterialName();
                if(materialName.endsWith(MaterialLot.STOCKOUT_TYPE_35) || materialName.endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                    handledQty = handledQty.add(materialLot.getCurrentQty());
                } else {
                    handledQty = handledQty.add(materialLot.getCurrentSubQty());
                }
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.WLT_OTHER_SHIP_BY_ORDER__RULE_ID);
            }

            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            if (unHandleQty.compareTo(BigDecimal.ZERO) < 0) {
                throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
            }

            BigDecimal docHandedQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLots) {
                BigDecimal circleQty = BigDecimal.ZERO;
                if(materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_4) || MaterialLot.STOCKOUT_TYPE_35.equals(materialLot.getReserved54())){
                    docHandedQty = docHandedQty.add(materialLot.getCurrentQty());
                    materialLot.setCurrentQty(BigDecimal.ZERO);
                } else {
                    circleQty = materialLot.getCurrentSubQty();
                    docHandedQty = docHandedQty.add(materialLot.getCurrentSubQty());
                    materialLot.setCurrentSubQty(BigDecimal.ZERO);
                }
                saveDocLineRrnAndChangeStatus(materialLot, documentLine);

                if(SystemPropertyUtils.getWltStockOutToComThrowWaferTabFlag() && MaterialLot.BONDED_LIST.contains(materialLot.getReserved6())){
                    comThrowWaferTabList = addUnitToComThrowWaferTab (documentLine, materialLot, circleQty.intValue(), comThrowWaferTabList);
                }
            }

            documentLine.setHandledQty(documentLine.getHandledQty().add(docHandedQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(docHandedQty));
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            if(ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())){
                updateDocQyAndErpSobSynStatusAndQty(documentLine, docHandedQty);
            } else if(ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())) {
                updateDocQyAndErpSoaSynStatusAndQty(documentLine, docHandedQty);
            }

            if(CollectionUtils.isNotEmpty(comThrowWaferTabList)){
                log.info("comThrowWaferTabList size is " + comThrowWaferTabList.size());
                comThrowWaferTabRepository.saveAll(comThrowWaferTabList);
                log.info("comThrowWaferTabList write data end");
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP出货  消耗的都是片数
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void wltCpStockOut(List<DocumentLine> documentLines, List<MaterialLot> materialLots, String actionType) throws ClientException{
        try {
            List<ComThrowWaferTab> comThrowWaferTabList = Lists.newArrayList();
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for(DocumentLine documentLine : documentLines){
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    BigDecimal circleQty = BigDecimal.ZERO;//定义一个每次循环扣减数量
                    if(MaterialLot.STOCKOUT_TYPE_35.equals(materialLot.getReserved54()) || materialLot.getMaterialName().endsWith(MaterialLot.STOCKOUT_TYPE_4)){
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty = BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                            iterator.remove();
                        }
                    } else {
                        BigDecimal currentSubQty = materialLot.getCurrentSubQty();
                        if (unhandedQty.compareTo(currentSubQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentSubQty);
                            circleQty = currentSubQty;
                            currentSubQty = BigDecimal.ZERO;
                        } else {
                            currentSubQty = currentSubQty.subtract(unhandedQty);
                            circleQty = unhandedQty;
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentSubQty(currentSubQty);
                        if (materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) == 0){
                            saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                            iterator.remove();
                        }
                    }

                    if(SystemPropertyUtils.getWltStockOutToComThrowWaferTabFlag() && MaterialLot.BONDED_LIST.contains(materialLot.getReserved6())){
                        if (WltStockOutRequest.ACTION_WLTSTOCKOUT.equals(actionType) || WltStockOutRequest.ACTION_WLTOTHERSTOCKOUT.equals(actionType)
                                || WltStockOutRequest.ACTION_MOBILE_WLT_STOCK_OUT.equals(actionType) || WltStockOutRequest.ACTION_HN_SAMPLE_COLLECTION_STOCK_OUT.equals(actionType)
                                || WltStockOutRequest.ACTION_HN_WAREHOUSE_WLT_OTHER_STOCK_OUT.equals(actionType)){
                            comThrowWaferTabList = addUnitToComThrowWaferTab (documentLine, materialLot, circleQty.intValue(), comThrowWaferTabList);
                        }
                    }

                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

                    if(ErpSob.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())){
                        updateDocQyAndErpSobSynStatusAndQty(documentLine, handledQty);
                    } else if(ErpSoa.SOURCE_TABLE_NAME.equals(documentLine.getReserved31())) {
                        updateDocQyAndErpSoaSynStatusAndQty(documentLine, handledQty);
                    }
                }

                if(CollectionUtils.isNotEmpty(comThrowWaferTabList)){
                    log.info("comThrowWaferTabList size is " + comThrowWaferTabList.size());
                    comThrowWaferTabRepository.saveAll(comThrowWaferTabList);
                    log.info("comThrowWaferTabList write data end");
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * “材料/其他出”功能操作的保税属性为SWJF/SWKY/SWHT/WJF/WKY/WHT的materialLotUnit部分数据同步到 COM_THROW_WAFER_TAB中
     * @param documentLine 出货单
     * @param materialLot
     * @throws ClientException
     */
    private List<ComThrowWaferTab> addUnitToComThrowWaferTab(DocumentLine documentLine, MaterialLot materialLot, Integer circleQty, List<ComThrowWaferTab> comThrowWaferTabList) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            SimpleDateFormat formats = new SimpleDateFormat(MaterialLot.DEFAULT_DATE_PATTERN);
            if (!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                materialLotList = materialLotRepository.getByParentMaterialLotId(materialLot.getMaterialLotId());
            } else {
                materialLotList.add(materialLot);
            }
            for (MaterialLot lot : materialLotList) {
                List<MaterialLotUnit> mLotUnit = materialLotUnitRepository.findByMaterialLotIdAndReserved12IsNull(lot.getMaterialLotId());
                if (CollectionUtils.isNotEmpty(mLotUnit)){
                    for (MaterialLotUnit materialLotUnit : mLotUnit) {
                        ComThrowWaferTab comThrowWaferTab = new ComThrowWaferTab();
                        comThrowWaferTab.setWaferId(materialLotUnit.getUnitId());
                        comThrowWaferTab.setPdtId(materialLotUnit.getMaterialName());
                        comThrowWaferTab.setSecondCode(materialLotUnit.getReserved1());
                        comThrowWaferTab.setProperty(materialLotUnit.getReserved4());
                        comThrowWaferTab.setTimeStr(formats.format(materialLotUnit.getUpdated()));
                        comThrowWaferTab.setBillNum(documentLine.getDocId());
                        comThrowWaferTabList.add(comThrowWaferTab);

                        //unit保存reserved12记录历史
                        materialLotUnit.setReserved12(documentLine.getObjectRrn().toString());
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_THROW_WLT_OUT);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);

                        --circleQty;
                        if(circleQty == 0){
                            break;
                        }
                    }
                }
            }
            return comThrowWaferTabList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据种存在合单的单据，先扣减数量，其次按照单据数量扣减
     * @param documentLines
     * @return
     * @throws ClientException
     */
    private List<DocumentLine> vlidateDocMergeAndSortDocumentLinesBySeq(List<DocumentLine> documentLines) throws ClientException{
        try {
            List<DocumentLine> documentLineList = Lists.newArrayList();
            List<DocumentLine> mergeDocLineList = documentLines.stream().filter(documentLine -> DocumentLine.DOC_MERGE.equals(documentLine.getMergeDoc())).collect(Collectors.toList());
            List<DocumentLine> unMergeDocLineList = documentLines.stream().filter(documentLine -> StringUtils.isNullOrEmpty(documentLine.getMergeDoc())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(mergeDocLineList)){
                mergeDocLineList = mergeDocLineList.stream().sorted(Comparator.comparing(DocumentLine :: getCreated)).collect(Collectors.toList());
                documentLineList.addAll(mergeDocLineList);
            }
            if(CollectionUtils.isNotEmpty(unMergeDocLineList)){
                for(DocumentLine documentLine : unMergeDocLineList){
                    documentLine.setErpSeq(Integer.parseInt(documentLine.getReserved1()));
                }
                unMergeDocLineList = unMergeDocLineList.stream().sorted(Comparator.comparing(DocumentLine :: getErpSeq)).collect(Collectors.toList());
                documentLineList.addAll(unMergeDocLineList);
            }
            return documentLineList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 销售出单据信息修改，回写ERP_SOA表数量信息
     * @param documentLine
     * @param handledQty
     * @throws ClientException
     */
    private void updateDocQyAndErpSoaSynStatusAndQty(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 销售单修改单据数量并回写中间表（ETM_SOB）
     * @param documentLine
     * @param handledQty
     * @throws ClientException
     */
    private void updateDocQyAndErpSobSynStatusAndQty(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            OtherShipOrder otherShipOrder = (OtherShipOrder) otherShipOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherShipOrder.setHandledQty(otherShipOrder.getHandledQty().add(handledQty));
            otherShipOrder.setUnHandledQty(otherShipOrder.getUnHandledQty().subtract(handledQty));
            otherShipOrder = otherShipOrderRepository.saveAndFlush(otherShipOrder);
            baseService.saveHistoryEntity(otherShipOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSob> erpSobOptional = erpSobOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSobOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_SOB_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSob erpSob = erpSobOptional.get();
                erpSob.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSob.setLeftNum(erpSob.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSob.getDeliveredNum())) {
                    erpSob.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSob.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSob.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSobOrderRepository.save(erpSob);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 记录单号至物料批次上，并且修改物料批次状态，记录历史
     * @param materialLot
     * @param documentLine
     */
    private void saveDocLineRrnAndChangeStatus(MaterialLot materialLot, DocumentLine documentLine) throws ClientException{
        try {
            if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
            } else {
                materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
            }
            changeMaterialLotStatusAndSaveHistory(materialLot);

            //单lot出货修改unit表晶圆状态，记录历史
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                changPackageDetailLotStatusAndSaveHis(materialLot);
            } else {
                materialLotUnitStockOutAndSaveHis(materialLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆出货并记录历史
     * @param materialLot
     * @throws ClientException
     */
    private void materialLotUnitStockOutAndSaveHis(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setState(MaterialLotUnit.STATE_OUT);
                materialLotUnit.setReserved4(materialLot.getReserved6());
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_STOCK_OUT);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 改变包装批次的状态及记录历史
     * @param materialLot
     * @throws ClientException
     */
    private void changPackageDetailLotStatusAndSaveHis(MaterialLot materialLot) throws ClientException{
        try {
            List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            for (MaterialLot packageLot : packageDetailLots){
                packageLot.setReserved6(materialLot.getReserved6());
                changeMaterialLotStatusAndSaveHistory(packageLot);
                materialLotUnitStockOutAndSaveHis(packageLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证Wlt/CP出货单据和物料批次是否吻合
     *  1，物料名称
     *  2. 二级代码（绝对相等）
     *  4. 保税属性
     * @param documentLine
     * @param materialLot
     * @throws ClientException
     */
    public void validationWltStockOutDocLine(DocumentLine documentLine, MaterialLot materialLot) throws ClientException{
        try {
            Assert.assertEquals(documentLine.getMaterialName(), materialLot.getMaterialName());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "materialName", documentLine.getMaterialName(), materialLot.getMaterialName());
        }
        try {
            Assert.assertEquals(documentLine.getReserved2(), materialLot.getReserved1());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "secondcode", documentLine.getReserved2(),  materialLot.getReserved1());
        }

        try {
            Assert.assertEquals(documentLine.getReserved7(), materialLot.getReserved6());
        } catch (AssertionError e) {
            throw new ClientParameterException(ContextException.MERGE_SOURCE_VALUE_IS_NOT_SAME_TARGET_VALUE, "other1", documentLine.getReserved7(), materialLot.getReserved6());
        }
    }

    /**
     * 晶圆出货标注
     * @param materialLotActions
     * @param stockTagNote
     * @param customerName
     * @param stockOutType
     * @param poId
     * @throws ClientException
     */
    public void waferStockOutTagging(List<MaterialLotAction> materialLotActions, String stockTagNote, String customerName, String stockOutType, String poId, String address) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            if(!StringUtils.isNullOrEmpty(poId)){
                //按照产品型号分组扣减PO，PO号和产品号唯一确定一条PO信息
                Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
                for(String materialName : materialNameMap.keySet()){
                    BigDecimal totalTaggingQty = materialNameMap.get(materialName).stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot :: getCurrentSubQty));
                    GCOutSourcePo outSourcePo = outSourcePoRepository.findByPoIdAndMaterialName(poId, materialName);
                    if(outSourcePo != null){
                        if(outSourcePo.getUnHandledQty().subtract(totalTaggingQty).compareTo(BigDecimal.ZERO) < 0){
                            throw new ClientParameterException(GcExceptions.MATERIAL_LOT_TAG_QTY_OVER_PO_QTY, poId);
                        } else {
                            BigDecimal unHandledQty = outSourcePo.getUnHandledQty();
                            BigDecimal handledQty = outSourcePo.getHandledQty();
                            unHandledQty = unHandledQty.subtract(totalTaggingQty);
                            handledQty = handledQty.add(totalTaggingQty);
                            outSourcePo.setUnHandledQty(unHandledQty);
                            outSourcePo.setHandledQty(handledQty);
                            outSourcePo = outSourcePoRepository.saveAndFlush(outSourcePo);

                            GCOutSourcePoHis history = (GCOutSourcePoHis) baseService.buildHistoryBean(outSourcePo, GCOutSourcePoHis.TRANS_TYPE_STOCK_OUT_TAG);
                            outSourcePoHisRepository.save(history);
                        }
                    }
                }
            }

            for(MaterialLot materialLot : materialLotList){
                taggingMaterialLotAndSaveHis(materialLot, stockOutType, customerName, poId, stockTagNote, address);
            }

            //如果LOT已经装箱，验证箱中所有的LOT是否已经标注，如果全部标注，对箱号进行标注
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    validateMaterilaLotTaggingInfo(materialLot, stockOutType, customerName, poId, stockTagNote,address);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 标注物料批次并记录历史
     * @param materialLot
     * @param stockOutType
     * @param customerName
     * @param poId
     * @param stockTagNote
     * @throws ClientException
     */
    private void taggingMaterialLotAndSaveHis(MaterialLot materialLot, String stockOutType, String customerName, String poId, String stockTagNote,String address) throws ClientException{
        try {
            materialLot.setReserved54(stockOutType);
            materialLot.setReserved55(customerName);
            materialLot.setReserved56(poId);
            materialLot.setReserved57(stockTagNote);
            materialLot.setVenderAddress(address);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证箱中LOT是否全部标注，全部标注则对箱号标注
     * @param materialLot
     * @param stockOutType
     * @param customerName
     * @param poId
     * @param stockTagNote
     * @throws ClientException
     */
    private void validateMaterilaLotTaggingInfo(MaterialLot materialLot, String stockOutType, String customerName, String poId, String stockTagNote, String address) throws ClientException{
        try {
            List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            List<MaterialLot> unTaggingMLots = materialLots.stream().filter(mLot -> StringUtils.isNullOrEmpty(mLot.getReserved54())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(unTaggingMLots)){
                taggingMaterialLotAndSaveHis(materialLot, stockOutType, customerName, poId, stockTagNote, address);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 清除晶圆出货标注信息
     * @param materialLotActions
     * @throws ClientException
     */
    public void waferUnStockOutTagging(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLotList){
                //还原PO标注数量信息
                String poId = materialLot.getReserved56();
                if(!StringUtils.isNullOrEmpty(poId)){
                    Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
                    for(String materialName : materialNameMap.keySet()){
                        GCOutSourcePo outSourcePo = outSourcePoRepository.findByPoIdAndMaterialName(poId, materialName);
                        if(outSourcePo != null){
                            BigDecimal poHandleQty = outSourcePo.getHandledQty().subtract(materialLot.getCurrentSubQty());
                            BigDecimal poUnHandleQty = outSourcePo.getUnHandledQty().add(materialLot.getCurrentSubQty());
                            outSourcePo.setHandledQty(poHandleQty);
                            outSourcePo.setUnHandledQty(poUnHandleQty);
                            outSourcePo = outSourcePoRepository.saveAndFlush(outSourcePo);

                            GCOutSourcePoHis history = (GCOutSourcePoHis) baseService.buildHistoryBean(outSourcePo, GCOutSourcePoHis.TRANS_TYPE_UNSTOCK_OUT_TAG);
                            outSourcePoHisRepository.save(history);
                        }
                    }
                }

                unTaggingMaterialLot(materialLot);
            }

            //清除箱上的标注信息
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    unTaggingMaterialLot(materialLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 清除物料批次的标注信息
     * @param materialLot
     * @throws ClientException
     */
    private void unTaggingMaterialLot(MaterialLot materialLot) throws ClientException{
        try {
            materialLot.clearTaggingInfo();
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UN_STOCK_OUT_TAG);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料批次的供应商是否一致
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationMaterialLotVender(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set venderInfo = materialLotList.stream().map(materialLot -> materialLot.getReserved22()).collect(Collectors.toSet());
            if (venderInfo != null &&  venderInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_VENDER_IS_NOT_SAME, materialLotList.get(0).getReserved22());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证物料批次的产品号是否一致
     * @param materialLotActions
     * @throws ClientException
     */
    public void validationMLotMaterialName(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Set productInfo = materialLotList.stream().map(materialLot -> materialLot.getMaterialName()).collect(Collectors.toSet());
            if (productInfo != null &&  productInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_MATERIAL_NAME_IS_NOT_SAME, materialLotList.get(0).getMaterialName());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public MaterialLot getMaterialLotByTableRrnAndMaterialLotIdOrLotId(Long tableRrn, String queryLotId) throws ClientException {
        try {
            MaterialLot materialLot = new MaterialLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();

            StringBuffer clauseBuffer = new StringBuffer(_whereClause);
            clauseBuffer.append(" AND materialLotId = ");
            clauseBuffer.append("'" + queryLotId + "'");
            _whereClause = clauseBuffer.toString();
            List<MaterialLot> materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if(CollectionUtils.isEmpty(materialLotList)){
                clauseBuffer = new StringBuffer(nbTable.getWhereClause());
                clauseBuffer.append(" AND lotId = ");
                clauseBuffer.append("'" + queryLotId+ "'");
                _whereClause = clauseBuffer.toString();
                materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            }
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLot = materialLotList.get(0);
            }
            return materialLot;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 获取装箱检验的物料批次信息
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByTableRrnAndMLotId(String mLotId, long tableRrn) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" AND parentMaterialLotId = ");
            clauseBuffer.append("'" + mLotId + "'");
            whereClause = clauseBuffer.toString();
            materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);

            if(CollectionUtils.isEmpty(materialLotList)){
                clauseBuffer = new StringBuffer(nbTable.getWhereClause());
                clauseBuffer.append(" AND lotId = ");
                clauseBuffer.append("'" + mLotId+ "'");
                clauseBuffer.append(" AND statusCategory = 'Stock' and status = 'In' ");
                whereClause = clauseBuffer.toString();
                materialLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            }

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取加密之后的二级代码
     * @param subcode
     * @return
     * @throws ClientException
     */
    public String getEncryptionSubCode(String grade, String subcode) throws ClientException{
        try {
            String targetSubcode = StringUtils.EMPTY;
            String endString = StringUtils.EMPTY;
            if(subcode.length() > 4){
                subcode = subcode.substring(0, 4);
            }
            List<NBOwnerReferenceList> encryptionSubcodeList = getReferenceListByName(REFERENCE_NAME_ENCRYPTION_SUBCODE_LIST);
            Map<String, List<NBOwnerReferenceList>> subcodeMap = encryptionSubcodeList.stream().collect(Collectors.groupingBy(NBOwnerReferenceList:: getKey));
            if(subcodeMap.containsKey(grade)){
                if(MaterialLot.GEADE_NA.equals(grade) || MaterialLot.GEADE_DA.equals(grade) ||  MaterialLot.GEADE_EA.equals(grade) ||  MaterialLot.GEADE_AA.equals(grade)){
                    NBOwnerReferenceList referenceList = subcodeMap.get(grade).get(0);
                    targetSubcode = subcode + referenceList.getValue();
                } else {
                    NBOwnerReferenceList referenceList = subcodeMap.get(grade).get(0);
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + referenceList.getValue();
                }
            } else {
                List<String> randomNumber = getTwoRandomChar();
                if(MaterialLot.GEADE_TA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_ZERO, subcode, randomNumber);
                } else if(MaterialLot.GEADE_HA.equals(grade) || MaterialLot.GEADE_HA1.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_ONE, subcode, randomNumber);
                } else if(MaterialLot.GEADE_SA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_TWO, subcode, randomNumber);
                } else if(MaterialLot.GEADE_MA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_THREE, subcode, randomNumber);
                } else if(MaterialLot.GEADE_WA.equals(grade)){
                    targetSubcode = getTargetSubCode(MaterialLot.GRADE_FIXED_CHAR_FOUR, subcode, randomNumber);
                } else if(MaterialLot.GEADE_HA2.equals(grade)){
                    for(int i=0; i< randomNumber.size(); i++){
                        endString += randomNumber.get(i);
                    }
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString + MaterialLot.GRADE_FIXED_CHAR_Q;
                } else if(MaterialLot.GEADE_HA3.equals(grade)){
                    for(int i=0; i< randomNumber.size(); i++){
                        endString += randomNumber.get(i);
                    }
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString + MaterialLot.GRADE_FIXED_CHAR_Z;
                } else {
                    targetSubcode = MaterialLot.GRADE_FIRST + subcode + grade;
                }
            }
            return targetSubcode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取加密的等级信息
     * @param fixedChar
     * @param subcode
     * @param randomNumber
     * @return
     * @throws ClientException
     */
    private String getTargetSubCode(String fixedChar, String subcode, List<String> randomNumber) throws ClientException{
        try {
            String targetSubcode = StringUtils.EMPTY;
            String endString = StringUtils.EMPTY;
            randomNumber.add(fixedChar);
            Collections.shuffle(randomNumber);
            for(int i=0; i< randomNumber.size(); i++){
                endString += randomNumber.get(i);
            }
            targetSubcode = MaterialLot.GRADE_FIRST + subcode + endString;
            return targetSubcode;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 从5-9、A-Z中随机获取两个字符串
     * @return
     * @throws ClientException
     */
    private List<String> getTwoRandomChar() throws ClientException{
        try {
            List<String> twoRandomCharList = Lists.newArrayList();
            Random random = new Random();
            String [] numberStrArray = "5,6,7,8,9".split(",");
            String [] letterStrArray = "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");
            String number = numberStrArray[random.nextInt(numberStrArray.length)];
            String letter = letterStrArray[random.nextInt(letterStrArray.length)];
            twoRandomCharList.add(number);
            twoRandomCharList.add(letter);
            return twoRandomCharList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public GCWorkorderRelation saveWorkorderGradeHoldInfo(GCWorkorderRelation workorderRelation, String transType) throws ClientException{
        try {
            GCWorkorderRelation oldWorkorderRelation = workorderRelationRepository.findByBoxIdAndWorkOrderIdAndGrade(workorderRelation.getBoxId(), workorderRelation.getWorkOrderId(), workorderRelation.getGrade());
            if(NBHis.TRANS_TYPE_CREATE.equals(transType)){
                if(oldWorkorderRelation == null){
                    workorderRelation = workorderRelationRepository.saveAndFlush(workorderRelation);

                    GCWorkorderRelationHis history = (GCWorkorderRelationHis) baseService.buildHistoryBean(workorderRelation, transType);
                    workorderRelationHisRepository.save(history);
                } else {
                    throw new ClientParameterException(GcExceptions.WORKORDER_GRADE_HOLD_INFO_IS_EXIST, workorderRelation.getWorkOrderId());
                }
            } else {
                workorderRelation = workorderRelationRepository.saveAndFlush(workorderRelation);

                GCWorkorderRelationHis history = (GCWorkorderRelationHis) baseService.buildHistoryBean(workorderRelation, transType);
                workorderRelationHisRepository.save(history);
            }

            return  workorderRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private void validateMLotAndDocLineByRule(DocumentLine documentLine, MaterialLot materialLot, String ruleName) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setSourceObject(materialLot);
            mLotDocRuleContext.setTargetObject(documentLine);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            mLotDocRuleContext.validation();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<DocumentLine>> groupDocLineByMLotDocRule(List<DocumentLine> documentLineList, String ruleName) throws ClientException{
        try {
            Map<String,List<DocumentLine>> documentLineMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setDocumentLineList(documentLineList);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            documentLineMap = mLotDocRuleContext.validationAndGetDocLine();
            return documentLineMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private Map<String,List<MaterialLot>> groupMaterialLotByMLotDocRule(List<MaterialLot> materialLots, String ruleId) throws ClientException{
        try {
            Map<String,List<MaterialLot>> materialLotMap = Maps.newHashMap();
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleId, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.MLOT_DOC_VALIDATE_RULE_IS_NOT_EXIST, ruleId);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setMaterialLotList(materialLots);
            mLotDocRuleContext.setRuleId(ruleId);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            materialLotMap = mLotDocRuleContext.validateAndGetMLot();
            return materialLotMap;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT来料导入
     * @param materialLotUnits
     * @param importType
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> createFTMaterialLotAndGetImportCode(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialUnitIdMap = materialLotUnits.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getUnitId));
            for(String unitId : materialUnitIdMap.keySet()){
                if(materialUnitIdMap.get(unitId).size() > 1){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_UNIT_ID_REPEATS, unitId);
                }
                MaterialLot materialLot = mmsService.getMLotByMLotId(unitId);
                if(materialLot != null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, unitId);
                }
            }
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                materialLotUnit.setLotId(materialLotUnit.getUnitId().toUpperCase());
                materialLotUnit.setMaterialLotId(materialLotUnit.getUnitId().toUpperCase());
                materialLotUnit.setReceiveQty(materialLotUnit.getCurrentQty());
                materialLotUnit.setCurrentSubQty(BigDecimal.ONE);
                materialLotUnit.setReserved6(StringUtils.EMPTY);//来料导入时reserved6不是报税属性，暂时清空
                materialLotUnit.setReserved7(MaterialLotUnit.PRODUCT_CLASSIFY_SENSOR);//晶圆信息不保存产品型号
                materialLotUnit.setReserved18("0");
                materialLotUnit.setReserved30(materialLotUnit.getReserved30().split("\\.")[0]);
                materialLotUnit.setReserved32(materialLotUnit.getCurrentQty().toString());
                materialLotUnit.setReserved49(MaterialLot.IMPORT_SENSOR);
                materialLotUnit.setReserved50(MaterialLot.SENSOR_WAFER_SOURCE);

                //sensor封装回货(-3未测) 如果materialName未配置,materialName后缀为"-3"转换为"-3.5"。
                if (MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType)){
                    GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductIdAndModelCategory(materialLotUnit.getMaterialName(), MaterialLot.IMPORT_FT);
                    if(productModelConversion == null && materialLotUnit.getMaterialName().endsWith("-3")){
                        materialLotUnit.setMaterialName(materialLotUnit.getMaterialName() + ".5");
                    }
                    RawMaterial material = mmsService.getRawMaterialByName(materialLotUnit.getMaterialName());
                    if(material == null){
                        material = new RawMaterial();
                        material.setName(materialLotUnit.getMaterialName());
                        mmsService.createRawMaterial(material);
                    }
                    //若gc_wlatoft_testbit的WLA_TEST_BIT 不为空，二级代码增加一位
                    GcWlatoftTesebit gcWlatoftTesebit = wlatoFtTestBitRepository.findByWaferId(materialLotUnit.getLotId());
                    if(gcWlatoftTesebit!=null && !StringUtils.isNullOrEmpty(gcWlatoftTesebit.getWlaTestBit())){
                        materialLotUnit.setSubCode5(gcWlatoftTesebit.getWlaTestBit());
                    }
                    if(materialLotUnit.getMaterialName().endsWith("-3.5")){
                        materialLotUnit.setGrade(MaterialLotUnit.MAIN_GRADE);
                    }
                }
            }
            materialLotUnits = materialLotUnitService.createFTMLot(materialLotUnits);

            return materialLotUnits;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT晶圆接收(unitId与materialLotId一致)
     * @param materialLotUnits
     * @throws ClientException
     */
    public void receiveFTWafer(List<MaterialLotUnit> materialLotUnits) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotUnits.stream().map(materialLotUnit -> mmsService.getMLotByMLotId(materialLotUnit.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLotUnit materialLotUnit : materialLotUnits){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotUnit.getMaterialLotId());
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                mmsService.stockIn(materialLot, materialLotAction);

                materialLotUnit.setState(MaterialLotUnit.STATE_IN);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);
                MaterialLotUnitHistory history = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotUnitHistory.TRANS_TYPE_IN);
                history.setTransQty(materialLotUnit.getCurrentQty());
                materialLotUnitHisRepository.save(history);
            }

            for(MaterialLot materialLot : materialLotList){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_CRMA.equals(importType) || MaterialLot.IMPORT_RETURN.equals(importType) || MaterialLot.IMPORT_RMA.equals(importType) ||
                        MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_RMA.equals(materialLot.getReserved7())){
                    continue;
                }
                String prodCate = MaterialLotUnit.PRODUCT_TYPE_PROD;
                Warehouse warehouse  = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                if(!StringUtils.isNullOrEmpty(materialLot.getProductType())){
                    prodCate = materialLot.getProductType();
                }

                ErpInStock erpInStock = new ErpInStock();
                erpInStock.setProdCate(prodCate);
                erpInStock.setMaterialLot(materialLot);
                if(ErpInStock.WAREHOUSE_ZJ_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.ZJ_STOCK);
                } else if(ErpInStock.WAREHOUSE_SH_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.SH_STOCK);
                } else if(ErpInStock.WAREHOUSE_HK_STOCK.equals(warehouse.getName())){
                    erpInStock.setWarehouse(ErpInStock.HK_STOCK);
                } else {
                    throw new ClientParameterException(GcExceptions.ERP_WAREHOUSE_CODE_IS_UNDEFINED, warehouse.getName());
                }
                erpInStockRepository.save(erpInStock);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取入库位晶圆信息
     * @param unitId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> queryFTMLotByUnitIdAndTableRrn(String unitId, long tableRrn) throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" AND unitId = ");
            clauseBuffer.append("'" + unitId + "'");
            whereClause = clauseBuffer.toString();
            materialLotUnitList = materialLotUnitRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);

            return materialLotUnitList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT来料入中转箱
     * @param stockInModels
     * @throws ClientException
     */
    public void stockInFTWafer(List<StockInModel> stockInModels) throws ClientException {
        try {
            stockIn(stockInModels);
            List<MaterialLot> materialLots = stockInModels.stream().map(model -> mmsService.getMLotByMLotId(model.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                Warehouse warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                saveErpInStock(materialLot, materialLot.getProductType(), warehouse.getName());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存产品箱数量绑定关系
     * @param productNumberRelation
     * @return
     * @throws ClientException
     */
    public GCProductNumberRelation saveProductNumberRelation(GCProductNumberRelation productNumberRelation, String transType) throws ClientException {
        try {
            GCProductNumberRelation oldProductNumberRelation = new GCProductNumberRelation();
            String productId = productNumberRelation.getProductId();
            BigDecimal packageQty = productNumberRelation.getPackageQty();
            BigDecimal boxPackedQty = productNumberRelation.getBoxPackedQty();
            String defaultFlag = productNumberRelation.getDefaultFlag();
            if(GCProductNumberRelation.TRANS_TYPE_CREATE.equals(transType)){
                oldProductNumberRelation = productNumberRelationRepository.findByProductIdAndPackageQtyAndBoxPackedQty(productId, packageQty, boxPackedQty);
                if(oldProductNumberRelation != null){
                    throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_EXIST, productId);
                }
            }
            if(StringUtils.YES.equals(defaultFlag)){
                oldProductNumberRelation = productNumberRelationRepository.findByProductIdAndDefaultFlag(productId, defaultFlag);
                if(oldProductNumberRelation != null){
                    oldProductNumberRelation.setDefaultFlag(StringUtils.NO);
                    productNumberRelationRepository.save(oldProductNumberRelation);

                    GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(oldProductNumberRelation, GCProductNumberRelation.TRANS_TYPE_UPDATE);
                    productNumberRelationHisRepository.save(productNumberRelationHis);
                }
            }

            productNumberRelation = productNumberRelationRepository.saveAndFlush(productNumberRelation);

            if(GCProductNumberRelation.TRANS_TYPE_CREATE.equals(transType)){
                GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_CREATE);
                productNumberRelationHisRepository.save(productNumberRelationHis);
            } else {
                GCProductNumberRelationHis productNumberRelationHis = (GCProductNumberRelationHis) baseService.buildHistoryBean(productNumberRelation, GCProductNumberRelation.TRANS_TYPE_UPDATE);
                productNumberRelationHisRepository.save(productNumberRelationHis);
            }
            return productNumberRelation;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据产品号获取包装规格
     * @param documentLineRrn
     * @return
     * @throws ClientException
     */
    public List<GCProductNumberRelation> getProductNumberRelationByDocRrn(Long documentLineRrn) throws ClientException{
        try {
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            String productId = documentLine.getMaterialName();
            List<GCProductNumberRelation> gcProductNumberRelationList = productNumberRelationRepository.findByProductId(productId);
            if(CollectionUtils.isEmpty(gcProductNumberRelationList)){
                throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_NOT_EXIST, productId);
            }
            return gcProductNumberRelationList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据包装规则和单据挑选满足条件的物料批次
     * @param documentLineRrn
     * @param materialLotActions
     * @param packageRule
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByPackageRuleAndDocLine(Long documentLineRrn, List<MaterialLotAction> materialLotActions, String packageRule) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLineRrn);
            String materialNmae = documentLine.getMaterialName();
            BigDecimal boxPackedQty = new BigDecimal(packageRule);
            List<GCProductNumberRelation> productNumberRelations = productNumberRelationRepository.findByProductIdAndBoxPackedQty(materialNmae, boxPackedQty);
            if(CollectionUtils.isNotEmpty(productNumberRelations) && productNumberRelations.size() > 1){
                throw new ClientParameterException(GcExceptions.PRODUCT_NUMBER_RELATION_IS_ERROR, materialNmae);
            }
            GCProductNumberRelation productNumberRelation = productNumberRelations.get(0);
            BigDecimal totalQty = documentLine.getUnReservedQty();//单据备货单未备货数量
            List<MaterialLot> wholeBoxMLots = Lists.newArrayList();//整箱
            List<MaterialLot> zeroBoxMLots = Lists.newArrayList();//零箱
            List<MaterialLot> wholeVboxMLots = Lists.newArrayList();//整包
            List<MaterialLot> zeroVBoxMLots = Lists.newArrayList();//零包
            BigDecimal totalNumber = productNumberRelation.getTotalNumber();//整箱颗数
            BigDecimal packageQty = productNumberRelation.getPackageQty();//整包颗数
            //将物料批次按照箱号做分组
            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if(packedLotMap != null && packedLotMap.keySet().size() > 0){
                for(String parentMaterialLotId : packedLotMap.keySet()){
                    MaterialLot materialLot = mmsService.getMLotByMLotId(parentMaterialLotId, true);
                    BigDecimal unreservedQty = materialLot.getCurrentQty().subtract(materialLot.getReservedQty());
                    if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                        zeroBoxMLots.add(materialLot);
                    } else if(totalNumber.compareTo(unreservedQty) == 0){
                        wholeBoxMLots.add(materialLot);
                    } else if(totalNumber.compareTo(unreservedQty) > 0){
                        zeroBoxMLots.add(materialLot);
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKAGE_RULE_IS_ERROR, parentMaterialLotId);
                    }
                    materialLots.removeAll(packedLotMap.get(parentMaterialLotId));
                }
            }
            //未装箱的物料批次信息
            if(CollectionUtils.isNotEmpty(materialLots)){
                for(MaterialLot materialLot : materialLots){
                    if(packageQty.compareTo(materialLot.getCurrentQty()) == 0){
                        wholeVboxMLots.add(materialLot);
                    } else if(packageQty.compareTo(materialLot.getCurrentQty()) > 0){
                        zeroVBoxMLots.add(materialLot);
                    } else {
                        throw new ClientParameterException(GcExceptions.MATERIALLOT_PACKAGE_RULE_IS_ERROR, materialLot.getMaterialLotId());
                    }
                }
            }
            //先挑整箱的
            if(CollectionUtils.isNotEmpty(wholeBoxMLots)){
                wholeBoxMLots = getBoxMLotsTempDate(wholeBoxMLots);
                wholeBoxMLots = wholeBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getTempDate)).collect(Collectors.toList());
            }
            Iterator<MaterialLot> iterator = wholeBoxMLots.iterator();
            while (iterator.hasNext()){
                MaterialLot materialLot = iterator.next();
                if(totalQty.compareTo(materialLot.getCurrentQty()) >= 0){
                    materialLotList.addAll(packedLotMap.get(materialLot.getMaterialLotId()));
                    totalQty = totalQty.subtract(materialLot.getCurrentQty());
                    iterator.remove();
                } else {
                  break;
                }
            }
            //再挑未装箱的真空包（整包）
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(wholeVboxMLots)){
                    List<MaterialLot> wholeVboxMLotList = wholeVboxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for(MaterialLot materialLot: wholeVboxMLotList){
                        if(totalQty.compareTo(materialLot.getCurrentQty()) >= 0){
                            materialLotList.add(materialLot);
                            totalQty = totalQty.subtract(materialLot.getCurrentQty());
                        } else {
                            break;
                        }
                    }
                }
            }
            //已经装箱的零数箱（先装箱的先挑）
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(zeroBoxMLots)){
                    zeroBoxMLots = getBoxMLotsTempDate(zeroBoxMLots);
                    List<MaterialLot> zeroBoxMLotList = zeroBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getTempDate)).collect(Collectors.toList());
                    for (MaterialLot materialLot: zeroBoxMLotList){
                        BigDecimal unreservedQty = BigDecimal.ZERO;
                        if(MaterialLot.HOLD_STATE_ON.equals(materialLot.getHoldState())){
                            List<MaterialLot> mLotDetials = packedLotMap.get(materialLot.getMaterialLotId());
                            Long totalUnhandledQty = mLotDetials.stream().collect(Collectors.summingLong(mLot -> mLot.getCurrentQty().longValue()));
                            unreservedQty = new BigDecimal(totalUnhandledQty);
                        } else {
                            unreservedQty = materialLot.getCurrentQty().subtract(materialLot.getReservedQty());
                        }
                        if(totalQty.compareTo(unreservedQty) >= 0){
                            materialLotList.addAll(packedLotMap.get(materialLot.getMaterialLotId()));
                            totalQty = totalQty.subtract(unreservedQty);
                        } else {
                            break;
                        }
                    }
                }
            }
            //再挑已经装箱的整箱中的真空包
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(wholeBoxMLots)){
                    boolean falg = false;
                    List<MaterialLot> wholeBoxMLotList = wholeBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                    for(MaterialLot packagedLot : wholeBoxMLotList){
                        List<MaterialLot> packedDetials = packedLotMap.get(packagedLot.getMaterialLotId());
                        for(MaterialLot packedMLot : packedDetials){
                            if(totalQty.compareTo(packedMLot.getCurrentQty()) >= 0){
                                materialLotList.add(packedMLot);
                                totalQty = totalQty.subtract(packedMLot.getCurrentQty());
                            } else {
                                falg = true;
                                break;
                            }
                            if(falg){
                                break;
                            }
                        }
                    }
                }
            }

            //最后挑选未装箱的真空包（只挑零包）、零包需先挑选数量多的，数量相同的按照先进先出的原则挑选
            if(totalQty.compareTo(BigDecimal.ZERO) > 0){
                if(CollectionUtils.isNotEmpty(zeroVBoxMLots)){
                    List<MaterialLot> zeroVBoxMLotList = zeroVBoxMLots.stream().sorted(Comparator.comparing(MaterialLot::getCurrentQty).reversed()).collect(Collectors.toList());
                    Map<BigDecimal, List<MaterialLot>> mLotMap = zeroVBoxMLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getCurrentQty));
                    for(BigDecimal qty : mLotMap.keySet()){
                        List<MaterialLot> zeroMLots = mLotMap.get(qty).stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                        for(MaterialLot zeroVbox : zeroMLots){
                            if(totalQty.compareTo(zeroVbox.getCurrentQty()) >= 0){
                                totalQty = totalQty.subtract(zeroVbox.getCurrentQty());
                                materialLotList.add(zeroVbox);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取箱中真空包最小创新时间
     * @param materialLots
     * @return
     * @throws ClientException
     */
    private List<MaterialLot> getBoxMLotsTempDate(List<MaterialLot> materialLots) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLots){
                List<MaterialLot> packedLotList = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                packedLotList = packedLotList.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
                materialLot.setTempDate(packedLotList.get(0).getCreated());
            }
            return materialLots;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原料清单导入 保存
     * @param materialLotList
     * @param importType
     * @return
     * @throws ClientException
     */
    @Override
    public String importRawMaterialLotList(List<MaterialLot> materialLotList, String importType) throws  ClientException{
        try {
            materialLotList = validateRawMaterialAndMaterialLot(materialLotList, importType);
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            if (warehouse == null) {
                throw new ClientParameterException(MmsException.MM_WAREHOUSE_IS_NOT_EXIST, WAREHOUSE_ZJ);
            }
            Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));

            String importCode = generatorMLotsTransId(MaterialLot.GENERATOR_INCOMING_MLOT_IMPORT_CODE_RULE);
            for(String materialName : materialLotMap.keySet()){
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                List<MaterialLot> materialLots = materialLotMap.get(materialName);
                for(MaterialLot materialLot : materialLots){
                    materialLot.setMaterial(rawMaterial);
                    materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                    materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                    materialLot.setStatusModelRrn(rawMaterial.getStatusModelRrn());
                    materialLot.initialMaterialLot();
                    materialLot.setProductType(StringUtils.EMPTY);
                    materialLot.setReserved13(warehouse.getObjectRrn().toString());
                    materialLot.setReserved48(importCode);
                    materialLot.setReserved49(importType);
                    materialLot.setReserved50(MaterialLot.RAW_MATERIAL_WAFER_SOURCE);
                    materialLot.setGrade(MaterialLot.GEADE_A);
                    materialLotRepository.save(materialLot);
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                    materialLotHistoryRepository.save(history);
                }
            }
            return importCode;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证原材料型号是否存在，导入模板与导入类型是否一致
     * @param materialLotList
     * @param importType
     * @throws ClientException
     */
    private List<MaterialLot> validateRawMaterialAndMaterialLot(List<MaterialLot> materialLotList, String importType) throws ClientException{
        try{
            List<MaterialLot> rawMaterialLotList = Lists.newArrayList();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MaterialLot.DEFAULT_NO_FORMAT_DATE_PATTERN);
            SimpleDateFormat formats = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            Map<String, Date> dateMap = new HashMap<>();
            if(Material.MATERIAL_TYPE_IRA.equals(importType)){
                List<MaterialLot> materialLots = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getLotId())).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(materialLots)){
                    throw new ClientParameterException(GcExceptions.IRA_RAW_MATERIAL_BOX_ID_CANNOT_EMPTY, materialLots.get(0).getMaterialLotId());
                } else {
                    Map<String, List<MaterialLot>> materialNameMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
                    for (String materialName : materialNameMap.keySet()) {
                        List<MaterialLot> mLotList =  materialNameMap.get(materialName).stream().filter(materialLot -> !materialLot.getMaterialLotId().startsWith(materialName)).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(mLotList)){
                            throw new ClientParameterException(GcExceptions.MATERIAL_TYPE_AND_MATERIAL_LOT_IS_NOT_SAME);
                        }
                    }
                    Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getLotId));
                    for(String lotId : materialLotMap.keySet()){
                        if(!lotId.startsWith(Material.IRA_MATERIAL_BOX_ID_START)){
                            throw new ClientParameterException(GcExceptions.IRA_MATERIAL_LOT_BOX_MUST_SATRT_WITH_GCB, lotId);
                        }
                        List<MaterialLot> mLotList = materialLotRepository.findByLotIdAndMaterialCategoryAndMaterialType(lotId, Material.TYPE_MATERIAL, Material.MATERIAL_TYPE_IRA);
                        if(CollectionUtils.isNotEmpty(mLotList)){
                            throw new ClientParameterException(GcExceptions.IRA_RAW_MATERIAL_BOX_ID_IS_EXISTS, lotId);
                        }
                        //对同一箱IRA的物料批次生产日期校验并取最小时间保存
                        List<MaterialLot> lotList = materialLotMap.get(lotId);
                        lotList = lotList.stream().sorted(Comparator.comparing(MaterialLot::getMfgDateValue)).collect(Collectors.toList());
                        String mfgDateValue = formats.format(simpleDateFormat.parse(lotList.get(0).getMfgDateValue()));
                        for (MaterialLot materialLot : lotList) {
                            materialLot.setEarlierExpDate(formats.parse(mfgDateValue));
                        }
                    }
                }
            }
            Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for(String materialName : materialLotMap.keySet()){
                RawMaterial rawMaterial = mmsService.getRawMaterialByName(materialName);
                if (rawMaterial == null){
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
                }
                String materialType = rawMaterial.getMaterialType();
                if (!importType.equals(materialType)){
                    throw new ClientParameterException(MmsException.MM_RAW_MATERIAL_TYPE_NOT_SAME, importType);
                }
                List<MaterialLot> materialLots = materialLotMap.get(materialName);
                for(MaterialLot materialLot : materialLots){
                    MaterialLot oldmaterialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                    if(oldmaterialLot != null){
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLot.getMaterialLotId());
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getMfgDateValue())){
                        if(dateMap.containsKey(materialLot.getMfgDateValue())){
                            materialLot.setMfgDate(dateMap.get(materialLot.getMfgDateValue()));
                        } else {
                            String msgDate = formats.format(simpleDateFormat.parse(materialLot.getMfgDateValue()));
                            materialLot.setMfgDate(formats.parse(msgDate));
                            dateMap.put(materialLot.getMfgDateValue(), materialLot.getMfgDate());
                        }
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getExpDateValue())){
                        if(dateMap.containsKey(materialLot.getExpDateValue())){
                            materialLot.setExpDate(dateMap.get(materialLot.getExpDateValue()));
                        } else {
                            String expDateValue = formats.format(simpleDateFormat.parse(materialLot.getExpDateValue()));
                            Date expDate = formats.parse(expDateValue);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(expDate);
                            calendar.add(Calendar.HOUR,23);
                            calendar.add(Calendar.MINUTE,59);
                            calendar.add(Calendar.SECOND,59);
                            materialLot.setExpDate(calendar.getTime());
                            dateMap.put(materialLot.getExpDateValue(), materialLot.getExpDate());
                        }
                    }
                    if(!StringUtils.isNullOrEmpty(materialLot.getShippingDateValue())){
                        if(dateMap.containsKey(materialLot.getShippingDateValue())){
                            materialLot.setShippingDate(dateMap.get(materialLot.getShippingDateValue()));
                        } else {
                            String shippingDate = formats.format(simpleDateFormat.parse(materialLot.getShippingDateValue()));
                            materialLot.setShippingDate(formats.parse(shippingDate));
                            dateMap.put(materialLot.getShippingDateValue(), materialLot.getShippingDate());
                        }
                    }
                    if (materialLot.getMfgDate().after(new Date())){
                        throw new ClientParameterException(GcExceptions.RAW_MATERIAL_LOT_MFG_DATE_IS_AFTER_CURRENT_TIME, materialLot.getMaterialLotId());
                    }
                    if (materialLot.getMfgDate().after(materialLot.getExpDate())){
                        throw new ClientParameterException(GcExceptions.RAW_MATERIAL_LOT_MFG_DATE_IS_AFTER_EXP_DATE, materialLot.getMaterialLotId());
                    }
                    //验证原材料有效时间，超出有效时间不允许导入(默认有效时间单位为天)
                    if(rawMaterial.getWarningLife() != null && rawMaterial.getWarningLife() > 0){
                        Long warningLife = rawMaterial.getWarningLife();
                        Long effectiveTime = materialLot.getExpDate().getTime() - new Date().getTime();//这样得到的差值是毫秒级别
                        Long effectiveDays = effectiveTime / (1000 * 60 * 60 * 24);
                        if(effectiveDays < warningLife){
                            throw new ClientParameterException(GcExceptions.RAW_MATERIAL_LOT_EXPDATE_LESS_THAN_WARNING_LIFE, materialLot.getMaterialLotId());
                        }
                    }
                    rawMaterialLotList.add(materialLot);
                }
            }
            return rawMaterialLotList;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过导入型号验证是否做产品型号转换，并保存原产品型号（后续接收时需要保存至中间表ETM_IN_STOCK）
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> validateAndChangeMaterialNameByImportType(List<MaterialLotUnit> materialLotUnits, String importType) throws ClientException{
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnits.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialName));
            if(MaterialLotUnit.SENSOR_CP.equals(importType) || MaterialLotUnit.FAB_SENSOR.equals(importType) || MaterialLotUnit.SENSOR_CP_KLT.equals(importType)
                    || MaterialLotUnit.SENSOR_UNMEASURED.equals(importType) || MaterialLotUnit.FAB_SENSOR_2UNMEASURED.equals(importType)
                    || MaterialLotUnit.SOC_WAFER_UNMEASURED.equals(importType)){
                changeMaterialNameByModelCategory(materialLotUnitMap, MaterialLot.IMPORT_SENSOR_CP);
            } else if(MaterialLotUnit.LCD_CP_25UNMEASURED.equals(importType) || MaterialLotUnit.FAB_LCD_PTC.equals(importType) || MaterialLotUnit.FAB_LCD_SILTERRA.equals(importType)
                    || MaterialLotUnit.LCD_CP.equals(importType)){
                changeMaterialNameByModelCategory(materialLotUnitMap, MaterialLot.IMPORT_LCD_CP);
            } else if(MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType) || MaterialLotUnit.SENSOR_PACK_RETURN_COGO.equals(importType) || MaterialLotUnit.SENSOR_TPLCC.equals(importType)){
                for(String materialName : materialLotUnitMap.keySet()){
                    GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductIdAndModelCategory(materialName, MaterialLot.IMPORT_FT);
                    String conversionModelId = StringUtils.EMPTY;
                    if(productModelConversion != null){
                        conversionModelId = productModelConversion.getConversionModelId();
                        conversionModelId = conversionModelId.substring(0, conversionModelId.lastIndexOf("-")) + "-3.5";
                    }
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitMap.get(materialName);
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        if(!StringUtils.isNullOrEmpty(conversionModelId)){
                            materialLotUnit.setSourceProductId(materialName);
                            materialLotUnit.setMaterialName(conversionModelId);
                        }
                        //Sensor封装回货的物料批次验证数量是否正确
                        if(MaterialLotUnit.SENSOR_PACK_RETURN.equals(importType)){
                            validateMaterialLotUnitQty(materialLotUnit);
                        }
                    }
                }
            }
            return  materialLotUnits;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证导入的晶圆信息数量是否正确
     * @param materialLotUnit
     * @return
     * @throws ClientException
     */
    public void validateMaterialLotUnitQty(MaterialLotUnit materialLotUnit) throws ClientException{
        try {
            BigDecimal passDieQty = new BigDecimal(materialLotUnit.getReserved34());
            BigDecimal ngDieQty = new BigDecimal(materialLotUnit.getReserved35());
            BigDecimal totalQty = passDieQty.add(ngDieQty);
            if(materialLotUnit.getCurrentQty().compareTo(totalQty) != 0){
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_UNIT_QTY_IS_ERROR, materialLotUnit.getUnitId());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过产品型号转换表修改来料的晶圆型号
     * @param materialLotUnitMap
     * @param modelCategory
     * @throws ClientException
     */
    private void changeMaterialNameByModelCategory(Map<String,List<MaterialLotUnit>> materialLotUnitMap, String modelCategory) throws ClientException{
        try {
            for(String materialName : materialLotUnitMap.keySet()){
                GCProductModelConversion productModelConversion = gcProductModelConversionRepository.findByProductIdAndModelCategory(materialName, modelCategory);
                if(productModelConversion != null){
                    String conversionModelId = productModelConversion.getConversionModelId();
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitMap.get(materialName);
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setMaterialName(conversionModelId);
                        materialLotUnit.setSourceProductId(materialName);
                    }
                }
            }
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void mesSaveMaterialLotUnitHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException{
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void mesReceiveRawMaterialAndSaveHis(List<MaterialLot> materialLotList, String transType) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                materialLot = mmsService.getMLotByMLotId(materialLot.getMaterialLotId());
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_MES_RECEIVE, StringUtils.EMPTY);

                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
                materialLotHistoryRepository.save(materialLotHistory);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * IRA从mes退料到仓库
     * @param materialLotList
     * @param transType
     * @param materialType
     * @return
     * @throws ClientException
     */
    public void mesRawMaterialReturnWarehouse(List<MaterialLot> materialLotList, String transType, String materialType) throws ClientException{
        try {
            SimpleDateFormat format = new SimpleDateFormat(DateUtils.DEFAULT_DATETIME_PATTERN);
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            Map<String, List<MaterialLot>> materialLotMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for(String materialName: materialLotMap.keySet()){
                Material material = mmsService.getRawMaterialByName(materialName);
                if(material == null){
                    RawMaterial rawMaterial = new RawMaterial();
                    rawMaterial.setName(materialName);
                    rawMaterial.setMaterialCategory(Material.TYPE_MATERIAL);
                    rawMaterial.setMaterialType(materialType);
                    material = mmsService.createRawMaterial(rawMaterial);
                }
                List<MaterialLot> materialLots = materialLotMap.get(materialName);
                for(MaterialLot materialLot : materialLots){
                    MaterialLot oldMLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                    if(oldMLot != null){
                        oldMLot.setReceiveQty(materialLot.getCurrentQty());
                        oldMLot.setCurrentQty(materialLot.getCurrentQty());
                        oldMLot.setReservedQty(BigDecimal.ZERO);
                        oldMLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        oldMLot.setStatus(MaterialStatus.STATUS_CREATE);
                        oldMLot.setGrade(materialLot.getGrade());
                        oldMLot.setReserved12(StringUtils.EMPTY);
                        oldMLot.setReserved16(StringUtils.EMPTY);
                        oldMLot.setReserved17(StringUtils.EMPTY);
                        oldMLot.setReserved46(Material.RAW_MATERIAL_RETURN_FLAD);
                        oldMLot.setEarlierExpDate(oldMLot.getMfgDate());
                        oldMLot = materialLotRepository.saveAndFlush(oldMLot);
                        MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(oldMLot, transType);
                        materialLotHistoryRepository.save(materialLotHistory);
                    } else {
                        materialLot.initialMaterialLot();
                        materialLot.setMaterial(material);
                        materialLot.clearPackedMaterialLot();
                        materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                        materialLot.setStatusModelRrn(material.getStatusModelRrn());
                        materialLot.setReserved13(warehouse.getObjectRrn().toString());
                        materialLot.setReserved49(material.getMaterialType());
                        materialLot.setReserved50(MaterialLot.RAW_MATERIAL_WAFER_SOURCE);
                        materialLot.setMfgDate(format.parse(materialLot.getMfgDateValue()));
                        materialLot.setExpDate(format.parse(materialLot.getExpDateValue()));
                        materialLot.setEarlierExpDate(materialLot.getMfgDate());
                        if(!StringUtils.isNullOrEmpty(materialLot.getShippingDateValue())){
                            materialLot.setShippingDate(format.parse(materialLot.getShippingDateValue()));
                        }
                        materialLot.setReserved46(Material.RAW_MATERIAL_RETURN_FLAD);
                        materialLot = materialLotRepository.saveAndFlush(materialLot);

                        MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
                        materialLotHistoryRepository.save(materialLotHistory);
                    }
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * MES系统测试工单计划下达，记录工单号及工单计划时间
     * 记录历史
     * @param materialLotList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void mesMaterialLotBindWorkOrderAndSaveHis(List<MaterialLot> materialLotList, String transId) throws ClientException {
        try {
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLotList){
                String workOrderId = materialLot.getWorkOrderId();
                String workOrderPlanputTime = materialLot.getWorkOrderPlanputTime();
                String innerLotId = materialLot.getInnerLotId();
                String retestWorkorderId = materialLot.getReserved11();
                String retestTime = materialLot.getReserved15();
                materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                materialLot.setWorkOrderId(workOrderId);
                materialLot.setWorkOrderPlanputTime(workOrderPlanputTime);
                materialLot.setInnerLotId(innerLotId);
                materialLot.setReserved11(retestWorkorderId);
                materialLot.setReserved15(retestTime);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                materialLotHistoryRepository.save(history);

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReportMLotList.add(materialLot);
                }

                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                    for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                        materialLotUnit.setWorkOrderId(workOrderId);
                        materialLotUnit.setWorkOrderPlanputTime(workOrderPlanputTime);
                        materialLotUnit.setReserved18("1");//WLT下达需修改给定投批标记1
                        materialLotUnit.setReserved11(retestWorkorderId);//FT重测工单号
                        materialLotUnit.setReserved15(retestTime);//FT重测时间
                        materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                        MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                        materialLotUnitHisRepository.save(materialLotUnitHistory);
                    }
                }
            }

            log.info("scm materialLots is " + scmReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                sendMaterialStateToScmReport(scmReportMLotList, transId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 工单绑定晶圆，修改物料批次的工单及工单计划时间
     * 并记录历史
     * @param materialLotUnitList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void mesMaterialLotUnitBindWorkorderAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException {
        try {
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            String workOrderId = materialLotUnitList.get(0).getWorkOrderId();
            String workOrderPlanTime = materialLotUnitList.get(0).getWorkOrderPlanputTime();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit = materialLotUnitRepository.findByMaterialLotIdAndUnitId(materialLotUnit.getMaterialLotId(), materialLotUnit.getUnitId());
                materialLotUnit.setWorkOrderPlanputTime(workOrderPlanTime);
                materialLotUnit.setWorkOrderId(workOrderId);
                materialLotUnit.setReserved18("1");
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }

            for(String materialLotId : materialLotUnitMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                materialLot.setWorkOrderId(workOrderId);
                materialLot.setWorkOrderPlanputTime(workOrderPlanTime);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                        || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                    scmReportMLotList.add(materialLot);
                }

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                materialLotHistoryRepository.save(history);
            }

            log.info("scm materialLots is " + scmReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                sendMaterialStateToScmReport(scmReportMLotList, transId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 线边仓晶圆接收EngHold验证
     * 暂时不对MaterialLot做修改
     * @param materialLotUnitList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void lswMaterialLotUnitEngHoldAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException {
        try {
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit = materialLotUnitRepository.findByMaterialLotIdAndUnitId(materialLotUnit.getMaterialLotId(), materialLotUnit.getUnitId());
                materialLotUnit.setState(MaterialLotUnit.STATUS_ENGHOLD);
                materialLotUnit.setWorkOrderId(null);
                materialLotUnit.setWorkOrderPlanputTime(null);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆Recon
     * @param materialLotUnitList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void reconMaterialLotUnitAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException {
        try {
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit = materialLotUnitRepository.findByMaterialLotIdAndUnitId(materialLotUnit.getMaterialLotId(), materialLotUnit.getUnitId());
                materialLotUnit.setWorkOrderPlanputTime(null);
                materialLotUnit.setWorkOrderId(null);
                materialLotUnit.setState(MaterialLotUnit.STATUS_MERGED);
                materialLotUnit.setCurrentQty(BigDecimal.ZERO);
                materialLotUnit.setReceiveQty(BigDecimal.ZERO);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }

            for(String materialLotId : materialLotUnitMap.keySet()) {
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLotId);
                List<MaterialLotUnit> bindWorkOrderIdMLotUnits = materialLotUnits.stream().filter(materialLotUnit -> !StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId())).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(bindWorkOrderIdMLotUnits)) {
                    materialLot.setWorkOrderPlanputTime(null);
                    materialLot.setWorkOrderId(null);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                    materialLotHistoryRepository.save(history);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆工单解绑并记录历史
     * @param materialLotUnitList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void mesMaterialLotUnitUnBindWorkorderAndSaveHis(List<MaterialLotUnit> materialLotUnitList, String transId) throws ClientException {
        try{
            log.info("unbindWorkorder materialLotUnitList is " + materialLotUnitList);
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            Map<String, List<MaterialLotUnit>> materialLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit:: getMaterialLotId));
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit = materialLotUnitRepository.findByMaterialLotIdAndUnitId(materialLotUnit.getMaterialLotId(), materialLotUnit.getUnitId());
                if(materialLotUnit != null){
                    materialLotUnit.setWorkOrderId(null);
                    materialLotUnit.setWorkOrderPlanputTime(null);
                    materialLotUnit.setInnerLotId(null);
                    materialLotUnit.setReserved18("0");
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }

            for(String materialLotId : materialLotUnitMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                if(materialLot != null && !StringUtils.isNullOrEmpty(materialLot.getReserved11())){
                    materialLot.setReserved11(null);
                    materialLot.setReserved15(null);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                    materialLotHistoryRepository.save(history);
                }
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLotId);
                if(CollectionUtils.isNotEmpty(materialLotUnits)){
                    List<MaterialLotUnit> bindWorkOrderIdMLotUnits = materialLotUnits.stream().filter(materialLotUnit -> !StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId())).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(bindWorkOrderIdMLotUnits)){
                        materialLot.setWorkOrderPlanputTime(null);
                        materialLot.setWorkOrderId(null);
                        materialLot.setInnerLotId(null);
                        materialLot = materialLotRepository.saveAndFlush(materialLot);

                        if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                                || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                            scmReportMLotList.add(materialLot);
                        }

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                        materialLotHistoryRepository.save(history);
                    }
                }
            }

            log.info("scm report materialLots is " + scmReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                sendMaterialStateToScmReport(scmReportMLotList, transId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 晶圆工单解绑并记录历史
     * @param materialLotList
     * @param transId
     * @throws ClientException
     */
    @Override
    public void mesMaterialLotUnBindWorkorderAndSaveHis(List<MaterialLot> materialLotList, String transId) throws ClientException {
        try{
            log.info("unbindWorkorder materialLotList is " + materialLotList);
            List<MaterialLot> scmReportMLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLotList){
                materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getMaterialLotId(), ThreadLocalContext.getOrgRrn());
                if(materialLot != null){
                    materialLot.setWorkOrderId(null);
                    materialLot.setWorkOrderPlanputTime(null);
                    materialLot.setInnerLotId(null);
                    materialLot.setReserved18("0");
                    materialLot.setReserved11(null);
                    materialLot.setReserved15(null);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    if(!StringUtils.isNullOrEmpty(materialLot.getLotId()) && (MaterialLotUnit.PRODUCT_CATEGORY_LCP.equals(materialLot.getReserved7())
                            || MaterialLotUnit.PRODUCT_CATEGORY_SCP.equals(materialLot.getReserved7()) || MaterialLotUnit.PRODUCT_CLASSIFY_CP.equals(materialLot.getReserved7()))){
                        scmReportMLotList.add(materialLot);
                    }

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transId);
                    materialLotHistoryRepository.save(history);

                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    if(CollectionUtils.isNotEmpty(materialLotUnits)){
                        for(MaterialLotUnit materialLotUnit : materialLotUnits){
                            materialLotUnit.setWorkOrderId(null);
                            materialLotUnit.setWorkOrderPlanputTime(null);
                            materialLotUnit.setInnerLotId(null);
                            materialLotUnit.setReserved18("0");
                            materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                            MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, transId);
                            materialLotUnitHisRepository.save(materialLotUnitHistory);
                        }
                    }
                }
            }
            log.info("scm report materialLots is " + scmReportMLotList);
            if(CollectionUtils.isNotEmpty(scmReportMLotList)){
                sendMaterialStateToScmReport(scmReportMLotList, transId);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发送物料批次状态报告给SCM
     * @param scmReportMLotList
     * @param transId
     * @throws ClientException
     */
    private void sendMaterialStateToScmReport(List<MaterialLot> scmReportMLotList, String transId) throws ClientException{
        try{
            log.info("materialLot send to scm  start report materialLots is " + scmReportMLotList);
            List<MaterialLotUnit> materialLotUnitList = Lists.newArrayList();
            for(MaterialLot materialLot : scmReportMLotList){
                List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                materialLotUnitList.addAll(materialLotUnits);
            }
            if(MaterialLot.TRANSTYPE_BIND_WORKORDER.equals(transId)){
                scmService.sendMaterialStateReport(materialLotUnitList, MaterialLotStateReportRequestBody.ACTION_TYPE_PLAN);
            } else if(MaterialLot.TRANSTYPE_UN_BIND_WORKORDER.equals(transId)){
                scmService.sendMaterialStateReport(materialLotUnitList, MaterialLotStateReportRequestBody.ACTION_TYPE_UN_PLAN);
            }
            log.info("materialLot send to scm  report request end");
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * HongKong仓接收物料批次（暂时只做接收入库，不对晶圆做eng验证，不写入中间表）
     * @param materialLotActions
     * @throws ClientException
     */
    public void hongKongMLotReceive(List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                finishProductMaterialLotStockIn(materialLot);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取香港仓待出货的真空包信息
     * @param tableRrn
     * @param materialLotId
     * @return
     * @throws ClientException
     */
    public MaterialLot getHKWarehouseStockOutMLot(Long tableRrn, String materialLotId) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            List<MaterialLot> materialLots= queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, materialLotId);
            if(CollectionUtils.isNotEmpty(materialLots)){
                materialLot = materialLots.get(0);
            }
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 香港仓出货验证物料批次基础信息
     * @param materialLot
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public boolean validationHKStockOutMaterialLot(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            boolean falg = true;
            falg = validationMaterialLotInfo(materialLot, materialLotActions, falg);
            return falg;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 香港仓依订单出货
     * 只允许箱号出货（真空包不允许）
     * @param documentLine
     * @param materialLotActions
     * @throws ClientException
     */
    public void hongKongWarehouseByOrderStockOut(DocumentLine documentLine, List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            String threeSideTransaction = documentLine.getThreeSideTransaction();
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            List<MaterialLot> unPackMaterialLot = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getPackageType())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(unPackMaterialLot)){
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_IS_NOT_PACKED, unPackMaterialLot.get(0).getMaterialLotId());
            }
            Long totalMaterialLotQty = materialLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentSubQty().longValue()));
            if(documentLine.getUnHandledQty().longValue() < totalMaterialLotQty){
                throw new ClientException(GcExceptions.OVER_DOC_QTY);
            }
            documentLine = (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            for(MaterialLot materialLot : materialLots){
                validateMLotAndDocLineByRule(documentLine, materialLot, MaterialLot.HKWAREHOUSE_BY_ORDER_STOCK_OUT_RULE_ID);
            }
            //如果单据客户代码是C1001，C2837则对物料批次进行转仓，否则做出货
            if(!StringUtils.isNullOrEmpty(threeSideTransaction) && (DocumentLine.CUSCODE_C1001.equals(threeSideTransaction) || DocumentLine.CUSCODE_C2837.equals(threeSideTransaction))){
                Warehouse warehouse = new Warehouse();
                String location = StringUtils.EMPTY;
                if(DocumentLine.CUSCODE_C1001.equals(threeSideTransaction)){
                    List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_SH, ThreadLocalContext.getOrgRrn());
                    warehouse = warehouseList.get(0);
                    location = BONDED_PROPERTITY_SH;
                } else {
                    List<Warehouse> warehouseList = warehouseRepository.findByNameAndOrgRrn(WAREHOUSE_ZJ, ThreadLocalContext.getOrgRrn());
                    warehouse = warehouseList.get(0);
                    location = BONDED_PROPERTITY_ZSH;
                }

                for(MaterialLot materialLot: materialLots){
                    //状态恢复为待接收，仓库信息修改为GCSH或者GCZJ，修改报税属性，清空库位
                    mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_TRANSFER, StringUtils.EMPTY);
                    updateWarehouseAndSaveHis(materialLot, warehouse, location);

                    List<MaterialLot> packedDetial = materialLotRepository.getPackageDetailLots(materialLot.getObjectRrn());
                    for(MaterialLot packedMLot : packedDetial){
                        updateWarehouseAndSaveHis(packedMLot, warehouse, location);
                    }
                }
            } else {
                for (MaterialLot materialLot : materialLots) {
                    materialLot.setCurrentSubQty(BigDecimal.ZERO);
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    changeMaterialLotStatusAndSaveHistory(materialLot);

                    List<MaterialLot> packageDetailLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
                    if(CollectionUtils.isNotEmpty(packageDetailLots)){
                        for (MaterialLot packageLot : packageDetailLots){
                            changeMaterialLotStatusAndSaveHistory(packageLot);
                        }
                    }
                }
            }

            BigDecimal handledQty = new BigDecimal(totalMaterialLotQty);
            BigDecimal unHandleQty =  documentLine.getUnHandledQty().subtract(handledQty);
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(unHandleQty);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            // 获取到主单据
            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

            validateAndUpdateErpSoa(documentLine, handledQty);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改箱号仓库信息并报错历史
     * @param materialLot
     * @param warehouse
     * @param location
     * @throws ClientException
     */
    private void updateWarehouseAndSaveHis(MaterialLot materialLot, Warehouse warehouse, String location) throws ClientException{
        try {
            materialLot.setReserved13(warehouse.getObjectRrn().toString());
            materialLot.setReserved6(location);
            materialLot.setReserved14(StringUtils.EMPTY);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                materialLotUnit.setReserved4(location);
                materialLotUnit.setReserved13(warehouse.getObjectRrn().toString());
                materialLotUnit.setReserved14(StringUtils.EMPTY);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory materialLotUnitHistory =  (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_TRANSFER_WAREHOUSE);
                materialLotUnitHisRepository.save(materialLotUnitHistory);
            }

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, TRANS_TYPE_TRANSFER_WAREHOUSE);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT出货，出货单据（ETM_SOA）
     * FT出货先备货，按照备货单出货
     * @param materialLotActions
     * @param documentLineList
     * @throws ClientException
     */
    public void ftStockOut(List<MaterialLotAction> materialLotActions, List<DocumentLine> documentLineList, String ruleId) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());

            validateCobMaterialLotDocInfo(materialLots);
            validationStockMLotReservedDocLineByRuleId(documentLineList, materialLots, ruleId);
            Map<String, List<MaterialLot>> mlotDocMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getReserved16));
            for(String docLineRrn : mlotDocMap.keySet()){
                List<MaterialLot> materialLotList = mlotDocMap.get(docLineRrn);
                DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(docLineRrn));
                ftShipByDocLie(documentLine, materialLotList);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * FT正常出货
     * @param documentLine
     * @param materialLotList
     * @throws ClientException
     */
    public void ftShipByDocLie(DocumentLine documentLine, List<MaterialLot> materialLotList) throws ClientException{
        try {
            String expressNumber = getExpressNumberByMaterialLots(materialLotList);
            BigDecimal handledQty = BigDecimal.ZERO;
            for (MaterialLot materialLot : materialLotList) {
                handledQty = handledQty.add(materialLot.getCurrentQty());
                materialLot.setReserved12(documentLine.getObjectRrn().toString());
                materialLot.setCurrentQty(BigDecimal.ZERO);
                if(MaterialLot.RW_WAFER_SOURCE.equals(materialLot.getReserved50())){
                    materialLot.clearCobReservedDocInfo();
                }
                changeMaterialLotStatusAndSaveHistory(materialLot);
                changPackageDetailLotStatusAndSaveHis(materialLot);
            }

            // 验证当前操作数量是否超过待检查数量
            documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn());
            documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
            documentLine.setUnHandledQty(documentLine.getUnHandledQty().subtract(handledQty));
            documentLine.setExpressNumber(expressNumber);
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

            OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
            otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
            otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
            baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);
            validateAndUpdateErpSoa(documentLine, handledQty);

            if (SystemPropertyUtils.getConnectMscmFlag()) {
                scmService.addScmTracking(documentLine.getDocId(), materialLotList);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB出货时临时set单据信息
     * @param materialLotList
     */
    public void validateCobMaterialLotDocInfo(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                if(MaterialLot.RW_WAFER_SOURCE.equals(materialLot.getReserved50())){
                    String materialName = materialLot.getMaterialName();
                    String grade = materialLot.getGrade();
                    String subCode = materialLot.getReserved1() + materialLot.getGrade();
                    String bondedProperty = materialLot.getReserved6();
                    List<DocumentLine> documentLines = documentLineRepository.findByDocIdAndMaterialNameAndReserved3AndReserved2AndReserved7AndReserved17AndUnHandledQtyGreaterThan(materialLot.getReserved56(), materialName, grade, subCode, bondedProperty, materialLot.getReserved4(), BigDecimal.ZERO);
                    if(CollectionUtils.isEmpty(documentLines)){
                        throw new ClientParameterException(GcExceptions.ORDER_IS_NOT_EXIST, materialLot.getReserved56());
                    } else if(documentLines.size() > 1){
                        throw new ClientParameterException(GcExceptions.THERE_ARE_MULTIPLE_DOCUMENTS_PLEASE_MERGE_DOC, materialLot.getReserved56());
                    }
                    materialLot.setShipper(documentLines.get(0).getReserved12());
                    materialLot.setReserved16(documentLines.get(0).getObjectRrn().toString());
                    materialLot.setReserved17(documentLines.get(0).getDocId());
                    materialLot.setReserved51(documentLines.get(0).getReserved15());
                    materialLot.setReserved52(documentLines.get(0).getReserved20());
                    materialLot.setReserved53(documentLines.get(0).getReserved21());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次通过ERP_SOA单据出货
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void materialLotStockOutByErpSoa(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for(DocumentLine documentLine : documentLines){
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty = BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        saveDocLineRrnAndChangeStatus(materialLot, documentLine);
                        iterator.remove();
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_SHIP);

                    // 获取到主单据
                    OtherStockOutOrder otherStockOutOrder = (OtherStockOutOrder) otherStockOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    otherStockOutOrder.setHandledQty(otherStockOutOrder.getHandledQty().add(handledQty));
                    otherStockOutOrder.setUnHandledQty(otherStockOutOrder.getUnHandledQty().subtract(handledQty));
                    otherStockOutOrder = otherStockOutOrderRepository.saveAndFlush(otherStockOutOrder);
                    baseService.saveHistoryEntity(otherStockOutOrder, MaterialLotHistory.TRANS_TYPE_SHIP);

                    validateAndUpdateErpSoa(documentLine, handledQty);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证并接收COG来料信息
     * @param documentLineList
     * @param materialLotActionList
     * @throws ClientException
     */
    public void validateAndReceiveCogMLot(List<DocumentLine> documentLineList, List<MaterialLotAction> materialLotActionList,String receiveWithDoc) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            if(!StringUtils.isNullOrEmpty(receiveWithDoc)){
                Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.COG_MLOT_RECEIVE_DOC_VALIDATE_RULE_ID);
                Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLots, MaterialLot.COG_MLOT_RECEIVE_DOC_VALIDATE_RULE_ID);
                for (String key : materialLotMap.keySet()) {
                    if (!documentLineMap.keySet().contains(key)) {
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                    }
                    Long totalMLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                    Long totalDocUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                    if (totalMLotQty.compareTo(totalDocUnhandledQty) > 0) {
                        throw new ClientException(GcExceptions.OVER_DOC_QTY);
                    }
                    receiveCogMLot(documentLineMap.get(key), materialLotMap.get(key));
                }
            } else {
                for(MaterialLot materialLot : materialLots){
                    finishProductMaterialLotStockIn(materialLot);
                }
            }

        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收COG来料
     * @param documentLines
     * @param materialLots
     * @throws ClientException
     */
    private void receiveCogMLot(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            documentLines = vlidateDocMergeAndSortDocumentLinesBySeq(documentLines);
            for (DocumentLine documentLine: documentLines) {
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty =  BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        //数量进行还原。不能扣减。
                        finishProductMaterialLotStockIn(materialLot);
                        iterator.remove();
                    }
                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }

                BigDecimal handledQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handledQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handledQty));
                    documentLine.setUnHandledQty(unhandedQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    CogReceiveOrder cogReceiveOrder = (CogReceiveOrder) cogReceiveOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                    cogReceiveOrder.setHandledQty(cogReceiveOrder.getHandledQty().add(handledQty));
                    cogReceiveOrder.setUnHandledQty(cogReceiveOrder.getUnHandledQty().subtract(handledQty));
                    cogReceiveOrder = cogReceiveOrderRepository.saveAndFlush(cogReceiveOrder);
                    baseService.saveHistoryEntity(cogReceiveOrder, MaterialLotHistory.TRANS_TYPE_RECEIVE);

                    validateDocAndUpdateErpSo(documentLine, handledQty);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 成品箱号或者真空包号接收入库
     * 箱号接收时，真空包记录一笔接收历史
     * @param materialLot
     * @throws ClientException
     */
    private void finishProductMaterialLotStockIn(MaterialLot materialLot) throws ClientException{
        try {
            materialLot.setCurrentQty(materialLot.getReceiveQty());
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTargetWarehouseRrn(Long.parseLong(materialLot.getReserved13()));
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            if(!StringUtils.isNullOrEmpty(materialLot.getPackageType())){
                materialLot.setReserved2(null);
                mmsService.stockIn(materialLot, MaterialEvent.EVENT_BOX_RECEIVE, materialLotAction);
            } else {
                mmsService.stockIn(materialLot, MaterialEvent.EVENT_STOCK_IN, materialLotAction);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证单据是否时合单单据，并更新中间表单据数量信息
     * @param documentLine
     * @param handledQty
     */
    private void validateDocAndUpdateErpSo(DocumentLine documentLine, BigDecimal handledQty) throws ClientException{
        try {
            if(StringUtils.isNullOrEmpty(documentLine.getMergeDoc())){
                Optional<ErpSo> erpSoOptional = erpSoRepository.findById(Long.valueOf(documentLine.getReserved1()));
                if (!erpSoOptional.isPresent()) {
                    throw new ClientParameterException(GcExceptions.ERP_RECEIVE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
                }

                ErpSo erpSo = erpSoOptional.get();
                erpSo.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                erpSo.setLeftNum(erpSo.getLeftNum().subtract(handledQty));
                if (StringUtils.isNullOrEmpty(erpSo.getDeliveredNum())) {
                    erpSo.setDeliveredNum(handledQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpSo.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handledQty);
                    erpSo.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpSoRepository.save(erpSo);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 保存未确认晶圆追踪设置信息
     * @param unConfirmWaferSet
     * @param transType
     * @return
     * @throws ClientException
     */
    public GcUnConfirmWaferSet saveUnConfirmWaferTrackSetInfo(GcUnConfirmWaferSet unConfirmWaferSet, String transType) throws ClientException{
        try {
            if(GcUnConfirmWaferSet.TRANS_TYPE_CREATE.equals(transType)){
                String serialNumber = generatorMLotsTransId(GcUnConfirmWaferSet.GENERATOR_ISERIAL_NUMBER_RULE);
                unConfirmWaferSet.setSerialNumber(serialNumber);
            }
            unConfirmWaferSet = unConfirmWaferSetRepository.saveAndFlush(unConfirmWaferSet);

            GCUnConfirmWaferSetHis unConfirmWaferSetHis = (GCUnConfirmWaferSetHis) baseService.buildHistoryBean(unConfirmWaferSet, transType);
            unConfirmWaferSetHisRepository.save(unConfirmWaferSetHis);
            return unConfirmWaferSet;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收RMA物料批次
     * @param materialLotActionList
     * @throws ClientException
     */
    public List<MaterialLot> receiveRmaMLot(List<MaterialLotAction> materialLotActionList) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotActionList.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLots){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                mmsService.stockIn(materialLot, materialLotAction);
                mmsService.stockInMaterialLotUnitAndSaveHis(materialLot);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料接收
     * 写入中间表MTE_MATERIAL_IN
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveRawMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            SimpleDateFormat formats = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            for(MaterialLot materialLot : materialLotList){
                Warehouse warehouse = new Warehouse();
                if(!StringUtils.isNullOrEmpty(materialLot.getReserved13())){
                    warehouse = warehouseRepository.getOne(Long.parseLong(materialLot.getReserved13()));
                }
                if(warehouse == null){
                    throw new ClientParameterException(GcExceptions.WAREHOUSE_CANNOT_EMPTY);
                }
                MaterialLotAction materialLotAction = new MaterialLotAction();
                materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
                materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
                materialLotAction.setTransQty(materialLot.getCurrentQty());
                materialLotAction.setTransCount(materialLot.getCurrentSubQty());
                mmsService.stockIn(materialLot, materialLotAction);

                ErpMaterialIn erpMaterialIn = new ErpMaterialIn();
                erpMaterialIn.setMaterialLot(materialLot);
                erpMaterialInRepository.save(erpMaterialIn);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料报废操作
     * @param materialLotList
     * @param reason
     * @param remarks
     * @throws ClientException
     */
    public void scrapRawMaterial(List<MaterialLot> materialLotList, String reason, String remarks) throws ClientException{
        try {
            for(MaterialLot materialLot: materialLotList){
                mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_SCRAP, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RAW_SCRAP);
                history.setActionReason(reason);
                history.setActionComment(remarks);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 删除物料批次信息
     * @param materialLotList
     * @param remarks
     * @throws ClientException
     */
    public void deleteMaterialLotAndSaveHis(List<MaterialLot> materialLotList, String remarks) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                materialLotRepository.delete(materialLot);
                List<MaterialLotInventory> materialLotInventoryList = materialLotInventoryRepository.findByMaterialLotRrn(materialLot.getMaterialRrn());
                if (CollectionUtils.isNotEmpty(materialLotInventoryList)){
                    materialLotInventoryRepository.deleteByMaterialLotRrn(materialLotInventoryList.get(0).getMaterialLotRrn());
                }
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_DELETE);
                history.setActionComment(remarks);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * WLT/CP晶圆无订单发料
     */
    public void waferOutOrderIssue(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());

            SimpleDateFormat formats = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            String ddate = formats.format(new Date());
            //写数据到中间表etm_material_out表中，WLA/CP一个lot写入一条，FT的一个晶圆写入一条
            for(MaterialLot materialLot: materialLots){
                String importType = materialLot.getReserved49();
                if(MaterialLot.IMPORT_SENSOR_CP.equals(importType) || MaterialLot.IMPORT_LCD_CP.equals(importType) || MaterialLot.IMPORT_WLA.equals(importType)){
                    ErpMaterialOut erpMaterialOut = new ErpMaterialOut();
                    erpMaterialOut.setCmocode(materialLot.getLotId());
                    erpMaterialOut.setMaterialLot(materialLot);
                    if(materialLot.getCurrentSubQty().compareTo(BigDecimal.ZERO) > 0){
                        erpMaterialOut.setFqty(materialLot.getCurrentSubQty().toString());
                    }
                    if(materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) > 0){
                        erpMaterialOut.setMaterialQty(materialLot.getCurrentQty().toString());
                    }
                    erpMaterialOut.setCmaker(ThreadLocalContext.getUsername());
                    erpMaterialOut.setDmakerdate(ddate);
                    erpMaterialOutRepository.save(erpMaterialOut);
                } else if(MaterialLot.IMPORT_FT.equals(importType) || MaterialLot.IMPORT_SENSOR.equals(importType)){
                    List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    for(MaterialLotUnit materialLotUnit: materialLotUnitList){
                        ErpMaterialOut erpMaterialOut = new ErpMaterialOut();
                        erpMaterialOut.setMaterialLot(materialLot);
                        erpMaterialOut.setCmocode(materialLotUnit.getUnitId());
                        erpMaterialOut.setCmaker(ThreadLocalContext.getUsername());
                        erpMaterialOut.setFqty(ErpMaterialOut.DEFAULT_FQTY);
                        if(materialLotUnit.getCurrentQty().compareTo(BigDecimal.ZERO) > 0){
                            erpMaterialOut.setMaterialQty(materialLotUnit.getCurrentQty().toString());
                        }
                        erpMaterialOut.setDmakerdate(ddate);
                        erpMaterialOutRepository.save(erpMaterialOut);
                    }
                 }
            }

            waferIssueWithOutDocument(materialLots);

            //将晶圆信息保存至Mes backendWaferReceive表中
            mesService.saveBackendWaferReceive(materialLots);

            boolean waferIssueToMesPlanLot = SystemPropertyUtils.getWaferIssueToMesPlanLot();
            log.info("wafer issue to mes plan lot flag is " + waferIssueToMesPlanLot);
            if(waferIssueToMesPlanLot){
                log.info("wafer issue to mes plan lot start ");
                mesService.materialLotUnitPlanLot(materialLots);
                log.info("wafer issue to mes plan lot end ");
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料发料
     * @param documentLineList
     * @param materialLotList
     * @param issueWithDoc
     * @throws ClientException
     */
    public void validateAndRawMaterialIssue(List<DocumentLine> documentLineList, List<MaterialLot> materialLotList, String issueWithDoc) throws ClientException{
        try {
            List<MaterialLot> materialLots = materialLotList.stream().map(materialLot -> mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> materialNameMap = materialLots.stream().collect(Collectors.groupingBy(MaterialLot :: getMaterialName));
            for (String materialName : materialNameMap.keySet()) {
                List<MaterialLot> materialNameList = materialNameMap.get(materialName);
                List<MaterialLot> glueMLots = materialNameList.stream().filter(materialLot -> Material.MATERIAL_TYPE_GLUE.equals(materialLot.getMaterialType())).collect(Collectors.toList());
                List<MaterialLot> goldMLots = materialNameList.stream().filter(materialLot -> Material.MATERIAL_TYPE_GOLD.equals(materialLot.getMaterialType())).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(glueMLots)) {
                    validateRawIssueExpDate(glueMLots, materialName, Material.MATERIAL_TYPE_GLUE);
                }
                if (CollectionUtils.isNotEmpty(goldMLots)) {
                    validateRawIssueExpDate(goldMLots, materialName, Material.MATERIAL_TYPE_GOLD);
                }
            }
            if (StringUtils.isNullOrEmpty(issueWithDoc)){
                for(MaterialLot materialLot : materialLots){
                    materialLot.setCurrentQty(BigDecimal.ZERO);
                    materialLot = mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);

                    MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RAW_MATERIAL_ISSUE);
                    materialLotHistoryRepository.save(materialLotHistory);

                    materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                }
            }else {
                Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByImportType(materialLots, MaterialLot.RAW_MATERIAL_ISSUE_DOC_VALIDATE_RULE_ID, MaterialLot.COB_WAFER_RECEIVE_DOC_VALIDATE_RULE_ID);
                documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine) documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
                Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.RAW_MATERIAL_ISSUE_DOC_VALIDATE_RULE_ID);
                for (String key : materialLotMap.keySet()) {
                    if (!documentLineMap.keySet().contains(key)) {
                        throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getLotId());
                    }
                    Long totalRawMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                    Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                    if (totalRawMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                        throw new ClientException(GcExceptions.OVER_DOC_QTY);
                    }
                    rawMaterialIssueBySpareOrder(documentLineMap.get(key), materialLotMap.get(key));
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端原材料发料
     * @param materialLotList
     * @param erpTime
     * @throws ClientException
     */
    public void mobileValidateAndRawMaterialIssue(List<MaterialLot> materialLotList, String erpTime, String issueWithDoc) throws ClientException{
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_RAW_ISSUE_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            validateAndRawMaterialIssue(documentLineList, materialLotList, issueWithDoc);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 通过时间来匹配单据
     * @param nbTable
     * @param erpTime
     * @return
     * @throws ClientException
     */
    private List<DocumentLine> findDocumentLineByTime(NBTable nbTable, String erpTime) throws ClientException {
        try {
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" and erpCreated = to_date('"+ erpTime +"', 'yyyy-MM-dd')");
            whereClause = clauseBuffer.toString();
            List<DocumentLine> documentLineList = documentLineRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            return documentLineList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证发料是否都是最早日期
     * @param materialLots
     * @throws ClientException
     */
    private void validateRawIssueExpDate(List<MaterialLot> materialLots, String materialName, String materialType) throws ClientException {
        try {
            if (materialType.equals(Material.MATERIAL_TYPE_GLUE)) {
                for (MaterialLot glueMLot : materialLots) {
                    if (glueMLot.getExpDate().before(new Date())) {
                        throw new ClientParameterException(GcExceptions.GLUE_MATERIAL_HAS_EXPIRED, glueMLot.getMaterialLotId());
                    }
                }
            }
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot::getCreated)).collect(Collectors.toList());
            Date maxExpDate = materialLots.get(materialLots.size()-1).getExpDate();
            List<MaterialLot> scanMaterialLotList = materialLots.stream().filter(materialLot -> materialLot.getExpDate().before(maxExpDate)).collect(Collectors.toList());
            SimpleDateFormat formats = new SimpleDateFormat(MaterialLot.DEFAULT_DATE_PATTERN);
            String maxDate = formats.format(maxExpDate);

            NBTable nbTable = uiService.getNBTableByName(MaterialLot.GC_RAW_MATERIAL_WAIT_ISSUE_MLOT);
            String whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer(whereClause);
            clauseBuffer.append(" and expDate < to_date('"+ maxDate +"', 'yyyy-MM-dd hh24:mi:ss')");
            clauseBuffer.append(" and materialName = '"+ materialName +"'");
            clauseBuffer.append(" and materialType = '"+ materialType +"'");
            whereClause = clauseBuffer.toString();
            List<MaterialLot> waitIssueMLots = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
            if (waitIssueMLots.size() > scanMaterialLotList.size()) {
                throw new ClientException(GcExceptions.PLEASE_ISSUE_MATERIAL_LOT_EXP_DATE_EARLIER);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料批次自动匹配发料单发料
     * @param documentLines
     * @param materialLots
     */
    private void rawMaterialIssueBySpareOrder(List<DocumentLine> documentLines, List<MaterialLot> materialLots) throws ClientException{
        try {
            for (DocumentLine documentLine: documentLines) {
                BigDecimal unhandedQty = documentLine.getUnHandledQty();
                Iterator<MaterialLot> iterator = materialLots.iterator();
                while (iterator.hasNext()) {
                    MaterialLot materialLot = iterator.next();
                    if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                        materialLot.setReserved12(documentLine.getObjectRrn().toString());
                    } else {
                        materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                    }
                    BigDecimal currentQty = materialLot.getCurrentQty();
                    if (unhandedQty.compareTo(currentQty) >= 0) {
                        unhandedQty = unhandedQty.subtract(currentQty);
                        currentQty =  BigDecimal.ZERO;
                    } else {
                        currentQty = currentQty.subtract(unhandedQty);
                        unhandedQty = BigDecimal.ZERO;
                    }
                    materialLot.setCurrentQty(currentQty);
                    if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                        mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                        materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                        iterator.remove();
                    } else {
                        List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                        if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                            MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                            materialLotInv.setStockQty(currentQty);
                            materialLotInv.setCurrentSubQty(materialLot.getCurrentSubQty());
                            materialLotInventoryRepository.save(materialLotInv);
                        }
                    }

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                    materialLotHistoryRepository.save(history);

                    if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
                BigDecimal handleQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                if(handleQty.compareTo(BigDecimal.ZERO) == 0){
                    break;
                } else {
                    BigDecimal unHandledQty = documentLine.getUnHandledQty().subtract(handleQty);
                    if(unHandledQty.compareTo(BigDecimal.ZERO) < 0){
                        throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
                    }
                    documentLine.setHandledQty(documentLine.getHandledQty().add(handleQty));
                    documentLine.setUnHandledQty(unHandledQty);
                    documentLine = documentLineRepository.saveAndFlush(documentLine);
                    baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RAW_MATERIAL_ISSUE);

                    updateRawMaterialIssueOrderAndErpMaterialOutaOrderAndSaveHis(documentLine, handleQty, MaterialLotHistory.TRANS_TYPE_RAW_MATERIAL_ISSUE);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 报废原材料出库 （单据自动匹配）
     * @param documentLineList
     * @param materialLotList
     * @throws ClientException
     */
    @Override
    public void scrapRawMaterialShip(List<DocumentLine> documentLineList, List<MaterialLot> materialLotList) throws ClientException {
        try {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.RW_MLOT_SCRAP_AND_SHIP_VALIDATE_RULE_ID);
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, MaterialLot.RW_MLOT_SCRAP_AND_SHIP_VALIDATE_RULE_ID);
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientException(GcExceptions.OVER_DOC_QTY);
                }
                documentLineList = vlidateDocMergeAndSortDocumentLinesBySeq(documentLineMap.get(key));
                for (DocumentLine documentLine : documentLineList) {
                    BigDecimal unhandedQty = documentLine.getUnHandledQty();
                    Iterator<MaterialLot> iterator = materialLotMap.get(key).iterator();
                    while (iterator.hasNext()) {
                        MaterialLot materialLot = iterator.next();
                        if (StringUtils.isNullOrEmpty(materialLot.getReserved12())) {
                            materialLot.setReserved12(documentLine.getObjectRrn().toString());
                        } else {
                            materialLot.setReserved12(materialLot.getReserved12() + StringUtils.SEMICOLON_CODE + documentLine.getObjectRrn().toString());
                        }
                        BigDecimal currentQty = materialLot.getCurrentQty();
                        if (unhandedQty.compareTo(currentQty) >= 0) {
                            unhandedQty = unhandedQty.subtract(currentQty);
                            currentQty =  BigDecimal.ZERO;
                        } else {
                            currentQty = currentQty.subtract(unhandedQty);
                            unhandedQty = BigDecimal.ZERO;
                        }
                        materialLot.setCurrentQty(currentQty);
                        if (materialLot.getCurrentQty().compareTo(BigDecimal.ZERO) == 0) {
                            mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                            materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());
                            iterator.remove();
                        } else {
                            List<MaterialLotInventory> materialLotInvList = mmsService.getMaterialLotInv(materialLot.getObjectRrn());
                            if (CollectionUtils.isNotEmpty(materialLotInvList)) {
                                MaterialLotInventory materialLotInv = materialLotInvList.get(0);
                                materialLotInv.setStockQty(currentQty);
                                materialLotInv.setCurrentSubQty(materialLot.getCurrentSubQty());
                                materialLotInventoryRepository.save(materialLotInv);
                            }
                        }

                        MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                        materialLotHistoryRepository.save(history);

                        if (unhandedQty.compareTo(BigDecimal.ZERO) == 0) {
                            break;
                        }
                    }
                    BigDecimal handleQty = documentLine.getUnHandledQty().subtract(unhandedQty);
                    if(handleQty.compareTo(BigDecimal.ZERO) == 0){
                        break;
                    } else {
                        BigDecimal unHandledQty = documentLine.getUnHandledQty().subtract(handleQty);
                        if(unHandledQty.compareTo(BigDecimal.ZERO) < 0){
                            throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLine.getDocId());
                        }
                        documentLine.setHandledQty(documentLine.getHandledQty().add(handleQty));
                        documentLine.setUnHandledQty(unHandledQty);
                        documentLine = documentLineRepository.saveAndFlush(documentLine);
                        baseService.saveHistoryEntity(documentLine, MaterialLotHistory.TRANS_TYPE_RAW_MATERIAL_ISSUE);

                        RawMaterialOtherOutOrder rawMaterialOtherOutOrder = (RawMaterialOtherOutOrder) rawMaterialOtherOutOrderRepository.findByObjectRrn(documentLine.getDocRrn());
                        rawMaterialOtherOutOrder.setHandledQty(rawMaterialOtherOutOrder.getHandledQty().add(handleQty));
                        rawMaterialOtherOutOrder.setUnHandledQty(rawMaterialOtherOutOrder.getUnHandledQty().subtract(handleQty));
                        rawMaterialOtherOutOrderRepository.save(rawMaterialOtherOutOrder);
                        baseService.saveHistoryEntity(rawMaterialOtherOutOrder, MaterialLotHistory.TRANS_TYPE_SCRAP_SHIP);

                        validateDocAndUpdateErpSo(documentLine, handleQty);
                    }
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手持端 报废原材料出库 ==>  “原材料其他出库”功能
     * @param erpTime
     * @param materialLotList
     * @throws ClientException
     */
    public void mobileRawMaterialOtherShip(String erpTime, List<MaterialLot> materialLotList) throws ClientException {
        try {
            NBTable nbTable = uiService.getNBTableByName(MaterialLot.MOBILE_RAW_MATERIAL_SCRAP_SHIP_WHERE_CLAUSE);
            List<DocumentLine> documentLineList = findDocumentLineByTime(nbTable, erpTime);
            if (CollectionUtils.isEmpty(documentLineList)){
                throw new ClientException(GcExceptions.RAW_DOCUMENT_LINE_IS_EMPTY);
            }
            scrapRawMaterialShip(documentLineList, materialLotList);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取消原材料备料
     * @param materialLotList
     * @throws ClientException
     */
    @Override
    public void unRawMaterialSpare(List<MaterialLot> materialLotList) throws ClientException {
        try{
            for(MaterialLot materialLot : materialLotList){
                materialLot.restoreStatus();
                materialLot.setReservedQty(BigDecimal.ZERO);
                materialLot.setReserved16(StringUtils.EMPTY);
                materialLot.setReserved17(StringUtils.EMPTY);
                materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RAW_UN_SPARE);
                materialLotHistoryRepository.save(materialLotHistory);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改原材料发料单及中间表数量信息
     * @param documentLine
     * @param handleQty
     * @param transType
     * @throws ClientException
     */
    private void updateRawMaterialIssueOrderAndErpMaterialOutaOrderAndSaveHis(DocumentLine documentLine, BigDecimal handleQty, String transType) throws ClientException{
        try {
            MaterialIssueOrder materialIssueOrder = (MaterialIssueOrder) materialIssueOrderRepository.findByObjectRrn(documentLine.getDocRrn());
            materialIssueOrder.setHandledQty(materialIssueOrder.getHandledQty().add(handleQty));
            materialIssueOrder.setUnHandledQty(materialIssueOrder.getUnHandledQty().subtract(handleQty));
            materialIssueOrderRepository.save(materialIssueOrder);
            baseService.saveHistoryEntity(materialIssueOrder, transType);

            Optional<ErpMaterialOutaOrder> erpMaterialOutAOrderOptional = erpMaterialOutAOrderRepository.findById(Long.valueOf(documentLine.getReserved1()));
            if(erpMaterialOutAOrderOptional.isPresent()) {
                ErpMaterialOutaOrder erpMaterialOutaOrder = erpMaterialOutAOrderOptional.get();
                erpMaterialOutaOrder.setLeftNum(erpMaterialOutaOrder.getLeftNum().subtract(handleQty));
                erpMaterialOutaOrder.setSynStatus(ErpMaterialOutOrder.SYNC_STATUS_OPERATION);
                if (StringUtils.isNullOrEmpty(erpMaterialOutaOrder.getDeliveredNum())) {
                    erpMaterialOutaOrder.setDeliveredNum(handleQty.toPlainString());
                } else {
                    BigDecimal docHandledQty = new BigDecimal(erpMaterialOutaOrder.getDeliveredNum());
                    docHandledQty = docHandledQty.add(handleQty);
                    erpMaterialOutaOrder.setDeliveredNum(docHandledQty.toPlainString());
                }
                erpMaterialOutAOrderRepository.save(erpMaterialOutaOrder);
            } else {
                throw new ClientParameterException(GcExceptions.ERP_RAW_MATERIAL_ISSUE_ORDER_IS_NOT_EXIST, documentLine.getReserved1());
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据物料合批规则验证物料批次信息是否一致
     * @param materialLot
     * @param materialLotActions
     * @return
     * @throws ClientException
     */
    public boolean validateMLotByPackageRule(MaterialLot materialLot,  List<MaterialLotAction> materialLotActions) throws ClientException{
        try {
            boolean flag = true;
            if (CollectionUtils.isNotEmpty(materialLotActions)) {
                List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
                materialLotList.add(materialLot);
                flag = mmsService.validationMLotByMergeRule(MaterialLot.WLT_SHIP_MLOT_MERGE_RULE, materialLotList);
            }
            return flag;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改Lot信息
     * @param materialLot
     * @throws ClientException
     */
    public void updateMaterialLotInfo(MaterialLot materialLot) throws ClientException{
        try {
            materialLot = materialLotRepository.saveAndFlush(materialLot);
            MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UPDATE);
            materialLotHistoryRepository.save(materialLotHistory);

            List<MaterialLotUnit> materialLotUnitList = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
            if(CollectionUtils.isNotEmpty(materialLotUnitList)){
                for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                    materialLotUnit.setReserved4(materialLot.getReserved6());
                    materialLotUnit.setReserved22(materialLot.getReserved22());
                    materialLotUnit.setReserved25(materialLot.getReserved25());
                    materialLotUnit.setReserved1(materialLot.getReserved1());
                    materialLotUnit.setGrade(materialLot.getGrade());
                    materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                    MaterialLotUnitHistory materialLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, MaterialLotHistory.TRANS_TYPE_UPDATE);
                    materialLotUnitHisRepository.save(materialLotUnitHistory);
                }
            }

        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改MRB结论备注
     * @param materialLotList
     * @param mrbComments
     * @throws ClientException
     */
    @Override
    public void updateMRBComments(List<MaterialLot> materialLotList, String mrbComments) throws ClientException {
        try {
            for (MaterialLot materialLot : materialLotList) {
                if (StringUtils.isNullOrEmpty(mrbComments)) {
                    materialLot.setMrbComments(mrbComments);
                } else {
                    materialLot.setMrbComments(mrbComments.trim());
                }
                materialLot = materialLotRepository.saveAndFlush(materialLot);
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_UPDATE_MRB_COMMENTS);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB晶圆出货标注根据导入文件获取物料批次
     * @param materialLotUnitList
     * @param nbTable
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> getMaterialLotUnitListByImportFileAndNbTable(List<MaterialLotUnit> materialLotUnitList, NBTable nbTable)throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = Lists.newArrayList();
            String orderBy = nbTable.getOrderBy();
            String queryLotId = StringUtils.EMPTY;
            for(MaterialLotUnit materialLotUnit : materialLotUnitList){
                String whereClause = nbTable.getWhereClause();
                StringBuffer clauseBuffer = new StringBuffer(whereClause);
                if(!StringUtils.isNullOrEmpty(materialLotUnit.getMaterialLotId())){
                    queryLotId = materialLotUnit.getMaterialLotId();
                    clauseBuffer.append(" AND materialLotId = ");
                    clauseBuffer.append("'" + queryLotId + "'");
                } else if(!StringUtils.isNullOrEmpty(materialLotUnit.getLotId())){
                    queryLotId = materialLotUnit.getLotId();
                    clauseBuffer.append(" AND lotId = ");
                    clauseBuffer.append("'" + materialLotUnit.getLotId() + "'");
                } else if(!StringUtils.isNullOrEmpty(materialLotUnit.getDurable())){
                    queryLotId = materialLotUnit.getDurable();
                    clauseBuffer.append(" AND durable = ");
                    clauseBuffer.append("'" + materialLotUnit.getDurable() + "'");
                } else {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_IMPORT_FILE_IS_ERRROR);
                }
                whereClause = clauseBuffer.toString();
                List<MaterialLot> mLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
                if(CollectionUtils.isEmpty(mLotList)){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, queryLotId);
                } else {
                    MaterialLot materialLot = mLotList.get(0);
                    List<MaterialLotUnit> mlLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnits.addAll(mlLotUnits);
                }
            }
            return materialLotUnits;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
    /**
     * 根据导入文件获取等待Hold的物料批次
     * @param materialLotList
     * @param nbTable
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotsByImportFileAndNbTable(List<MaterialLot> materialLotList, NBTable nbTable, String queryParentBoxFlag) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            String orderBy = nbTable.getOrderBy();
            String queryMLotId = StringUtils.EMPTY;
            for(MaterialLot materialLot : materialLotList){
                String whereClause = nbTable.getWhereClause();
                StringBuffer clauseBuffer = new StringBuffer(whereClause);
                if(!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())){
                    queryMLotId = materialLot.getParentMaterialLotId();
                    if(StringUtils.isNullOrEmpty(queryParentBoxFlag)){
                        clauseBuffer.append(" AND materialLotId = ");
                    } else {
                        clauseBuffer.append(" AND parentMaterialLotId = ");
                    }
                    clauseBuffer.append("'" + queryMLotId + "'");
                } else if(!StringUtils.isNullOrEmpty(materialLot.getMaterialLotId())){
                    queryMLotId = materialLot.getMaterialLotId();
                    clauseBuffer.append(" AND materialLotId = ");
                    clauseBuffer.append("'" + queryMLotId + "'");
                } else if(!StringUtils.isNullOrEmpty(materialLot.getDurable())){
                    queryMLotId = materialLot.getDurable();
                    clauseBuffer.append(" AND durable = ");
                    clauseBuffer.append("'" + materialLot.getDurable() + "'");
                }  else if(!StringUtils.isNullOrEmpty(materialLot.getLotId())){
                    queryMLotId = materialLot.getLotId();
                    clauseBuffer.append(" AND lotId = ");
                    clauseBuffer.append("'" + materialLot.getLotId() + "'");
                }  else {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_IMPORT_FILE_IS_ERRROR);
                }
                whereClause = clauseBuffer.toString();
                List<MaterialLot> mLotList = materialLotRepository.findAll(ThreadLocalContext.getOrgRrn(), whereClause, orderBy);
                if(CollectionUtils.isEmpty(mLotList)){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, queryMLotId);
                } else {
                    if(!StringUtils.isNullOrEmpty(materialLot.getLotId())  && materialLot.getLotId().startsWith(PRE_FIX_GCB)){
                        materialLots.addAll(mLotList);
                    }else {
                        materialLot = mLotList.get(0);
                        materialLots.add(materialLot);
                    }
                }
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据合并
     * @param documentLines
     * @throws ClientException
     */
    public void valaidateAndMergeErpDocLine(List<DocumentLine> documentLines, String ruleId) throws ClientException {
        try {
            Map<String, List<DocumentLine>> docTypeMap = documentLines.stream().collect(Collectors.groupingBy(DocumentLine :: getReserved31));
            for(String docType : docTypeMap.keySet()){
                List<DocumentLine> documentLineList = docTypeMap.get(docType);
                List<Long> seqList = Lists.newArrayList();
                //根据单据验证规则验证单据信息是否满足合批条件
                validationDocMergeRule(ruleId, documentLines);

                //将所有的单据合并成一条documentLine单据
                Long totalDocQty = documentLines.stream().collect(Collectors.summingLong(documentLine -> documentLine.getQty().longValue()));
                DocumentLine documentLine = new DocumentLine();
                documentLine.setDocumentLine(documentLines.get(0));
                documentLine.setQty(new BigDecimal(totalDocQty));
                documentLine.setUnHandledQty(documentLine.getQty());
                documentLine.setUnReservedQty(documentLine.getQty());
                documentLine.setMergeDoc(DocumentLine.DOC_MERGE);
                documentLine = documentLineRepository.saveAndFlush(documentLine);

                baseService.saveHistoryEntity(documentLine, DocumentLineHistory.TRANS_TYPE_MERGE_DOC);

                for(DocumentLine docLine : documentLines){
                    if(StringUtils.isNullOrEmpty(docLine.getReserved1())){
                        throw new ClientParameterException(GcExceptions.ERP_ORDER_CANNOT_EMPTY, documentLine.getDocId(), "seq" + documentLine.getReserved1());
                    }
                    seqList.add(Long.parseLong(docLine.getReserved1()));

                    //合单不删除原单据，数量至零，以便取消合单回复单据信息，并且记录合单时候的单据主键reserved32
                    docLine.setUnHandledQty(BigDecimal.ZERO);
                    docLine.setUnReservedQty(BigDecimal.ZERO);
                    docLine.setReserved32(documentLine.getObjectRrn().toString());
                    documentLineRepository.saveAndFlush(docLine);
                    baseService.saveHistoryEntity(docLine, DocumentLineHistory.TRANS_TYPE_MERGE_DOC);
                }

                //根据单据的类型更新中间表单据的状态
                updateErpOrderStatusBySeq(docType, DocumentLine.SYNC_STATUS_MERGE, DocumentLine.ERROR_MEMO, seqList);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 合单规则验证
     * @param ruleName
     * @param documentLines
     * @throws ClientException
     */
    public void validationDocMergeRule(String ruleName, List<DocumentLine> documentLines) throws ClientException{
        try {
            List<MLotDocRule> mLotDocLineRule = mLotDocRuleRepository.findByNameAndOrgRrn(ruleName, ThreadLocalContext.getOrgRrn());
            if (CollectionUtils.isEmpty(mLotDocLineRule)) {
                throw new ClientParameterException(GcExceptions.DOCUMENT_LINE_MERGE_RULE_IS_NOE_EXIST, ruleName);
            }
            MLotDocRuleContext mLotDocRuleContext = new MLotDocRuleContext();
            mLotDocRuleContext.setSourceObject(documentLines.get(0));
            mLotDocRuleContext.setDocumentLineList(documentLines);
            mLotDocRuleContext.setMLotDocRuleLines(mLotDocLineRule.get(0).getLines());
            mLotDocRuleContext.validationDocMerge();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 取消合单
     * @param documentLineList
     * @throws ClientException
     */
    @Override
    public void cancelErpMergeDocOrder(List<DocumentLine> documentLineList) throws ClientException {
        try {
            Map<String, List<DocumentLine>> docTypeMap = documentLineList.stream().collect(Collectors.groupingBy(DocumentLine :: getReserved31));
            for(String docType : docTypeMap.keySet()){
                List<Long> seqList = Lists.newArrayList();
                List<DocumentLine> docLineList = docTypeMap.get(docType);
                for(DocumentLine documentLine : docLineList){
                    if(documentLine.getHandledQty().compareTo(BigDecimal.ZERO) > 0 || documentLine.getReservedQty().compareTo(BigDecimal.ZERO) > 0){
                        throw new ClientParameterException(GcExceptions.THE_DOCUMENT_HAS_BEEN_OPERATED_AND_CANNOT_BE_CANCELED , documentLine.getDocId());
                    }
                    //恢复原单据数量，清除合单标记，删除已合单据
                    List<DocumentLine> documentLines = documentLineRepository.findByDocIdAndReserved32(documentLine.getDocId(), documentLine.getObjectRrn().toString());
                    if(CollectionUtils.isEmpty(documentLines)){
                        throw new ClientParameterException(GcExceptions.THE_DOCUMENT_IS_ERROR_PLEASE_CALL_ENGINNER, documentLine.getDocId());
                    }
                    for(DocumentLine docLine : documentLines){
                        seqList.add(Long.parseLong(docLine.getReserved1()));
                        docLine.setUnReservedQty(docLine.getQty());
                        docLine.setUnHandledQty(docLine.getQty());
                        docLine.setReserved32(null);
                        docLine = documentLineRepository.saveAndFlush(docLine);
                        baseService.saveHistoryEntity(docLine, DocumentLineHistory.TRANS_TYPE_CANCEL_MERGE_DOC);
                    }
                    documentLineRepository.deleteById(documentLine.getObjectRrn());
                    baseService.saveHistoryEntity(documentLine, DocumentLineHistory.TRANS_TYPE_DELETE);

                    updateErpOrderStatusBySeq(docType, ErpSo.SYNC_STATUS_SYNC_SUCCESS, DocumentLine.CANCEL_ERROR_MEMO, seqList);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改中间表单据状态
     * @param docType
     * @param syncStatus
     * @param errorMemo
     * @param seqList
     * @throws ClientException
     */
    private void updateErpOrderStatusBySeq(String docType, String syncStatus, String errorMemo, List<Long> seqList) throws ClientException{
        try {
            if(ErpSo.SOURCE_TABLE_NAME.equals(docType)){
                erpSoRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(syncStatus, errorMemo, Document.SYNC_USER_ID, seqList);
            } else if(ErpSoa.SOURCE_TABLE_NAME.equals(docType)){
                erpSoaOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(syncStatus, errorMemo, Document.SYNC_USER_ID, seqList);
            } else if(ErpSob.SOURCE_TABLE_NAME.equals(docType)){
                erpSobOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(syncStatus, errorMemo, Document.SYNC_USER_ID, seqList);
            } else if(ErpMaterialOutOrder.SOURCE_TABLE_NAME.equals(docType)){
                erpMaterialOutOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(syncStatus, errorMemo, Document.SYNC_USER_ID, seqList);
            } else if(ErpMaterialOutaOrder.SOURCE_TABLE_NAME.equals(docType)){
                erpMaterialOutAOrderRepository.updateSynStatusAndErrorMemoAndUserIdBySeq(syncStatus, errorMemo, Document.SYNC_USER_ID, seqList);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB晶圆标注自动挑选
     * @param materialLotUnitList
     * @param pickQty
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> rwTagginggAutoPickMLotUnit(List<MaterialLotUnit> materialLotUnitList, BigDecimal pickQty) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            List<MaterialLotUnit> materialLotUnits = Lists.newArrayList();
            Map<String, List<MaterialLotUnit>> mLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit :: getMaterialLotId));
            for(String materialLotId : mLotUnitMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotList.add(materialLot);
            }
            List<MaterialLot> materialLots = rwTagginggAutoPickMLot(materialLotList, pickQty);
            if(CollectionUtils.isNotEmpty(materialLots)){
                for(MaterialLot materialLot : materialLots){
                    List<MaterialLotUnit>  mLotUnits = materialLotUnitService.getUnitsByMaterialLotId(materialLot.getMaterialLotId());
                    materialLotUnits.addAll(mLotUnits);
                }
            }
            return materialLotUnits;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
    /**
     * RW批次标注自动挑选(按照先进先出的原则，先挑选装箱的)
     * @param materialLotList
     * @param pickQty
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> rwTagginggAutoPickMLot(List<MaterialLot> materialLotList, BigDecimal pickQty) throws ClientException{
        try {
            List<MaterialLot> pickMLotList = Lists.newArrayList();
            materialLotList = materialLotList.stream().sorted(Comparator.comparing(MaterialLot::getCurrentQty).reversed()).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLotList){
                if(pickQty.compareTo(materialLot.getCurrentQty()) >= 0){
                    pickMLotList.add(materialLot);
                    pickQty = pickQty.subtract(materialLot.getCurrentQty());
                }
            }
            return pickMLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB晶圆出货标注
     * @param materialLotUnitList
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @throws ClientException
     */
    public void cobMaterialLotUnitStockOutTag(List<MaterialLotUnit> materialLotUnitList, String customerName, String abbreviation, String remarks) throws ClientException{
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            Map<String, List<MaterialLotUnit>> mLotUnitMap = materialLotUnitList.stream().collect(Collectors.groupingBy(MaterialLotUnit :: getMaterialLotId));
            for(String materialLotId : mLotUnitMap.keySet()){
                MaterialLot materialLot = mmsService.getMLotByMLotId(materialLotId);
                materialLotList.add(materialLot);
            }
            rwMaterialLotStockOutTag(materialLotList, customerName, abbreviation, remarks);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
    /**
     * RW出货标注
     * @param materialLotList
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @throws ClientException
     */
    public void rwMaterialLotStockOutTag(List<MaterialLot> materialLotList, String customerName, String abbreviation, String remarks) throws ClientException{
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            String nowDate = simpleDateFormat.format(new Date());
            //验证装箱的Lot客户标识和客户简称是否一致，不一致不能标注
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMaterialLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMaterialLotId, ThreadLocalContext.getOrgRrn());
                validateMLotTagInfo(materialLot, customerName, abbreviation);

                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks, nowDate, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
            }

            for(MaterialLot materialLot : materialLotList){
                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks, nowDate, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     *
     * @param materialLotActions
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @throws ClientException
     */
    public void shipTagUpdate(List<MaterialLotAction> materialLotActions, String customerName, String abbreviation, String remarks)throws ClientException{
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            String nowDate = simpleDateFormat.format(new Date());
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMaterialLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMaterialLotId, ThreadLocalContext.getOrgRrn());
                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks, nowDate, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG_UPDATE);
            }
            for(MaterialLot materialLot : materialLotList){
                saveMaterialLotTaggingInfoAndSaveHis(materialLot, customerName, abbreviation, remarks, nowDate, MaterialLotHistory.TRANS_TYPE_STOCK_OUT_TAG_UPDATE);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
    /**
     * 保存标注信息并记录历史
     * @param materialLot
     * @param customerName
     * @param abbreviation
     * @param remarks
     * @param nowDate
     * @throws ClientException
     */
    private void saveMaterialLotTaggingInfoAndSaveHis(MaterialLot materialLot, String customerName, String abbreviation, String remarks, String nowDate, String transType) throws ClientException{
        try {
            materialLot.setCustomerId(abbreviation);
            materialLot.setShipper(customerName);
            materialLot.setReserved55(customerName);
            materialLot.setReserved54(MaterialLot.STOCKOUT_TYPE_4);
            materialLot.setReserved57(remarks);
            materialLot.setTagDate(DateUtils.parseDate(nowDate));
            materialLot.setTagUser(ThreadLocalContext.getUsername());
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, transType);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证箱中的Lot是否已经标注，如果已经标注，则验证客户标识与客户简称是否一致
     * @param materialLot
     * @param customerName
     * @param abbreviation
     * @throws ClientException
     */
    private void validateMLotTagInfo(MaterialLot materialLot, String customerName, String abbreviation) throws ClientException{
        try {
            List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn());
            for(MaterialLot mLot : materialLots){
                if(!StringUtils.isNullOrEmpty(mLot.getReserved55()) && !abbreviation.equals(mLot.getReserved55())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_CUSTOMER_NAME_IS_NOT_SAME, materialLot.getMaterialLotId());
                }
                if(!StringUtils.isNullOrEmpty(mLot.getCustomerId()) && !customerName.equals(mLot.getCustomerId())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_ABBREVIATION_IS_NOT_SAME, materialLot.getMaterialLotId());
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次添加快递单号
     * 暂时将出货单号记录到reserved56栏位（于CP出货标注PO共用一个栏位）
     * @param materialLotActions
     * @param shipOrderId
     * @throws ClientException
     */
    public void rwMaterialLotAddShipOrderId(List<MaterialLotAction> materialLotActions, String shipOrderId) throws ClientException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            String nowDate = simpleDateFormat.format(new Date());
            Date date = simpleDateFormat.parse(nowDate);
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMLotId, ThreadLocalContext.getOrgRrn());
                List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn()).stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getReserved56())).collect(Collectors.toList());
                if( CollectionUtils.isNotEmpty(materialLots) && !shipOrderId.equals(materialLots.get(0).getReserved56())){
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_SHIP_ORDER_ID_IS_NOT_SAME, parentMLotId);
                }

                saveMaterialLotShipOrderIdAndSaveHis(materialLot, shipOrderId, date);
            }
            for(MaterialLot materialLot : materialLotList){
                saveMaterialLotShipOrderIdAndSaveHis(materialLot, shipOrderId, date);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 物料批次取消快递单号
     * 清除出货单号reserved56
     * @param materialLotActions
     * @throws ClientException
     */
    public void rwMaterialLotCancelShipOrderId(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLotList = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLotList){
                cancelMaterialLotShipOrderIdAndSaveHis(materialLot);
            }
            Map<String, List<MaterialLot>> packedLotMap = materialLotList.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            for(String parentMLotId : packedLotMap.keySet()){
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMLotId, ThreadLocalContext.getOrgRrn());
                List<MaterialLot> materialLots = packageService.getPackageDetailLots(materialLot.getObjectRrn()).stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getReserved56())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(materialLots)){
                    cancelMaterialLotShipOrderIdAndSaveHis(materialLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * COB出货标注的物料批次做分组进行数量统计
     * @param materialLotList
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> previewRwShipTagUpdateMaterialLotList(List<MaterialLot> materialLotList) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, MaterialLot.RW_SHIP_TAG_UPDATE_PREVIEW_RULE_ID);
            for(String key : materialLotMap.keySet()){
                List<MaterialLot> mLots = materialLotMap.get(key);
                Long totalCurrentSubQty = mLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentSubQty().longValue()));
                Long totalCurrentQty = mLots.stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentQty().longValue()));
                MaterialLot materialLot = new MaterialLot();
                materialLot = mLots.get(0);
                materialLot.setCurrentQty(new BigDecimal(totalCurrentQty));
                materialLot.setCurrentSubQty(new BigDecimal(totalCurrentSubQty));
                materialLot.setScanSeq(mLots.size());
                materialLots.add(materialLot);
            }
            materialLots = materialLots.stream().sorted(Comparator.comparing(MaterialLot :: getMaterialName)
                    .thenComparing(MaterialLot :: getGrade)).collect(Collectors.toList());
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW保存物料批次出货单号
     * @param materialLot
     * @param shipOrderId
     * @throws ClientException
     */
    private void saveMaterialLotShipOrderIdAndSaveHis(MaterialLot materialLot, String shipOrderId, Date date) throws ClientException{
        try {
            materialLot.setReserved56(shipOrderId);
            materialLot.setDocDate(date);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_ADD_SHIP_ORDER_ID);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW取消物料批次出货单号
     * @param materialLot
     * @throws ClientException
     */
    private void cancelMaterialLotShipOrderIdAndSaveHis(MaterialLot materialLot) throws ClientException{
        try {
            materialLot.setReserved56(null);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CANCEL_SHIP_ORDER_ID);
            materialLotHistoryRepository.save(history);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW取消出货标注
     * @param materialLotActions
     * @throws ClientException
     */
    public void rwMaterialLotCancelStockTag(List<MaterialLotAction> materialLotActions) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotActions.stream().map(materialLotAction -> mmsService.getMLotByMLotId(materialLotAction.getMaterialLotId(), true)).collect(Collectors.toList());
            for(MaterialLot materialLot : materialLots){
                unTaggingMaterialLot(materialLot);
            }

            Map<String, List<MaterialLot>> packedLotMap = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId()))
                    .collect(Collectors.groupingBy(MaterialLot :: getParentMaterialLotId));
            if (packedLotMap != null && packedLotMap.keySet().size() > 0) {
                for (String parentMaterialLotId : packedLotMap.keySet()) {
                    MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(parentMaterialLotId, ThreadLocalContext.getOrgRrn());
                    unTaggingMaterialLot(materialLot);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW出货
     * @param materialLotList
     * @param documentLineList
     * @throws ClientException
     */
    public void rwStockOut(List<MaterialLot> materialLotList, List<DocumentLine> documentLineList) throws ClientException{
        try {
            documentLineList = documentLineList.stream().map(documentLine -> (DocumentLine)documentLineRepository.findByObjectRrn(documentLine.getObjectRrn())).collect(Collectors.toList());
            materialLotList = materialLotList.stream().map(materialLot -> mmsService.getMLotByMLotId(materialLot.getMaterialLotId(), true)).collect(Collectors.toList());
            Map<String, List<MaterialLot>> materialLotMap = groupMaterialLotByMLotDocRule(materialLotList, MaterialLot.RW_MLOT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            Map<String, List<DocumentLine>> documentLineMap = groupDocLineByMLotDocRule(documentLineList, MaterialLot.RW_MLOT_STOCK_OUT_DOC_VALIDATE_RULE_ID);
            for (String key : materialLotMap.keySet()) {
                if (!documentLineMap.keySet().contains(key)) {
                    throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_MATCH_ORDER, materialLotMap.get(key).get(0).getMaterialLotId());
                }
                Long totalMaterialLotQty = materialLotMap.get(key).stream().collect(Collectors.summingLong(materialLot -> materialLot.getCurrentSubQty().longValue()));
                Long totalUnhandledQty = documentLineMap.get(key).stream().collect(Collectors.summingLong(documentLine -> documentLine.getUnHandledQty().longValue()));
                if (totalMaterialLotQty.compareTo(totalUnhandledQty) > 0) {
                    throw new ClientParameterException(GcExceptions.OVER_DOC_QTY, documentLineMap.get(key).get(0).getDocId());
                }
                materialLotStockOutByErpSoa(documentLineMap.get(key), materialLotMap.get(key));
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 根据二维码信息获取辅料信息
     * 固定供应商Lintec(reserved22)、二维码第一段为供应商信息码（reserved23）
     * @param tapeMaterialCode
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> getMaterialLotByTapeMaterialCode(String tapeMaterialCode) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            List<String> mLotIdList = Lists.newArrayList();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MaterialLot.DEFAULT_NO_FORMAT_DATE_PATTERN);
            SimpleDateFormat formats = new SimpleDateFormat(DateUtils.DEFAULT_DATE_PATTERN);
            String[] tepaArray = tapeMaterialCode.split(" ");
            if(tepaArray.length < 116 ){
                throw new ClientParameterException(GcExceptions.TAPA_MATERIAL_CODE_IS_ERROR, tapeMaterialCode);
            }
            String[] tepaInfoArray = new String[20];
            int count = 0;
            for(int i=0; i<tepaArray.length; i++){
                if(!StringUtils.isNullOrEmpty(tepaArray[i])){
                    tepaInfoArray[count] = tepaArray[i];
                    count++;
                }
            }
            String materialName = tepaInfoArray[2];
            Material material = mmsService.getRawMaterialByName(materialName);
            if(material == null || !Material.MATERIAL_TYPE_TAPE.equals(material.getMaterialType())){
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, materialName);
            }
            String tepeType = tepaInfoArray[3];
            String tapeSize = tepeType.substring(2, 5) + "mm*" + tepeType.substring(10, 13) + "m*" + tepeType.substring(23, 24) + "R";
            String dateAndlots = tepaInfoArray[4];
            String mfgDate = formats.format(simpleDateFormat.parse(dateAndlots.substring(1, 9)));
            String expDate = formats.format(simpleDateFormat.parse(dateAndlots.substring(9, 17)));

            String materialLotIdList = dateAndlots.substring(17);
            while (materialLotIdList.length() >= 10) {
                MaterialLot materialLot = new MaterialLot();
                String materialLotId = materialLotIdList.substring(0, 10);
                if(CollectionUtils.isNotEmpty(mLotIdList) && mLotIdList.contains(materialLotId)){
                    throw new ClientParameterException(GcExceptions.TAPE_MATERIAL_LOT_ID_IS_REPEAT, materialLotId);
                }
                mLotIdList.add(materialLotId);
                MaterialLot oldMaterialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
                if(oldMaterialLot != null){
                    throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, materialLotId);
                }
                materialLot.setMaterial(material);
                materialLot.setCurrentQty(BigDecimal.ONE);
                materialLot.setCurrentSubQty(BigDecimal.ONE);
                materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
                materialLot.setStatus(MaterialStatus.STATUS_CREATE);
                materialLot.setStatusModelRrn(material.getStatusModelRrn());
                materialLot.initialMaterialLot();
                materialLot.setMaterialLotId(materialLotId);
                materialLot.setReserved2(tapeSize);
                materialLot.setReserved22(Material.MATERIAL_SHIPPER_NAME);
                materialLot.setReserved23(tepaArray[0]);
                materialLot.setReserved27(Material.MATERIAL_PO_EMPTY);
                materialLot.setReserved49(Material.MATERIAL_TYPE_TAPE);
                materialLot.setMfgDate(formats.parse(mfgDate));
                materialLot.setExpDate(formats.parse(expDate));
                materialLot.setTapeMaterialCode(tapeMaterialCode);

                materialLotList.add(materialLot);

                materialLotIdList = materialLotIdList.substring(10);
            }
            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * Tape辅料接收入库
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveTapeMaterial(List<MaterialLot> materialLotList, String tapeSize) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            Map<String, List<MaterialLot>> tapeMaterialMap = materialLotList.stream().collect(Collectors.groupingBy(MaterialLot :: getTapeMaterialCode));
            for(String tapeCode : tapeMaterialMap.keySet()){
                String lotId = generatorMLotsTransId(MaterialLot.TAPE_MATERIAL_LOT_ID_RULE);
                List<MaterialLot> materialLots = tapeMaterialMap.get(tapeCode);
                for(MaterialLot materialLot : materialLots){
                    materialLot.setLotId(lotId);
                    materialLot.setReserved13(warehouse.getObjectRrn().toString());
                    materialLot.setReserved24(tapeSize);
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    // 记录历史
                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                    materialLotHistoryRepository.save(history);

                    materialStockAndSaveToErpMaterialLotIn(materialLot, warehouse);
                }
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 原材料接收并保存至中间表
     * @param materialLot
     * @param warehouse
     * @throws ClientException
     */
    private void materialStockAndSaveToErpMaterialLotIn(MaterialLot materialLot, Warehouse warehouse) throws ClientException{
        try {
            MaterialLotAction materialLotAction = new MaterialLotAction();
            materialLotAction.setMaterialLotId(materialLot.getMaterialLotId());
            materialLotAction.setTargetWarehouseRrn(warehouse.getObjectRrn());
            materialLotAction.setTransQty(materialLot.getCurrentQty());
            materialLotAction.setTransCount(materialLot.getCurrentSubQty());
            mmsService.stockIn(materialLot, materialLotAction);

//            ErpMaterialIn erpMaterialIn = new ErpMaterialIn();
//            erpMaterialIn.setMaterialLot(materialLot);
//            erpMaterialInRepository.save(erpMaterialIn);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * Blade原材料查询
     * @param bladeMaterialCode
     * @return
     * @throws ClientException
     */
    public MaterialLot getMaterialLotByBladeMaterialCode(String bladeMaterialCode) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            Material material = mmsService.getRawMaterialByName(bladeMaterialCode);
            if(material == null || !Material.MATERIAL_TYPE_BLADE.equals(material.getMaterialType())){
                throw new ClientParameterException(MM_RAW_MATERIAL_IS_NOT_EXIST, bladeMaterialCode);
            }
            materialLot.setMaterial(material);
            materialLot.setCurrentSubQty(BigDecimal.ONE);
            materialLot.setStatusCategory(MaterialStatus.STATUS_CREATE);
            materialLot.setStatus(MaterialStatus.STATUS_CREATE);
            materialLot.setStatusModelRrn(material.getStatusModelRrn());
            materialLot.initialMaterialLot();

            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 验证并获取Blade原材料批次号
     * @param bladeMaterialLotCode
     * @return
     * @throws ClientException
     */
    public String validateAndGetBladeMLotId(String bladeMaterialLotCode) throws ClientException{
        try {
            if(bladeMaterialLotCode.length() < 15){
                throw new ClientParameterException(GcExceptions.BLADE_MATERIAL_CODE_IS_ERROR, bladeMaterialLotCode);
            }
            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(bladeMaterialLotCode, ThreadLocalContext.getOrgRrn());
            if(materialLot != null){
                throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_EXIST, bladeMaterialLotCode);
            }
            return bladeMaterialLotCode;
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 接收Blade原材料
     * 默认存在浙江仓库
     * @param materialLotList
     * @throws ClientException
     */
    public void receiveBladeMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            Warehouse warehouse = mmsService.getWarehouseByName(WAREHOUSE_ZJ);
            //有效日期增加100年
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 100);

            for(MaterialLot materialLot : materialLotList){
                materialLot.setObjectRrn(null);
                materialLot.setReserved13(warehouse.getObjectRrn().toString());
                materialLot.setReserved22(Material.MATERIAL_DISCO);
                materialLot.setReserved27(Material.MATERIAL_PO_EMPTY);
                materialLot.setReserved49(Material.MATERIAL_TYPE_BLADE);
                materialLot.setMfgDate(new Date());
                materialLot.setExpDate(cal.getTime());
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                // 记录历史
                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, NBHis.TRANS_TYPE_CREATE);
                materialLotHistoryRepository.save(history);

                materialStockAndSaveToErpMaterialLotIn(materialLot, warehouse);
            }
        }catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW材料备料
     * @param materialLotList
     * @throws ClientException
     */
    public void spareRwMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot: materialLotList){
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_MATEREIAL_SPARE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_MATERIAL_SPARE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW材料取消备料
     * @param materialLotList
     * @throws ClientException
     */
    public void CancelSpareRwMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot: materialLotList){
                materialLot = mmsService.changeMaterialLotState(materialLot,  MaterialEvent.EVENT_CANCEL_MATEREIAL_SPARE, StringUtils.EMPTY);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CANCEL_MATERIAL_SPARE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RW原材料发料
     * @param materialLotList
     * @throws ClientException
     */
    public void issueRwMaterial(List<MaterialLot> materialLotList) throws ClientException{
        try {
            for(MaterialLot materialLot : materialLotList){
                mmsService.changeMaterialLotState(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE, StringUtils.EMPTY);
                materialLotInventoryRepository.deleteByMaterialLotRrn(materialLot.getObjectRrn());

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, GCMaterialEvent.EVENT_WAFER_ISSUE);
                materialLotHistoryRepository.save(history);
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取RW原材料批次信息
     * @param materialLotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public MaterialLot getRwMaterialLotByMaterialLotIdAndTableRrn(String materialLotId, Long tableRrn) throws ClientException{
        try {
            MaterialLot materialLot = new MaterialLot();
            if(materialLotId.length() > 20){
                materialLotId = materialLotId.substring(materialLotId.length() - 15, materialLotId.length() - 1);
            } else if(materialLotId.length() == 11){
                materialLotId = materialLotId.substring(0, materialLotId.length() - 1);
            }
            List<MaterialLot> materialLotList = queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, materialLotId);
            if(CollectionUtils.isNotEmpty(materialLotList)){
                materialLot = materialLotList.get(0);
            } else {
                materialLot.setMaterialLotId(materialLotId);
            }
            return materialLot;
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * wafer拆箱
     * @param materialLotUnits
     * @throws ClientException
     * @return
     */
    @Override
    public List<MaterialLot> waferUnpackMLot(List<MaterialLotUnit> materialLotUnits) throws ClientException {
        try {
            List<MaterialLot> materialLotList = Lists.newArrayList();
            String materialLotId = materialLotUnits.get(0).getMaterialLotId();
            MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLotId, ThreadLocalContext.getOrgRrn());
            Integer totalCurrentSubQty = materialLotUnits.size();
            Long totalCurrentQty = materialLotUnits.stream().collect(Collectors.summingLong(materialLotUnit -> materialLotUnit.getCurrentQty().longValue()));
            Long totalReceiveQty = materialLotUnits.stream().collect(Collectors.summingLong(materialLotUnit -> materialLotUnit.getReceiveQty().longValue()));

            materialLot = updateMaterialLotQtyAndSaveHis(materialLot, totalCurrentSubQty, totalCurrentQty, totalReceiveQty);
            materialLotList.add(materialLot);
            if (!StringUtils.isNullOrEmpty(materialLot.getParentMaterialLotId())) {
                MaterialLot parentMLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(materialLot.getParentMaterialLotId(), ThreadLocalContext.getOrgRrn());
                updateMaterialLotQtyAndSaveHis(parentMLot, totalCurrentSubQty, totalCurrentQty, totalReceiveQty);
            }

            MaterialLot targetMaterialLot = new MaterialLot();
            PropertyUtils.copyProperties(materialLot, targetMaterialLot);
            //生成新的箱号、LotId、CstId
            String targetMLotId = generatorMLotsTransId(MaterialLot.GENERATOR_MATERIAL_LOT_ID_RULE);
            String targetLotId = generatorByObjectAndRule(targetMaterialLot, MaterialLot.CREATE_WAFER_LOT_ID_RULE);
            String targetCstId = generatorByObjectAndRule(targetMaterialLot, MaterialLot.CREATE_WAFER_CST_ID_RULE);

            targetMaterialLot.setMaterialLotId(targetMLotId);
            targetMaterialLot.setLotId(targetLotId);
            targetMaterialLot.setDurable(targetCstId);
            targetMaterialLot.setCurrentSubQty(BigDecimal.valueOf(totalCurrentSubQty));
            targetMaterialLot.setCurrentQty(new BigDecimal(totalCurrentQty));
            targetMaterialLot.setReceiveQty(new BigDecimal(totalReceiveQty));
            targetMaterialLot = materialLotRepository.saveAndFlush(targetMaterialLot);
            materialLotList.add(targetMaterialLot);

            MaterialLotHistory materialLotHistory = (MaterialLotHistory) baseService.buildHistoryBean(targetMaterialLot, NBHis.TRANS_TYPE_CREATE);
            materialLotHistoryRepository.save(materialLotHistory);

            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                materialLotUnit.setMaterialLotId(targetMLotId);
                materialLotUnit.setLotId(targetLotId);
                materialLotUnit.setDurable(targetCstId);
                materialLotUnit = materialLotUnitRepository.saveAndFlush(materialLotUnit);

                MaterialLotUnitHistory mLotUnitHistory = (MaterialLotUnitHistory) baseService.buildHistoryBean(materialLotUnit, NBHis.TRANS_TYPE_CREATE);
                materialLotUnitHisRepository.save(mLotUnitHistory);
            }

            return materialLotList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 修改currentSubQty、currentQty、receiveQty，并存储历史
     * @param materialLot
     * @param currentSubQty
     * @param currentQty
     * @param receiveQty
     * @throws ClientException
     */
    private MaterialLot updateMaterialLotQtyAndSaveHis(MaterialLot materialLot, Integer currentSubQty, Long currentQty, Long receiveQty) throws ClientException {
        try {
            BigDecimal remainCurrentSubQty = materialLot.getCurrentSubQty().subtract(BigDecimal.valueOf(currentSubQty));
            BigDecimal remainCurrentQty = materialLot.getCurrentQty().subtract(new BigDecimal(currentQty));
            BigDecimal remainReceiveQty = materialLot.getReceiveQty().subtract(new BigDecimal(receiveQty));

            materialLot.setCurrentSubQty(remainCurrentSubQty);
            materialLot.setCurrentQty(remainCurrentQty);
            materialLot.setReceiveQty(remainReceiveQty);
            materialLot = materialLotRepository.saveAndFlush(materialLot);

            MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_WAFER_UNPACK);
            materialLotHistoryRepository.save(history);
            return materialLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 查询原材料批次信息，批次号位GCB开头时，当做LotId做查询，其余的当作物料批次做查询
     * @param queryLotId
     * @param tableRrn
     * @return
     * @throws ClientException
     */
    public List<MaterialLot> queryIssueRawMaterialByMaterialLotIdOrLotIdAndTableRrn(String queryLotId, Long tableRrn) throws ClientException{
        try {
            List<MaterialLot> materialLots = Lists.newArrayList();
            if(!StringUtils.isNullOrEmpty(queryLotId) && queryLotId.startsWith(Material.IRA_MATERIAL_BOX_ID_START)){
                materialLots = getIRARawMaterialByLotIdAndTableRrn(queryLotId, tableRrn);
            } else {
                materialLots = queryMaterialLotByTableRrnAndMaterialLotId(tableRrn, queryLotId);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 获取物料批次晶圆信息
     * @param materialLotList
     * @return
     * @throws ClientException
     */
    public List<MaterialLotUnit> getMaterialLotUnitListByMaterialLotList(List<MaterialLot> materialLotList)  throws ClientException{
        try {
            List<MaterialLotUnit> materialLotUnits = Lists.newArrayList();
            for(MaterialLot materialLot : materialLotList){
                List<MaterialLotUnit> materialLotUnitList = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                materialLotUnits.addAll(materialLotUnitList);
            }
            return materialLotUnits;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 查询补打真空包标签信息
     * @param tableRrn
     * @param vboxId
     * @return
     * @throws ClientException
     */
    public MesPackedLot queryVboxByTableRrnAndVboxId(Long tableRrn, String vboxId) throws ClientException {
        try {
            MesPackedLot mesPackedLot = new MesPackedLot();
            NBTable nbTable = uiService.getDeepNBTable(tableRrn);
            String _whereClause = nbTable.getWhereClause();
            String orderBy = nbTable.getOrderBy();
            StringBuffer clauseBuffer = new StringBuffer();
            clauseBuffer.append(" boxId = ");
            clauseBuffer.append("'" + vboxId + "'");

            if (!StringUtils.isNullOrEmpty(_whereClause)) {
                clauseBuffer.append(" AND ");
                clauseBuffer.append(_whereClause);
            }
            _whereClause = clauseBuffer.toString();
            List<MesPackedLot> mesPackedLots = mesPackedLotRepository.findAll(ThreadLocalContext.getOrgRrn(), _whereClause, orderBy);
            if(CollectionUtils.isNotEmpty(mesPackedLots)){
                mesPackedLot = mesPackedLots.get(0);
            } else {
                MaterialLot materialLot = materialLotRepository.findByMaterialLotIdAndOrgRrn(vboxId, ThreadLocalContext.getOrgRrn());
                if(materialLot != null){
                    if(MaterialLot.COG_WAFER_SOURCE.equals(materialLot.getReserved50())){
                        mesPackedLot.setProductCategory(MaterialLot.IMPORT_COG);
                    } else {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, vboxId);
                    }
                    mesPackedLot.setPackedLotRrn(materialLot.getObjectRrn());
                    mesPackedLot.setBoxId(materialLot.getMaterialLotId());
                    mesPackedLot.setProductId(materialLot.getMaterialName());
                    mesPackedLot.setGrade(materialLot.getGrade());
                    mesPackedLot.setLevelTwoCode(materialLot.getReserved1());
                    mesPackedLot.setLocation(materialLot.getReserved6());
                    mesPackedLot.setFinalOperationTime(materialLot.getUpdated());
                    mesPackedLot.setQuantity(materialLot.getCurrentQty().intValue());
                }
            }
            return mesPackedLot;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
