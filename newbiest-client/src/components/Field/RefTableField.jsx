import RefTableManagerRequestBody from '../../api/ref-table-manager/RefTableManagerRequestBody';
import RefTableManagerRequestHeader from '../../api/ref-table-manager/RefTableManagerRequestHeader';
import {UrlConstant} from "../../api/const/ConstDefine";
import MessageUtils from "../../api/utils/MessageUtils";

import Request from '../../api/Request';
import Combox from './Combox';
import * as PropTypes from 'prop-types';

export default class RefTableField extends Combox {

    static displayName = 'RefTableField';
    
    queryData = () => {
        let self = this;
        let requestBody = RefTableManagerRequestBody.buildGetData(this.props.refTableName)
        let requestHeader = new RefTableManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RefTableManagerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                let refTable = responseBody.referenceTable;
                let data = [];
                responseBody.dataList.map(d => {
                    let refData = {
                        key: d[refTable.keyField],
                        value: d[refTable.textField]
                    };
                    data.push(refData);
                }) 
                self.setState({data: data})
            }
        }
        MessageUtils.sendRequest(requestObject);
    }
}
RefTableField.prototypes = {
    refTableName: PropTypes.string.isRequired
}