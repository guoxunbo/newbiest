/**
 * 请求EntityManager操作相关的消息请求类
 */
import EntityManagerRequestHeader from './EntityManagerRequestHeader';
import EntityManagerRequestBody from './EntityManagerRequestBody';
import {UrlConstant, DefaultRowKey} from '../const/ConstDefine';
import MessageUtils from '../utils/MessageUtils';
import * as PropTypes from 'prop-types';
import Request from '../Request';

export default class EntityManagerRequest {

    modelClass;
    values;

    /**
     * 发送merge信息请求
     */
    static sendMergeRequest = (object) => {
        // 如果数据是新增的，清空objectRrn栏位
        if (object.values.newFlag) {
            object.values[DefaultRowKey] = undefined;
        }
        let requestBody = EntityManagerRequestBody.buildMergeEntity(object.modelClass, object.values);
        let requestHeader = new EntityManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.EntityManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
    
    static sendDeleteRequest = (object) => {
        let requestBody = EntityManagerRequestBody.buildDeleteEntity(object.modelClass, object.values, object.deleteRelationEntityFlag);
        let requestHeader = new EntityManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.EntityManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }


}
EntityManagerRequest.prototypes = {
    modelClass: PropTypes.string.isRequired,
    values: PropTypes.object,
}