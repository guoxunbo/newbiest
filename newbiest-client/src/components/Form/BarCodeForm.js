import  React, { Component } from 'react';

import { Form, Input, } from 'antd';
import EntityForm from './EntityForm';
import JsBarcode from 'jsbarcode';
import SimpleBarCode from '../code/SimpleBarCode';

const FormItem = Form.Item;

/**
 * 展示一维码以及二维码的Form
 */

export default class BarCodeForm extends EntityForm {

    static displayName = 'BarCodeForm';

    handleSave= () => {
        this.props.onOk();
    }

    buildForm = () => {
        return (<SimpleBarCode value={this.props.value}></SimpleBarCode>)
    }

}