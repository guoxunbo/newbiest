package com.newbiest.vanchip.service.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.google.common.collect.Lists;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.msg.DefaultParser;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.*;
import com.newbiest.mms.dto.MaterialLotAction;
import com.newbiest.mms.exception.DocumentException;
import com.newbiest.mms.exception.MmsException;
import com.newbiest.mms.model.*;
import com.newbiest.mms.repository.*;
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
import com.newbiest.vanchip.dto.erp.backhaul.scrap.ScrapRequest;
import com.newbiest.vanchip.dto.erp.backhaul.scrap.ScrapRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.scrap.ScrapRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.StockInRequest;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.StockInRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.StockInRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming.IncomingStockInRequest;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming.IncomingStockInRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming.IncomingStockInRequestItem;
import com.newbiest.vanchip.dto.erp.backhaul.stockin.incoming.IncomingStockInRequestTxItem;
import com.newbiest.vanchip.dto.erp.backhaul.stocktransfer.StockTransferRequestHeader;
import com.newbiest.vanchip.dto.erp.backhaul.stocktransfer.StockTransferRequestItem;
import com.newbiest.vanchip.dto.erp.delivery.*;
import com.newbiest.vanchip.dto.erp.incoming.IncomingRequestHeader;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponse;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponseHeader;
import com.newbiest.vanchip.dto.erp.incoming.IncomingResponseItem;
import com.newbiest.vanchip.dto.erp.split.SplitRequestHeader;
import com.newbiest.vanchip.service.ErpService;
import com.newbiest.vanchip.service.VanChipService;
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
import org.springframework.scheduling.annotation.Async;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.http.impl.client.HttpClientBuilder.create;

/**
 * 连接ERP的服务相关
 */
@Transactional
@Component
@Slf4j
@Service
@Async
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

    /**
     * 成品入库
     */
    public static final String BACKHAUL_STOCK_IN_URL = "ZFMPP008";

    /**
     * 主材入库/RMA非自身原因入库
     */
    public static final String BACKHAUL_MAIN_MATERIAL_STOCK_IN_URL = "ZFMMM041";

    /**
     * 分批
     */
    public static final String SPLIT_MLOT_URL = "ZFMMM042";

    //发货模式
    //01-Reel
    public final static String REEL_SHIP_MODE = "01";
    //02-产品型号
    public final static String PRODUCT_SHIP_MODE = "02";
    //03-版本号
    public final static String VERSION_SHIP_MODE = "03";

    //交货单状态
    //未读 新增/修改
    public final static String CFM_DELIVERY_STATUS = "CFM";
    //整单删除
    public final static String DEL_DELIVERY_STATUS = "DEL";
    //已读
    public final static String RED_DELIVERY_STATUS = "RED";

    /**
     * 移动类型 入库
     */
    private static final String BWART_STOCK_IN_MLOT = "511";

    /**
     * 移动类型 退库
     */
    private static final String BWART_RETURN_MLOT = "512";

    //量产
    private static final String MP_SHIPPING_TYPE = "01";

    //工程样品
    private static final String ES_SHIPPING_TYPE = "02";

    //客户样品
    private static final String CS_SHIPPING_TYPE = "03";



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

    @Autowired
    MaterialLotUnitRepository materialLotUnitRepository;

    @Autowired
    DocumentMLotRepository  documentMLotRepository;

    @Autowired
    MLotCheckSheetRepository  mLotCheckSheetRepository;

    @Autowired
    VanChipService  vanChipService;

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
                BigDecimal totalQty = incomingResponseItems.stream().collect(CollectorsUtils.summingBigDecimal(IncomingResponseItem::getLGMNG));
                if (IncomingResponseHeader.INCOMING_LFART.equals(docCategory)){
                    IncomingOrder erpIncomingOrder = new IncomingOrder();
                    erpIncomingOrder.setName(responeHeader.getVBELN());
                    erpIncomingOrder.setQty(totalQty);
                    erpIncomingOrder.setUnHandledQty(totalQty);

                    Date expectedDeliveryDate = null ;
                    if (!StringUtils.isNullOrEmpty(responeHeader.getLFDAT())){
                        expectedDeliveryDate = formatter.parse(responeHeader.getLFDAT());
                    }
                    List<MaterialLot> erpMaterialLotList = Lists.newArrayList();
                    for (IncomingResponseItem responseItem : incomingResponseItems) {
                        MaterialLot materialLot = new MaterialLot();
                        materialLot = responeHeader.copyIncomingHeaderToMaterialLot(materialLot, responeHeader);
                        materialLot = responseItem.copyIncomingItemToMaterialLot(materialLot, responseItem);
                        if (!StringUtils.isNullOrEmpty(responseItem.getHSDAT())){
                            materialLot.setProductionDate(formatter.parse(responseItem.getHSDAT()));
                        }
                        if (expectedDeliveryDate != null){
                            materialLot.setExpectedDeliveryDate(expectedDeliveryDate);
                        }
                        materialLot.setMaterialName(responseItem.getMATNR());
                        erpMaterialLotList.add(materialLot);
                    }

                    IncomingOrder incomingOrder = (IncomingOrder)documentService.getDocumentByName(responeHeader.getVBELN(), false);
                    List<MaterialLot> materialLots = mmsService.getMLotByIncomingDocId(responeHeader.getVBELN());

                    asyncIncomingInfo(erpIncomingOrder, erpMaterialLotList, incomingOrder, materialLots);
                }else if (IncomingResponseHeader.RETURN_LFART.equals(docCategory)){
                    //退料
                    ReturnMLotOrder returnMLotOrder = (ReturnMLotOrder)documentService.getDocumentByName(responeHeader.getVBELN(), false);

                    ReturnMLotOrder erpReturnMLotOrder = new ReturnMLotOrder();
                    erpReturnMLotOrder.setName(responeHeader.getVBELN());
                    erpReturnMLotOrder.setReserved1(responeHeader.getERNAM());
                    erpReturnMLotOrder.setQty(totalQty);
                    erpReturnMLotOrder.setUnHandledQty(totalQty);
                    erpReturnMLotOrder.setReserved5(responeHeader.getSTRAS());

                    if (!StringUtils.isNullOrEmpty(responeHeader.getLFDAT())){
                        erpReturnMLotOrder.setShippingDate(formatter.parse(responeHeader.getLFDAT()));
                    }

                    List<MaterialLot> erpMaterialLotList = Lists.newArrayList();
                    for (IncomingResponseItem responseItem : incomingResponseItems) {
                        erpReturnMLotOrder.setReserved3(responseItem.getZRETURNMRA());
                        erpReturnMLotOrder.setReserved4(responseItem.getINCO2_L());
                        erpReturnMLotOrder.setReserved6(responseItem.getTELNUMBER());
                        erpReturnMLotOrder.setReserved7(responseItem.getCONTACT());

                        MaterialLot materialLot = new MaterialLot();
                        materialLot = responeHeader.copyIncomingHeaderToMaterialLot(materialLot, responeHeader);
                        materialLot = responseItem.copyIncomingItemToMaterialLot(materialLot, responseItem);

                        materialLot.setLastWarehouseId(responseItem.getLGORT());
                        materialLot.setMaterialName(responseItem.getMATNR());
                        materialLot.setMaterialLotId(responseItem.getLICHN());
                        erpMaterialLotList.add(materialLot);
                    }
                    asyncReturnInfo(erpReturnMLotOrder, erpMaterialLotList, returnMLotOrder);
                }
            }
            return null;
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 辅材退料过账-退料节点
     * @param docId
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulReturnMLot(String docId, List<MaterialLot> materialLots)throws ClientException {
        try {
            ErpRequest request = new ErpRequest();
            IncomingOrReturnRequestHeader requestHeader = new IncomingOrReturnRequestHeader();
            String stockInDate = this.nowByErpDateFormat();
            requestHeader.setBUDAT(stockInDate);

            List<IncomingOrReturnRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                IncomingOrReturnRequestItem requestItem = new IncomingOrReturnRequestItem();
                requestItem.setVBELN_IM(docId);
                requestItem.setPOSNR(materialLot.getItemId());
                requestItem.setMENGE(materialLot.getCurrentQty());
                requestItem.setMATNR(materialLot.getMaterialName());
                requestItem.setLGORT(materialLot.getLastWarehouseId());
                IncomingOrReturnRequestTXItem requestTXItem = new IncomingOrReturnRequestTXItem();
                requestTXItem.copyMaterialLotToRequestTXItem(requestTXItem, materialLot);
                requestTXItem.setZ_BATCH_POSTEDATE(stockInDate);
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
     * 辅材来料入库过账-入库节点
     * 返回失败不回滚
     * @param materialLots
     * @throws ClientException
     */
    public void backhaulIncomingStockIn(List<MaterialLot> materialLots)throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            ErpRequest request = new ErpRequest();
            IncomingOrReturnRequestHeader requestHeader = new IncomingOrReturnRequestHeader();
            String stockInDate = this.nowByErpDateFormat();
            requestHeader.setBUDAT(stockInDate);

            List<String> materialLotIds = materialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MLotCheckSheet> mLotCheckSheetList = mLotCheckSheetRepository.findByMaterialLotIdIn(materialLotIds);

            Integer itemId = 0;
            List<IncomingOrReturnRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                Material material = mmsService.getMaterialByName(materialLot.getMaterialName(), false);
                IncomingOrReturnRequestItem requestItem = new IncomingOrReturnRequestItem();
                itemId++;
                requestItem.setZEILE(itemId + "");
                requestItem = requestItem.copyMaterialLotToRequestItem(requestItem, materialLot);

                IncomingOrReturnRequestTXItem requestTXItem = new IncomingOrReturnRequestTXItem();
                requestTXItem.setZ_BATCH_TYPEDES(material.getReserved4());
                if(materialLot.getProductionDate() != null){
                    requestTXItem.setZ_BATCH_MADATE(formatter.format(materialLot.getProductionDate()));
                }
                if(materialLot.getExpireDate() != null){
                    requestTXItem.setZ_BATCH_OVERDATE(formatter.format(materialLot.getExpireDate()));
                }

                if (CollectionUtils.isNotEmpty(mLotCheckSheetList)){
                    Optional<MLotCheckSheet> mLotCheckSheetFirst = mLotCheckSheetList.stream().filter(mLotCheckSheet -> mLotCheckSheet.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                    if (mLotCheckSheetFirst.isPresent()) {
                        MLotCheckSheet mLotCheckSheet = mLotCheckSheetFirst.get();
                        requestTXItem.setZ_BATCH_IQC(mLotCheckSheet.getCheckResult());
                    }
                }

                requestTXItem.setZ_BATCH_POSTEDATE(stockInDate);
                requestTXItem.copyMaterialLotToRequestTXItem(requestTXItem, materialLot);

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
            requestHeader.setBUDAT(this.nowByErpDateFormat());
            requestHeader.setItem(items);
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_DEPARTMENT_ISSUE_OR_RETURN, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 盘点回传
     * @param documentLine 盘点单
     * @param materialLots
     * @param warehouseName 仓库代码
     * @throws ClientException
     */
    public void backhaulCheck(DocumentLine documentLine, List<MaterialLot> materialLots, String warehouseName)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            CheckRequestHeader requestHeader = new CheckRequestHeader();
            requestHeader.setIBLNR(documentLine.getDocId());
            requestHeader.setBLDAT(this.nowByErpDateFormat());
            requestHeader.setLGORT(warehouseName);

            List<CheckRequestItem> requestItems = Lists.newArrayList();
            Integer itemId = 0;

            if (CollectionUtils.isEmpty(materialLots)){
                CheckRequestItem requestItem = new CheckRequestItem();
                requestItem.setMATNR(documentLine.getMaterialName());
                requestHeader.setLGORT(documentLine.getReserved28());
                requestItem.setERFMG(BigDecimal.ZERO);
                requestItem.setZEILI(itemId.toString());
                requestItems.add(requestItem);
            }else {
                for (MaterialLot materialLot : materialLots) {
                    if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                        if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                            CheckRequestItem requestItem = new CheckRequestItem();
                            itemId++;
                            requestItem.setZEILI(itemId.toString());
                            requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                            requestItems.add(requestItem);
                        }else {
                            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                                CheckRequestItem requestItem = new CheckRequestItem();
                                requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                                itemId++;
                                requestItem.setZEILI(itemId.toString());
                                requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                                requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
                                requestItem.setERFMG(materialLotUnit.getQty());
                                requestItems.add(requestItem);
                            }
                        }
                    }else if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                        CheckRequestItem requestItem = new CheckRequestItem();
                        itemId++;
                        requestItem.setZEILI(itemId.toString());
                        requestItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
                        requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                        requestItems.add(requestItem);
                    } else {
                        CheckRequestItem requestItem = new CheckRequestItem();
                        itemId++;
                        requestItem.setZEILI(itemId.toString());
                        requestItem = requestItem.copyMaterialLotToCheckRequestItem(requestItem, materialLot);
                        requestItems.add(requestItem);
                    }
                }
            }
            requestHeader.setItem(requestItems);
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
    @Async
    public void backhaulStockTransfer(List<MaterialLot> materialLots, List<MaterialLotAction> materialLotActions)throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            StockTransferRequestHeader requestHeader = new StockTransferRequestHeader();
            requestHeader.setBUDAT(this.nowByErpDateFormat());
            List<StockTransferRequestItem> items = Lists.newArrayList();
            Integer itemId = 0;
            for (MaterialLot materialLot : materialLots) {
                Optional<MaterialLotAction> OptionalMaterialLotAction = materialLotActions.stream().filter(action -> action.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                MaterialLotAction firstMaterialLotAction = OptionalMaterialLotAction.get();

                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    if ((!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())) || (!StringUtils.isNullOrEmpty(materialLot.getRmaFlag()))){
                        StockTransferRequestItem requestItem = new StockTransferRequestItem();
                        itemId++;
                        requestItem.setZEILE(itemId + "");
                        requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                        requestItem = requestItem.copyMaterialLotActionToStockTransferRequestItem(requestItem, firstMaterialLotAction);
                        requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                        requestItem.setZ_BATCH_BOXNO("");
                        items.add(requestItem);
                    }else {
                        // List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                        List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                        for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                            StockTransferRequestItem requestItem = new StockTransferRequestItem();
                            itemId++;
                            requestItem.setZEILE(itemId + "");
                            requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                            requestItem = requestItem.copyMaterialLotActionToStockTransferRequestItem(requestItem, firstMaterialLotAction);

                            requestItem.setZ_BATCH_REEL(materialLot.getMaterialLotId());
                            requestItem.setZ_BATCH_TBATCH(materialLotUnit.getUnitId());
                            items.add(requestItem);
                        }
                    }
                }else {
                    StockTransferRequestItem requestItem = new StockTransferRequestItem();
                    itemId++;
                    requestItem.setZEILE(itemId + "");
                    requestItem = requestItem.copyMaterialLotToStockTransferRequestItem(requestItem, materialLot);
                    requestItem = requestItem.copyMaterialLotActionToStockTransferRequestItem(requestItem, firstMaterialLotAction);

                    if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                        requestItem.setZ_BATCH_BOXNO(materialLot.getMaterialLotId());
                    }
                    items.add(requestItem);
                }
            }
            requestHeader.setITEM(items);
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
    public void backhaulScrap(String documentId, List<MaterialLot> materialLots)throws ClientException{
        try {
            ScrapRequest request = new ScrapRequest();
            ScrapRequestHeader requestHeader = new ScrapRequestHeader();
            requestHeader.setBUDAT(this.nowByErpDateFormat());
            requestHeader.setZSCRAP(documentId);
            request.setHeader(requestHeader);

            List<ScrapRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                    List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                    for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                        ScrapRequestItem requestItem = new ScrapRequestItem();
                        requestItem.setZITEM(materialLotUnit.getItemId());
                        requestItem.setMEINS(materialLot.getStoreUom());
                        requestItem.setZMENGE(materialLotUnit.getQty());
                        requestItems.add(requestItem);
                    }
                }else {
                    ScrapRequestItem requestItem = new ScrapRequestItem();
                    requestItem.setZITEM(materialLot.getItemId());
                    requestItem.setMEINS(materialLot.getStoreUom());
                    requestItem.setZMENGE(materialLot.getCurrentQty());
                    requestItems.add(requestItem);
                }
            }
            request.setItem(requestItems);

            sendErpRequest(request, BACKHAUL_SCRAP_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 交货单状态 回传
     *  @param documentId
     * @param documentLine
     * @param materialLots
     * @param deliveryStatus
     * @param shippingNo
     * @throws ClientException
     */
    public void backhaulDeliveryStatus(String documentId, DocumentLine documentLine, List<MaterialLot> materialLots, String deliveryStatus, String shippingNo)throws ClientException{
        try {
            DeliveryStatusRequest request = new DeliveryStatusRequest();

            List<DeliveryStatusRequestHeader> headers = Lists.newArrayList();
            DeliveryStatusRequestHeader header = new DeliveryStatusRequestHeader();
            header.setDelivery(documentId);
            header.setStatus(deliveryStatus);

            //发货回填
            header.setShipping_no(shippingNo);

            if (documentLine != null){
                if (DocumentServiceImpl.CREATE_BY_CUSTOMER_PRODUCT_RESERVED_RULE.equals(documentLine.getReserved24())){
                    header.setMode(PRODUCT_SHIP_MODE);
                }else if (DocumentServiceImpl.CREATE_BY_CUSTOMER_VERSION_RESERVED_RULE.equals(documentLine.getReserved24())){
                    header.setMode(VERSION_SHIP_MODE);
                }else {
                    header.setMode(REEL_SHIP_MODE);
                }
            }
            headers.add(header);

            List<DeliveryStatusRequestItem> items = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(materialLots)){
                for (MaterialLot materialLot : materialLots) {
                    if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                        if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                            DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                            item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                            item.setDelivery(documentId);
                            item.setTest_batch(materialLot.getMaterialLotId());
                        }else{
                            List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                            //List<MaterialLotUnit> materialLotUnits = materialLot.getMaterialLotUnits();
                            for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                                DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                                item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                                if (DeliveryStatus.POSTING_DELIVERY_STATUS.equals(deliveryStatus)){
                                    item = item.copyMaterialLotUnitToDeliveryStatusRequestItem(item, materialLotUnit);
                                }
                                item.setDelivery(documentId);
                                item.setReel(materialLot.getMaterialLotId());
                                item.setTest_batch(materialLotUnit.getUnitId());
                                items.add(item);
                            }
                        }
                    } else if (Material.MATERIAL_CATEGORY_MAIN_MATERIAL.equals(materialLot.getMaterialCategory())){
                        DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                        item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                        item.setDelivery(documentId);
                        item.setBox_no(materialLot.getMaterialLotId());
                        items.add(item);
                    } else {
                        DeliveryStatusRequestItem item = new DeliveryStatusRequestItem();
                        item = item.copyMaterialLotToDeliveryStatusRequestItem(item, materialLot);
                        item.setDelivery(documentId);
                        items.add(item);
                    }
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
            Date beginDate = DateUtils.minus(now, 50, ChronoUnit.DAYS);
            String beginDateStr = erpFormatter.format(beginDate);
            String endingDateStr = erpFormatter.format(now);

            List<Delivery> deliverys = Lists.newArrayList();
            Delivery delivery = new Delivery();
            delivery.setDelivery("8000000209");
            deliverys.add(delivery);

            List<DeliveryStatus> deliveryStatus = Lists.newArrayList();
//            DeliveryStatus deliveryStatu = new DeliveryStatus();
//            deliveryStatu.setStatu("DEL");
//            deliveryStatus.add(deliveryStatu);

            List<DeliveryType> deliveryTypes = Lists.newArrayList();
//            DeliveryType deliveryType = new DeliveryType();
//            deliveryType.setType("ZTLR");
//            deliveryTypes.add(deliveryType);

            deliveryInfo(beginDateStr, endingDateStr, deliverys, deliveryStatus, deliveryTypes);
        }catch (Exception e){
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     *  同步 采购到货通知单（待测品）/退货通知
     * 发货通知单，不良品发货通知单/RMA退货通知单
     * @param beginDateStr 开始日期 20210729
     * @param endingDateStr 结束日期
     * @param deliveryList 交货单号
     * @param statusList 交货状态
     * @param typeList 交货类型
     * @throws ClientException
     */
    public void deliveryInfo(String beginDateStr, String endingDateStr, List<Delivery> deliveryList, List<DeliveryStatus> statusList, List<DeliveryType> typeList) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);

            DeliveryInfoRequest request = new DeliveryInfoRequest();
            DeliveryInfoRequestHeader requestHeader = new DeliveryInfoRequestHeader();
            requestHeader.setBegin_date(beginDateStr);
            requestHeader.setEnd_date(endingDateStr);
            requestHeader.setDelivery(deliveryList);
            requestHeader.setStatu(statusList);
            requestHeader.setType(typeList);
            request.setHeader(requestHeader);

            String responseStr = sendErpRequest(request, DELIVERY_INFO_URL);
            DeliveryInfoResponse response = (DeliveryInfoResponse)DefaultParser.getObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).readValue(responseStr, DeliveryInfoResponse.class);
            List<DeliveryInfoResponseData> responseDataList = response.getData();
            ErpResponseReturn responseReturn = response.getReturn();
            if(!ErpResponseReturn.SUCCESS_STATUS.equals(responseReturn.getSTATUS())){
                return;
            }
            List<String> documentIds = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(responseDataList)){
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

                    //交货类型
                    String deliveryType = responseData.getType();

                    //发货类型
                    String shipMode = responseData.getShip_mode();
                    if (DeliveryType.DELIVERY_TYPE_SHIP.equals(deliveryType) || DeliveryType.DELIVERY_TYPE_REJ_SHIP.equals(deliveryType)){
                        //ZTLF-精测正向交货单、发货单 / ZTL2-精测不良品交货
                        if(responseData.getStatu().equals(DEL_DELIVERY_STATUS)){
                            //删除
                            DocumentLine documentLine = documentLineRepository.findByLineId(responseData.getDelivery());
                            if (documentLine != null){
                                documentLine.setStatus("Delete");
                                baseService.saveEntity(documentLine);
                                //this.backhaulDeliveryStatus(responseData.getDelivery(), null, null, DeliveryStatus.DEL_DELIVERY_STATUS, null);
                                continue;
                            }
                        }
                        DocumentLine erpDocumentLine = new DocumentLine();
                        erpDocumentLine = responseData.copyDeliveryInfoToDcoumentLine(responseData, erpDocumentLine);
                        erpDocumentLine.setShippingDate(shippingDate);
                        erpDocumentLine.setLineId(responseData.getDelivery());

                        List<MaterialLot> reelMLotList = Lists.newArrayList();
                        for (DeliveryInfoResponseItem responseItem : items) {
                            MaterialLot materialLot = new MaterialLot();

                            materialLot.setMaterialName(responseItem.getMaterial());
                            materialLot.setMaterialLotId(responseItem.getReel());
                            materialLot.setCurrentQty(responseItem.getQuantity());
                            materialLot.setUnitId(responseItem.getTest_batch());

                            reelMLotList.add(materialLot);

                            erpDocumentLine.setReserved34(responseItem.getMaterial_desc());
                        }

                        createDeliveryOrder(erpDocumentLine, reelMLotList, shipMode);
                    }else if (DeliveryType.DELIVERY_TYPE_RMA_INCOMING.equals(deliveryType)){
                        //ZTLR-精测RMA-自身原因
                        List<MaterialLot> erpMaterialLots = Lists.newArrayList();
                        for (DeliveryInfoResponseItem responseItem : items) {
                            MaterialLot materialLot = new MaterialLot();
                            materialLot = responseData.copyDeliveryInfoToMaterialLot(responseData, materialLot);
                            materialLot = responseItem.copyDeliveryInfoResponseItemToMaterialLot(responseItem, materialLot);
                            materialLot.setMaterialLotId(responseItem.getReel());
                            materialLot.setReserved61(responseData.getDelivery());
                            materialLot.setRmaFlag("ZTLR");

                            erpMaterialLots.add(materialLot);
                        }
                        RMAIncomingOrder rmaIncomingOrder = (RMAIncomingOrder)documentService.getDocumentByName(responseData.getDelivery(), false);
                        if (responseData.getStatu().equals(DEL_DELIVERY_STATUS)) {
                            //删除
                            if (rmaIncomingOrder.getHandledQty().compareTo(BigDecimal.ZERO) == 0) {
                                baseService.delete(rmaIncomingOrder);
                                this.backhaulDeliveryStatus(responseData.getDelivery(), null, null, DeliveryStatus.DEL_DELIVERY_STATUS, null);
                                continue;
                            }
                        }

                        RMAIncomingOrder erpRMAIncomingOrder = new RMAIncomingOrder();
                        erpRMAIncomingOrder.setQty(responseData.getTotal());
                        erpRMAIncomingOrder.setName(responseData.getDelivery());

                        createRAMIncomingMLotOrder(erpRMAIncomingOrder, erpMaterialLots, rmaIncomingOrder);
                    }
                    documentIds.add(responseData.getDelivery());
                }
                documentIds.forEach(docId -> {
                    this.backhaulDeliveryStatus(docId, null, null, DeliveryStatus.READ_DELIVERY_STATUS, null);
                });
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 发货单
     * @param erpDocumentLine
     * @param erpMaterialLots
     * @param shipMode
     * @throws ClientException
     */
    public void createDeliveryOrder(DocumentLine erpDocumentLine, List<MaterialLot> erpMaterialLots, String shipMode) throws ClientException{
        try {
            Boolean updateFlag = false;
            DocumentLine documentLine = documentLineRepository.findByLineId(erpDocumentLine.getLineId());

            if (REEL_SHIP_MODE.equals(shipMode)){
                erpDocumentLine.setReserved24("");
            }else if (PRODUCT_SHIP_MODE.equals(shipMode)){
                erpDocumentLine.setReserved24(DocumentServiceImpl.CREATE_BY_CUSTOMER_PRODUCT_RESERVED_RULE);
            }else if (VERSION_SHIP_MODE.equals(shipMode)){
                erpDocumentLine.setReserved24(DocumentServiceImpl.CREATE_BY_CUSTOMER_VERSION_RESERVED_RULE);
            }else{
                throw new ClientParameterException(DocumentException.SHIP_TYPE_IS_NOT_EXIST, shipMode);
            }

            if (MP_SHIPPING_TYPE.equals(erpDocumentLine.getReserved27())){
                erpDocumentLine.setReserved27(DocumentLine.MP_SHIPPING_TYPE);
            }else if (ES_SHIPPING_TYPE.equals(erpDocumentLine.getReserved27())){
                erpDocumentLine.setReserved27(DocumentLine.ES_SHIPPING_TYPE);
            }else if (CS_SHIPPING_TYPE.equals(erpDocumentLine.getReserved27())){
                erpDocumentLine.setReserved27(DocumentLine.CS_SHIPPING_TYPE);
            }

            if (documentLine == null){
                DeliveryOrder deliveryOrder = (DeliveryOrder)documentService.createDocument(new DeliveryOrder(), erpDocumentLine.getDocId(), DeliveryOrder.GENERATOR_DELIVERY_ORDER_ID_RULE, false, erpDocumentLine.getQty());
                documentLine = new DocumentLine();

                Map<String, Object> propsMap = PropertyUtils.convertObj2Map(erpDocumentLine);
                if (propsMap != null && propsMap.size() > 0) {
                    for (String propName : propsMap.keySet()) {
                        Object propValue = propsMap.get(propName);
                        if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                            continue;
                        }
                        PropertyUtils.setProperty(documentLine, propName, propsMap.get(propName));
                    }
                }
                erpDocumentLine.setDocument(deliveryOrder);
                erpDocumentLine.setReservedQty(BigDecimal.ZERO);
                erpDocumentLine.setUnReservedQty(documentLine.getQty());

                baseService.saveEntity(erpDocumentLine);

            }else {
                updateFlag = true;
                //如果单据已经备货，不能修改
                if (BigDecimal.ZERO.compareTo(documentLine.getReservedQty()) != 0){
                    throw new ClientParameterException(DocumentException.DOC_CAN_NOT_BE_MODIFIED, documentLine.getLineId());
                }
                Map<String, Object> propsMap = PropertyUtils.convertObj2Map(erpDocumentLine);
                if (propsMap != null && propsMap.size() > 0) {
                    for (String propName : propsMap.keySet()) {
                        Object propValue = propsMap.get(propName);
                        if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                            continue;
                        }
                        PropertyUtils.setProperty(documentLine, propName, propsMap.get(propName));
                    }
                }
                documentLine.setReservedQty(BigDecimal.ZERO);
                documentLine.setUnReservedQty(documentLine.getQty());
                documentLine.setStatus("Create");
                baseService.saveEntity(documentLine);

                Document deliveryOrder = documentService.getDocumentByName(documentLine.getDocId(), false);
                deliveryOrder.setQty(documentLine.getQty());
                deliveryOrder.setUnHandledQty(documentLine.getQty());
                baseService.saveEntity(deliveryOrder);
            }
            List<MaterialLot> materialLots = materialLotRepository.findByMaterialCategoryAndStatus(Material.TYPE_PRODUCT, MaterialStatus.STATUS_IN);

            if (updateFlag){
                Map<String, List<MaterialLot>> materialLotIdMap = erpMaterialLots.stream().collect(Collectors.groupingBy(materialLot -> materialLot.getMaterialLotId()));
                for (String materialLotId : materialLotIdMap.keySet()) {

                    Optional<MaterialLot> materialLotFirst = materialLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLotId)).findFirst();
                    if (materialLotFirst.isPresent()){
                        MaterialLot materialLot = materialLotFirst.get();
                        if (StringUtils.isNullOrEmpty(materialLot.getReserved45()) && !documentLine.getLineId().equals(materialLot.getReserved45())){
                            materialLot.setReserved45(documentLine.getLineId());
                            baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PRE_RESERVED);
                        }
                    }else {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
                    }
                }

                //对原先绑定了的reel,进行校验,此次可能取消了绑定
                List<MaterialLot> materialLotList = materialLots.stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getReserved45()) && erpDocumentLine.getLineId().equals(mLot.getReserved45())).collect(Collectors.toList());
                List<String> erpMaterialLotId = erpMaterialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
                List<MaterialLot> updateMLots = materialLotList.stream().filter(mLot -> !erpMaterialLotId.contains(mLot.getMaterialLotId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(updateMLots)){
                    updateMLots.forEach(materialLot -> {
                        materialLot.setReserved45("");
                        baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_UN_PRE_RESERVED);
                    });
                }
            }else {
                Map<String, List<MaterialLot>> materialLotIdMap = erpMaterialLots.stream().filter(mLot-> !StringUtils.isNullOrEmpty(mLot.getMaterialLotId())).collect(Collectors.groupingBy(materialLot -> materialLot.getMaterialLotId()));
                for (String materialLotId : materialLotIdMap.keySet()) {

                    Optional<MaterialLot> materialLotFirst = materialLots.stream().filter(mLot -> mLot.getMaterialLotId().equals(materialLotId)).findFirst();
                    if (materialLotFirst.isPresent()){
                        MaterialLot materialLot = materialLotFirst.get();
                        materialLot.setReserved45(documentLine.getLineId());
                        baseService.saveEntity(materialLot, MaterialLotHistory.TRANS_TYPE_PRE_RESERVED);
                    }else {
                        throw new ClientParameterException(MmsException.MM_MATERIAL_LOT_IS_NOT_EXIST, materialLotId);
                    }
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * RAM品来料单-自身原因
     * @param
     * @throws ClientException
     */
    public void createRAMIncomingMLotOrder(RMAIncomingOrder erpRMAIncomingOrder, List<MaterialLot> erpMaterialLotList, RMAIncomingOrder rmaIncomingOrder) throws ClientException{
        try {
            if (rmaIncomingOrder == null) {
                rmaIncomingOrder = (RMAIncomingOrder)documentService.createDocument(erpRMAIncomingOrder, erpRMAIncomingOrder.getName(), RMAIncomingOrder.GENERATOR_RMA_INCOMING_ORDER_RULE, true, erpRMAIncomingOrder.getQty());
            } else {
                if (!rmaIncomingOrder.getQty().equals(erpRMAIncomingOrder.getQty())){
                    BigDecimal differQty = erpRMAIncomingOrder.getQty().subtract(rmaIncomingOrder.getQty());
                    rmaIncomingOrder.setQty(erpRMAIncomingOrder.getQty());
                    rmaIncomingOrder.setUnHandledQty(rmaIncomingOrder.getUnHandledQty().add(differQty));
                    baseService.saveEntity(rmaIncomingOrder);
                }else {
                    throw new ClientParameterException(DocumentException.DOC_CAN_NOT_BE_MODIFIED, rmaIncomingOrder.getName());
                }
            }
            List<MaterialLot> materialLots = mmsService.getMLotByIncomingDocId(rmaIncomingOrder.getName());

            asyncIncomingMLot(rmaIncomingOrder, erpMaterialLotList, materialLots);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步退料信息
     * @param erpReturnMLotOrder erp单据信息 name,qty
     * @param erpMaterialLotList erp批次信息
     * @param returnMLotOrder wms单据信息 ReturnMLotOrder
     * @throws ClientException
     */
    public void asyncReturnInfo(ReturnMLotOrder erpReturnMLotOrder, List<MaterialLot> erpMaterialLotList, ReturnMLotOrder returnMLotOrder) throws ClientException{
        try {
            if (returnMLotOrder == null) {
                returnMLotOrder = (ReturnMLotOrder)documentService.createDocument(erpReturnMLotOrder, erpReturnMLotOrder.getName(), ReturnMLotOrder.GENERATOR_RETURN_MLOT_ORDER_RULE, true, erpReturnMLotOrder.getQty());
            } else {
                if(returnMLotOrder.getHandledQty().compareTo(BigDecimal.ZERO) != 0){
                    //如果单据已进行操作，不能修改
                    return;
                }
                if (!returnMLotOrder.getQty().equals(erpReturnMLotOrder.getQty())){
                    BigDecimal differQty = erpReturnMLotOrder.getQty().subtract(returnMLotOrder.getQty());
                    returnMLotOrder.setQty(erpReturnMLotOrder.getQty());
                    returnMLotOrder.setUnHandledQty(returnMLotOrder.getUnHandledQty().add(differQty));
                    baseService.saveEntity(returnMLotOrder);
                }
            }

            List<DocumentLine> documentLineList = documentLineRepository.findByDocId(returnMLotOrder.getName());
            List<MaterialLot> materialLotList = erpMaterialLotList.stream().filter(mLot -> StringUtils.isNullOrEmpty(mLot.getMaterialLotId()) && StringUtils.isNullOrEmpty(mLot.getUnitId())).collect(Collectors.toList());
            for (MaterialLot materialLot : materialLotList) {
                DocumentLine documentLine = new DocumentLine();
                Optional<DocumentLine> documentLinefirst = documentLineList.stream().filter(docLine -> docLine.getReserved30().equals(materialLot.getItemId()) && !StringUtils.isNullOrEmpty(docLine.getReserved24())).findFirst();
                if (!documentLinefirst.isPresent()){
                    String docLineId = documentService.generatorDocId("CreateDocLineIdByDocIdRule", returnMLotOrder);
                    documentLine.setLineId(docLineId);
                }else {
                    documentLine = documentLinefirst.get();
                }
                Material material = mmsService.getMaterialByName(materialLot.getMaterialName(), true);
                documentLine.setMaterial(material);
                documentLine.setDocument(returnMLotOrder);
                documentLine.setUnHandledQty(materialLot.getCurrentQty());
                documentLine.setUnReservedQty(materialLot.getCurrentQty());
                documentLine.setQty(materialLot.getCurrentQty());
                documentLine.setReserved24(ReturnMLotOrder.DEFAULT_RETURN_MLOT_RESERVED_RULE);
                documentLine.setReserved28(materialLot.getLastWarehouseId());
                documentLine.setReserved30(materialLot.getItemId());
                documentLine.setReserved32(returnMLotOrder.getReserved3());
                baseService.saveEntity(documentLine);
            }

            List<DocumentMLot> documentMLots = Lists.newArrayList();
            List<MaterialLot> erpMLots = erpMaterialLotList.stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getMaterialLotId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(erpMLots)){
                BigDecimal docLineQty = erpMLots.stream().collect(CollectorsUtils.summingBigDecimal(MaterialLot::getCurrentQty));

                DocumentLine documentLine = new DocumentLine();
                Optional<DocumentLine> documentLineFirst = documentLineList.stream().filter(docLine -> StringUtils.isNullOrEmpty(docLine.getReserved24())).findFirst();
                if (!documentLineFirst.isPresent()){
                    String docLineId = documentService.generatorDocId("CreateDocLineIdByDocIdRule", returnMLotOrder);
                    documentLine.setLineId(docLineId);
                }else {
                    documentLine = documentLineFirst.get();
                    documentMLots = documentMLotRepository.findByDocumentId(documentLine.getDocId());
                }
                documentLine.setDocument(returnMLotOrder);
                documentLine.setUnHandledQty(docLineQty);
                documentLine.setQty(docLineQty);
                documentLine.setUnReservedQty(docLineQty);
                documentLine.setReserved24(StringUtils.EMPTY);
                documentLine.setReserved32(returnMLotOrder.getReserved3());
                baseService.saveEntity(documentLine);

                for (MaterialLot materialLot :erpMLots) {
                    //Optional<MaterialLot> mLot = materialLotList.stream().filter(mLot -> mLot.getMaterialLotId().).findFirst();
                    if (CollectionUtils.isNotEmpty(documentMLots)){
                        Optional<DocumentMLot> documentMLotFirst = documentMLots.stream().filter(docMLot -> docMLot.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                        if (documentMLotFirst.isPresent()){
                            documentMLotRepository.delete(documentMLotFirst.get());
                        }
                    }
                    DocumentMLot documentMLot = new DocumentMLot();
                    documentMLot.setDocumentId(returnMLotOrder.getName());
                    documentMLot.setMaterialLotId(materialLot.getMaterialLotId());
                    documentMLot.setStatus(DocumentMLot.STATUS_CREATE);
                    documentMLot.setItemId(materialLot.getItemId());
                    documentMLot = documentMLotRepository.save(documentMLot);
                    //documentService.validationMLotBoundOrder();
                }
            }
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 同步来料信息
     * @param erpIncomingOrder erp单据信息 name,qty
     * @param erpMaterialLotList erp批次信息
     * @param incomingOrder wms单据信息 IncomingOrder
     * @param materialLotList 来料单下的批次信息
     * @throws ClientException
     */
    public void asyncIncomingInfo(IncomingOrder erpIncomingOrder, List<MaterialLot> erpMaterialLotList, IncomingOrder incomingOrder,List<MaterialLot> materialLotList) throws ClientException{
        try {
            //来料单据
            if (incomingOrder == null) {
                incomingOrder = (IncomingOrder)documentService.createDocument(erpIncomingOrder, erpIncomingOrder.getName(), IncomingOrder.GENERATOR_INCOMING_ORDER_ID_RULE, true, erpIncomingOrder.getQty());
            } else {
                if (incomingOrder.getHandledQty().compareTo(BigDecimal.ZERO) != 0 ){
                    return;
                }
                if(erpIncomingOrder.getQty().compareTo(incomingOrder.getQty()) != 0){
                    BigDecimal differQty = erpIncomingOrder.getQty().subtract(incomingOrder.getQty());
                    incomingOrder.setQty(erpIncomingOrder.getQty());
                    incomingOrder.setUnHandledQty(incomingOrder.getUnHandledQty().add(differQty));
                    baseService.saveEntity(incomingOrder);
                }
            }

            asyncIncomingMLot(incomingOrder, erpMaterialLotList, materialLotList);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void asyncIncomingMLot(Document document, List<MaterialLot> erpMaterialLotList, List<MaterialLot> materialLotList) throws ClientException{
        try {
            Map<String, List<MaterialLot>> materialNameMap = erpMaterialLotList.stream().collect(Collectors.groupingBy(MaterialLot::getMaterialName));
            for (String materialName:materialNameMap.keySet()) {
                Material material = mmsService.getMaterialByName(materialName, true);
                MaterialStatusModel materialStatusModel = mmsService.getStatusModelByRrn(material.getStatusModelRrn());

                List<MaterialLot> erpMaterialLots = materialNameMap.get(materialName);
                for (MaterialLot erpMaterialLot : erpMaterialLots) {
                    if (StringUtils.isNullOrEmpty(erpMaterialLot.getReserved2())){
                        erpMaterialLot.setReserved2(material.getReserved5());
                    }
                    if (StringUtils.isNullOrEmpty(erpMaterialLot.getReserved10())){
                        erpMaterialLot.setReserved10(material.getReserved11());
                    }
                    erpMaterialLot.setIncomingDocRrn(document.getObjectRrn());
                    erpMaterialLot.setIncomingDocId(document.getName());
                    erpMaterialLot.setMaterial(material);
                    Map<String, Object> propsMap = PropertyUtils.convertObj2Map(erpMaterialLot);

                    //itemId + incomingDocId 可以确认一条数据,如果多条表示已进行分批。
                    List<MaterialLot> mLots = materialLotList.stream().filter(mLot -> !StringUtils.isNullOrEmpty(mLot.getItemId()) && mLot.getItemId().equals(erpMaterialLot.getItemId())).collect(Collectors.toList());

                    //批次信息不存在
                    if (mLots.size() == 0) {
                        MaterialLot mLot = mmsService.createMLot(material, materialStatusModel, erpMaterialLot.getMaterialLotId(),
                                erpMaterialLot.getCurrentQty(), erpMaterialLot.getCurrentSubQty(), propsMap);
                    } else if (mLots.size() == 1) {
                        MaterialLot ordMLot = mLots.get(0);
                        //非创建状态不能修改
                        if (propsMap != null && propsMap.size() > 0) {
                            for (String propName : propsMap.keySet()) {
                                Object propValue = propsMap.get(propName);
                                if (propValue == null || StringUtils.isNullOrEmpty(propValue.toString())) {
                                    continue;
                                }
                                PropertyUtils.setProperty(ordMLot, propName, propsMap.get(propName));
                            }
                        }
                        ordMLot = materialLotRepository.saveAndFlush(ordMLot);
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
     * 成品入库回传接口
     * @param materialLotList
     * @throws ClientException
     */
    public void backhaulStockIn(List<MaterialLot> materialLotList) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            String posteDate = this.nowByErpDateFormat();

            MaterialLot mLot = materialLotList.get(0);

            StockInRequest request = new StockInRequest();
            StockInRequestHeader requestHeader = new StockInRequestHeader();
            requestHeader.setMATNR(mLot.getMaterialName());
            requestHeader.setLGORT(mLot.getLastWarehouseId());

            List<StockInRequestItem> requestItems = Lists.newArrayList();
            for (MaterialLot materialLot : materialLotList) {

                String packingDate = StringUtils.EMPTY;
                String expireDate = StringUtils.EMPTY;
                if (materialLot.getProductionDate() != null){
                    packingDate = formatter.format(materialLot.getProductionDate());
                }
                if (materialLot.getExpireDate() != null){
                    expireDate = formatter.format(materialLot.getExpireDate());
                }
                if (Material.MATERIAL_CATEGORY_PRODUCT.equals(materialLot.getMaterialCategory())){
                    List<MaterialLotUnit> materialLotUnits = materialLotUnitRepository.findByMaterialLotId(materialLot.getMaterialLotId());
                    //次品
                    if (!StringUtils.isNullOrEmpty(materialLot.getInferiorProductsFlag()) && StringUtils.YES.equals(materialLot.getInferiorProductsFlag())){
                        MaterialLotUnit materialLotUnit = materialLotUnits.get(0);
                        StockInRequestItem requestItem = new StockInRequestItem();
                        requestItem = requestItem.copyProductMLotToStockInRequestItem(materialLot, requestItem);
                        requestItem = requestItem.copyMLotUnitToStockInRequestItem(materialLotUnit, requestItem);

                        if (!StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId()) && materialLotUnit.getWorkOrderId().contains(StringUtils.SPLIT_CODE)){
                            requestItem.setZ_BATCH_INTERORDOR(materialLotUnit.getWorkOrderId().substring(materialLotUnit.getWorkOrderId().indexOf(StringUtils.SPLIT_CODE)));
                        }
                        requestItem.setMENGE(materialLot.getCurrentQty());
                        requestItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
                        requestItem.setZ_BATCH_PID(materialLot.getReserved47());
                        requestItem.setZ_BATCH_POSTEDATE(posteDate);
                        requestItem.setZ_BATCH_PDATE(packingDate);
                        requestItem.setZ_BATCH_OVERDATE(expireDate);
                        requestItems.add(requestItem);
                    }else {
                        //好品
                        for (MaterialLotUnit materialLotUnit : materialLotUnits) {
                            StockInRequestItem requestItem = new StockInRequestItem();
                            requestItem = requestItem.copyProductMLotToStockInRequestItem(materialLot, requestItem);
                            requestItem = requestItem.copyMLotUnitToStockInRequestItem(materialLotUnit, requestItem);

                            if (!StringUtils.isNullOrEmpty(materialLotUnit.getWorkOrderId()) && materialLotUnit.getWorkOrderId().contains(StringUtils.SPLIT_CODE)){
                                requestItem.setZ_BATCH_INTERORDOR(materialLotUnit.getWorkOrderId().substring(materialLotUnit.getWorkOrderId().indexOf(StringUtils.SPLIT_CODE)));
                            }
                            requestItem.setZ_BATCH_OVERDATE(expireDate);
                            requestItem.setZ_BATCH_BINTYPE(materialLot.getGrade());
                            requestItem.setZ_BATCH_POSTEDATE(posteDate);
                            requestItem.setZ_BATCH_PDATE(packingDate);
                            requestItem.setZ_BATCH_PID(materialLot.getReserved47());
                            requestItems.add(requestItem);
                        }
                    }
                }
            }
            requestHeader.setITEM(requestItems);
            request.setHEADER(requestHeader);

            sendErpRequest(request, BACKHAUL_STOCK_IN_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 主材来料入库/RMA非自身原因入库回传接口
     * @param materialLots
     * @throws ClientException
     */
    @Async
    public void backhaulMainMaterialStockIn(List<MaterialLot> materialLots) throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            List<String> materialLotIds = materialLots.stream().map(mLot -> mLot.getMaterialLotId()).collect(Collectors.toList());
            List<MLotCheckSheet> mLotCheckSheetList = mLotCheckSheetRepository.findByMaterialLotIdIn(materialLotIds);
            String posteDate = this.nowByErpDateFormat();

            IncomingStockInRequest request = new IncomingStockInRequest();
            IncomingStockInRequestHeader requestHeader = new IncomingStockInRequestHeader();
            requestHeader.setBUDAT(posteDate);
            requestHeader.setBWART(BWART_STOCK_IN_MLOT);

            List<IncomingStockInRequestItem> items = Lists.newArrayList();

            for (MaterialLot materialLot : materialLots) {
                IncomingStockInRequestItem requestItem = new IncomingStockInRequestItem();
                requestItem = requestItem.copyMaterialLotToRequestItem(materialLot, requestItem);

                IncomingStockInRequestTxItem requestTxItem = new IncomingStockInRequestTxItem();
                requestTxItem = requestTxItem.copyMaterialLotToRequestTxItem(materialLot, requestTxItem);

                if (materialLot.getProductionDate() != null){
                    requestTxItem.setZ_BATCH_MADATE(formatter.format(materialLot.getProductionDate()));
                }
                if (materialLot.getExpireDate() != null){
                    requestTxItem.setZ_BATCH_OVERDATE(formatter.format(materialLot.getExpireDate()));
                }
                if (materialLot.getReceiveDate() != null){
                    requestTxItem.setZ_BATCH_RC(formatter.format(materialLot.getReceiveDate()));
                }
                requestTxItem.setZ_BATCH_POSTEDATE(posteDate);

                if (CollectionUtils.isNotEmpty(mLotCheckSheetList)){
                    Optional<MLotCheckSheet> mLotCheckSheetFirst = mLotCheckSheetList.stream().filter(mLotCheckSheet -> mLotCheckSheet.getMaterialLotId().equals(materialLot.getMaterialLotId())).findFirst();
                    if (mLotCheckSheetFirst.isPresent()) {
                        MLotCheckSheet mLotCheckSheet = mLotCheckSheetFirst.get();
                        requestTxItem.setZ_BATCH_IQC(mLotCheckSheet.getCheckResult());
                    }
                }
                requestItem.setTXItem(requestTxItem);
                items.add(requestItem);
            }
            requestHeader.setItem(items);
            request.setHeader(requestHeader);
            sendErpRequest(request, BACKHAUL_MAIN_MATERIAL_STOCK_IN_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 主材退料/退供应商
     * @param materialLots
     * @throws ClientException
     */
    @Async
    public void backhaulReturnMainMaterial(String docId, List<MaterialLot> materialLots) throws ClientException{
        try {
            IncomingStockInRequest request = new IncomingStockInRequest();
            IncomingStockInRequestHeader requestHeader = new IncomingStockInRequestHeader();
            requestHeader.setBUDAT(this.nowByErpDateFormat());
            requestHeader.setBWART(BWART_RETURN_MLOT);

            List<IncomingStockInRequestItem> items = Lists.newArrayList();
            for (MaterialLot materialLot : materialLots) {
                IncomingStockInRequestItem requestItem = new IncomingStockInRequestItem();
                requestItem = requestItem.copyMaterialLotToRequestItem(materialLot, requestItem);

                IncomingStockInRequestTxItem requestTxItem = new IncomingStockInRequestTxItem();
                requestTxItem.copyMaterialLotToRequestTxItem(materialLot, requestTxItem);
                requestTxItem.setZ_BATCH_GFNO(docId);

                requestItem.setTXItem(requestTxItem);
                items.add(requestItem);
            }
            requestHeader.setItem(items);
            request.setHeader(requestHeader);

            sendErpRequest(request, BACKHAUL_MAIN_MATERIAL_STOCK_IN_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 分批
     * @param materialLot
     * @throws ClientException
     */
    @Async
    public void splitMLot(MaterialLot materialLot) throws ClientException{
        try {
            ErpRequest request = new ErpRequest();
            SplitRequestHeader requestHeader = new SplitRequestHeader();
            requestHeader.copyMaterialLotToSplitRequestHeader(materialLot, requestHeader);
            requestHeader.setBUDAT(this.nowByErpDateFormat());

            request.setHeader(requestHeader);
            sendErpRequest(request, SPLIT_MLOT_URL, ErpResponse.class);
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 重发
     * @param interfaceFailList
     * @throws ClientException
     */
    public void retry(List<InterfaceFail> interfaceFailList) throws ClientException{
        try {
            for (InterfaceFail interfaceFail : interfaceFailList) {
                String responseStr = sendErpRequest(interfaceFail.getRequestTxt(), interfaceFail.getDestination(), InterfaceHistory.TRANS_TYPE_RETRY);
                if(responseStr != null){
                    ErpResponse response = (ErpResponse)DefaultParser.getObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).readValue(responseStr, ErpResponse.class);
                    if (response.getReturn() != null && ErpResponseReturn.SUCCESS_STATUS.equals(response.getReturn().getSTATUS())){
                        interfaceFailRepository.delete(interfaceFail);
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
                log.debug(String.format("Send data erp. RequestString is [%s]", requestString));
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
                if (!StringUtils.isNullOrEmpty(responseString)) {
                    ErpResponse response = (ErpResponse)DefaultParser.getObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).readValue(responseString, responseClass);
                    if (response != null && response.getReturn() != null){
                        if (!ErpResponseReturn.SUCCESS_STATUS.equals(response.getReturn().getSTATUS())){
                            String responseMessage = response.getReturn().getMESSAGE();
                            //返回信息存在中文
                            if (!StringUtils.isNullOrEmpty(responseMessage) && responseMessage.length() > InterfaceHistory.ACTION_CODE_MAX_LENGTH / 2) {
                                responseMessage = responseMessage.substring(0, InterfaceHistory.ACTION_CODE_MAX_LENGTH/2);
                            }
                            throw new ClientException(responseMessage);
                        }
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

    public String nowByErpDateFormat() throws ClientException{
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(ERP_DEFAULT_DATE_FORMAT);
            return formatter.format(DateUtils.now());
        }catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
