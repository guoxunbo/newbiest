import TableManagerRequestBody from "./TableManagerRequestBody";
import TableManagerRequestHeader from "./TableManagerRequestHeader";
import Request from "../Request";
import MessageUtils from "../utils/MessageUtils";
import { UrlConstant } from "../const/ConstDefine";

export default class TableManagerRequest {

    /**
     * 根据主键获取table
     * 携带Table的所有栏位以及tab
     * @param object 动态表 @example {tableRrn: 10}
     */
    static sendGetByRrnRequest = (object) => {
        let requestBody = TableManagerRequestBody.buildGetByRrn(object.tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 根据名称获取table
     * 携带Table的所有栏位以及tab
     * @param object 动态表 @example {name: "TableA"}
     */
    static sendGetByNameRequest = (object) => {
        let requestBody = TableManagerRequestBody.buildGetByName(object.name);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 根据主键获取table的对应的实体数据
     * @param object 动态表 @example {tableRrn: 10}
     */
    static sendGetDataByRrnRequest = (object) => {
        let requestBody = TableManagerRequestBody.buildGetDataByRrn(object.tableRrn, object.whereClause);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 根据名称获取table的对应的实体数据
     * @param object 动态表 @example {tableName: "NBTable"}
     */
    static sendGetDataByNameRequest = (object) => {
        let requestBody = TableManagerRequestBody.buildGetDataByName(object.tableName, object.whereClause);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.TableMangerUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendRequest(requestObject);
    }

    /**
     * 发送导出数据请求
     * @param object 动态表 @example {tableRrn: 10}
     * @param expTemplate 是否是导出模板标识
     */
    static sendExportRequest = (object, expTemplate) => {
        let requestBody = TableManagerRequestBody.buildExport(object.tableRrn, expTemplate);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ExporttUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendExpRequest(requestObject, object.fileName);
    }

    static sendImportRequest = (object, file) => {
        let requestBody = TableManagerRequestBody.buildImport(object.tableRrn);
        let requestHeader = new TableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.ImportUrl);
        let requestObject = {
            request: request,
            success: object.success
        }
        MessageUtils.sendImportData(requestObject, file);
    }

}