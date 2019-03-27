import MaterialLotManagerRequestHeader from './MaterialLotManagerRequestHeader';
import MaterialLotManagerRequestBody from './MaterialLotManagerRequestBody';
import {UrlConstant} from '../const/ConstDefine';
import MessageUtils from '../utils/MessageUtils';
import Request from '../Request';

export default class MaterialLotManagerRequest {

    static sendReceiveMaterialLotRequest = (object) => {
        let receiveMaterial = object.receiveMaterial;
        let requestBody = MaterialLotManagerRequestBody.buildReceiveMaterialLot(receiveMaterial.materialName, receiveMaterial.warehouseRrn, receiveMaterial.transQty);
        let requestHeader = new MaterialLotManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    static sendStockOutRequest = (object) => {
        let requestBody = MaterialLotManagerRequestBody.buildStockOut(object.mLotInventory);
        let requestHeader = new MaterialLotManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }
    
    static sendTransferStockRequest = (object) => {
        let requestBody = MaterialLotManagerRequestBody.buildTransferInv(object.transferInvObject);
        let requestHeader = new MaterialLotManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.MaterialLotManagerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

}