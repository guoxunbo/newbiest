import  React, { Component } from 'react';

import EntityForm from './EntityForm';
import SimpleBarCode from '../code/SimpleBarCode';
import QRCode from 'qrcode.react';
import * as PropTypes from 'prop-types';

/**
 * 展示一维码和二维码的Form
 */
const CodeType ={
    BarCode: "BarCode",
    QrCode: "QrCode"
}
export default class BarCodeForm extends EntityForm {

    static displayName = 'BarCodeForm';

    constructor(props) {
        super(props);
    }

    handleOk= () => {
        if (this.props.type == CodeType.BarCode) {
            const win = window.open('','printwindow'); 
            win.document.write(window.document.getElementById('barcode').innerHTML);
            win.print();
            win.close();
        } else if (this.props.type == CodeType.QrCode) {
            let elink = document.createElement('a');
            elink.download = "qrcode.png";
            elink.style.display = 'none';
            elink.href = window.document.getElementById('qrCode').toDataURL();
            document.body.appendChild(elink);
            elink.click();
            document.body.removeChild(elink);
        }
        this.props.onOk();
    }

    buildForm = () => {
        if (this.props.type == CodeType.BarCode) {
            return this.buildBarCode();
        } else if (this.props.type == CodeType.QrCode) {
            return this.buildQrCode();
        }
    }

    buildQrCode = () => {
        return (<QRCode id="qrCode" value={this.props.value} size={250}></QRCode>)
    }

    buildBarCode = () => {
        return (
            <div id="barcode">
                <SimpleBarCode value={this.props.value}></SimpleBarCode>
            </div>)
    }
    
}

BarCodeForm.propTypes={
    type: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
}


export {CodeType}