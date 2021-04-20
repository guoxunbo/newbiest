package com.newbiest.gc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kyexpress.openapi.sdk.KyeClient;
import com.kyexpress.openapi.sdk.client.DefaultKyeClient;
import com.kyexpress.openapi.sdk.client.KyeAccessTokenClient;
import com.kyexpress.openapi.sdk.internal.util.KyeConstants;
import com.kyexpress.openapi.sdk.model.KyeAccessToken;
import com.kyexpress.openapi.sdk.model.KyeAppInfo;
import com.kyexpress.openapi.sdk.request.DefaultRequest;
import com.kyexpress.openapi.sdk.response.AccessTokenKyeResponse;
import com.kyexpress.openapi.sdk.response.DefaultKyeResponse;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.ExpressConfiguration;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.express.dto.OrderInfo;
import com.newbiest.gc.express.dto.WaybillDelivery;
import com.newbiest.gc.service.ExpressService;
import com.newbiest.mms.model.DeliveryOrder;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import com.newbiest.mms.model.MaterialLotHistory;
import com.newbiest.mms.repository.DeliveryOrderRepository;
import com.newbiest.mms.repository.DocumentLineRepository;
import com.newbiest.mms.repository.MaterialLotHistoryRepository;
import com.newbiest.mms.repository.MaterialLotRepository;
import com.newbiest.msg.DefaultParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author guoxunbo
 * @date 2020-07-08 13:52
 */
@Service
@Slf4j
@Data
public class ExpressServiceImpl implements ExpressService {

    public static final String ZJ_SHIPPING_ADDRESS = "ZJShippingAddress";
    public static final String SH_SHIPPING_ADDRESS= "SHShippingAddress";

    public static final String EXPRESS_ORDER_TIME= "ExpressOrderTime";

    /**
     * 浙江账套
     */
    public static final String ZJ_BOOK = "601";

    @Autowired
    ExpressConfiguration expressConfiguration;

    @Autowired
    UIService uiService;

    @Autowired
    MaterialLotRepository materialLotRepository;

    @Autowired
    MaterialLotHistoryRepository materialLotHistoryRepository;

    @Autowired
    BaseService baseService;

    @Autowired
    DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    DocumentLineRepository documentLineRepository;

    @Value("${spring.profiles.active}")
    private String profiles;

    private boolean isProdEnv() {
        return "production".equalsIgnoreCase(profiles);
    }

    /**
     * 获取跨域请求的的token
     * @throws ClientException
     */
    private String getToken() throws ClientException {
        try {
            KyeAppInfo appInfo = new KyeAppInfo();
            appInfo.setAppkey(expressConfiguration.getAppKey());
            appInfo.setAppsecret(expressConfiguration.getAppSecret());

            AccessTokenKyeResponse response = KyeAccessTokenClient.accessToken(isProdEnv() ? KyeConstants.TOKEN_SERVER_URL : KyeConstants.SANDBOX_TOKEN_SERVER_URL, appInfo);

            KyeAccessToken accessToken = null;
            if(response != null && response.isSuccess()) {
                accessToken = response.getAccessToken();
            }

            if(accessToken == null || StringUtils.isNullOrEmpty(accessToken.getToken())) {
                throw new ClientException(GcExceptions.GET_EXPRESS_TOKEN_ERROR);
            }

            if (log.isDebugEnabled()) {
                log.debug("Get Token [" + accessToken.getToken() + "]");
            }
            return accessToken.getToken();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    private String sendRequest(String methodCode, Object parameter) throws ClientException {
        try {
            if (log.isInfoEnabled()) {
                log.info("Start to send [" + methodCode + "] to express.");
            }
            String token = getToken();
            KyeClient kyeClient = new DefaultKyeClient(isProdEnv() ? KyeConstants.SERVER_URL : KyeConstants.SANDBOX_SERVER_URL, expressConfiguration.getAppKey(), expressConfiguration.getAppSecret(), token);
            DefaultRequest request = new DefaultRequest(methodCode, parameter, KyeConstants.REQUEST_DATA_FORMAT_JSON, KyeConstants.RESPONSE_DATA_FORMAT_JSON);
            DefaultKyeResponse response = kyeClient.execute(request);
            if (response == null) {
                throw new ClientException(GcExceptions.EXPRESS_NETWORK_ERROR);
            }
            if (log.isInfoEnabled()) {
                log.info("end to send [" + methodCode + "] to express.");
                log.info("response code is [" + response.getCode() + "]");
                log.info("response msg is [" + response.getMsg() + "]");
                log.info("response data is [" + response.getData() + "]");
            }

            if (!response.isSuccess()) {
                throw new ClientException(response.getMsg());
            }
            return response.getData();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 绑定快递单号
     * @param materialLots
     * @param expressNumber
     */
    public List<MaterialLot> recordExpressNumber(List<MaterialLot> materialLots, String expressNumber, String expressCompany, String planOrderType) throws ClientException{
        try {
            validateMLotAddressAndShipper(materialLots);
            for (MaterialLot materialLot : materialLots) {
                materialLot.setExpressNumber(expressNumber);
                materialLot.setExpressCompany(expressCompany);
                materialLot.setPlanOrderType(planOrderType);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RECORD_EXPRESS);
                materialLotHistoryRepository.save(history);
            }
            return materialLots;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 给跨域速递下单
     * @param materialLots 物料批次
     * @param serviceMode 服务模式 次日达等等
     * @param payMode 支付方式
     */
    public List<Map<String, String>> planOrder(List<MaterialLot> materialLots, int serviceMode, int payMode, String orderTime) throws ClientException {
        try {
            List<Map<String, String>> parameterMapList = Lists.newArrayList();
            Optional optional = materialLots.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getReserved51())).findFirst();
            if (optional.isPresent()) {
                throw new ClientException(GcExceptions.PICKUP_ADDRESS_IS_NULL);
            }

            String books = validateMLotAddressAndShipper(materialLots);

            Map<String, Object> requestParameters = Maps.newHashMap();
            requestParameters.put("platformFlag", expressConfiguration.getPlatformFlag());

            if (ZJ_BOOK.equals(books)) {
                requestParameters.put("customerCode", expressConfiguration.getZjCustomerCode());
            } else {
                requestParameters.put("customerCode", expressConfiguration.getCustomerCode());
            }
            List<OrderInfo> orderInfos = Lists.newArrayList();
            OrderInfo orderInfo = new OrderInfo();
            // 寄件人信息
            orderInfo.setPreWaybillDelivery(buildPreWaybillDelivery());
            // 收货人信息
            WaybillDelivery preWaybillPickup = new WaybillDelivery();
            preWaybillPickup.setPerson(materialLots.get(0).getReserved52());
            preWaybillPickup.setMobile(materialLots.get(0).getReserved53());
            preWaybillPickup.setAddress(materialLots.get(0).getReserved51());
            DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(materialLots.get(0).getReserved16()));
            preWaybillPickup.setCompanyName(documentLine.getReserved8());

            orderInfo.setPreWaybillPickup(preWaybillPickup);

            orderInfo.setServiceMode(serviceMode);
            orderInfo.setPayMode(payMode);

            //下单时间为空时默认当天19：30
            if(!StringUtils.isNullOrEmpty(orderTime)){
                orderInfo.setGoodsTime(orderTime);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = formatter.format(new Date());
            orderInfo.setOrderTime(date);

            orderInfo.setOrderId(ExpressConfiguration.PLAN_ORDER_DEFAULT_ORDER_ID);
            orderInfo.setPaymentCustomer(expressConfiguration.getCustomerCode());
            if (ZJ_BOOK.equals(books)) {
                orderInfo.setPaymentCustomer(expressConfiguration.getZjCustomerCode());
            }
            if (OrderInfo.RECEIVE_PAY_MODE.equals(payMode)) {
                orderInfo.setPaymentCustomer(StringUtils.EMPTY);
            }
            orderInfos.add(orderInfo);

            requestParameters.put("orderInfos", orderInfos);

            String responseData = sendRequest(ExpressConfiguration.PLAN_ORDER_METHOD, requestParameters);
            List responseMap =  DefaultParser.getObjectMapper().readValue(responseData, List.class);

            String waybillNumber = (String) ((Map)responseMap.get(0)).get("waybillNumber");

            if (log.isDebugEnabled()) {
                List<String> materialLotIds = materialLots.stream().map(MaterialLot :: getMaterialLotId).collect(Collectors.toList());
                log.debug(String.format("MaterialLotIds [%s] records express number [%s]", materialLotIds, waybillNumber));
            }

            for (MaterialLot materialLot : materialLots) {
                materialLot.setExpressNumber(waybillNumber);
                materialLot.setPlanOrderType(MaterialLot.PLAN_ORDER_TYPE_AUTO);
                materialLot = materialLotRepository.saveAndFlush(materialLot);

                MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_RECORD_EXPRESS);
                materialLotHistoryRepository.save(history);
            }

            parameterMapList = getPrintLabelParameterList(materialLots, waybillNumber);

            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 手工下单或者跨域下单验证客户和地址以及账套必须一致
     * @param materialLots
     * @retun 账套
     * @throws ClientException
     */
    private String validateMLotAddressAndShipper(List<MaterialLot> materialLots) throws ClientException{
        try {
            String books = StringUtils.EMPTY;
            Set<String> pickUpAddresses = materialLots.stream().map(MaterialLot :: getReserved51).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(pickUpAddresses)  && pickUpAddresses.size() != 1) {
                throw new ClientException(GcExceptions.PICKUP_ADDRESS_MORE_THEN_ONE);
            }

            Set<String> shipper = materialLots.stream().map(MaterialLot :: getShipper).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(shipper)  && shipper.size() != 1) {
                throw new ClientException(GcExceptions.SHIPPER_IS_NOT_SAME);
            }
            Set<String> documentLineRrnSet = materialLots.stream().map(MaterialLot :: getReserved16).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(documentLineRrnSet)) {
                for (String documentLineRrn : documentLineRrnSet) {
                    DocumentLine documentLine = (DocumentLine) documentLineRepository.findByObjectRrn(Long.parseLong(documentLineRrn));
                    if (StringUtils.isNullOrEmpty(books)) {
                        books = documentLine.getReserved30();
                    } else {
                        if (!books.equals(documentLine.getReserved30())) {
                            throw new ClientException(GcExceptions.BOOKS_IS_NOT_SAME);
                        }
                    }

                }
            }
            return books;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void cancelOrderByMaterialLots(List<MaterialLot> materialLots) throws ClientException {
        try {
            List<String> expressNumbers = materialLots.stream().filter(materialLot -> !StringUtils.isNullOrEmpty(materialLot.getExpressNumber()))
                    .map(MaterialLot :: getExpressNumber).collect(Collectors.toList());
            for (String expressNumber : expressNumbers) {
                cancelOrder(expressNumber);
            }
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void cancelOrder(String expressNumber) throws ClientException {
        try {
            List<MaterialLot> materialLots = materialLotRepository.getByExpressNumber(expressNumber);
            if (CollectionUtils.isNotEmpty(materialLots)) {
                String planOrderType = materialLots.get(0).getPlanOrderType();
                if (MaterialLot.PLAN_ORDER_TYPE_AUTO.equals(planOrderType)) {
                    Map<String, Object> requestParameters = Maps.newHashMap();
                    requestParameters.put("customerCode", expressConfiguration.getCustomerCode());
                    requestParameters.put("waybillNumber", expressNumber);
                    sendRequest(ExpressConfiguration.CANCEL_ORDER_METHOD, requestParameters);
                }
                for (MaterialLot materialLot : materialLots) {
                    materialLot.clearExpressInfo();
                    materialLot = materialLotRepository.saveAndFlush(materialLot);

                    MaterialLotHistory history = (MaterialLotHistory) baseService.buildHistoryBean(materialLot, MaterialLotHistory.TRANS_TYPE_CANCEL_EXPRESS);
                    materialLotHistoryRepository.save(history);
                }
            }

        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 创建寄件人信息
     * @return
     * @throws ClientException
     */
    public WaybillDelivery buildPreWaybillDelivery() throws ClientException{
        try {
            // TODO 当前只有ZJ发货，后续如果有别的发货的话，再加入判断逻辑
            String shippingAddress = ZJ_SHIPPING_ADDRESS;
            List<? extends NBReferenceList> nbReferenceLists = uiService.getReferenceList(shippingAddress, NBReferenceList.CATEGORY_SYSTEM);
            if (CollectionUtils.isEmpty(nbReferenceLists)) {
                throw new ClientParameterException(GcExceptions.SHIPPING_ADDRESS_IS_NULL, shippingAddress);
            }
            WaybillDelivery preWaybillDelivery = new WaybillDelivery();
            for (NBReferenceList nbReference : nbReferenceLists) {
                if (nbReference.getKey().equals("companyName")) {
                    preWaybillDelivery.setCompanyName(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("person")) {
                    preWaybillDelivery.setPerson(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("phone")) {
                    preWaybillDelivery.setPhone(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("mobile")) {
                    preWaybillDelivery.setMobile(nbReference.getValue());
                    continue;
                }if (nbReference.getKey().equals("provinceName")) {
                    preWaybillDelivery.setProvinceName(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("cityName")) {
                    preWaybillDelivery.setCityName(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("countyName")) {
                    preWaybillDelivery.setCountyName(nbReference.getValue());
                    continue;
                }
                if (nbReference.getKey().equals("address")) {
                    preWaybillDelivery.setAddress(nbReference.getValue());
                    continue;
                }
            }
            return preWaybillDelivery;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 单据上记录快递单号
     * @return
     * @throws ClientException
     */
    public List<DocumentLine> recordExpressNumber(List<DocumentLine> documentLines) throws ClientException {
        List<DocumentLine> documentLineList = Lists.newArrayList();
        for (DocumentLine documentLine : documentLines) {
            documentLine = documentLineRepository.saveAndFlush(documentLine);
            documentLineList.add(documentLine);
            baseService.saveHistoryEntity(documentLine, "RecordExpress");
        }
        return documentLineList;
    }

    public List<Map<String,String>> getPrintLabelParameterList(List<MaterialLot> materialLotList, String expressNumber) throws ClientException{
        try {
            List<Map<String, String>> parameterMapList =  Lists.newArrayList();
            List<MaterialLot> expressNumberInfoList = materialLotList.stream().filter(materialLot -> StringUtils.isNullOrEmpty(materialLot.getExpressNumber())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(expressNumberInfoList)){
                throw new ClientParameterException(GcExceptions.MATERIAL_LOT_NOT_RECORD_EXPRESS, expressNumberInfoList.get(0).getMaterialLotId());
            }

            Integer seq = 1;
            Integer numfix = materialLotList.size();
            //按照称重的先后排序打印标签
            List<MaterialLot> materialLots = Lists.newArrayList();
            List<MaterialLot> mLotList = Lists.newArrayList();
            for(MaterialLot materialLot : materialLotList){
                if(StringUtils.isNullOrEmpty(materialLot.getWeightSeq())){
                    materialLots.add(materialLot);
                } else {
                    mLotList.add(materialLot);
                }
            }
            if(CollectionUtils.isNotEmpty(mLotList)){
                mLotList = mLotList.stream().sorted(Comparator.comparing(MaterialLot::getWeightSeq)).collect(Collectors.toList());
                materialLots.addAll(mLotList);
            }
            for (MaterialLot materialLot : materialLots){
                Map<String, String> parameterMap =  Maps.newHashMap();
                parameterMap.put("CSNAME", materialLot.getShipper());
                parameterMap.put("NUMCHANG", seq.toString());
                parameterMap.put("NUMFIX", numfix.toString());
                if(StringUtils.isNullOrEmpty(expressNumber)){
                    parameterMap.put("EXNUM", materialLot.getExpressNumber());
                }else {
                    parameterMap.put("EXNUM", expressNumber);
                }
                parameterMapList.add(parameterMap);
                ++seq;
            }
            return parameterMapList;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


    /**
     * 判断所有的备货单号是否一致
     * @param materialLots
     */
    @Override
    public void validateReservedOrderId(List<MaterialLot> materialLots) throws ClientException{
        try {
            Set reservedDocIdInfo = materialLots.stream().map(materialLot -> materialLot.getReserved17()).collect(Collectors.toSet());
            if (reservedDocIdInfo != null &&  reservedDocIdInfo.size() > 1) {
                throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_DOCID_IS_NOT_SAME);
            }
        }catch (Exception e) {
            throw ExceptionManager.handleException(e,log);
        }
    }

    /**
     * 获取快递单信息
     * @param wayBillNumber
     * @return
     * @throws ClientException
     */
    public OrderInfo getOrderInfoByWayBillNumber(String wayBillNumber) throws ClientException{
        try {
            Map<String, Object> requestParameters = Maps.newHashMap();
            requestParameters.put("waybillNumber", wayBillNumber);
            String responseData = sendRequest(ExpressConfiguration.QUERY_ORDER_STATUS_METHOD, requestParameters);
            OrderInfo orderInfo  =  DefaultParser.getObjectMapper().readValue(responseData, OrderInfo.class);
            if(orderInfo != null && !OrderInfo.ORDER_STATUS_UN_DISPATCH.equals(orderInfo.getOrderStatus())){
                throw new ClientParameterException(GcExceptions.MATERIALLOT_RESERVED_DOCID_IS_NOT_SAME, orderInfo.getWaybillNumber());
            }
            return  orderInfo;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 批量取消快递单号
     * @param orderInfoList
     * @throws ClientException
     */
    public void batchCancelOrderByWayBillNumber(List<OrderInfo> orderInfoList) throws ClientException{
        try {
            if(CollectionUtils.isNotEmpty(orderInfoList)){
                for (OrderInfo orderInfo : orderInfoList){
                    String wayBillNumber = orderInfo.getWaybillNumber();
                    cancelOrder(wayBillNumber);
                }
            }
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }

}
