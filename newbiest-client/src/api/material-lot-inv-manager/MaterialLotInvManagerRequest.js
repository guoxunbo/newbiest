import MaterialLotInvManagerRequestHeader from './MaterialLotInvManagerRequestHeader';
import MaterialLotInvManagerRequestBody from './MaterialLotInvManagerRequestBody';
import {UrlConstant} from '../const/ConstDefine';
import MessageUtils from '../utils/MessageUtils';
import Request from '../Request';

export default class MaterialLotInvManagerRequest {

    static sendStockOutRequest = (object) => {
        let requestBody = MaterialLotInvManagerRequestBody.buildStockOut(object.mLotInventory);
        let requestHeader = new MaterialLotInvManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotInvManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
    
    static sendTransferStockRequest = (object) => {
        let requestBody = MaterialLotInvManagerRequestBody.buildTransferInv(object.transferInvObject);
        let requestHeader = new MaterialLotInvManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotInvManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendPickRequest = (object) => {
        let requestBody = MaterialLotInvManagerRequestBody.buildPick(object.mLotInventory);
        let requestHeader = new MaterialLotInvManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotInvManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendCheckRequest = (object) => {
        let requestBody = MaterialLotInvManagerRequestBody.buildCheck(object.checkInvObject);
        let requestHeader = new MaterialLotInvManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotInvManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
}