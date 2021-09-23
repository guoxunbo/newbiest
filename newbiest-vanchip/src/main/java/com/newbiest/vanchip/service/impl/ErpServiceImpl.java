package com.newbiest.vanchip.service.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.*;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.DocumentLineRepository;
import com.newbiest.mms.repository.InterfaceFailRepository;
import com.newbiest.mms.repository.InterfaceHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.mms.service.DocumentService;
import com.newbiest.mms.service.MmsService;
import com.newbiest.mms.service.impl.DocumentServiceImpl;
import com.newbiest.mms.state.model.MaterialStatus;
import com.newbiest.mms.state.model.MaterialStatusModel;
import com.newbiest.vanchip.dto.erp.ErpRequest;
import com.newbiest.vanchip.dto.erp.ErpResponse;
import com.newbiest.vanchip.dto.erp.ErpResponseReturn;
import com.newbiest.vanchip.dto.erp.backhaul.IncomingOrReturnRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.IncomingOrReturnRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.IncomingOrReturnRequestTXItem;
import com.newbiest.vanchip.dto.erp.backhaul.check.CheckRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.check.CheckRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.deliverystatus.DeliveryStatusRequest;
import com.newbiest.vanchip.dto.erp.backhaul.deliverystatus.DeliveryStatusRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.deliverystatus.DeliveryStatusRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.issue.IssueOrReturnRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.issue.IssueOrReturnRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.scrap.ScrapRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.scrap.ScrapRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.stocktransfer.StockTransferRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.stocktransfer.StockTransferRequestItem;
import com.newbiest.vanchip.dto.erp.delivery.*;
import com.newbiest.vanchip.dto.erp.incoming.IncomingRequestHeader;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponse;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponseHeader;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponseItem;
import com.newbiest.vanchip.service.ErpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 连接ERP的服务相关
 */
@Transactional
@Component
@Slf4j
@Service
public class ErpServiceImpl implements ErpService {

    /**
     * 连接ERP的超时时间 单位秒
     */
    public static final int ERP_CONNECTION_TIME_OUT = 30;

    /**
     * 读取ERP的超时时间 单位秒
     */
    public static final int ERP_READ_TIME_OUT = 60;

    @Value("${vc.erpUrl}")
    private String erpUrl;

    @Value("${vc.erpPo.userName}")
    private String erpUserName;

    @Value("${vc.erpPo.password}")
    private String erpPassword;
    /**
     * ERP默认的日期格式
     */
    public static final String ERP_DEFAULT_DATE_FORMAT= "yyyyMMdd";

    /**
     * 同步查询的时间间隔 3
     */
    public static final Integer ASYNC_QUERY_DATE_UNTIL= 3;

    /**
     * 来料/退料 信息同步批次
     */
    public static final String INCOMING_OR_RETURN_URL = "ZFMMM002";

    /**
     * 来料/退料 （入库/退料）回传
     */
    public static final String BACKHAUL_INCOMING_OR_RETURN_URL = "ZFMMM003";

    /**
     * 部门领料(发料)/退料 回传
     */
    public static final String BACKHAUL_DEPARTMENT_ISSUE_OR_RETURN = "ZFMMM005";

    /**
     * 盘点结果 回传
     */
    public static final String BACKHAUL_CHECK_URL = "ZFMMM007";

    /**
     * 库存调拨过账,涉及仓库变动：1.发往MES; 2.转库(仓库改变)
     */
    public static final String BACKHAUL_STOCK_TRANSFER_URL = "ZFMMM008";

    /**
     * 报废 回传
     */
    public static final String BACKHAUL_SCRAP_URL = "ZFMMM010";

    /**
     * 交货单状态 回传
     */
    public static final String BACKHAUL_DELIVERY_STATUS_URL = "ZFMSD001";

    /**
     * 交货单信息同步接口
     */
    public static final String DELIVERY_INFO_URL = "ZFMSD002";

    public static final String BACKHAUL_STOCK_IN_URL = "ZFMPP008";


    private RestTemplate restTemplate;

    @Autowired
    DocumentService documentService;

    @Autowired
    MmsService mmsService;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    BaseService baseService;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Autowired
    InterfaceFailRepository interfaceFailRepository;

    @Autowired
    InterfaceHistoryRepository interfaceHistoryRepository;

    @PostConstruct
    public void init() {
        CloseableHttpClient client = createHttpClient().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        requestFactory.setConnectTimeout(ERP_CONNECTION_TIME_OUT * 1000);
        requestFactory.setReadTimeout(ERP_READ_TIME_OUT * 1000);
        restTemplate = new RestTemplate(requestFactory);
    }

    protected HttpClientBuilder createHttpClient() {
        return create().useSystemProperties().disableRedirectHandling().disableCookieManagement();
    }


    public void asyncIncomingOrReturn() throws ClientException{
        try {
            SimpleDateFormat erpFormatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            Date now = DateUtils.now();
            Date beginDate = DateUtils.minus(now, ASYNC_QUERY_DATE_UNTIL, ChronoUnit.DAYS);

            String beginDateStr = erpFormatter.format(beginDate);
            String endingDateStr = erpFormatter.format(now);
            incomingOrReturn(beginDateStr, endingDateStr);
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * 获取来料/退料信息，信息同步接口， 辅材
     * 自动拉取,手动拉取
     * @param beginDate 20210701
     * @param endingDate 20210709
     * @throws ClientException
     */
    public List<MaterialLot> incomingOrReturn(String beginDate, String endingDate) throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            IncomingRequestHeader requestHeader = new IncomingRequestHeader();
            requestHeader.setBGDAT(beginDate);
            requestHeader.setENDAT(endingDate);
            request.setHeader(requestHeader);

            String responseStr = sendErpRequest(request, INCOMING_OR_RETURN_URL);
            IncomingResponse response = (IncomingResponse)DefaultParser.getObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .readValue(responseStr, IncomingResponse.class);

            List<IncomingResponseHeader> responeHeaders = response.getHeader();
            List<IncomingResponseItem> responeItems = response.getItem();
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            for (IncomingResponseHeader responeHeader : responeHeaders) {
                //获得单据下的物料信息；
                List<IncomingResponseItem> incomingResponseItems = responeItems.stream().filter(item -> item.getVBELN().equals(responeHeader.getVBELN())).collect(Collectors.toList());
                String docCategory = responeHeader.getLFART();
                if (IncomingResponseHeader.INCOMING_LFART.equals(docCategory)){
                    BigDecimal totalQty = incomingResponseItems.stream().collect(CollectorsUtils.summingBigDecimal(IncomingResponseItem::getLGMNG));
                    IncomingOrder erpIncomingOrder = new IncomingOrder();
                    erpIncomingOrder.setName(responeHeader.getVBELN());
                    erpIncomingOrder.setQty(totalQty);
                    erpIncomingOrder.setUnHandledQty(totalQty);

                    List<MaterialLot> erpMaterialLotList = Lists.newArrayList();
                    for (IncomingResponseItem responseItem : incomingResponseItems) {
                        MaterialLot materialLot = new MaterialLot();
                        materialLot = responeHeader.copyIncomingHeaderToMaterialLot(materialLot, responeHeader);
                        materialLot = responseItem.copyIncomingItemToMaterialLot(materialLot, responseItem);
                        materialLot.setProductionDate(formatter.parse(responseItem.getHSDAT()));
                        materialLot.setMaterialName(responseItem.getMATNR());
                        erpMaterialLotList.add(materialLot);
                    }

                    IncomingOrder incomingOrder = (IncomingOrder)documentService.getDocumentByName(responeHeader.getVBELN(), false);
                    List<MaterialLot> materialLots = mmsService.getMLotByIncomingDocId(responeHeader.getVBELN());

                    asyncIncomingInfo(erpIncomingOrder, erpMaterialLotList, incomingOrder, materialLots);
                }else if (IncomingResponseHeader.RETURN_LFART.equals(docCategory)){
                    //退料
                   createReturnMLot(responeHeader, incomingResponseItems);
                }
            }
            return null;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 辅材采购退料
     * @param returnHeader 单据信息 单据号，创建人
     * @param returnItems 单据详细信息 物料号，采购订单号，数量
     */
    public void createReturnMLot(IncomingResponseHeader returnHeader, List<IncomingResponseItem> returnItems) throws ClientException{
        try {
            Boolean updateFlag = false;
            List<DocumentLine> documentLineList = Lists.newArrayList();
            //退料单据 新建/修改,
            String returnOrderId = returnHeader.getVBELN();
            ReturnMLotOrder returnMLotOrder = (ReturnMLotOrder)documentService.getDocumentByName(returnOrderId, false);
            BigDecimal totalQty = returnItems.stream().collect(CollectorsUtils.summingBigDecimal(IncomingResponseItem::getLGMNG));
            if(returnMLotOrder == null){
                returnMLotOrder = new ReturnMLotOrder();
                returnMLotOrder.setReserved1(returnHeader.getERNAM());
                returnMLotOrder = (ReturnMLotOrder)documentService.createDocument(returnMLotOrder, returnOrderId, ReturnMLotOrder.GENERATOR_RETURN_MLOT_ORDER_RULE, true, totalQty);
            }else {
                //单据为处理且数量未百变化无需修改
                if(totalQty.compareTo(returnMLotOrder.getQty()) != 0 && returnMLotOrder.getHandledQty().compareTo(BigDecimal.ZERO) == 0){
                    BigDecimal differQty = totalQty.subtract(returnMLotOrder.getQty());
                    returnMLotOrder.setQty(totalQty);
                    returnMLotOrder.setUnHandledQty(returnMLotOrder.getUnHandledQty().add(differQty.negate()));
                    baseService.saveEntity(returnMLotOrder);
                }
                updateFlag = true;
                documentLineList = documentLineRepository.findByDocId(returnOrderId);
            }

            Map<String, List<IncomingResponseItem>> materialNameMap = returnItems.stream().collect(Collectors.groupingBy(IncomingResponseItem::getMATNR));
            for (String materialName : materialNameMap.keySet()) {
                Material material = mmsService.getMaterialByName(materialName, true);
                List<IncomingResponseItem> incomingResponseItems = materialNameMap.get(materialName);
                BigDecimal docLinetotalQty = incomingResponseItems.stream().collect(CollectorsUtils.summingBigDecimal(IncomingResponseItem::getLGMNG));
                DocumentLine documentLine = new DocumentLine();

                if (updateFlag) {
                    Optional<DocumentLine> documentLinefirst = documentLineList.stream().filter(docLine -> docLine.getMaterialName().equals(materialName)).findFirst();
                    documentLine = documentLinefirst.get();
                } else {
                    String docLineId = documentService.generatorDocId("ReturnMLotByMaterial");
                    documentLine.setLineId(docLineId);
                    documentLine.setDocument(returnMLotOrder);
                    documentLine.setMaterial(material);
                }
                if (documentLine.getHandledQty().compareTo(BigDecimal.ZERO) == 0) {
                    documentLine.setReserved29(incomingResponseItems.get(0).getVGBEL());
                    documentLine.setUnHandledQty(docLinetotalQty);
                    documentLine.setQty(docLinetotalQty);
                    documentLine.setReserved24(ReturnMLotOrder.DEFAULT_RETURN_MLOT_RESERVED_RULE);
                    documentLine.setReserved30(incomingResponseItems.get(0).getPOSNR());
                    baseService.saveEntity(documentLine);
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 来料/退料 过账（入库节点/退料节点） 辅材
     * 返回失败不回滚
     * @param documentLine
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulIncomingOrReturn(DocumentLine documentLine, List<MaterialLot> materialLots)throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            ErpRequest request = new ErpRequest();
            IncomingOrReturnRequestHeader requestHeader = new IncomingOrReturnRequestHeader();
            String stockInDate = this.erpDateFormat();
            requestHeader.setBUDAT(stockInDate);

            Integer itemId = 0;
            List<IncomingOrReturnRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                Material material = mmsService.getMaterialByName(materialLot.getMaterialName(), false);
                IncomingOrReturnRequestItem requestItem = new IncomingOrReturnRequestItem();
                itemId++;
                requestItem.setZEILE(itemId + "");

                requestItem = requestItem.copyMaterialLotToRequestItem(requestItem, materialLot);
                if (documentLine != null){
                    requestItem.setVBELN_IM(documentLine.getDocId());
                    requestItem.setPOSNR(documentLine.getReserved30());
                    requestItem.setEBELP("");
                    requestItem.setEBELN("");
                }

                IncomingOrReturnRequestTXItem requestTXItem = new IncomingOrReturnRequestTXItem();
                requestTXItem.setZ_BATCH_TYPEDES(material.getReserved4());
                requestTXItem.setZ_BATCH_MADATE(formatter.format(materialLot.getProductionDate()));
                requestTXItem.setZ_BATCH_OVERDATE(formatter.format(materialLot.getExpireDate()));
                requestTXItem.setZ_BATCH_POSTEDATE(erpDateFormat());
                requestTXItem.copyMaterialLotToRequestTXItem(requestTXItem, materialLot);

                //分批给出母批号
                if (materialLot.getSubMaterialLotFlag()){
                    requestTXItem.setZ_BATCH_WMSBATCH(materialLot.getParentMaterialLotId());
                }
                requestItem.setTXItem(requestTXItem);

                requestItems.add(requestItem);
            }
            requestHeader.setItem(requestItems);
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_INCOMING_OR_RETURN_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 部门领料(发料)/退料 回传
     * @param materialLots
     * @param bwart 移动类型 发料201/退料202
     * @param kostl 成品中心
     * @throws ClientException
     */
    public void backhaulDepartmentIssueOrReturn(List<MaterialLot> materialLots, String bwart, String kostl)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            IssueOrReturnRequestHeader requestHeader = new IssueOrReturnRequestHeader();
            List<IssueOrReturnRequestItem> items = Lists.newArrayList();
            Integer itemId = 0;
            for (MaterialLot materialLot : materialLots) {
                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                        IssueOrReturnRequestItem requestItem = new IssueOrReturnRequestItem();
                        itemId++;
                        requestItem.setZEILE(itemId.toString());
                        requestItem = requestItem.copyMaterialLotToIssueOrReturnRequestItem(requestItem, materialLot);
                        requestItem.setBWART(bwart);
                        requestItem.setKOSTL(kostl);
                        requestItem.setZ_BATCH_TBATCH(materialLot.getMaterialLotId());
                        items.add(requestItem);
                    }else {
                        List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                        for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                            IssueOrReturnRequestItem requestItem = new IssueOrReturnRequestItem();
                            itemId++;
                            requestItem.setZEILE(itemId.toString());
                            requestItem = requestItem.copyMaterialLotToIssueOrReturnRequestItem(requestItem, materialLot);
                            requestItem.setBWART(bwart);
                            requestItem.setKOSTL(kostl);
                            requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                            requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
                            items.add(requestItem);
                        }
                    }
                } else {
                    IssueOrReturnRequestItem requestItem = new IssueOrReturnRequestItem();
                    itemId++;
                    requestItem.setZEILE(itemId.toString());
                    requestItem = requestItem.copyMaterialLotToIssueOrReturnRequestItem(requestItem, materialLot);
                    requestItem.setBWART(bwart);
                    requestItem.setKOSTL(kostl);
                    if(Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                        requestItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
                    }
                    items.add(requestItem);
                }
            }
            requestHeader.setBUDAT(this.erpDateFormat());
            requestHeader.setItem(items);
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_INCOMING_OR_RETURN_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盘点回传
     * @param documentId 盘点单
     * @param materialLots
     * @param recheckFlag 重复盘点标识
     * @throws ClientException
     */
    public void backhaulCheck(String documentId, List<MaterialLot> materialLots, Boolean recheckFlag)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            CheckRequestHeader requestHeader = new CheckRequestHeader();
            requestHeader.setIBLNR(documentId);
            requestHeader.setBLDAT(this.erpDateFormat());

            List<CheckRequestItem> requestItems = Lists.newArrayList();
            Integer itemId = 0;
            for (MaterialLot materialLot : materialLots) {
                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        CheckRequestItem requestItem = new CheckRequestItem();
                        requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                        itemId++;
                        requestItem.setZEILI(itemId.toString());
                        if (recheckFlag){
                            requestItem.setZCOUNT("X");
                        }
                        requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                        if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                            requestItem.setZ_BATCH_REEL("");
                        }
                        requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
                        requestItems.add(requestItem);
                    }
                }else if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                    CheckRequestItem requestItem = new CheckRequestItem();
                    itemId++;
                    requestItem.setZEILI(itemId.toString());
                    if (recheckFlag){
                        requestItem.setZCOUNT("X");
                    }
                    requestItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
                    requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                    requestItems.add(requestItem);
                } else {
                    CheckRequestItem requestItem = new CheckRequestItem();
                    itemId++;
                    requestItem.setZEILI(itemId.toString());
                    if (recheckFlag){
                        requestItem.setZCOUNT("X");
                    }
                    requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                    requestItems.add(requestItem);
                }
            }
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_CHECK_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 调拨 回传
     * @param materialLots
     * @param materialLotActions 目标仓库
     * @throws ClientException
     */
    public void backhaulStockTransfer(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            StockTransferRequestHeader requestHeader = new StockTransferRequestHeader();
            List<StockTransferRequestItem> items = Lists.newArrayList();
            Integer itemId = 0;
            for (MaterialLot materialLot : materialLots) {
                Optional<MaterialLotAction> OptionalMaterialLotAction = materialLotActions.stream().filter(action -> action.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                MaterialLotAction firstMaterialLotAction = OptionalMaterialLotAction.get();

                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        StockTransferRequestItem requestItem = new StockTransferRequestItem();
                        requestItem.setZEILE(itemId++);
                        requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                        requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                        if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                            requestItem.setZ_BATCH_REEL("");
                        }
                        requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
                        requestItem.setUMLGO(firstMaterialLotAction.getTargetWarehouseId());
                        items.add(requestItem);
                    }
                }else if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                    StockTransferRequestItem requestItem = new StockTransferRequestItem();
                    requestItem.setZEILE(itemId++);
                    requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                    requestItem.setUMLGO(firstMaterialLotAction.getTargetWarehouseId());
                    requestItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
                    items.add(requestItem);
                } else {
                    StockTransferRequestItem requestItem = new StockTransferRequestItem();
                    requestItem.setZEILE(itemId++);
                    requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                    requestItem.setUMLGO(firstMaterialLotAction.getTargetWarehouseId());
                    items.add(requestItem);
                }
            }
            requestHeader.setItem(items);
            request.setHeader(requestHeader);
            sendErpRequest(request, BACKHAUL_STOCK_TRANSFER_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 报废 回传
     * @param documentId
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulScrap(String documentId, String kostl, List<MaterialLot> materialLots)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            ScrapRequestHeader requestHeader = new ScrapRequestHeader();
            requestHeader.setBUDAT(this.erpDateFormat());
            requestHeader.setZSCRAP(documentId);
            requestHeader.setKOSTL(kostl);

            Integer itemId = 0;
            List<ScrapRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                ScrapRequestItem requestItem = new ScrapRequestItem();
                requestItem.setZITEM(itemId++);
                requestItem.setMEINS(materialLot.getStoreUom());
                requestItem.setZMENGE(materialLot.getCurrentQty());
                requestItems.add(requestItem);
            }

            requestHeader.setItem(requestItems);
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_SCRAP_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 交货单状态 回传
     * @param documentLine
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulDeliveryStatus(String documentId, DocumentLine documentLine, List<MaterialLot> materialLots, String deliveryStatus)throws ClientException{
        try {
            DeliveryStatusRequest request = new DeliveryStatusRequest();

            List<DeliveryStatusRequestHeader> headers = Lists.newArrayList();
            DeliveryStatusRequestHeader header = new DeliveryStatusRequestHeader();
            header.setDelivery(documentId);
            header.setStatus(deliveryStatus);
            if (documentLine != null){
                if (DocumentServiceImpl.CREATE_BY_CUSTOMER_PRODUCT_RESERVED_RULE.equals(documentLine.getReserved24())){
                    header.setMode(DeliveryStatusRequestHeader.PRODUCT_DELIVERY_MODE);
                }else if (DocumentServiceImpl.CREATE_BY_CUSTOMER_VERSION_RESERVED_RULE.equals(documentLine.getReserved24())){
                    header.setMode(DeliveryStatusRequestHeader.VERSION_DELIVERY_MODE);
                }else {
                    header.setMode(DeliveryStatusRequestHeader.REEL_DELIVERY_MODE);
                }
                header.setShipping_no("");
            }
            headers.add(header);

            List<DeliveryStatusRequestItem> items = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                        item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                        item.setDelivery("单据号");
                        item.setReel(materialLot.getMaterialLotId());
                        if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                            item.setReel("");
                        }
                        item.setTest_batch(materialLotUnit.getUnitId());
                        items.add(item);
                    }
                } else if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                    DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                    item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                    item.setDelivery("单据号");
                    item.setBox_no(materialLot.getMaterialLotId());
                    items.add(item);
                } else {
                    DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                    item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                    item.setDelivery("单据号");
                    items.add(item);
                }

            }
            request.setData(headers);
            request.setItem(items);

            sendErpRequest(request, BACKHAUL_DELIVERY_STATUS_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步交货信息
     * @throws ClientException
     */
    public void asyncDeliveryInfo() throws ClientException{
        try {
            SimpleDateFormat erpFormatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            Date now = DateUtils.now();
            Date beginDate = DateUtils.minus(now, ASYNC_QUERY_DATE_UNTIL, ChronoUnit.DAYS);
            String beginDateStr = erpFormatter.format(beginDate);
            String endingDateStr = erpFormatter.format(now);

            deliveryInfo(beginDateStr, endingDateStr, null, null, null);
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     *  同步 采购到货通知单（待测品）/退货通知
     * 发货通知单，不良品发货通知单/RMA退货通知单
     * @param beginDateStr 开始日期 20210729
     * @param endingDateStr 结束日期
     * @param delivery 交货单号
     * @param status 交货状态
     * @param type 交货类型
     * @throws ClientException
     */
    public void deliveryInfo(String beginDateStr, String endingDateStr, List<Delivery> delivery, List<DeliveryStatus> status, List<DeliveryType> type) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            ErpRequest request = new ErpRequest();
            DeliveryInfoRequestHeader requestHeader = new DeliveryInfoRequestHeader();
            requestHeader.setBegin_date(beginDateStr);
            requestHeader.setEnd_date(endingDateStr);
            requestHeader.setDelivery(delivery);
            requestHeader.setStatu(status);
            requestHeader.setType(type);
            request.setHeader(requestHeader);

            String responseStr = sendErpRequest(request, DELIVERY_INFO_URL);
            DeliveryInfoResponse response = new DeliveryInfoResponse();
            if (!StringUtils.isNullOrEmpty(responseStr)) {
                response = DefaultParser.getObjectMapper().readerFor(Map.class).readValue(responseStr);
            }
            List<DeliveryInfoResponseData> responseDataList = response.getDATA();
            ErpResponseReturn responseMessage = response.getMESSAGE();

            //批次只有在Create状态,且未分批才能update;
            List<MaterialLot> createStatusMLots = materialLotRepository.findByStatus(MaterialStatus.STATUS_CREATE);

            for (DeliveryInfoResponseData responseData : responseDataList) {

                List<DeliveryInfoResponseItem> items = responseData.getItems();

                String shippingDateStr = responseData.getPlan_ship_date();
                Date shippingDate = new Date();
                if (!StringUtils.isNullOrEmpty(shippingDateStr)){
                    shippingDate = formatter.parse(shippingDateStr);
                }

                List<MaterialLot> erpMaterialLotList = Lists.newArrayList();
                for (DeliveryInfoResponseItem responseItem : items) {
                    MaterialLot materialLot = new MaterialLot();
                    materialLot = responseData.copyDeliveryInfoToMaterialLot(responseData, materialLot);
                    materialLot = responseItem.copyDeliveryInfoResponseItemToMaterialLot(responseItem, materialLot);
                    erpMaterialLotList.add(materialLot);
                }

                DocumentLine documentLine = new DocumentLine();
                documentLine = responseData.copyDeliveryInfoToDcoumentLine(responseData, documentLine);
                documentLine.setShippingDate(shippingDate);

                //交货类型
                String deliveryType = responseData.getType();

                //发货类型
                String shipMode = responseData.getShip_mode();
                if (DeliveryType.DELIVERY_TYPE_SHIP.equals(deliveryType)){

                    //ZTLF-精测正向交货单、发货单
                    createDeliveryOrder(documentLine, erpMaterialLotList, shipMode);
                }else if (DeliveryType.DELIVERY_TYPE_REJ_SHIP.equals(deliveryType)){

                    //ZTL2-精测不良品交货、次品发货
                    createDeliveryOrder(documentLine, erpMaterialLotList, shipMode);
                }else if (DeliveryType.DELIVERY_TYPE_RMA_INCOMING.equals(deliveryType)){

                    //ZTLR-精测RMA-自身原因、RMA到货
                    createRAMIncomingMLotOrder(responseData, shippingDate);
                }else if (DeliveryType.DELIVERY_TYPE_RMA_INCOMING2.equals(deliveryType)){

                    //ZTR2-精测RMA-非自身原因、RMA到货
                    createRAMIncomingMLotOrder(responseData, shippingDate);
                }else if (DeliveryType.DELIVERY_TYPE_INCOMING.equals(deliveryType)){
                    //ZEL-精测客供料入库交货、待测品到货

                    IncomingOrder erpIncomingOrder = new IncomingOrder();
                    erpIncomingOrder.setName(responseData.getDelivery());
                    erpIncomingOrder.setQty(responseData.getTotal());
                    erpIncomingOrder.setUnHandledQty(responseData.getTotal());

                    List<MaterialLot> materialLots = createStatusMLots.stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getItemId()) && mLot.getItemId().equals(responseData.getDelivery())).collect(Collectors.toList());
                    IncomingOrder incomingOrder = (IncomingOrder)documentService.getDocumentByName(responseData.getDelivery(), false);

                    asyncIncomingInfo(erpIncomingOrder, erpMaterialLotList, incomingOrder, materialLots);
                }else if (DeliveryType.DELIVERY_TYPE_RETURN.equals(deliveryType)){

                    //ZRL-精测客供料出库退货。待测品退货
                    createReturnMLotOrder(responseData, shippingDate);
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发货单
     * @param documentLine
     * @param erpMaterialLots
     * @param shipMode
     * @throws ClientException
     */
    public void createDeliveryOrder(DocumentLine documentLine, List<MaterialLot> erpMaterialLots, String shipMode) throws ClientException{
        try {
            BigDecimal totalQty = erpMaterialLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RAM品来料单
     * @param responseData
     * @throws ClientException
     */
    public void createRAMIncomingMLotOrder(DeliveryInfoResponseData responseData, Date shippingDate) throws ClientException{
        try {

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 待测品退料单
     * @param responseData
     * @throws ClientException
     */
    public void createReturnMLotOrder(DeliveryInfoResponseData responseData, Date shippingDate) throws ClientException{
        try {

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步来料信息
     * @param erpIncomingOrder erp单据信息 name,qty
     * @param erpMaterialLotList erp批次信息
     * @param incomingOrder wms单据信息 IncomingOrder
     * @param materialLotList wms创建状态下的批次信息
     * @throws ClientException
     */
    public void asyncIncomingInfo(IncomingOrder erpIncomingOrder, List<MaterialLot> erpMaterialLotList, IncomingOrder incomingOrder,List<MaterialLot> materialLotList) throws ClientException{
        try {
            //来料单据
            if (incomingOrder == null) {
                incomingOrder = (IncomingOrder)documentService.createDocument(erpIncomingOrder, erpIncomingOrder.getName(), IncomingOrder.GENERATOR_INCOMING_ORDER_ID_RULE, true, erpIncomingOrder.getQty());
            } else {
                //单据数量不一致且单据未进行处理进行修改
                if(erpIncomingOrder.getQty().compareTo(incomingOrder.getQty()) != 0){
                    BigDecimal differQty = erpIncomingOrder.getQty().subtract(incomingOrder.getQty());
                    incomingOrder.setQty(erpIncomingOrder.getQty());
                    incomingOrder.setUnHandledQty(incomingOrder.getUnHandledQty().add(differQty));
                    baseService.saveEntity(incomingOrder);
                }
            }

            Map<String, List<MaterialLot>> materialNameMap = erpMaterialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName:materialNameMap.keySet()) {

                Material material = mmsService.getMaterialByName(materialName, true);
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(material.getStatusModelRrn());

                List<MaterialLot> erpMaterialLots = materialNameMap.get(materialName);
                for (MaterialLot erpMaterialLot : erpMaterialLots) {

                    //itemId + incomingDocId 可以确认一条数据,如果多条表示已进行分批。
                    List<MaterialLot> mLots = materialLotList.stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getItemId()) && mLot.getItemId().equals(erpMaterialLot.getItemId())).collect(Collectors.toList());
                    Map<String, Object> propsMap = PropertyUtils.convertObj2Map(erpMaterialLot);

                    //批次信息不存在
                    if (mLots.size() == 0) {
                        //新建
                        erpMaterialLot.setIncomingDocRrn(incomingOrder.getObjectRrn());
                        erpMaterialLot.setIncomingDocId(incomingOrder.getName());
                        MaterialLot mLot = mmsService.createMLot(material, materialStatusModel, erpMaterialLot.getMaterialLotId(),
                                erpMaterialLot.getCurrentQty(), erpMaterialLot.getCurrentSubQty(), propsMap);
                    } else if (mLots.size() == 1) {
                        //修改
                        MaterialLot ordMLot = mLots.get(0);
                        Map<String, Object> ordPropsMap = PropertyUtils.convertObj2Map(ordMLot);

                        Boolean updateFalg = false;
                        //非创建状态不能修改
                        if (!MaterialStatus.STATUS_CREATE.equals(ordMLot.getStatus())){
                            continue;
                        }
                        if (propsMap != null && propsMap.size() > 0) {
                            for (String propName : propsMap.keySet()) {
                                Object propValue = propsMap.get(propName);
                                if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                                    continue;
                                }
                                Object ordPropValue = ordPropsMap.get(propName);
                                if (propValue instanceof BigDecimal && ((BigDecimal) propValue).compareTo((BigDecimal)ordPropValue) != 0){
                                    updateFalg = true;
                                    PropertyUtils.setProperty(ordMLot, propName, propsMap.get(propName));
                                }else {
                                    updateFalg = true;
                                    PropertyUtils.setProperty(ordMLot, propName, propsMap.get(propName));
                                }
                            }
                        }
                        if (updateFalg){
                            ordMLot.setMaterial(material);
                            ordMLot.setIncomingQty(erpMaterialLot.getIncomingQty());
                            ordMLot.setCurrentQty(erpMaterialLot.getIncomingQty());
                            ordMLot = materialLotRepository.saveAndFlush(ordMLot);
                        }
                    }
                }
            }

            //删除情况
            List<String> erpItemIds = erpMaterialLotList.stream().map(mLot -> mLot.getItemId()).collect(Collectors.toList());
            List<MaterialLot> detMLots = materialLotList.stream().filter(mLot -> !erpItemIds.contains(mLot.getItemId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(detMLots)){
                detMLots.forEach(mLot ->baseService.delete(mLot));
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }



    /**
     * 成品入库，待测品入库回传接口
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulStockIn(List<MaterialLot> materialLots) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            //sendErpRequest(request, BACKHAUL_STOCK_IN_URL, DeliveryInfoResponse.class);

        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 重发
     * @throws ClientException
     */
    public void retry() throws ClientException{
        try {
            List<InterfaceFail> interfaceFails = interfaceFailRepository.findAll();
            interfaceFails = interfaceFails.stream().sorted(Comparator.comparing(InterfaceFail::getCreated)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(interfaceFails)) {
                for (InterfaceFail interfaceFail : interfaceFails) {
                    String responseStr = sendErpRequest(interfaceFail.getRequestTxt(), interfaceFail.getDestination(), InterfaceHistory.TRANS_TYPE_RETRY);
                    ErpResponse response = (ErpResponse)DefaultParser.getObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).readValue(responseStr, ErpResponse.class);
                    if (response.getReturn() != null){
                        if (ErpResponseReturn.SUCCESS_STATUS.equals(response.getReturn().getSTATUS())){
                            interfaceFailRepository.delete(interfaceFail);
                        }
                    }
                }
            }
        } catch(Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 用于过账 不管接口的返回信息，回复失败不回滚
     * @param requestInfo
     * @param apiUrl
     * @throws ClientException
     */
    public void sendErpRequest(Object requestInfo, String apiUrl, Class responeClass) throws ClientException {
        sendErpRequest(requestInfo, erpUrl + apiUrl, responeClass, InterfaceHistory.TRANS_TYPE_NORMAL, false);
    }

    /**
     * 回复失败回滚
     * @param requestInfo
     * @param apiUrl
     * @throws ClientException
     */
    public String sendErpRequest(Object requestInfo, String apiUrl) throws ClientException {
        return sendErpRequest(requestInfo, erpUrl + apiUrl, null, InterfaceHistory.TRANS_TYPE_NORMAL, true);
    }

    /**
     * 可用于重发
     * @param requestInfo
     * @param url
     * @param transType
     * @return
     * @throws ClientException
     */
    public String sendErpRequest(Object requestInfo, String url, String transType) throws ClientException {
        return sendErpRequest(requestInfo, url, null, transType, false);
    }



    /**
     *
     * @param requestInfo 请求信息
     * @param url 请求路径
     * @param responseClass 处理响应的类
     * @param transType 事务类型
     * @param failBackFlag 回复失败是否回滚:true-回滚,false-不回滚
     * @return responseString 响应的字符串
     * @throws ClientException
     */
    public String sendErpRequest(Object requestInfo, String url, Class responseClass, String transType, Boolean failBackFlag) throws ClientException{
        InterfaceHistory interfaceHistory = new InterfaceHistory();
        interfaceHistory.setDestination(url);
        interfaceHistory.setTransType(transType);
        String responseString = StringUtils.EMPTY;
        try {
            String requestString = StringUtils.EMPTY;
            if (requestInfo instanceof String){
                requestString = (String) requestInfo;
            }else {
                requestString = DefaultParser.getObjectMapper().writeValueAsString(requestInfo);
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("Send data. RequestString is [%s]", requestString));
            }
            interfaceHistory.setRequestTxt(requestString);

            HttpHeaders headers = new HttpHeaders();
            String encodedPassword = new BASE64Encoder().encode((erpUserName +":"+ erpPassword).getBytes(StringUtils.getUtf8Charset()));
            headers.put(HttpHeaders.AUTHORIZATION, Lists.newArrayList("Basic" + StringUtils.BLANK_SPACE + encodedPassword));
            headers.put(HttpHeaders.CONTENT_TYPE, Lists.newArrayList("application/json"));

            RequestEntity<byte[]> requestEntity = new RequestEntity<>(requestString.getBytes(), headers, HttpMethod.POST, new URI(url));
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            responseString = new String(responseEntity.getBody(), StringUtils.getUtf8Charset());

            if (log.isDebugEnabled()) {
                log.debug(String.format("Get response by erp. Response is [%s]", responseString));
            }
            interfaceHistory.setResponseTxt(responseString);

            //回复不成功是否回滚
            if(!failBackFlag){
                if (responseClass == null){
                    responseClass = ErpResponse.class;
                }
                ErpResponse response = (ErpResponse)DefaultParser.getObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).readValue(responseString, responseClass);
                if (response.getReturn() != null){
                    if (!ErpResponseReturn.SUCCESS_STATUS.equals(response.getReturn().getSTATUS())){
                        throw new ClientException(response.getReturn().getMESSAGE());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String errorMessage = e.getMessage();
            if (!StringUtils.isNullOrEmpty(errorMessage) && errorMessage.length() > InterfaceHistory.ACTION_CODE_MAX_LENGTH) {
                errorMessage = errorMessage.substring(0, InterfaceHistory.ACTION_CODE_MAX_LENGTH);
            }
            interfaceHistory.setActionCode(errorMessage);
            interfaceHistory.setResult(InterfaceHistory.RESULT_FAIL);
        }
        interfaceHistory.setResponseTxt(responseString);
        if (InterfaceHistory.RESULT_FAIL.equals(interfaceHistory.getResult()) && !InterfaceHistory.TRANS_TYPE_RETRY.equals(transType)) {
            InterfaceFail interfaceFail = new InterfaceFail(interfaceHistory);
            interfaceFailRepository.saveAndFlush(interfaceFail);
        }
        interfaceHistoryRepository.saveAndFlush(interfaceHistory);
        return responseString;
    }

    public String erpDateFormat() throws ClientException{
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            return formatter.format(date);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
