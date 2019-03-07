import RawMaterialManagerRequestHeader from './RawMaterialManagerRequestHeader';
import RawMaterialManagerRequestBody from './RawMaterialManagerRequestBody';
import {UrlConstant, DefaultRowKey} from '../const/ConstDefine';
import MessageUtils from '../utils/MessageUtils';
import Request from '../Request';

export default class RawMaterialManagerRequest {

    /**
     * 发送merge信息请求
     */
    static sendMergeRequest = (object) => {
        // 如果数据是新增的，清空objectRrn栏位
        if (object.rawMaterial.newFlag) {
            object.rawMaterial[DefaultRowKey] = undefined;
        }
        let requestBody = RawMaterialManagerRequestBody.buildMergeRawMaterial(object.rawMaterial);
        let requestHeader = new RawMaterialManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RawMaterialManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
}