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
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.gc.ExpressConfiguration;
import com.newbiest.gc.GcExceptions;
import com.newbiest.gc.express.dto.ExpressResponse;
import com.newbiest.gc.express.dto.OrderInfo;
import com.newbiest.gc.express.dto.WaybillDelivery;
import com.newbiest.gc.service.ExpressService;
import com.newbiest.mms.model.DocumentLine;
import com.newbiest.mms.model.MaterialLot;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author guoxunbo
 * @date 2020-07-08 13:52
 */
@Service
@Slf4j
@ConfigurationProperties(prefix = "gc.express")
@Data
public class ExpressServiceImpl implements ExpressService {

    public static final String ZJ_SHIPPING_ADDRESS = "ZJShippingAddress";
    public static final String SH_SHIPPING_ADDRESS= "SHShippingAddress";

    @Autowired
    ExpressConfiguration expressConfiguration;

    @Autowired
    UIService uiService;

    @Value("${spring.profiles.active}")
    private String profiles;

    private boolean isProdEnv() {
        return "prod".equalsIgnoreCase(profiles);
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

    private ExpressResponse sendRequest(String methodCode, Object parameter) throws ClientException{
        try {
            if (log.isInfoEnabled()) {
                log.info("Start to send [" + methodCode + "] to express.");
            }
            String token = getToken();
            KyeClient kyeClient = new DefaultKyeClient(isProdEnv() ? KyeConstants.SERVER_URL : KyeConstants.SANDBOX_SERVER_URL, expressConfiguration.getAppKey(), expressConfiguration.getAppSecret(), token);
            DefaultRequest request = new DefaultRequest(methodCode, parameter, KyeConstants.REQUEST_DATA_FORMAT_JSON, KyeConstants.RESPONSE_DATA_FORMAT_JSON);
            DefaultKyeResponse response = kyeClient.execute(request);
            if (response == null) {
                throw new ClientException("网络异常");
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
            return response.getData(ExpressResponse.class);
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    /**
     * 给跨域速递下单
     * @param expressNumber 快递单号，如果没值，由快递生成
     * @param materialLots 物料批次
     * @param serviceMode 服务模式 次日达等等
     * @param payMode 支付方式
     */
    public String planOrder(String expressNumber, List<MaterialLot> materialLots, int serviceMode, int payMode) throws ClientException {
        try {
            //TODO 收货人地址待从单据上来。现在还没提供
            String shippingAddress = "tttttt";

            Map<String, Object> requestParameters = Maps.newHashMap();
            requestParameters.put("customerCode", expressConfiguration.getCustomerCode());
            requestParameters.put("platformFlag", expressConfiguration.getPlatformFlag());

            List<OrderInfo> orderInfos = Lists.newArrayList();
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setWaybillNumber(expressNumber);
            orderInfo.setPreWaybillDelivery(buildPreWaybillDelivery());

            WaybillDelivery preWaybillPickup = new WaybillDelivery();
            preWaybillPickup.setAddress(shippingAddress);
            orderInfo.setPreWaybillPickup(preWaybillPickup);

            orderInfo.setServiceMode(serviceMode);
            orderInfo.setPayMode(payMode);

            orderInfo.setOrderId("GC00000001");
            orderInfos.add(orderInfo);

            requestParameters.put("orderInfos", orderInfos);

            ExpressResponse response = sendRequest(ExpressConfiguration.PLAN_ORDER_METHOD, requestParameters);
            return response.getWaybillNumber();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    public void cancelOrder(String expressNumber) throws ClientException {
        try {
            Map<String, Object> requestParameters = Maps.newHashMap();
            requestParameters.put("customerCode", expressConfiguration.getCustomerCode());
            requestParameters.put("waybillNumber", expressNumber);
            sendRequest(ExpressConfiguration.PLAN_ORDER_METHOD, requestParameters);
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
                //TODO 抛异常
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
            return preWaybillDelivery.build();
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }


}
