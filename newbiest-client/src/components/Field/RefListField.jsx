import RefListManagerRequestBody from '../../api/ref-list-manager/RefListManagerRequestBody';
import RefListManagerRequestHeader from '../../api/ref-list-manager/RefListManagerRequestHeader';
import {UrlConstant} from "../../api/const/ConstDefine";
import MessageUtils from "../../api/utils/MessageUtils";
import Request from '../../api/Request';
import Combox from './Combox';

import * as PropTypes from 'prop-types';

export default class RefListField extends Combox {

    static displayName = 'RefListField';

    constructor(props) {
        super(props);
    }

    queryData = () => {
        let self = this;
        let requestBody = {};
        if (this.props.owner) {
            requestBody = RefListManagerRequestBody.buildOwnerData(this.props.referenceName)
        } else {
            requestBody = RefListManagerRequestBody.buildSystemData(this.props.referenceName)
        }
        let requestHeader = new RefListManagerRequestHeader();
        let request = new Request(requestHeader, requestBody, UrlConstant.RefListMangerUrl);
        let requestObject = {
            request: request,
            success: function(responseBody) {
                self.setState({
                    data: responseBody.dataList,
                });
            }
        }
        MessageUtils.sendRequest(requestObject);
    }
}

RefListField.prototypes = {
    owner: PropTypes.bool,
    referenceName: PropTypes.string.isRequired
}